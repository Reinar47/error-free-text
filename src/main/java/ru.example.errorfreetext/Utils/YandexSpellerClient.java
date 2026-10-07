package ru.example.errorfreetext.Utils;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import ru.example.errorfreetext.constant.Language;
import ru.example.errorfreetext.exception.YandexApiException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import static ru.example.errorfreetext.messages.ErrorFreeTextMessages.yandexApiChunksMismatchMessage;
import static ru.example.errorfreetext.messages.ErrorFreeTextMessages.yandexApiEmptyResponseMessage;
import static ru.example.errorfreetext.messages.ErrorFreeTextMessages.yandexApiRequestFailedMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class YandexSpellerClient {

    private static final Pattern HAS_DIGIT = Pattern.compile("\\d");

    private static final Pattern HAS_URL = Pattern.compile(
            "(?i)(" +
                    "https?://\\S+" +
                    "|ftp://\\S+" +
                    "|www\\.\\S+" +
                    "|\\b[\\w.+-]+@[\\w-]+\\.[\\w.-]+\\b" +
                    "|\\b[\\w-]+\\.(?:com|ru|org|net|edu|gov|io|dev|info|biz|me|co|uk|de|fr|jp|cn|рф)(?:/\\S*)?\\b" +
                    ")"
    );

    private static final Pattern HAS_FILE_EXTENSION = Pattern.compile(
            "(?i)\\b[\\w-]+\\.(?:txt|pdf|docx?|xlsx?|pptx?|png|jpe?g|gif|bmp|svg|webp|zip|rar|7z|tar|gz|bz2|mp3|mp4|avi|mkv|mov|exe|dll|jar|war|class|java|py|cpp|c|h|js|ts|html?|css|xml|json|yaml?|yml|sql|log|csv)\\b"
    );

    /**
     * Флаги API Yandex Speller.
     */
    private static final int FLAG_IGNORE_DIGITS = 2;
    private static final int FLAG_IGNORE_URLS = 4;

    private final RestClient yandexSpellerRestClient;

    /**
     * Отправляет список фрагментов текста в Yandex Speller методом checkTexts.
     *
     * @param chunks список фрагментов (каждый не более 10 000 символов)
     * @param language язык проверки
     * @return корневой JSON-узел ответа (массив массивов ошибок)
     * @throws YandexApiException если запрос завершился ошибкой или ответ пуст
     */
    public JsonNode checkTexts(List<String> chunks, Language language) {
        List<Integer> options = buildOptions(chunks);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        chunks.forEach(chunk -> formData.add("text", chunk));
        formData.add("lang", language.name().toLowerCase());
        if (!options.isEmpty()) {
            formData.add("options", joinOptions(options));
        }

        try {
            JsonNode response = yandexSpellerRestClient.post()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null || !response.isArray()) {
                throw new YandexApiException(yandexApiEmptyResponseMessage);
            }
            if (response.size() != chunks.size()) {
                throw new YandexApiException(
                        yandexApiChunksMismatchMessage + chunks.size() + " but got " + response.size());
            }

            log.debug("Yandex Speller вернул ответ для {} фрагмент(ов)", chunks.size());
            return response;

        } catch (YandexApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Ошибка при обращении к Yandex Speller", ex);
            throw new YandexApiException(yandexApiRequestFailedMessage + ex.getMessage(), ex);
        }
    }

    /**
     * Применяет правки Yandex Speller к исходным фрагментам текста.
     *
     * @param originalChunks исходные фрагменты
     * @param response ответ Yandex Speller
     * @return список исправленных фрагментов
     */
    public List<String> applyCorrections(List<String> originalChunks, JsonNode response) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < originalChunks.size(); i++) {
            String src = originalChunks.get(i);
            JsonNode errors = response.get(i);
            result.add(applyToChunk(src, errors));
        }
        return result;
    }

    private List<Integer> buildOptions(List<String> chunks) {
        String joined = String.join(" ", chunks);
        List<Integer> options = new ArrayList<>();

        if (HAS_DIGIT.matcher(joined).find()) {
            options.add(FLAG_IGNORE_DIGITS);
        }
        if (HAS_URL.matcher(joined).find() || HAS_FILE_EXTENSION.matcher(joined).find()) {
            options.add(FLAG_IGNORE_URLS);
        }
        return options;
    }

    private String joinOptions(List<Integer> options) {
        int sum = options.stream()
                .mapToInt(Integer::intValue)
                .sum();
        return String.valueOf(sum);
    }

    private String applyToChunk(String src, JsonNode errors) {
        if (errors == null || !errors.isArray() || errors.isEmpty()) {
            return src;
        }

        List<JsonNode> list = new ArrayList<>();
        errors.forEach(list::add);
        list.sort(Comparator.comparingInt(a -> a.get("pos").asInt()));

        StringBuilder sb = new StringBuilder();
        int cursor = 0;
        for (JsonNode err : list) {
            int pos = err.get("pos").asInt();
            int len = err.get("len").asInt();
            JsonNode s = err.get("s");
            String replacement = (s != null && s.isArray() && s.size() > 0)
                    ? s.get(0).asText()
                    : err.path("word").asText();

            if (pos < cursor) {
                continue;
            }
            sb.append(src, cursor, pos).append(replacement);
            cursor = pos + len;
        }
        sb.append(src.substring(cursor));
        return sb.toString();
    }
}

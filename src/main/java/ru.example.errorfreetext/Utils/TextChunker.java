package ru.example.errorfreetext.Utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TextChunker {

    /**
     * Максимальный размер одного фрагмента для POST-запроса Yandex Speller.
     */
    public static final int MAX_CHUNK_SIZE = 10_000;

    /**
     * Режет текст на фрагменты длиной не более {@link #MAX_CHUNK_SIZE} символов,
     * стараясь не разрывать слова: если возможно, граница сдвигается к последнему пробелу.
     *
     * @param text исходный текст
     * @return список фрагментов
     */
    public List<String> split(String text) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return chunks;
        }

        int start = 0;
        int length = text.length();

        while (start < length) {
            int end = Math.min(start + MAX_CHUNK_SIZE, length);

            if (end < length) {
                int lastSpace = text.lastIndexOf(' ', end);
                if (lastSpace > start) {
                    end = lastSpace;
                }
            }

            String chunk = text.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }

            start = end;
            while (start < length && text.charAt(start) == ' ') {
                start++;
            }
        }

        log.debug("Текст длиной {} разбит на {} фрагмент(ов)", length, chunks.size());
        return chunks;
    }
}
package ru.example.errorfreetext.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.example.errorfreetext.Utils.TextChunker;
import ru.example.errorfreetext.Utils.YandexSpellerClient;
import ru.example.errorfreetext.constant.TaskStatus;
import ru.example.errorfreetext.entity.TaskEntity;
import ru.example.errorfreetext.exception.YandexApiException;
import ru.example.errorfreetext.repository.TaskRepository;
import ru.example.errorfreetext.service.TextCorrectionService;

import java.util.List;

import static ru.example.errorfreetext.messages.ErrorFreeTextMessages.yandexApiRequestEmptyResponseMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextCorrectionServiceImpl implements TextCorrectionService {

    private final TaskRepository taskRepository;
    private final TextChunker textChunker;
    private final YandexSpellerClient yandexSpellerClient;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process(TaskEntity task) {
        log.info("Начало обработки задачи с ID: {}", task.getId());

        task.setStatus(TaskStatus.IN_PROGRESS);
        taskRepository.save(task);

        try {
            List<String> chunks = textChunker.split(task.getOriginalText());
            log.debug("Задача {} разбита на {} фрагмент(ов)", task.getId(), chunks.size());


            JsonNode response = yandexSpellerClient.checkTexts(chunks, task.getLanguage());

            if (response.isEmpty()) {
                log.error("Задача с ID: {} завершилась ошибкой: {}", task.getId(),
                        yandexApiRequestEmptyResponseMessage);

                task.setStatus(TaskStatus.ERROR);
                task.setErrorMessage(yandexApiRequestEmptyResponseMessage);
                taskRepository.save(task);

            }


            List<String> correctedChunks = yandexSpellerClient.applyCorrections(chunks, response);
            String correctedText = String.join(" ", correctedChunks);

            task.setCorrectedText(correctedText);
            task.setStatus(TaskStatus.DONE);
            task.setErrorMessage("Ошибок нет");
            taskRepository.save(task);

            log.info("Задача с ID: {} успешно обработана", task.getId());

        } catch (YandexApiException ex) {
            log.error("Задача с ID: {} завершилась ошибкой: {}", task.getId(), ex.getMessage());
            task.setStatus(TaskStatus.ERROR);
            task.setErrorMessage(ex.getMessage());
            taskRepository.save(task);
        }
    }
}
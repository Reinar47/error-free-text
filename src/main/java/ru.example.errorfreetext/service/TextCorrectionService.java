package ru.example.errorfreetext.service;

import ru.example.errorfreetext.entity.TaskEntity;

public interface TextCorrectionService {

    /**
     * Обрабатывает задачу: режет текст, обращается к Yandex Speller,
     * сохраняет результат и обновляет статус.
     *
     * @param task задача в статусе NEW
     */
    void process(TaskEntity task);
}

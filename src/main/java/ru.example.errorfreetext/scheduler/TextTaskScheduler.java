package ru.example.errorfreetext.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.example.errorfreetext.constant.TaskStatus;
import ru.example.errorfreetext.entity.TaskEntity;
import ru.example.errorfreetext.repository.TaskRepository;
import ru.example.errorfreetext.service.TextCorrectionService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TextTaskScheduler {

    private final TaskRepository taskRepository;
    private final TextCorrectionService textCorrectionService;

    @Value("${scheduler.batch-size:10}")
    private int batchSize;

    /**
     * Периодически забирает из БД новые задачи и передаёт их на обработку.
     * Периодичность настраивается через {@code scheduler.fixed-delay-ms}.
     */
    @Scheduled(fixedDelayString = "${scheduler.fixed-delay-ms:5000}")
    public void processNewTasks() {
        List<TaskEntity> tasks = taskRepository.findAllByStatusOrderByCreatedAtAsc(
                TaskStatus.NEW,
                PageRequest.of(0, batchSize));

        if (tasks.isEmpty()) {
            return;
        }

        log.debug("Планировщик забрал {} задач(у) на обработку", tasks.size());
        tasks.forEach(textCorrectionService::process);
    }
}
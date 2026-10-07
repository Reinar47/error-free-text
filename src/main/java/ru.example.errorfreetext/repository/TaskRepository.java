package ru.example.errorfreetext.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.errorfreetext.constant.TaskStatus;
import ru.example.errorfreetext.entity.TaskEntity;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

    List<TaskEntity> findAllByStatusOrderByCreatedAtAsc(TaskStatus status, Pageable pageable);
}

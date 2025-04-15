package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Status;
import com.example.taskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Поиск задач по статусу
    List<Task> findByStatus(Status status);

    // Поиск задач по категории
    List<Task> findByCategoryId(Long categoryId);

    // Поиск задач по названию (игнорируя регистр)
    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Task> searchByTitle(@Param("title") String title);


    List<Task> findByUserId(Long userId);
}

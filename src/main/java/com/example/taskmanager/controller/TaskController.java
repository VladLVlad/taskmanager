package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskDTO;
import com.example.taskmanager.entity.Status;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;


    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;

    }

    // Доступен всем (выводит все задачи)
//    @GetMapping("/all")
//    public List<TaskDTO> getAllTasks() {
//        List<Task> tasks = taskRepository.findAll();
//        return tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
//    }

    // Выводит только задачи текущего пользователя
    @GetMapping("/user")
    public List<TaskDTO> getUserTasks() {
        User currentUser = getCurrentUser();
        List<Task> tasks = taskRepository.findByUserId(currentUser.getId());
        return tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Поиск задач по статусу (только свои)
    @GetMapping("/status/{status}")
    public List<TaskDTO> getTasksByStatus(@PathVariable Status status) {
        User currentUser = getCurrentUser();
        List<Task> tasks = taskRepository.findByStatus(status).stream()
                .filter(task -> task.getUser().getId().equals(currentUser.getId()))
                .toList();

        return tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Поиск задач по категории (только свои)
    @GetMapping("/category/{categoryId}")
    public List<TaskDTO> getTasksByCategory(@PathVariable Long categoryId) {
        User currentUser = getCurrentUser();
        List<Task> tasks = taskRepository.findByCategoryId(categoryId).stream()
                .filter(task -> task.getUser().getId().equals(currentUser.getId()))
                .toList();

        return tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    // Поиск задач по названию (только свои)
    @GetMapping("/search")
    public ResponseEntity<List<TaskDTO>> searchTasksByTitle(@RequestParam String title) {
        User currentUser = getCurrentUser();  // Получаем текущего пользователя
        List<TaskDTO> tasks = taskRepository.searchByTitle(title).stream()
                .filter(task -> task.getUser().getId().equals(currentUser.getId()))  // Фильтруем задачи по пользователю
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tasks);
    }


    // Добавление новой задачи (привязывается к текущему пользователю)
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody Task task) {
        User currentUser = getCurrentUser();
        task.setUser(currentUser);
        Task savedTask = taskRepository.save(task);
        return ResponseEntity.ok(convertToDTO(savedTask));
    }

    // Обновление задачи (может редактировать только свою)
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User currentUser = getCurrentUser();

        if (!existingTask.getUser().equals(currentUser)) {
            return ResponseEntity.status(403).build();
        }

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStatus(updatedTask.getStatus());

        Task savedTask = taskRepository.save(existingTask);
        return ResponseEntity.ok(convertToDTO(savedTask));
    }

    // Удаление задачи (может удалить только свою)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User currentUser = getCurrentUser();

        if (!task.getUser().equals(currentUser)) {
            return ResponseEntity.status(403).build();
        }

        taskRepository.delete(task);
        return ResponseEntity.noContent().build();
    }

    // Метод для получения текущего пользователя
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Конвертация Task в TaskDTO
    private TaskDTO convertToDTO(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getUser().getId(),
                task.getCategory() != null ? task.getCategory().getId() : null
        );
    }
}

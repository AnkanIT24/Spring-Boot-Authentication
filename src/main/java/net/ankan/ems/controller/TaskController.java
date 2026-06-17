package net.ankan.ems.controller;

import jakarta.validation.Valid;
import net.ankan.ems.dto.TaskDto;
import net.ankan.ems.dto.TaskResponseDto;
import net.ankan.ems.entity.TaskStatus;
import net.ankan.ems.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // ADMIN — create task + assignments
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskDto taskDto) {
        TaskResponseDto response = taskService.createTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ADMIN — get all task assignments
    @GetMapping("/all")
    public ResponseEntity<List<TaskResponseDto>> getAllTaskAssignments() {
        return ResponseEntity.ok(taskService.getAllTaskAssignments());
    }

    // USER + ADMIN — get own task assignments
    @GetMapping("/my")
    public ResponseEntity<List<TaskResponseDto>> getMyTasks() {
        return ResponseEntity.ok(taskService.getMyTasks());
    }

    // USER + ADMIN — update status of an assignment
    @PutMapping("/{assignmentId}/status")
    public ResponseEntity<TaskResponseDto> updateStatus(
            @PathVariable Long assignmentId,
            @RequestBody Map<String, String> body) {
        TaskStatus status = TaskStatus.valueOf(body.get("status").toUpperCase());
        return ResponseEntity.ok(taskService.updateTaskStatus(assignmentId, status));
    }

    // ADMIN — delete task + all its assignments
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long taskId,
            @RequestBody TaskDto taskDto) {
        return ResponseEntity.ok(taskService.updateTask(taskId, taskDto));
    }
}
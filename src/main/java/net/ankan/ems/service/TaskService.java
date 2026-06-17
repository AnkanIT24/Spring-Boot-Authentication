package net.ankan.ems.service;

import net.ankan.ems.dto.TaskDto;
import net.ankan.ems.dto.TaskResponseDto;
import net.ankan.ems.entity.TaskStatus;

import java.util.List;

public interface TaskService {

    // ADMIN: create task + assignments
    TaskResponseDto createTask(TaskDto taskDto);

    // ADMIN: get all assignments across all tasks
    List<TaskResponseDto> getAllTaskAssignments();

    // USER: get own assignments only
    List<TaskResponseDto> getMyTasks();

    // BOTH: update status — service enforces ownership for USER
    TaskResponseDto updateTaskStatus(Long assignmentId, TaskStatus status);

    // ADMIN: delete task + all its assignments
    void deleteTask(Long taskId);

    //ADMIN:edit restrictions added
    TaskResponseDto updateTask(Long taskId, TaskDto taskDto);
}
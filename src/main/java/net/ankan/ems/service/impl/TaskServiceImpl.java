package net.ankan.ems.service.impl;

import net.ankan.ems.dto.TaskDto;
import net.ankan.ems.dto.TaskResponseDto;
import net.ankan.ems.entity.*;
import net.ankan.ems.exception.ResourceNotFoundException;
import net.ankan.ems.repository.*;
import net.ankan.ems.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private TaskAssignmentRepository taskAssignmentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private EmployeeRepository employeeRepository;

    // ── helpers ──────────────────────────────────────────────────────────────

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    private TaskResponseDto toDto(TaskAssignment assignment) {
        Task task = assignment.getTask();
        TaskResponseDto dto = new TaskResponseDto();
        dto.setAssignmentId(assignment.getId());
        dto.setTaskId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setDueDate(task.getDueDate());
        dto.setStatus(assignment.getStatus());
        dto.setAssignmentType(task.getAssignmentType());
        dto.setCreatedByName(task.getCreatedBy().getFullName());
        dto.setAssignedUserName(
                task.getAssignedUser() != null ? task.getAssignedUser().getFullName() : null);
        dto.setAssignedDepartmentName(
                task.getAssignedDepartment() != null
                        ? task.getAssignedDepartment().getDepartmentName() : null);
        return dto;
    }

    // ── department helper ─────────────────────────────────────────────────────

    private List<User> getUsersByDepartment(Department dept) {
        List<String> emails = employeeRepository.findByDepartmentId(dept.getId())
                .stream()
                .map(Employee::getEmail)
                .collect(Collectors.toList());

        return userRepository.findAll().stream()
                .filter(u -> emails.contains(u.getEmail()))
                .collect(Collectors.toList());
    }

    // ── create task + assignments ─────────────────────────────────────────────

    @Override
    @Transactional
    public TaskResponseDto createTask(TaskDto dto) {
        User admin = getCurrentUser();

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setCreatedBy(admin);
        task.setAssignmentType(dto.getAssignmentType());

        List<User> recipients = new ArrayList<>();

        switch (dto.getAssignmentType()) {

            case USER -> {
                if (dto.getAssignedUserId() == null)
                    throw new IllegalArgumentException(
                            "assignedUserId is required for USER assignment");
                // assignedUserId is now the actual users.id (not employee.id)
                User target = userRepository.findById(dto.getAssignedUserId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "User not found: " + dto.getAssignedUserId()));
                task.setAssignedUser(target);
                recipients.add(target);
            }

            case DEPARTMENT -> {
                if (dto.getAssignedDepartmentId() == null)
                    throw new IllegalArgumentException(
                            "assignedDepartmentId is required for DEPARTMENT assignment");
                Department dept = departmentRepository.findById(dto.getAssignedDepartmentId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Department not found: " + dto.getAssignedDepartmentId()));
                task.setAssignedDepartment(dept);
                recipients = getUsersByDepartment(dept);
            }

            case ALL -> {
                recipients = userRepository.findAll().stream()
                        .filter(u -> u.getRole() == Role.USER)
                        .collect(Collectors.toList());
            }
        }

        taskRepository.save(task);

        List<TaskAssignment> assignments = new ArrayList<>();
        for (User recipient : recipients) {
            TaskAssignment ta = new TaskAssignment();
            ta.setTask(task);
            ta.setAssignedUser(recipient);
            ta.setStatus(TaskStatus.PENDING);
            assignments.add(ta);
        }
        taskAssignmentRepository.saveAll(assignments);

        return assignments.isEmpty() ? new TaskResponseDto() : toDto(assignments.get(0));
    }

    // ── get all assignments (ADMIN) ───────────────────────────────────────────

    @Override
    public List<TaskResponseDto> getAllTaskAssignments() {
        return taskAssignmentRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ── get own assignments (USER) ────────────────────────────────────────────

    @Override
    public List<TaskResponseDto> getMyTasks() {
        User current = getCurrentUser();
        return taskAssignmentRepository.findByAssignedUser(current)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ── update status ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public TaskResponseDto updateTaskStatus(Long assignmentId, TaskStatus status) {
        User current = getCurrentUser();

        TaskAssignment assignment;

        if (current.getRole() == Role.ADMIN) {
            assignment = taskAssignmentRepository.findById(assignmentId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Assignment not found: " + assignmentId));
        } else {
            assignment = taskAssignmentRepository.findByIdAndAssignedUser(assignmentId, current)
                    .orElseThrow(() -> new AccessDeniedException(
                            "You can only update your own tasks"));
        }

        assignment.setStatus(status);
        taskAssignmentRepository.save(assignment);
        return toDto(assignment);
    }

    // ── delete task (ADMIN) ───────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found: " + taskId));

        taskAssignmentRepository.deleteAll(
                taskAssignmentRepository.findByTaskId(taskId));
        taskRepository.delete(task);
    }

    // ── update task content (ADMIN, PENDING only) ─────────────────────────────

    @Override
    @Transactional
    public TaskResponseDto updateTask(Long taskId, TaskDto dto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found: " + taskId));

        // Block edit if any assignment is already COMPLETED
        boolean anyCompleted = taskAssignmentRepository.findByTaskId(taskId)
                .stream()
                .anyMatch(a -> a.getStatus() == TaskStatus.COMPLETED);

        if (anyCompleted) {
            throw new AccessDeniedException("Cannot edit a task that has been completed");
        }

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        taskRepository.save(task);

        return taskAssignmentRepository.findByTaskId(taskId)
                .stream().findFirst()
                .map(this::toDto)
                .orElse(new TaskResponseDto());
    }
}
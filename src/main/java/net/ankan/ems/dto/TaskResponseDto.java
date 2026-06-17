package net.ankan.ems.dto;

import lombok.*;
import net.ankan.ems.entity.AssignmentType;
import net.ankan.ems.entity.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {

    private Long assignmentId;   // TaskAssignment.id — used for status updates
    private Long taskId;         // Task.id
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDate dueDate;
    private TaskStatus status;   // per-user status from TaskAssignment
    private AssignmentType assignmentType;
    private String createdByName;
    private String assignedUserName;     // nullable
    private String assignedDepartmentName; // nullable
}
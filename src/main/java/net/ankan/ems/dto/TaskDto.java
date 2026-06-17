package net.ankan.ems.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.ankan.ems.entity.AssignmentType;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private LocalDate dueDate;

    @NotNull(message = "Assignment type is required")
    private AssignmentType assignmentType;

    // Required when assignmentType = USER
    private Long assignedUserId;

    // Required when assignmentType = DEPARTMENT
    private Long assignedDepartmentId;
}
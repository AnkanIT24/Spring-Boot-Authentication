package net.ankan.ems.repository;

import net.ankan.ems.entity.TaskAssignment;
import net.ankan.ems.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {

    List<TaskAssignment> findByAssignedUser(User user);

    Optional<TaskAssignment> findByIdAndAssignedUser(Long id, User user);

    List<TaskAssignment> findByTaskId(Long taskId);
}
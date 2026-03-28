package com.dev.doable.repository;

import com.dev.doable.model.Task;
import com.dev.doable.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserOrderByCreatedAtDesc(User user);

    List<Task> findByUserAndIsDone(User user, Boolean isDone);

    List<Task> findByUserAndTitleContainingIgnoreCase(User user, String title);
}
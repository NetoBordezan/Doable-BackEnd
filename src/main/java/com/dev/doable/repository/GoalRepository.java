package com.dev.doable.repository;

import com.dev.doable.model.Goal;
import com.dev.doable.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByUserAndActiveTrue(User user);

    List<Goal> findByUser(User user);
}
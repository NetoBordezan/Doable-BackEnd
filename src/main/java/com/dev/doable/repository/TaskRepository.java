package com.dev.doable.repository;

import com.dev.doable.model.Task;
import com.dev.doable.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserOrderByCreatedAtDesc(User user);

    List<Task> findByUserAndIsDone(User user, Boolean isDone);

    List<Task> findByUserAndTitleContainingIgnoreCase(User user, String title);

    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.isDone = true " +
            "AND t.updatedAt >= :since ORDER BY t.updatedAt DESC")
    List<Task> findCompletedSince(@Param("user") User user,
                                  @Param("since") LocalDateTime since);

    @Query("SELECT t FROM Task t JOIN t.tags tag WHERE t.user = :user AND t.isDone = true " +
            "AND tag.id = :tagId AND t.updatedAt >= :since ORDER BY t.updatedAt DESC")
    List<Task> findCompletedSinceWithTag(@Param("user") User user,
                                         @Param("since") LocalDateTime since,
                                         @Param("tagId") Long tagId);

    @Query("SELECT CAST(t.updatedAt AS date), COUNT(t) FROM Task t " +
            "WHERE t.user = :user AND t.isDone = true AND t.updatedAt >= :since " +
            "GROUP BY CAST(t.updatedAt AS date) ORDER BY CAST(t.updatedAt AS date)")
    List<Object[]> countCompletedPerDay(@Param("user") User user,
                                        @Param("since") LocalDateTime since);
}
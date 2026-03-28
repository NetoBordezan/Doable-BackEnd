package com.dev.doable.repository;

import com.dev.doable.model.Tag;
import com.dev.doable.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByUser(User user);

    boolean existsByNameAndUser(String name, User user);
}
package com.dev.doable.service;

import com.dev.doable.dto.GoalDto;
import com.dev.doable.model.Goal;
import com.dev.doable.model.Tag;
import com.dev.doable.model.User;
import com.dev.doable.repository.GoalRepository;
import com.dev.doable.repository.TagRepository;
import com.dev.doable.repository.TaskRepository;
import com.dev.doable.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public List<GoalDto> findAll() {
        User user = getCurrentUser();
        return goalRepository.findByUser(user)
                .stream()
                .map(goal -> toDto(goal, user))
                .collect(Collectors.toList());
    }

    public List<GoalDto> findActive() {
        User user = getCurrentUser();
        return goalRepository.findByUserAndActiveTrue(user)
                .stream()
                .map(goal -> toDto(goal, user))
                .collect(Collectors.toList());
    }

    public GoalDto create(GoalDto dto) {
        if (dto.getPeriodDays() != 7 && dto.getPeriodDays() != 15 && dto.getPeriodDays() != 30) {
            throw new RuntimeException("Período deve ser 7, 15 ou 30 dias");
        }

        User user = getCurrentUser();

        Tag tag = null;
        if (dto.getTagId() != null) {
            tag = tagRepository.findById(dto.getTagId())
                    .orElseThrow(() -> new RuntimeException("Tag não encontrada"));
        }

        Goal goal = Goal.builder()
                .title(dto.getTitle())
                .targetCount(dto.getTargetCount())
                .periodDays(dto.getPeriodDays())
                .tag(tag)
                .user(user)
                .active(true)
                .build();

        return toDto(goalRepository.save(goal), user);
    }

    public GoalDto update(Long id, GoalDto dto) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meta não encontrada"));

        if (dto.getPeriodDays() != null) {
            if (dto.getPeriodDays() != 7 && dto.getPeriodDays() != 15 && dto.getPeriodDays() != 30) {
                throw new RuntimeException("Período deve ser 7, 15 ou 30 dias");
            }
            goal.setPeriodDays(dto.getPeriodDays());
        }

        if (dto.getTitle() != null) goal.setTitle(dto.getTitle());
        if (dto.getTargetCount() != null) goal.setTargetCount(dto.getTargetCount());
        if (dto.getActive() != null) goal.setActive(dto.getActive());

        if (dto.getTagId() != null) {
            Tag tag = tagRepository.findById(dto.getTagId())
                    .orElseThrow(() -> new RuntimeException("Tag não encontrada"));
            goal.setTag(tag);
        } else if (dto.getTagId() == null && dto.getTitle() != null) {
            goal.setTag(null);
        }

        User user = getCurrentUser();
        return toDto(goalRepository.save(goal), user);
    }

    public void delete(Long id) {
        goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meta não encontrada"));
        goalRepository.deleteById(id);
    }

    public GoalDto toDto(Goal goal, User user) {
        LocalDateTime since = LocalDateTime.now().minusDays(goal.getPeriodDays());

        int currentCount;
        if (goal.getTag() != null) {
            currentCount = taskRepository.findCompletedSinceWithTag(user, since, goal.getTag().getId()).size();
        } else {
            currentCount = taskRepository.findCompletedSince(user, since).size();
        }

        double progressPercent = goal.getTargetCount() > 0
                ? Math.min(100.0, (currentCount * 100.0) / goal.getTargetCount())
                : 0.0;

        return GoalDto.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .targetCount(goal.getTargetCount())
                .periodDays(goal.getPeriodDays())
                .tagId(goal.getTag() != null ? goal.getTag().getId() : null)
                .tagName(goal.getTag() != null ? goal.getTag().getName() : null)
                .tagColor(goal.getTag() != null ? goal.getTag().getColor() : null)
                .active(goal.getActive())
                .createdAt(goal.getCreatedAt())
                .currentCount(currentCount)
                .progressPercent(progressPercent)
                .achieved(currentCount >= goal.getTargetCount())
                .build();
    }
}
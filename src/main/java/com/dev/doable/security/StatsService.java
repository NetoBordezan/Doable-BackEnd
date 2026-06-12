package com.dev.doable.service;

import com.dev.doable.dto.GoalDto;
import com.dev.doable.dto.StatsDto;
import com.dev.doable.model.Task;
import com.dev.doable.model.User;
import com.dev.doable.repository.GoalRepository;
import com.dev.doable.repository.TaskRepository;
import com.dev.doable.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final TaskRepository taskRepository;
    private final GoalRepository goalRepository;
    private final GoalService goalService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public StatsDto getStats() {
        User user = getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        int completed7 = taskRepository.findCompletedSince(user, now.minusDays(7)).size();
        int completed15 = taskRepository.findCompletedSince(user, now.minusDays(15)).size();
        int completed30 = taskRepository.findCompletedSince(user, now.minusDays(30)).size();

        Map<String, Long> daily7 = buildDailyMap(user, now.minusDays(7), 7);
        Map<String, Long> daily15 = buildDailyMap(user, now.minusDays(15), 15);
        Map<String, Long> daily30 = buildDailyMap(user, now.minusDays(30), 30);

        List<Task> tasks30 = taskRepository.findCompletedSince(user, now.minusDays(30));
        Map<Long, StatsDto.TagStatDto> tagMap = new LinkedHashMap<>();

        for (Task task : tasks30) {
            for (var tag : task.getTags()) {
                tagMap.computeIfAbsent(tag.getId(), id -> StatsDto.TagStatDto.builder()
                        .tagId(tag.getId())
                        .tagName(tag.getName())
                        .tagColor(tag.getColor())
                        .completedCount(0)
                        .build()
                );
                StatsDto.TagStatDto stat = tagMap.get(tag.getId());
                stat.setCompletedCount(stat.getCompletedCount() + 1);
            }
        }

        List<StatsDto.TagStatDto> tagStats = tagMap.values().stream()
                .sorted(Comparator.comparingInt(StatsDto.TagStatDto::getCompletedCount).reversed())
                .collect(Collectors.toList());

        List<GoalDto> goalsProgress = goalRepository.findByUserAndActiveTrue(user)
                .stream()
                .map(goal -> goalService.toDto(goal, user))
                .collect(Collectors.toList());

        return StatsDto.builder()
                .completedLast7Days(completed7)
                .completedLast15Days(completed15)
                .completedLast30Days(completed30)
                .dailyCompletions7Days(daily7)
                .dailyCompletions15Days(daily15)
                .dailyCompletions30Days(daily30)
                .completionsByTag(tagStats)
                .goalsProgress(goalsProgress)
                .build();
    }

    private Map<String, Long> buildDailyMap(User user, LocalDateTime since, int days) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Map<String, Long> result = new LinkedHashMap<>();
        for (int i = days - 1; i >= 0; i--) {
            result.put(LocalDate.now().minusDays(i).format(fmt), 0L);
        }

        for (Object[] row : taskRepository.countCompletedPerDay(user, since)) {
            String dateStr;
            if (row[0] instanceof java.sql.Date) {
                dateStr = ((java.sql.Date) row[0]).toLocalDate().format(fmt);
            } else if (row[0] instanceof LocalDate) {
                dateStr = ((LocalDate) row[0]).format(fmt);
            } else {
                dateStr = row[0].toString().substring(0, 10);
            }
            result.put(dateStr, ((Number) row[1]).longValue());
        }

        return result;
    }
}
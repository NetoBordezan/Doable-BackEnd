package com.dev.doable.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsDto {

    private Integer completedLast7Days;
    private Integer completedLast15Days;
    private Integer completedLast30Days;

    private Map<String, Long> dailyCompletions7Days;
    private Map<String, Long> dailyCompletions15Days;
    private Map<String, Long> dailyCompletions30Days;

    private List<TagStatDto> completionsByTag;

    private List<GoalDto> goalsProgress;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TagStatDto {
        private Long tagId;
        private String tagName;
        private String tagColor;
        private Integer completedCount;
    }
}
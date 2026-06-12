package com.dev.doable.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalDto {

    private Long id;
    private String title;
    private Integer targetCount;
    private Integer periodDays;
    private Long tagId;
    private String tagName;
    private String tagColor;
    private Boolean active;
    private LocalDateTime createdAt;

    private Integer currentCount;
    private Double progressPercent;
    private Boolean achieved;
}
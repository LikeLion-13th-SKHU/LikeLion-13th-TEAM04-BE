package com.likelion.cheongsanghoe.portfolio.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AvailableTime {

    @Column(name = "weekday_available", nullable = false)
    private Boolean weekday;

    @Column(name = "weekend_available", nullable = false)
    private Boolean weekend;

    @Column(name = "evening_available", nullable = false)
    private Boolean evening;

    @Column(name = "flexible_available", nullable = false)
    private Boolean flexible;
}

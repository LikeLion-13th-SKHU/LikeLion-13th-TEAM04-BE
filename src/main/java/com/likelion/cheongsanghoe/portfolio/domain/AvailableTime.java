package com.likelion.cheongsanghoe.portfolio.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AvailableTime {

    @Column(name = "weekday_available")
    private Boolean weekday;

    @Column(name = "weekend_available")
    private Boolean weekend;

    @Column(name = "evening_available")
    private Boolean evening;

    @Column(name = "flexible_available")
    private Boolean flexible;
}
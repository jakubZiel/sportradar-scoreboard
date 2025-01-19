package com.sportradar.scoreboard.domain.team;

import java.util.List;

import lombok.Builder;


@Builder(setterPrefix = "with", toBuilder = true)
public record CardSet(List<Long> yellowCards, List<Long> redCards)
{
}

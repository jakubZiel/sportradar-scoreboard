package com.sportradar.scoreboard.domain.team;

import java.util.List;

import lombok.Builder;


@Builder(setterPrefix = "with", toBuilder = true)
public record Squad(long teamId, List<Long> firstEleven, List<Long> reserves, List<Long> substituted)
{
}

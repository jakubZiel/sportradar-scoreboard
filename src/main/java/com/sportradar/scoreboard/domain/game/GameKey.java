package com.sportradar.scoreboard.domain.game;

import com.sportradar.scoreboard.domain.team.Team;

import lombok.Builder;
import lombok.NonNull;


@Builder(setterPrefix = "with", toBuilder = true)
public record GameKey(Long gameId, @NonNull Team team1, @NonNull Team team2)
{
}

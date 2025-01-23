package com.sportradar.scoreboard.domain.game;

import java.util.Map;

import com.sportradar.scoreboard.domain.team.Team;

import lombok.Builder;
import lombok.NonNull;


@Builder(setterPrefix = "with", toBuilder = true)
public record Game(@NonNull GameKey key,
                   @NonNull Map<Team, @NonNull Integer> teamIdToScore,
                   int totalScore)
{
}

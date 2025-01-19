package com.sportradar.scoreboard.domain.game;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.sportradar.scoreboard.domain.team.CardSet;
import com.sportradar.scoreboard.domain.team.Squad;

import lombok.Builder;
import lombok.NonNull;


@Builder(setterPrefix = "with", toBuilder = true)
public record Game(GameKey key,
                   Map<Long, @NonNull Integer> teamIdToScore,
                   Map<Long, Squad> teamIdToSquad,
                   Map<Long, CardSet> teamIdToCardSet,
                   Duration currentMatchTime,
                   @NonNull Instant startTime,
                   List<Duration> overtimes,
                   Instant closeTime)
{
}

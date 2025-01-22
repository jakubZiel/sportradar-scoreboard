package com.sportradar.scoreboard.domain.util;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;

import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.game.GameKey;
import com.sportradar.scoreboard.domain.team.Squad;


public class GameTestUtil
{
    public static Game buildDefaultGame(final long gameStartTimeMs, final Long gameCloseTimeMs)
    {
        final var gameStartTime = Instant.ofEpochMilli(gameStartTimeMs);
        return Game.builder()
            .withStartTime(gameStartTime)
            .withCloseTime(gameCloseTimeMs != null ? Instant.ofEpochMilli(gameCloseTimeMs) : null)
            .withKey(new GameKey(1L, 2L, gameStartTime))
            .withOvertimes(List.of())
            .withTeamIdToScore(Map.of(1L, 0, 2L, 0))
            .withTeamIdToSquad(Map.of(
                1L, Squad.builder().withTeamId(1L)
                    .withFirstEleven(LongStream.rangeClosed(1, 11).boxed().toList())
                    .withReserves(LongStream.rangeClosed(12, 18).boxed().toList())
                    .build(),
                2L, Squad.builder().withTeamId(2L)
                    .withFirstEleven(LongStream.rangeClosed(21, 32).boxed().toList())
                    .withReserves(LongStream.rangeClosed(33, 39).boxed().toList())
                    .build()))
            .withTeamIdToCardSet(Map.of())
            .withCurrentMatchTime(Duration.ZERO)
            .build();
    }
}

package com.sportradar.scoreboard.domain.processing;

import java.util.HashMap;
import java.util.Map;

import com.sportradar.scoreboard.domain.event.GoalEvent;
import com.sportradar.scoreboard.domain.game.Game;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
class GoalEventProcessor
{
    public Game process(@NonNull final Game game, @NonNull final GoalEvent goalEvent)
    {
        final var updatedScore = new HashMap<>(game.teamIdToScore());
        updatedScore.computeIfPresent(goalEvent.getScoringTeam(), (team, score) -> score + 1);

        return game.toBuilder()
            .withTeamIdToScore(Map.copyOf(updatedScore))
            .withTotalScore(game.totalScore() + 1)
            .build();
    }
}

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
        final var scoringTeam = goalEvent.getScoringTeam();

        if (!updatedScore.containsKey(scoringTeam))
        {
            throw new IllegalArgumentException("Scoring team: %s is not associated with a game with a key: %s".formatted(scoringTeam, game.key()));
        }

        updatedScore.computeIfPresent(goalEvent.getScoringTeam(), (team, score) -> score + 1);

        return game.toBuilder()
            .withTeamIdToScore(Map.copyOf(updatedScore))
            .withTotalScore(game.totalScore() + 1)
            .build();
    }
}

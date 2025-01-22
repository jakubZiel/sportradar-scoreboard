package com.sportradar.scoreboard.domain.processing;

import java.util.HashMap;
import java.util.Map;

import com.sportradar.scoreboard.domain.event.GoalEvent;
import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.interfaces.outgoing.GameStateRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
class GoalEventProcessor
{
    private final GameStateRepository gameStateRepository;

    public void process(final Game game, final GoalEvent goalEvent)
    {
        final var updatedScore = new HashMap<>(game.teamIdToScore());
        updatedScore.computeIfPresent(goalEvent.getEventCommon().associatedTeamId(), (team, score) -> score + 1);

        final var updatedGame = game.toBuilder().withTeamIdToScore(Map.copyOf(updatedScore)).build();
        gameStateRepository.update(updatedGame);
    }
}

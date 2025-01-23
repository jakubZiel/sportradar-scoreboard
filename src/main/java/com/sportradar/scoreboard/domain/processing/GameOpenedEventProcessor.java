package com.sportradar.scoreboard.domain.processing;

import java.util.Map;

import com.sportradar.scoreboard.domain.event.GameOpenedEvent;
import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.game.GameKey;
import com.sportradar.scoreboard.interfaces.outgoing.GameStateRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
class GameOpenedEventProcessor
{
    private final GameStateRepository gameStateRepository;

    public Game process(final GameOpenedEvent gameOpenedEvent)
    {
        final var gameKey = gameOpenedEvent.getGameKey();
        final var foundGame = gameStateRepository.findByKey(gameKey);

        if (foundGame.isPresent())
        {
            throwOnCreatingAlreadyExistingGame(gameKey);
        }

        return Game.builder()
            .withKey(gameKey)
            .withTeamIdToScore(Map.of(gameKey.team1(), 0, gameKey.team2(), 0))
            .build();
    }

    private static void throwOnCreatingAlreadyExistingGame(final GameKey gameKey)
    {
        throw new IllegalStateException("There is already a game associated with a key: %s".formatted(gameKey));
    }
}

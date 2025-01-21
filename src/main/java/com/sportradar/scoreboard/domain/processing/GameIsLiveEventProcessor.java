package com.sportradar.scoreboard.domain.processing;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.sportradar.scoreboard.domain.event.GameIsLiveEvent;
import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.game.GameKey;
import com.sportradar.scoreboard.interfaces.outgoing.GameStateRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
class GameIsLiveEventProcessor
{
    private final GameStateRepository gameStateRepository;

    public void process(final GameIsLiveEvent gameIsLiveEvent)
    {
        final boolean isLive = gameIsLiveEvent.isLive();
        final var eventTime = gameIsLiveEvent.getEventTime();
        final var gameKey = gameIsLiveEvent.getEventCommon().gameKey();

        if (!isLive)
        {
            gameStateRepository.findByKey(gameKey)
                .ifPresentOrElse(g -> handleGameClosing(g, eventTime), () -> throwOnClosingNonExistingGame(gameKey));
            return;
        }

        gameStateRepository.findByKey(gameKey)
            .ifPresentOrElse(existingGame -> throwOnCreatingAlreadyExistingGame(gameKey), () -> handleGameOpening(gameKey, gameIsLiveEvent));
    }

    private void handleGameClosing(final Game game, final Instant closeTime)
    {
        if (game.closeTime() != null)
        {
            throwOnClosingAlreadyClosedGame(game.key());
        }

        final var closedGame = game.toBuilder()
            .withCloseTime(closeTime)
            .build();

        gameStateRepository.update(closedGame);
    }

    private void handleGameOpening(final GameKey gameKey, final GameIsLiveEvent gameIsLiveEvent)
    {
        final var newGame = Game.builder()
            .withKey(gameKey)
            .withStartTime(gameIsLiveEvent.getEventTime())
            .withCurrentMatchTime(Duration.ZERO)
            .withOvertimes(List.of())
            .withTeamIdToSquad(gameIsLiveEvent.getTeamIdToSquad())
            .withTeamIdToCardSet(Map.of())
            .withTeamIdToScore(Map.of(gameKey.team1Id(), 0, gameKey.team2Id(), 0))
            .build();

        gameStateRepository.save(newGame);
    }

    private static void throwOnCreatingAlreadyExistingGame(final GameKey gameKey)
    {
        throw new IllegalStateException("There is already a game associated with a key: %s".formatted(gameKey));
    }

    private static void throwOnClosingNonExistingGame(final GameKey gameKey)
    {
        throw new IllegalStateException("Can not close game, is no game associated with a key: %s".formatted(gameKey));
    }

    private static void throwOnClosingAlreadyClosedGame(final GameKey gameKey)
    {
        throw new IllegalStateException("Can not close game, game associated with a key: %s is already closed".formatted(gameKey));
    }
}

package com.sportradar.scoreboard.domain.processing;

import java.util.function.Function;

import com.sportradar.scoreboard.domain.event.GameAssociated;
import com.sportradar.scoreboard.domain.event.GameClosedEvent;
import com.sportradar.scoreboard.domain.event.GameOpenedEvent;
import com.sportradar.scoreboard.domain.event.GoalEvent;
import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.game.GameKey;
import com.sportradar.scoreboard.interfaces.outgoing.GameStateRepository;

import lombok.NonNull;


public class SportEventProcessor implements SportEventVisitor
{
    private final GoalEventProcessor goalEventProcessor = new GoalEventProcessor();
    private final GameOpenedEventProcessor gameOpenedEventProcessor;

    private final GameStateRepository gameStateRepository;

    public SportEventProcessor(final GameStateRepository gameStateRepository)
    {
        this.gameStateRepository = gameStateRepository;
        gameOpenedEventProcessor = new GameOpenedEventProcessor(gameStateRepository);
    }

    @Override
    public Game visit(@NonNull final GameOpenedEvent gameOpenedEvent)
    {
        final var gameUpdate = gameOpenedEventProcessor.process(gameOpenedEvent);
        return gameStateRepository.save(gameUpdate.key(), gameUpdate);
    }

    @Override
    public Game visit(@NonNull final GoalEvent goalEvent)
    {
        return modifyGame(game -> goalEventProcessor.process(game, goalEvent), goalEvent);
    }

    @Override
    public Game visit(@NonNull final GameClosedEvent gameClosedEvent)
    {
        return modifyGame(game -> null, gameClosedEvent);
    }

    private Game modifyGame(@NonNull final Function<Game, Game> gameModifier, @NonNull final GameAssociated event)
    {
        final var associatedGame = gameStateRepository.findByKey(event.getGameKey());

        if (associatedGame.isEmpty())
        {
            throwOnMissingGame(event.getGameKey());
        }

        final var gameUpdate = gameModifier.apply(associatedGame.get());
        gameStateRepository.save(event.getGameKey(), gameUpdate);
        return gameUpdate;
    }

    private static void throwOnMissingGame(final GameKey gameKey)
    {
        throw new IllegalArgumentException("There is no game associated with a key: %s".formatted(gameKey));
    }
}

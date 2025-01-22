package com.sportradar.scoreboard.domain.processing;

import java.util.function.Function;

import com.sportradar.scoreboard.domain.event.CardEvent;
import com.sportradar.scoreboard.domain.event.GameIsLiveEvent;
import com.sportradar.scoreboard.domain.event.GameOvertimeEvent;
import com.sportradar.scoreboard.domain.event.GoalEvent;
import com.sportradar.scoreboard.domain.event.SportEvent;
import com.sportradar.scoreboard.domain.event.SubstitutionEvent;
import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.game.GameKey;
import com.sportradar.scoreboard.interfaces.outgoing.GameStateRepository;

import lombok.NonNull;


public class SportEventProcessor implements SportEventVisitor
{
    private final SubstitutionEventProcessor substitutionEventProcessor = new SubstitutionEventProcessor();
    private final CardEventProcessor cardEventProcessor = new CardEventProcessor();
    private final GoalEventProcessor goalEventProcessor = new GoalEventProcessor();
    private final GameOvertimeProcessor gameOvertimeProcessor = new GameOvertimeProcessor();
    private final GameIsLiveEventProcessor gameIsLiveEventProcessor;

    private final GameStateRepository gameStateRepository;

    public SportEventProcessor(final GameStateRepository gameStateRepository)
    {
        this.gameStateRepository = gameStateRepository;
        gameIsLiveEventProcessor = new GameIsLiveEventProcessor(gameStateRepository);
    }

    @Override
    public void visit(final GoalEvent goalEvent)
    {
        modifyGame(game -> goalEventProcessor.process(game, goalEvent), goalEvent);
    }

    @Override
    public void visit(final SubstitutionEvent substitutionEvent)
    {
        modifyGame(game -> substitutionEventProcessor.process(game, substitutionEvent), substitutionEvent);
    }

    @Override
    public void visit(final CardEvent cardEvent)
    {
        modifyGame(game -> cardEventProcessor.process(game, cardEvent), cardEvent);
    }

    @Override
    public void visit(final GameOvertimeEvent gameOvertimeEvent)
    {
        modifyGame(game -> gameOvertimeProcessor.process(game, gameOvertimeEvent), gameOvertimeEvent);
    }

    @Override
    public void visit(final GameIsLiveEvent gameIsLiveEvent)
    {
        gameIsLiveEventProcessor.process(gameIsLiveEvent);
    }

    private void modifyGame(@NonNull final Function<Game, Game> gameModifier, @NonNull final SportEvent event)
    {
        final var key = event.getEventCommon().gameKey();

        gameStateRepository.findByKey(key)
            .map(game -> validateEventTime(game, event))
            .map(gameModifier)
            .map(game -> modifyGameTime(game, event))
            .ifPresentOrElse(gameStateRepository::save, () -> throwOnMissingGame(key));
    }

    @NonNull
    private static Game modifyGameTime(final Game game, final SportEvent event)
    {
        final var eventTime = event.getEventCommon().matchTime();
        final var gameTime = game.currentMatchTime();

        return game.toBuilder()
            .withCurrentMatchTime(eventTime.compareTo(gameTime) > 0 ? eventTime : gameTime)
            .build();
    }

    @NonNull
    private static Game validateEventTime(final Game game, final SportEvent sportEvent)
    {
        final var eventTime = sportEvent.getEventCommon().matchTime();

        if (eventTime.isNegative())
        {
            throw new IllegalArgumentException("Event time is negative: %s".formatted(eventTime));
        }

        return game;
    }

    private static void throwOnMissingGame(final GameKey gameKey)
    {
        throw new IllegalStateException("There is no game associated with a key: %s".formatted(gameKey));
    }
}

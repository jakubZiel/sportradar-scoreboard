package com.sportradar.scoreboard.domain.scoreboard;

import java.util.HashMap;

import com.sportradar.scoreboard.domain.processing.SportEventProcessor;
import com.sportradar.scoreboard.infrastructure.GameStateInMemoryRepository;
import com.sportradar.scoreboard.interfaces.incoming.ScoreBoardService;
import com.sportradar.scoreboard.interfaces.outgoing.GameStateRepository;


public class ScoreBoardServiceFactory
{
    private static final ScoreBoardService INSTANCE;

    static
    {
        final var gameStateRepository = new GameStateInMemoryRepository(new HashMap<>());
        INSTANCE = new ScoreBoardServiceImpl(new SportEventProcessor(gameStateRepository), gameStateRepository);
    }

    public static ScoreBoardService getInstance()
    {
        return INSTANCE;
    }

    public static ScoreBoardService build(final GameStateRepository gameStateRepository)
    {
        return new ScoreBoardServiceImpl(new SportEventProcessor(gameStateRepository), gameStateRepository);
    }
}

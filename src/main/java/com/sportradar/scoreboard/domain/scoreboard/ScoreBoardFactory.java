package com.sportradar.scoreboard.domain.scoreboard;

import java.util.HashMap;

import com.sportradar.scoreboard.domain.processing.SportEventProcessor;
import com.sportradar.scoreboard.infrastructure.GameStateInMemoryRepository;
import com.sportradar.scoreboard.infrastructure.SportEventInMemoryRepositoryImpl;
import com.sportradar.scoreboard.interfaces.incoming.ScoreBoardService;
import com.sportradar.scoreboard.interfaces.outgoing.GameStateRepository;
import com.sportradar.scoreboard.interfaces.outgoing.SportEventRepository;


public class ScoreBoardFactory
{
    public static ScoreBoardService buildDefault()
    {
        return build(new GameStateInMemoryRepository(new HashMap<>()), new SportEventInMemoryRepositoryImpl(new HashMap<>(), new HashMap<>()));
    }

    public static ScoreBoardService build(final GameStateRepository gameStateRepository,
                                          final SportEventRepository sportEventRepository)
    {
        return new ScoreBoardServiceImpl(new SportEventProcessor(gameStateRepository), sportEventRepository, gameStateRepository);
    }
}

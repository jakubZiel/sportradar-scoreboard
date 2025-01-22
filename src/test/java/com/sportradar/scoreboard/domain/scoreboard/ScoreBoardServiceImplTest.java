package com.sportradar.scoreboard.domain.scoreboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.sportradar.scoreboard.domain.processing.SportEventVisitor;
import com.sportradar.scoreboard.interfaces.outgoing.GameStateRepository;
import com.sportradar.scoreboard.interfaces.outgoing.SportEventRepository;


class ScoreBoardServiceImplTest
{
    @Mock
    private SportEventVisitor sportEventVisitor;
    @Mock
    private SportEventRepository sportEventRepository;
    @Mock
    private GameStateRepository gameStateRepository;

    private ScoreBoardServiceImpl tested;

    @BeforeEach
    void setUp()
    {
        tested = new ScoreBoardServiceImpl(sportEventVisitor, sportEventRepository, gameStateRepository);
    }

    @Test
    void test()
    {
        // given

        // when

        // then
    }
}
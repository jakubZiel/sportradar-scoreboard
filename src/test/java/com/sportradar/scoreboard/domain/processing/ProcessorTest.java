package com.sportradar.scoreboard.domain.processing;

import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.interfaces.outgoing.GameStateRepository;


@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
public class ProcessorTest
{
    protected static Game DEFAULT_GAME = GameTestUtil.buildDefaultGame(1000L, 30L * 60L * 1000L);

    @Mock
    protected GameStateRepository gameStateRepository;

    @BeforeEach
    void setUp()
    {
        Mockito.reset(gameStateRepository);
    }
}

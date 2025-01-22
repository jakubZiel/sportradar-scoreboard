package com.sportradar.scoreboard.domain.processing;

import static org.mockito.Mockito.verify;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.util.GameTestUtil;
import com.sportradar.scoreboard.interfaces.outgoing.GameStateRepository;


@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
public class ProcessorTest
{
    protected static Game DEFAULT_GAME = GameTestUtil.buildDefaultGame(1000L, null);

    @Mock
    protected GameStateRepository gameStateRepository;

    @Captor
    protected ArgumentCaptor<Game> gameUpdateCaptor;

    @BeforeEach
    void setUp()
    {
        Mockito.reset(gameStateRepository);
    }

    protected void verifyCapturedGameState(final SoftAssertions softly, final Game expectedGameState)
    {
        verify(gameStateRepository).save(gameUpdateCaptor.capture());
        final var capturedGame = gameUpdateCaptor.getValue();

        softly.assertThat(capturedGame).isEqualTo(expectedGameState);
    }
}

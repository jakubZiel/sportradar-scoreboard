package com.sportradar.scoreboard.domain.processing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sportradar.scoreboard.domain.event.GameOpenedEvent;
import com.sportradar.scoreboard.domain.util.TestUtil;
import com.sportradar.scoreboard.interfaces.outgoing.GameStateRepository;


@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
class GameOpenedEventProcessorTest
{

    @Mock
    private GameStateRepository gameStateRepository;

    private GameOpenedEventProcessor tested;

    @BeforeEach
    void setUp()
    {
        tested = new GameOpenedEventProcessor(gameStateRepository);
    }

    @Test
    void shouldThrowWhenGameKeyContainsSameTeams(final SoftAssertions softly)
    {
        // given
        final var gameKey = TestUtil.buildGameKey("USA", "USA");
        final var event = mock(GameOpenedEvent.class);

        when(event.getGameKey()).thenReturn(gameKey);

        // when
        final var exception = Assertions.assertThrows(IllegalArgumentException.class, () -> tested.process(event));

        // then
        softly.assertThat(exception)
            .hasMessage("Invalid gameKey, team1 is equal to team2 :GameKey[gameId=null, team1=Team[country=USA], team2=Team[country=USA]]");

        Mockito.verifyNoInteractions(gameStateRepository);
    }

    @Test
    void shouldThrowWhenOpeningAlreadyExistingGame(final SoftAssertions softly)
    {
        // given
        final var gameKey = TestUtil.buildGameKey("USA", "Canada", 1L);
        final var event = mock(GameOpenedEvent.class);
        final var existingGame = TestUtil.buildGame(0, 0, "USA", "Canada", 1L);

        when(event.getGameKey()).thenReturn(gameKey);
        when(gameStateRepository.findByKey(gameKey)).thenReturn(Optional.of(existingGame));

        // when
        final var exception = Assertions.assertThrows(IllegalArgumentException.class, () -> tested.process(event));

        // then
        softly.assertThat(exception)
            .hasMessage("There is already a game associated with a key: GameKey[gameId=1, team1=Team[country=USA], team2=Team[country=Canada]]");

        verify(gameStateRepository).findByKey(gameKey);
    }

    @Test
    void shouldCreateGameSuccessfullyWhenValid(final SoftAssertions softly)
    {
        // given
        final var gameKey = TestUtil.buildGameKey("USA", "Canada");
        final var event = mock(GameOpenedEvent.class);

        when(event.getGameKey()).thenReturn(gameKey);
        when(gameStateRepository.findByKey(gameKey)).thenReturn(Optional.empty());

        // when
        final var result = tested.process(event);

        // then
        softly.assertThat(result).isNotNull();
        softly.assertThat(result.key()).isEqualTo(gameKey);
        softly.assertThat(result.teamIdToScore()).containsExactlyInAnyOrderEntriesOf(Map.of(gameKey.team1(), 0, gameKey.team2(), 0));
        softly.assertThat(result.totalScore()).isEqualTo(0);

        verify(gameStateRepository).findByKey(gameKey);
    }
}

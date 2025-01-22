package com.sportradar.scoreboard.domain.processing;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.Duration;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sportradar.scoreboard.domain.event.GameOvertimeEvent;
import com.sportradar.scoreboard.domain.util.EventCommonTestUtil;


class GameOvertimeProcessorTest extends ProcessorTest
{
    private GameOvertimeProcessor tested;

    @BeforeEach
    void setUp()
    {
        tested = new GameOvertimeProcessor(gameStateRepository);
    }

    @Test
    void testProcess_givenOvertime_shouldAddOvertimeToGameState(final SoftAssertions softly)
    {
        // given
        final var gameOvertimeEvent = GameOvertimeEvent.builder()
            .withOvertime(Duration.ofMinutes(5))
            .withEventCommon(EventCommonTestUtil.from(DEFAULT_GAME.key(), 91, null))
            .build();

        final var expectedGame = DEFAULT_GAME.toBuilder()
            .withOvertimes(List.of(Duration.ofMinutes(5)))
            .build();

        // when
        tested.process(DEFAULT_GAME, gameOvertimeEvent);

        // then
        verifyCapturedGameState(softly, expectedGame);
    }

    @Test
    void testProcess_givenGameStateHasMoreThan2Overtimes_shouldThrow(final SoftAssertions softly)
    {
        // given
        final var gameOvertimeEvent = GameOvertimeEvent.builder()
            .withOvertime(Duration.ofMinutes(5))
            .withEventCommon(EventCommonTestUtil.from(DEFAULT_GAME.key(), 91, null))
            .build();

        final var originalGame = DEFAULT_GAME.toBuilder()
            .withOvertimes(List.of(Duration.ofMinutes(5), Duration.ofMinutes(4)))
            .build();

        // when & then
        assertThatThrownBy(() -> tested.process(originalGame, gameOvertimeEvent))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Game with a key: GameKey[team1Id=1, team2Id=2, scheduled=1970-01-01T00:00:01Z] already has 2 overtimes");
    }
}
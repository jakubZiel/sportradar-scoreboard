package com.sportradar.scoreboard.domain.processing;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sportradar.scoreboard.domain.event.GameIsLiveEvent;
import com.sportradar.scoreboard.domain.util.EventCommonTestUtil;


class GameIsLiveEventProcessorTest extends ProcessorTest
{
    private GameIsLiveEventProcessor tested;

    @BeforeEach
    void setUp()
    {
        tested = new GameIsLiveEventProcessor(gameStateRepository);
    }

    @Test
    void testProcess_givenValidGameIsLiveEvent_shouldCreateAGame(final SoftAssertions softly)
    {
        // given
        final var event = GameIsLiveEvent.builder()
            .withEventCommon(EventCommonTestUtil.from(DEFAULT_GAME.key(), 93, 1L))
            .withIsLive(true)
            .withEventTime(Instant.ofEpochMilli(2000L))
            .withTeamIdToSquad(DEFAULT_GAME.teamIdToSquad())
            .build();

        final var expectedGameState = DEFAULT_GAME.toBuilder()
            .withCloseTime(null)
            .withStartTime(Instant.ofEpochMilli(2000L))
            .withCurrentMatchTime(Duration.ZERO)
            .build();

        // when
        tested.process(event);

        // then
        verifyCapturedGameState(softly, expectedGameState);
    }

    @Test
    void testProcess_givenEventNotLive_gameStillLiveAtMoment_shouldCloseGame(final SoftAssertions softly)
    {
        // given
        final var event = GameIsLiveEvent.builder()
            .withEventCommon(EventCommonTestUtil.from(DEFAULT_GAME.key(), 93, 1L))
            .withIsLive(false)
            .withEventTime(Instant.ofEpochMilli(3000L))
            .build();

        final var existingGame = DEFAULT_GAME.toBuilder()
            .withStartTime(Instant.ofEpochMilli(2000L))
            .withCloseTime(null)
            .build();

        when(gameStateRepository.findByKey(DEFAULT_GAME.key())).thenReturn(Optional.of(existingGame));

        final var expectedGameState = existingGame.toBuilder()
            .withCloseTime(Instant.ofEpochMilli(3000L))
            .build();

        // when
        tested.process(event);

        // then
        verifyCapturedGameState(softly, expectedGameState);
    }

    @Test
    void testProcess_givenEventNotLiveAndGameNonExisting_shouldThrow()
    {
        // given
        final var event = GameIsLiveEvent.builder()
            .withEventCommon(EventCommonTestUtil.from(DEFAULT_GAME.key(), 93, 1L))
            .withIsLive(false)
            .withEventTime(Instant.ofEpochMilli(4000L))
            .build();

        // when & then
        assertThatThrownBy(() -> tested.process(event))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Can not close game, is no game associated with a key");
    }

    @Test
    void testProcess_givenEventNotLiveAndGameAlreadyClosed_shouldThrow()
    {
        // given
        final var event = GameIsLiveEvent.builder()
            .withEventCommon(EventCommonTestUtil.from(DEFAULT_GAME.key(), 93, 1L))
            .withIsLive(false)
            .withEventTime(Instant.ofEpochMilli(4000L))
            .build();

        final var existingGame = DEFAULT_GAME.toBuilder()
            .withStartTime(Instant.ofEpochMilli(2000L))
            .withCloseTime(Instant.ofEpochMilli(3000L))
            .build();

        when(gameStateRepository.findByKey(DEFAULT_GAME.key())).thenReturn(Optional.of(existingGame));

        // when & then
        assertThatThrownBy(() -> tested.process(event))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining(
                "Can not close game, game associated with a key: GameKey[team1Id=1, team2Id=2, scheduled=1970-01-01T00:00:01Z] is already closed");
    }

    @Test
    void testProcess_givenEventIsLiveAndGameAlreadyPresent_shouldThrow()
    {
        // given
        final var event = GameIsLiveEvent.builder()
            .withEventCommon(EventCommonTestUtil.from(DEFAULT_GAME.key(), 93, 1L))
            .withIsLive(true)
            .withEventTime(Instant.ofEpochMilli(2000L))
            .build();

        final var existingGame = DEFAULT_GAME.toBuilder()
            .withStartTime(Instant.ofEpochMilli(2000L))
            .withCloseTime(null)
            .build();

        when(gameStateRepository.findByKey(DEFAULT_GAME.key())).thenReturn(Optional.of(existingGame));

        // when & then
        assertThatThrownBy(() -> tested.process(event))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("There is already a game associated with a key");
    }
}
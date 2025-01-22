package com.sportradar.scoreboard.domain.processing;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import com.sportradar.scoreboard.domain.event.CardEvent;
import com.sportradar.scoreboard.domain.event.GameIsLiveEvent;
import com.sportradar.scoreboard.domain.event.GameOvertimeEvent;
import com.sportradar.scoreboard.domain.event.GoalEvent;
import com.sportradar.scoreboard.domain.event.SportEvent;
import com.sportradar.scoreboard.domain.event.SubstitutionEvent;
import com.sportradar.scoreboard.domain.util.EventCommonTestUtil;


@MockitoSettings(strictness = Strictness.LENIENT)
class SportEventProcessorTest extends ProcessorTest
{
    private SportEventProcessor tested;

    @Mock
    private SubstitutionEventProcessor substitutionEventProcessor;
    @Mock
    private CardEventProcessor cardEventProcessor;
    @Mock
    private GoalEventProcessor goalEventProcessor;
    @Mock
    private GameOvertimeProcessor gameOvertimeProcessor;
    @Mock
    private GameIsLiveEventProcessor gameIsLiveEventProcessor;

    private Map<Class<? extends SportEvent>, Runnable> eventTypeToMockUsageVerification;

    @BeforeEach
    void setUp()
    {
        eventTypeToMockUsageVerification = Map.of(
            SubstitutionEvent.class, () -> Mockito.verify(substitutionEventProcessor).process(any(), any()),
            CardEvent.class, () -> Mockito.verify(cardEventProcessor).process(any(), any()),
            GoalEvent.class, () -> Mockito.verify(goalEventProcessor).process(any(), any()),
            GameOvertimeEvent.class, () -> Mockito.verify(gameOvertimeProcessor).process(any(), any()),
            GameIsLiveEvent.class, () -> Mockito.verify(gameIsLiveEventProcessor).process(any())
        );

        tested = new SportEventProcessor(gameStateRepository);

        ReflectionTestUtils.setField(tested, "substitutionEventProcessor", substitutionEventProcessor);
        ReflectionTestUtils.setField(tested, "cardEventProcessor", cardEventProcessor);
        ReflectionTestUtils.setField(tested, "goalEventProcessor", goalEventProcessor);
        ReflectionTestUtils.setField(tested, "gameOvertimeProcessor", gameOvertimeProcessor);
        ReflectionTestUtils.setField(tested, "gameIsLiveEventProcessor", gameIsLiveEventProcessor);

        Mockito.reset(gameStateRepository);
        Mockito.reset(substitutionEventProcessor, cardEventProcessor, goalEventProcessor, gameIsLiveEventProcessor, gameIsLiveEventProcessor);

        setUpMocks();
    }

    @ParameterizedTest
    @MethodSource("getVisitTestCases")
    void testVisitEvents(final SportEvent sportEvent)
    {
        // when
        sportEvent.accept(tested);

        // then
        eventTypeToMockUsageVerification.get(sportEvent.getClass()).run();
        Mockito.verify(gameStateRepository, Mockito.times(1)).save(any());
    }

    @Test
    void testVisit_whenGameIsNotPresent_shouldThrow()
    {
        // given
        Mockito.reset(gameStateRepository);
        final var event = GoalEvent.builder().withEventCommon(EventCommonTestUtil.from(DEFAULT_GAME.key(), 14, 1L)).build();

        // when & then
        assertThatThrownBy(() -> tested.visit(event))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("There is no game associated with a key: GameKey[team1Id=1, team2Id=2, scheduled=1970-01-01T00:00:01Z]");
    }

    @Test
    void testVisit_givenValidEvent_shouldUpdateGameCurrentMatchTime(final SoftAssertions softly)
    {
        // given
        final var event = GoalEvent.builder().withEventCommon(EventCommonTestUtil.from(DEFAULT_GAME.key(), 14, 1L)).build();

        // is mocked, so score is not updated
        final var expectedGame = DEFAULT_GAME.toBuilder()
            .withCurrentMatchTime(Duration.ofMinutes(14))
            .build();

        // when
        tested.visit(event);

        // then
        verifyCapturedGameState(softly, expectedGame);
    }

    private static Stream<SportEvent> getVisitTestCases()
    {
        return Stream.of(
            GoalEvent.builder().withEventCommon(EventCommonTestUtil.from(DEFAULT_GAME.key(), 14, 1L)).build());
    }

    private void setUpMocks()
    {
        Mockito.doReturn(DEFAULT_GAME).when(substitutionEventProcessor).process(any(), any());
        Mockito.doReturn(DEFAULT_GAME).when(cardEventProcessor).process(any(), any());
        Mockito.doReturn(DEFAULT_GAME).when(goalEventProcessor).process(any(), any());
        Mockito.doReturn(DEFAULT_GAME).when(gameOvertimeProcessor).process(any(), any());

        Mockito.doReturn(Optional.of(DEFAULT_GAME)).when(gameStateRepository).findByKey(ArgumentMatchers.eq(DEFAULT_GAME.key()));
    }
}
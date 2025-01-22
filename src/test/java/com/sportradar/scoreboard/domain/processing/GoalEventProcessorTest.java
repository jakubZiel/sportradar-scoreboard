package com.sportradar.scoreboard.domain.processing;

import java.util.Map;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import com.sportradar.scoreboard.domain.event.GoalEvent;
import com.sportradar.scoreboard.domain.util.EventCommonTestUtil;


class GoalEventProcessorTest extends ProcessorTest
{
    private final GoalEventProcessor tested = new GoalEventProcessor();

    @Test
    void test(final SoftAssertions softly)
    {
        // given
        final var goalEvent = GoalEvent.builder()
            .withEventCommon(EventCommonTestUtil.from(DEFAULT_GAME.key(), 35, 1L))
            .withGoalScorerId(2)
            .build();

        final var expectedGame = DEFAULT_GAME.toBuilder()
            .withTeamIdToScore(Map.of(1L, 1, 2L, 0))
            .build();

        // when
        final var gameUpdate = tested.process(DEFAULT_GAME, goalEvent);

        // then
        softly.assertThat(gameUpdate).isEqualTo(expectedGame);
    }
}
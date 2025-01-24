package com.sportradar.scoreboard.domain.processing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sportradar.scoreboard.domain.event.GoalEvent;
import com.sportradar.scoreboard.domain.util.TestUtil;


@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
class GoalEventProcessorTest
{

    private final GoalEventProcessor tested = new GoalEventProcessor();

    @Test
    void shouldThrowWhenScoringTeamIsNotAssociatedWithGame(final SoftAssertions softly)
    {
        // given
        final var unrelatedTeam = TestUtil.buildTeam("Mexico");

        final var gameKey = TestUtil.buildGameKey("USA", "Canada");
        final var game = TestUtil.buildGame(1, 2, "USA", "Canada", 1L);

        final var goalEvent = mock(GoalEvent.class);
        when(goalEvent.getScoringTeam()).thenReturn(unrelatedTeam);

        // when
        final var exception = Assertions.assertThrows(IllegalArgumentException.class, () -> tested.process(game, goalEvent));

        // then
        softly.assertThat(exception)
            .hasMessage(
                "Scoring team: Team[country=Mexico] is not associated with a game with a key: GameKey[gameId=1, team1=Team[country=USA], team2=Team[country=Canada]]");
    }

    @Test
    void shouldSuccessfullyUpdateGameScores(final SoftAssertions softly)
    {
        // given
        final var team1 = TestUtil.buildTeam("USA");
        final var team2 = TestUtil.buildTeam("Canada");

        final var game = TestUtil.buildGame(1, 2, "USA", "Canada", 1L);

        final var goalEvent = mock(GoalEvent.class);
        when(goalEvent.getScoringTeam()).thenReturn(team1);

        // when
        final var updatedGame = tested.process(game, goalEvent);

        // then
        softly.assertThat(updatedGame).isNotNull();
        softly.assertThat(updatedGame.key()).isEqualTo(game.key());
        softly.assertThat(updatedGame.totalScore()).isEqualTo(4);
        softly.assertThat(updatedGame.teamIdToScore()).containsExactlyInAnyOrderEntriesOf(Map.of(team1, 2, team2, 2));
    }
}

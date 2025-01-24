package com.sportradar.scoreboard.domain.scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.sportradar.scoreboard.domain.event.GameClosedEvent;
import com.sportradar.scoreboard.domain.event.GameOpenedEvent;
import com.sportradar.scoreboard.domain.event.GoalEvent;
import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.game.GameKey;
import com.sportradar.scoreboard.domain.team.Team;
import com.sportradar.scoreboard.domain.util.TestUtil;
import com.sportradar.scoreboard.infrastructure.GameStateInMemoryRepository;
import com.sportradar.scoreboard.interfaces.incoming.ScoreBoardService;


@ExtendWith(SoftAssertionsExtension.class)
class ScoreBoardServiceImplTest
{
    private final HashMap<GameKey, Game> gameStateStore = new HashMap<>();
    private final ScoreBoardService tested = ScoreBoardServiceFactory.build(new GameStateInMemoryRepository(gameStateStore));

    @BeforeEach
    void setUp()
    {
        gameStateStore.clear();
    }

    @Test
    void testProcess_givenOpenAndGoalEvents_shouldContainCorrectState(final SoftAssertions softly)
    {
        // given
        final var gameA = tested.process(GameOpenedEvent.builder().withGameKey(TestUtil.buildGameKey("MEX", "CAN")).build()).orElseThrow();
        final var gameB = tested.process(GameOpenedEvent.builder().withGameKey(TestUtil.buildGameKey("ESP", "BR")).build()).orElseThrow();
        final var gameC = tested.process(GameOpenedEvent.builder().withGameKey(TestUtil.buildGameKey("GER", "FR")).build()).orElseThrow();
        final var gameD = tested.process(GameOpenedEvent.builder().withGameKey(TestUtil.buildGameKey("UR", "IT")).build()).orElseThrow();
        final var gameE = tested.process(GameOpenedEvent.builder().withGameKey(TestUtil.buildGameKey("ARG", "AUS")).build()).orElseThrow();

        Stream<GoalEvent> goalEvents = Stream.concat(
            buildStream(0, gameA.key().team1(), gameA.key()),
            buildStream(5, gameA.key().team2(), gameA.key())
        );

        goalEvents = Stream.concat(goalEvents, Stream.concat(
            buildStream(10, gameB.key().team1(), gameB.key()),
            buildStream(2, gameB.key().team2(), gameB.key())
        ));

        goalEvents = Stream.concat(goalEvents, Stream.concat(
            buildStream(2, gameC.key().team1(), gameC.key()),
            buildStream(2, gameC.key().team2(), gameC.key())
        ));

        goalEvents = Stream.concat(goalEvents, Stream.concat(
            buildStream(6, gameD.key().team1(), gameD.key()),
            buildStream(6, gameD.key().team2(), gameD.key())
        ));

        goalEvents = Stream.concat(goalEvents, Stream.concat(
            buildStream(3, gameE.key().team1(), gameE.key()),
            buildStream(1, gameE.key().team2(), gameE.key())
        ));

        // when
        goalEvents.forEach(tested::process);

        // then
        final var expectedScores = List.of(
            Map.of(gameD.key().team1(), 6, gameD.key().team2(), 6),
            Map.of(gameB.key().team1(), 10, gameB.key().team2(), 2),
            Map.of(gameA.key().team1(), 0, gameA.key().team2(), 5),
            Map.of(gameE.key().team1(), 3, gameE.key().team2(), 1),
            Map.of(gameC.key().team1(), 2, gameC.key().team2(), 2)
        );

        final var state = tested.getAll().stream().map(Game::teamIdToScore).toList();

        softly.assertThat(state).containsExactlyElementsOf(expectedScores);
    }

    @Test
    void testProcess_givenMatchClosing_shouldRemoveFromScoreBoard(final SoftAssertions softly)
    {
        // given
        final var game = tested.process(GameOpenedEvent.builder().withGameKey(TestUtil.buildGameKey("MEX", "CAN")).build()).orElseThrow();

        final var closeGameEvent = GameClosedEvent.builder()
            .withGameKey(game.key())
            .build();

        // when
        tested.process(closeGameEvent);

        // then
        softly.assertThat(tested.getAll()).isEmpty();
    }

    @Test
    void testGetGameState(final SoftAssertions softly)
    {
        // given
        final var game = tested.process(GameOpenedEvent.builder().withGameKey(TestUtil.buildGameKey("MEX", "CAN")).build()).orElseThrow();

        final var goalScored = GoalEvent.builder()
            .withGameKey(game.key())
            .withScoringTeam(game.key().team1())
            .build();

        tested.process(goalScored);

        // when
        final var foundGame = tested.getGameState(game.key()).orElseThrow();

        // then
        softly.assertThat(foundGame.key()).isEqualTo(game.key());
        softly.assertThat(foundGame.teamIdToScore()).isEqualTo(Map.of(game.key().team1(), 1, game.key().team2(), 0));
    }

    @Test
    void testProcess_givenNonExistingGameWhenProcessingEventOtherThanGameCreation_shouldThrow(final SoftAssertions softly)
    {
        // given
        final var invalidGameKey = TestUtil.buildGameKey("MEX", "CAN");
        final var eventWithoutAGame = GoalEvent.builder().withGameKey(invalidGameKey).withScoringTeam(invalidGameKey.team1()).build();

        // when
        final var exception = Assertions.assertThrows(IllegalArgumentException.class, () -> tested.process(eventWithoutAGame));

        // then
        softly.assertThat(exception).hasMessage("There is no game associated with a key: GameKey[gameId=null, team1=Team[country=MEX], team2=Team[country=CAN]]");
    }

    private Stream<GoalEvent> buildStream(final int goals, final Team scoringTeam, final GameKey gameKey)
    {
        return IntStream.range(0, goals).mapToObj(i -> GoalEvent.builder()
            .withScoringTeam(scoringTeam)
            .withGameKey(gameKey)
            .build());
    }
}
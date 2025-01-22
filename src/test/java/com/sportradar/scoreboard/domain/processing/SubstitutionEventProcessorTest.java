package com.sportradar.scoreboard.domain.processing;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.ArrayList;
import java.util.HashMap;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import com.sportradar.scoreboard.domain.event.SubstitutionEvent;
import com.sportradar.scoreboard.domain.team.Squad;
import com.sportradar.scoreboard.domain.util.EventCommonTestUtil;


class SubstitutionEventProcessorTest extends ProcessorTest
{
    private final SubstitutionEventProcessor tested = new SubstitutionEventProcessor();

    @Test
    void testProcess_givenValidSubstitutionEvent_shouldUpdateGameStateSquadsWithCorrectUpdate(final SoftAssertions softly)
    {
        // given
        final var originalGame = DEFAULT_GAME;
        final long playerIn = 13;
        final long playerOut = 4;

        final var subEvent = SubstitutionEvent.builder()
            .withEventCommon(EventCommonTestUtil.from(originalGame.key(), 40, originalGame.key().team1Id()))
            .withPlayerIn(playerIn)
            .withPlayerOut(playerOut)
            .build();

        final var expectedSquads = new HashMap<>(originalGame.teamIdToSquad());
        final var expectedFirst11 = new ArrayList<>(expectedSquads.get(originalGame.key().team1Id()).firstEleven());
        expectedFirst11.add(playerIn);
        expectedFirst11.removeIf(player -> player == playerOut);

        final var expectedReserves = new ArrayList<>(expectedSquads.get(originalGame.key().team1Id()).reserves());
        expectedReserves.add(playerOut);
        expectedReserves.removeIf(player -> player == playerIn);
        expectedSquads.put(1L, Squad.builder().withTeamId(1L).withReserves(expectedReserves).withFirstEleven(expectedFirst11).build());

        final var expectedGame = originalGame.toBuilder().withTeamIdToSquad(expectedSquads).build();

        // when
        final var gameUpdate = tested.process(originalGame, subEvent);

        // then
        softly.assertThat(gameUpdate).isEqualTo(expectedGame);
    }

    @Test
    void testProcess_givenPlayerNotInFirst11_shouldThrowException()
    {
        // given
        final var originalGame = DEFAULT_GAME;
        final long playerIn = 13;
        final long playerOut = 99; // Player not in first 11

        final var subEvent = SubstitutionEvent.builder()
            .withEventCommon(EventCommonTestUtil.from(originalGame.key(), 40, originalGame.key().team1Id()))
            .withPlayerIn(playerIn)
            .withPlayerOut(playerOut)
            .build();

        // when & then
        assertThatThrownBy(() -> tested.process(originalGame, subEvent))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(
                "First 11 of team: 1 in game with a key: GameKey[team1Id=1, team2Id=2, scheduled=1970-01-01T00:00:01Z] do not contain player 99");
    }

    @Test
    void testProcess_givenPlayerNotInReserves_shouldThrowException()
    {
        // given
        final var originalGame = DEFAULT_GAME;
        final long playerIn = 99; // Player not in reserves
        final long playerOut = 4;

        final var subEvent = SubstitutionEvent.builder()
            .withEventCommon(EventCommonTestUtil.from(originalGame.key(), 40, originalGame.key().team1Id()))
            .withPlayerIn(playerIn)
            .withPlayerOut(playerOut)
            .build();

        // when & then
        assertThatThrownBy(() -> tested.process(originalGame, subEvent))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(
                "Reserves of team: 1 in game with a key: GameKey[team1Id=1, team2Id=2, scheduled=1970-01-01T00:00:01Z] do not contain player 99");
    }

    @Test
    void testProcess_givenSquadDoesNotExist_shouldThrowException()
    {
        // given
        final var originalGame = DEFAULT_GAME;
        final long playerIn = 13;
        final long playerOut = 4;

        final var subEvent = SubstitutionEvent.builder()
            .withEventCommon(EventCommonTestUtil.from(originalGame.key(), 40, 99L)) // Non-existent squad ID
            .withPlayerIn(playerIn)
            .withPlayerOut(playerOut)
            .build();

        // when & then
        assertThatThrownBy(() -> tested.process(originalGame, subEvent))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("There is no squad with ID: 99 associated with a game key: GameKey[team1Id=1, team2Id=2, scheduled=1970-01-01T00:00:01Z]");
    }
}
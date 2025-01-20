package com.sportradar.scoreboard.domain.processing;

import static com.sportradar.scoreboard.domain.event.CardEvent.Card.RED;
import static com.sportradar.scoreboard.domain.event.CardEvent.Card.YELLOW;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.sportradar.scoreboard.domain.event.CardEvent;
import com.sportradar.scoreboard.domain.event.EventCommon;
import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.team.CardSet;
import com.sportradar.scoreboard.domain.team.Squad;


class CardEventProcessorTest extends ProcessorTest
{
    private CardEventProcessor tested;

    @Captor
    private ArgumentCaptor<Game> gameUpdateCaptor;

    @BeforeEach
    void setUp()
    {
        tested = new CardEventProcessor(gameStateRepository);
    }

    @Test
    void testProcess_givenYellowCardForAPlayerWithoutACard_shouldUpdateGameStateWithYellowCardForThatPlayer(final SoftAssertions softly)
    {
        // given
        final var cardEvent = CardEvent.builder()
            .withEventCommon(EventCommon.builder().withGameKey(DEFAULT_GAME.key()).withMatchTime(Duration.ofMinutes(31)).withAssociatedTeamId(1L).build())
            .withCard(YELLOW)
            .withCardReceiverId(1)
            .build();

        final var expectedUpdatedGame = DEFAULT_GAME.toBuilder()
            .withTeamIdToCardSet(Map.of(1L, CardSet.builder().withYellowCards(List.of(1L)).withRedCards(List.of()).build()))
            .build();

        // when
        tested.process(DEFAULT_GAME, cardEvent);

        // then
        verifyCapturedGameState(softly, expectedUpdatedGame);
    }

    @Test
    void testProcess_givenSecondYellowCardForPlayer_shouldConvertToRedCard(final SoftAssertions softly)
    {
        // given
        final var originalGame = DEFAULT_GAME.toBuilder()
            .withTeamIdToCardSet(Map.of(1L, CardSet.builder().withYellowCards(List.of(1L)).withRedCards(List.of()).build()))
            .build();

        final var cardEvent = CardEvent.builder()
            .withEventCommon(EventCommon.builder().withGameKey(DEFAULT_GAME.key()).withMatchTime(Duration.ofMinutes(45)).withAssociatedTeamId(1L).build())
            .withCard(YELLOW)
            .withCardReceiverId(1)
            .build();

        final var expectedUpdatedGame = originalGame.toBuilder()
            .withTeamIdToCardSet(Map.of(1L, CardSet.builder().withYellowCards(List.of()).withRedCards(List.of(1L)).build()))
            .build();

        // when
        tested.process(originalGame, cardEvent);

        // then
        verifyCapturedGameState(softly, expectedUpdatedGame);
    }

    @Test
    void testProcess_givenRedCardForPlayer_shouldUpdateGameStateWithRedCard(final SoftAssertions softly)
    {
        // given
        final var cardEvent = CardEvent.builder()
            .withEventCommon(EventCommon.builder().withGameKey(DEFAULT_GAME.key()).withMatchTime(Duration.ofMinutes(50)).withAssociatedTeamId(1L).build())
            .withCard(RED)
            .withCardReceiverId(2)
            .build();

        final var expectedUpdatedGame = DEFAULT_GAME.toBuilder()
            .withTeamIdToCardSet(Map.of(1L, CardSet.builder().withYellowCards(List.of()).withRedCards(List.of(2L)).build()))
            .build();

        // when
        tested.process(DEFAULT_GAME, cardEvent);

        // then
        verifyCapturedGameState(softly, expectedUpdatedGame);
    }

    @Test
    void testProcess_givenPlayerNotInSquad_shouldThrowException()
    {
        // given
        final var originalGame = DEFAULT_GAME.toBuilder()
            .withTeamIdToSquad(Map.of(1L, Squad.builder().withFirstEleven(List.of(2L, 3L)).build()))
            .build();

        final var cardEvent = CardEvent.builder()
            .withEventCommon(EventCommon.builder().withGameKey(DEFAULT_GAME.key()).withMatchTime(Duration.ofMinutes(20)).withAssociatedTeamId(1L).build())
            .withCard(YELLOW)
            .withCardReceiverId(1)
            .build();

        // when / then
        assertThatThrownBy(() -> tested.process(originalGame, cardEvent))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("First 11");
    }

    @Test
    void testProcess_givenSquadNotInMatch_shouldThrowException()
    {
        // given
        final var originalGame = DEFAULT_GAME;

        final var cardEvent = CardEvent.builder()
            .withEventCommon(EventCommon.builder().withGameKey(DEFAULT_GAME.key()).withMatchTime(Duration.ofMinutes(25)).withAssociatedTeamId(99L).build())
            .withCard(YELLOW)
            .withCardReceiverId(1)
            .build();

        // when / then
        assertThatThrownBy(() -> tested.process(originalGame, cardEvent))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining(
                "There is no squad with ID: 99 associated with a game key: GameKey[team1Id=1, team2Id=2, scheduled=1970-01-01T00:00:01Z]");
    }

    @ParameterizedTest
    @EnumSource(CardEvent.Card.class)
    void testProcess_givenPlayerWithThreeYellowCardsOrRedCard_shouldIgnoreAdditionalCard(final CardEvent.Card card)
    {
        // given
        final var originalGame = DEFAULT_GAME.toBuilder()
            .withTeamIdToCardSet(Map.of(1L, CardSet.builder().withYellowCards(List.of()).withRedCards(List.of(1L)).build()))
            .build();

        final var cardEvent = CardEvent.builder()
            .withEventCommon(EventCommon.builder().withGameKey(DEFAULT_GAME.key()).withMatchTime(Duration.ofMinutes(60)).withAssociatedTeamId(1L).build())
            .withCard(card)
            .withCardReceiverId(1)
            .build();

        // then & when
        assertThatThrownBy(() -> tested.process(originalGame, cardEvent))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Card received for a player with ID: 1, that already has a red card");
    }

    private void verifyCapturedGameState(final SoftAssertions softly, final Game expectedGameState)
    {
        verify(gameStateRepository).update(gameUpdateCaptor.capture());
        final var capturedGame = gameUpdateCaptor.getValue();

        softly.assertThat(capturedGame).isEqualTo(expectedGameState);
    }
}
package com.sportradar.scoreboard.domain.processing;

import static com.sportradar.scoreboard.domain.event.CardEvent.Card.YELLOW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.sportradar.scoreboard.domain.event.CardEvent;
import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.team.CardSet;
import com.sportradar.scoreboard.util.ExceptionUtil;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
class CardEventProcessor
{
    public Game process(final Game game, final CardEvent cardEvent)
    {
        final var updatedTeam = cardEvent.getEventCommon().associatedTeamId();
        final var card = cardEvent.getCard();

        final var squad = game.teamIdToSquad().get(updatedTeam);
        final long playerId = cardEvent.getCardReceiverId();

        if (squad == null)
        {
            ExceptionUtil.throwOnMissingSquad(game.key(), updatedTeam);
        }

        if (!squad.firstEleven().contains(playerId))
        {
            ExceptionUtil.throwOnPlayerMissingInSet("First 11", updatedTeam, game.key(), playerId);
        }

        final var cardSet = game.teamIdToCardSet().get(updatedTeam);

        if (cardSet != null && cardSet.redCards().contains(playerId))
        {
            throw new IllegalArgumentException("Card received for a player with ID: %s, that already has a red card".formatted(playerId));
        }

        final var cardSetUpdate = handleCard(playerId, card, game.teamIdToCardSet().get(updatedTeam));

        final var updatedTeamIdToCardSet = new HashMap<>(game.teamIdToCardSet());
        updatedTeamIdToCardSet.put(updatedTeam, cardSetUpdate);

        return game.toBuilder().withTeamIdToCardSet(Map.copyOf(updatedTeamIdToCardSet)).build();
    }

    private CardSet handleCard(final long playerId, final CardEvent.Card card, final CardSet originalCardSet)
    {
        final var effectiveCardSet = Optional.ofNullable(originalCardSet).orElse(CardSet.EMPTY);

        final var yellowCardsUpdate = new ArrayList<>(effectiveCardSet.yellowCards());
        final var redCardsUpdate = new ArrayList<>(effectiveCardSet.redCards());

        if (YELLOW.equals(card))
        {
            if (yellowCardsUpdate.contains(playerId))
            {
                yellowCardsUpdate.remove(playerId);
                redCardsUpdate.add(playerId);
            }
            else
            {
                yellowCardsUpdate.add(playerId);
            }
        }
        else
        {
            redCardsUpdate.add(playerId);
        }

        return CardSet.builder()
            .withRedCards(redCardsUpdate)
            .withYellowCards(yellowCardsUpdate)
            .build();
    }
}

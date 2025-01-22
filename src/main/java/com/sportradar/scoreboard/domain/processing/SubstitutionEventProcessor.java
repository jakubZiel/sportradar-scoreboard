package com.sportradar.scoreboard.domain.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sportradar.scoreboard.domain.event.SubstitutionEvent;
import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.util.ExceptionUtil;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class SubstitutionEventProcessor
{
    public Game process(final Game game, final SubstitutionEvent substitutionEvent)
    {
        final var updatedTeam = substitutionEvent.getEventCommon().associatedTeamId();

        final var squad = game.teamIdToSquad().get(updatedTeam);

        if (squad == null)
        {
            ExceptionUtil.throwOnMissingSquad(game.key(), updatedTeam);
        }

        final long playerIn = substitutionEvent.getPlayerIn();
        final long playerOut = substitutionEvent.getPlayerOut();

        if (!squad.firstEleven().contains(playerOut))
        {
            ExceptionUtil.throwOnPlayerMissingInSet("First 11", updatedTeam, game.key(), playerOut);
        }

        if (!squad.reserves().contains(playerIn))
        {
            ExceptionUtil.throwOnPlayerMissingInSet("Reserves", updatedTeam, game.key(), playerIn);
        }

        final var newFirst11 = handleSubstitution(squad.firstEleven(), playerIn, playerOut);
        final var newReserves = handleSubstitution(squad.reserves(), playerOut, playerIn);

        final var newSquad = squad.toBuilder()
            .withFirstEleven(newFirst11)
            .withReserves(newReserves)
            .build();

        final var newTeams = game.teamIdToSquad().entrySet().stream()
            .map(entry -> Map.entry(entry.getKey(), entry.getKey().equals(updatedTeam) ? newSquad : entry.getValue()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return game.toBuilder().withTeamIdToSquad(newTeams).build();
    }

    private List<Long> handleSubstitution(@NonNull final List<Long> squadList, final long addedPlayer, final long removedPlayer)
    {
        final var update = new ArrayList<>(squadList);
        update.add(addedPlayer);
        update.removeIf(player -> player == removedPlayer);
        return List.copyOf(update);
    }
}

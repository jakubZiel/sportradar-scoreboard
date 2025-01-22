package com.sportradar.scoreboard.domain.processing;

import java.util.ArrayList;
import java.util.List;

import com.sportradar.scoreboard.domain.event.GameOvertimeEvent;
import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.interfaces.outgoing.GameStateRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
class GameOvertimeProcessor
{
    private final GameStateRepository gameStateRepository;

    public void process(final Game game, final GameOvertimeEvent gameOvertimeEvent)
    {
        if (game.overtimes().size() >= 2)
        {
            throw new IllegalArgumentException("Game with a key: %s already has 2 overtimes".formatted(game.key()));
        }

        final var updatedOvertimes = new ArrayList<>(game.overtimes());
        updatedOvertimes.add(gameOvertimeEvent.getOvertime());

        final var newGame = game.toBuilder().withOvertimes(List.copyOf(updatedOvertimes)).build();
        gameStateRepository.update(newGame);
    }
}

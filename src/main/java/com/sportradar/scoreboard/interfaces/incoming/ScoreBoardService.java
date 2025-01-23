package com.sportradar.scoreboard.interfaces.incoming;

import java.util.List;
import java.util.Optional;

import com.sportradar.scoreboard.domain.event.SportEvent;
import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.game.GameKey;

import lombok.NonNull;


public interface ScoreBoardService
{
    Optional<Game> getGameState(@NonNull GameKey gameKey);

    List<Game> getAll();

    Optional<Game> process(@NonNull SportEvent event);
}

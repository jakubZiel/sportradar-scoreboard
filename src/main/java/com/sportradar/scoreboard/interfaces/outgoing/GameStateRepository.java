package com.sportradar.scoreboard.interfaces.outgoing;

import java.util.List;
import java.util.Optional;

import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.game.GameKey;


public interface GameStateRepository
{
    Game save(GameKey gameKey, Game game);

    Optional<Game> findByKey(GameKey key);

    List<Game> getAll();
}

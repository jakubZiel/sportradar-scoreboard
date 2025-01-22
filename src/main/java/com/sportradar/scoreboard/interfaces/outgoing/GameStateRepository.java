package com.sportradar.scoreboard.interfaces.outgoing;

import java.util.Optional;

import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.game.GameKey;


public interface GameStateRepository
{
    void save(Game game);

    Optional<Game> findByKey(GameKey key);
}

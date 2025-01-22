package com.sportradar.scoreboard.infrastructure;

import java.util.Map;
import java.util.Optional;

import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.game.GameKey;
import com.sportradar.scoreboard.interfaces.outgoing.GameStateRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class GameStateInMemoryRepository implements GameStateRepository
{
    private final Map<GameKey, Game> games;

    @Override
    public void save(final Game game)
    {
        games.put(game.key(), game);
    }

    @Override
    public Optional<Game> findByKey(final GameKey key)
    {
        return Optional.ofNullable(games.get(key));
    }
}

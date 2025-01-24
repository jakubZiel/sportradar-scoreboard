package com.sportradar.scoreboard.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.game.GameKey;
import com.sportradar.scoreboard.interfaces.outgoing.GameStateRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class GameStateInMemoryRepository implements GameStateRepository
{
    private long gameIdSequence;
    private final Map<GameKey, @NonNull Game> games;

    @Override
    public Game save(final GameKey gameKey, final Game game)
    {
        if (game == null)
        {
            games.remove(gameKey);
            return null;
        }

        final var gameId = Optional.ofNullable(gameKey.gameId()).orElseGet(() -> ++gameIdSequence);

        final var update = game.toBuilder()
            .withKey(gameKey.toBuilder().withGameId(gameId).build())
            .build();

        games.put(update.key(), update);
        return update;
    }

    @Override
    public Optional<Game> findByKey(final GameKey key)
    {
        return Optional.ofNullable(games.get(key));
    }

    @Override
    public List<Game> getAll()
    {
        return List.copyOf(games.values());
    }
}

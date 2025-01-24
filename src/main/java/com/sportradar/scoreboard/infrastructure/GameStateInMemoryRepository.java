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

    /**
     * Saves or updates a game in the storage based on the provided {@code gameKey} and {@code game}.
     * If the {@code game} is {@code null}, the method removes the entry associated with the {@code gameKey} from the storage
     * and returns {@code null}. If {@code gameKey} has {@link GameKey#gameId} it results in game update, otherwise it is interpreted
     * as a game creation and game is associated with new unique ID.
     *
     * @param gameKey The key associated with the game.
     * @param game    The game object to be saved or updated. If {@code null}, the game entry is removed.
     * @return The updated game that was saved or {@code null}.
     * @throws NullPointerException if {@code gameKey} is {@code null}.
     */
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

package com.sportradar.scoreboard.domain.scoreboard;

import java.util.List;
import java.util.Optional;

import com.sportradar.scoreboard.domain.event.SportEvent;
import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.game.GameKey;
import com.sportradar.scoreboard.domain.processing.SportEventVisitor;
import com.sportradar.scoreboard.interfaces.incoming.ScoreBoardService;
import com.sportradar.scoreboard.interfaces.outgoing.GameStateRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class ScoreBoardServiceImpl implements ScoreBoardService
{
    private final SportEventVisitor sportEventVisitor;
    private final GameStateRepository gameStateRepository;

    @Override
    public Optional<Game> getGameState(@NonNull final GameKey gameKey)
    {
        return gameStateRepository.findByKey(gameKey);
    }

    @Override
    public List<Game> getAll()
    {
        return gameStateRepository.getAll().stream().sorted(this::orderGame).toList();
    }

    @Override
    public Optional<Game> process(@NonNull final SportEvent event)
    {
        return Optional.ofNullable(event.accept(sportEventVisitor));
    }

    private int orderGame(@NonNull final Game game1, @NonNull final Game game2)
    {
        final var totalScoreOrderDesc = Integer.compare(game1.totalScore(), game2.totalScore()) * -1;
        final var creationTimeOrderDesc = game1.key().gameId().compareTo(game2.key().gameId()) * -1;
        return totalScoreOrderDesc != 0 ? totalScoreOrderDesc : creationTimeOrderDesc;
    }
}

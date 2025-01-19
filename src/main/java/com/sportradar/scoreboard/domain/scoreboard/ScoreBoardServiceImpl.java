package com.sportradar.scoreboard.domain.scoreboard;

import java.util.List;
import java.util.Optional;

import com.sportradar.scoreboard.domain.event.SportEvent;
import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.game.GameKey;
import com.sportradar.scoreboard.domain.processing.SportEventVisitor;
import com.sportradar.scoreboard.interfaces.incoming.ScoreBoardService;
import com.sportradar.scoreboard.interfaces.outgoing.GameStateRepository;
import com.sportradar.scoreboard.interfaces.outgoing.SportEventRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
class ScoreBoardServiceImpl implements ScoreBoardService
{
    private final SportEventVisitor sportEventVisitor;
    private final SportEventRepository sportEventRepository;
    private final GameStateRepository gameStateRepository;

    @Override
    public Optional<Game> getGameState(@NonNull final GameKey gameKey)
    {
        return gameStateRepository.findByKey(gameKey);
    }

    @Override
    public List<SportEvent> getGameHistory(@NonNull final GameKey gameKey)
    {
        return sportEventRepository.getHistory(gameKey);
    }

    @Override
    public <T extends SportEvent> List<T> getEventsByClass(@NonNull final GameKey gameKey, final Class<T> eventClass)
    {
        return sportEventRepository.getEvents(gameKey, eventClass);
    }

    @Override
    public void process(@NonNull final SportEvent event)
    {
        event.accept(sportEventVisitor);
        sportEventRepository.addEvent(event);
    }
}

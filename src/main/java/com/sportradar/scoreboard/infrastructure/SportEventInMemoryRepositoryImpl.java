package com.sportradar.scoreboard.infrastructure;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.sportradar.scoreboard.domain.event.SportEvent;
import com.sportradar.scoreboard.domain.game.GameKey;
import com.sportradar.scoreboard.interfaces.outgoing.SportEventRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class SportEventInMemoryRepositoryImpl implements SportEventRepository
{
    private final Map<GameKey, List<SportEvent>> gameToEventStore;
    private final Map<GameKey, Map<Class<? extends SportEvent>, List<? extends SportEvent>>> gameAndTypeToEventStore;

    @Override
    public void addEvent(final SportEvent sportEvent)
    {
        gameToEventStore
            .computeIfAbsent(sportEvent.getEventCommon().gameKey(), key -> new LinkedList<>()).
            add(sportEvent);

        gameAndTypeToEventStore
            .computeIfAbsent(sportEvent.getEventCommon().gameKey(), key -> new HashMap<>())
            .computeIfAbsent(sportEvent.getClass(), eventClass -> new LinkedList<>());
    }

    @Override
    public List<SportEvent> getHistory(final GameKey gameKey)
    {
        return gameToEventStore.get(gameKey);
    }

    @Override
    public <T extends SportEvent> List<T> getEvents(final GameKey gameKey, final Class<T> eventClass)
    {
        return gameToEventStore.get(gameKey).stream().map(eventClass::cast).toList();
    }
}

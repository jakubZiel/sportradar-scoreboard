package com.sportradar.scoreboard.interfaces.outgoing;

import java.util.List;

import com.sportradar.scoreboard.domain.event.SportEvent;
import com.sportradar.scoreboard.domain.game.GameKey;


public interface SportEventRepository
{
    void addEvent(SportEvent sportEvent);

    List<SportEvent> getHistory(GameKey gameKey);

    <T extends SportEvent> List<T> getEvents(GameKey gameKey, Class<T> eventClass);
}

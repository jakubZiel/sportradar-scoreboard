package com.sportradar.scoreboard.domain.processing;

import com.sportradar.scoreboard.domain.event.GameClosedEvent;
import com.sportradar.scoreboard.domain.event.GameOpenedEvent;
import com.sportradar.scoreboard.domain.event.GoalEvent;
import com.sportradar.scoreboard.domain.game.Game;

import lombok.NonNull;


public interface SportEventVisitor
{
    Game visit(@NonNull GoalEvent goalEvent);

    Game visit(@NonNull GameOpenedEvent gameOpenedEvent);

    Game visit(@NonNull GameClosedEvent gameOpenedEvent);
}

package com.sportradar.scoreboard.domain.event;

import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.game.GameKey;
import com.sportradar.scoreboard.domain.processing.SportEventVisitor;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder(setterPrefix = "with")
public class GameOpenedEvent implements SportEvent
{
    private GameKey gameKey;

    @Override
    public Game accept(final SportEventVisitor visitor)
    {
        return visitor.visit(this);
    }
}

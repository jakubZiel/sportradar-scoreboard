package com.sportradar.scoreboard.domain.event;

import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.processing.SportEventVisitor;


public interface SportEvent extends GameAssociated
{
    Game accept(SportEventVisitor visitor);
}

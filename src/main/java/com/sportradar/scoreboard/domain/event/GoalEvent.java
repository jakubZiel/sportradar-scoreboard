package com.sportradar.scoreboard.domain.event;

import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.game.GameKey;
import com.sportradar.scoreboard.domain.processing.SportEventVisitor;
import com.sportradar.scoreboard.domain.team.Team;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;


@Getter
@Builder(setterPrefix = "with")
public class GoalEvent implements SportEvent
{
    private final GameKey gameKey;
    @NonNull
    private final Team scoringTeam;

    @Override
    public Game accept(final SportEventVisitor visitor)
    {
        return visitor.visit(this);
    }
}

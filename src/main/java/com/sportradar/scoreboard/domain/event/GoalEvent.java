package com.sportradar.scoreboard.domain.event;

import com.sportradar.scoreboard.domain.processing.SportEventVisitor;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder(setterPrefix = "with")
public class GoalEvent implements SportEvent
{
    private final EventCommon eventCommon;
    private final long goalScorerId;
    private final boolean isOwnGoal;

    @Override
    public void accept(final SportEventVisitor visitor)
    {
        visitor.visit(this);
    }
}

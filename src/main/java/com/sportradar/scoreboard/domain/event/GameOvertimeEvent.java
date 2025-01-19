package com.sportradar.scoreboard.domain.event;

import java.time.Duration;

import com.sportradar.scoreboard.domain.processing.SportEventVisitor;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder(setterPrefix = "with")
public class GameOvertimeEvent implements SportEvent
{
    private final EventCommon eventCommon;
    private final Duration overtime;

    @Override
    public void accept(final SportEventVisitor visitor)
    {
        visitor.visit(this);
    }
}

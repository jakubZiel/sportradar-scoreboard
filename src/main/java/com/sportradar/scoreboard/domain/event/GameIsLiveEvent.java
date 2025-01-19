package com.sportradar.scoreboard.domain.event;

import java.time.Instant;

import com.sportradar.scoreboard.domain.processing.SportEventVisitor;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder(setterPrefix = "with")
public class GameIsLiveEvent implements SportEvent
{
    private final boolean isLive;
    private final Instant eventTime;

    private final EventCommon eventCommon;

    @Override
    public void accept(final SportEventVisitor visitor)
    {
        visitor.visit(this);
    }
}

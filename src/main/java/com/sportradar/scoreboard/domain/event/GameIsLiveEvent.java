package com.sportradar.scoreboard.domain.event;

import java.time.Instant;
import java.util.Map;

import com.sportradar.scoreboard.domain.processing.SportEventVisitor;
import com.sportradar.scoreboard.domain.team.Squad;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder(setterPrefix = "with")
public class GameIsLiveEvent implements SportEvent
{
    private final EventCommon eventCommon;
    private final boolean isLive;
    private final Instant eventTime;
    private final Map<Long, Squad> teamIdToSquad;

    @Override
    public void accept(final SportEventVisitor visitor)
    {
        visitor.visit(this);
    }
}

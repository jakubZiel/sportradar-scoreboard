package com.sportradar.scoreboard.domain.event;

import com.sportradar.scoreboard.domain.processing.SportEventVisitor;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder(setterPrefix = "with")
public class SubstitutionEvent implements SportEvent
{
    private final EventCommon eventCommon;

    private long playerIn;
    private long playerOut;

    @Override
    public void accept(final SportEventVisitor visitor)
    {
        visitor.visit(this);
    }
}


package com.sportradar.scoreboard.domain.event;

import com.sportradar.scoreboard.domain.processing.SportEventVisitor;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder(setterPrefix = "with")
public class CardEvent implements SportEvent
{
    private final EventCommon eventCommon;
    private final long cardReceiverId;
    private final Card card;

    @Override
    public void accept(final SportEventVisitor visitor)
    {
        visitor.visit(this);
    }

    public enum Card
    {
        RED, YELLOW;
    }
}

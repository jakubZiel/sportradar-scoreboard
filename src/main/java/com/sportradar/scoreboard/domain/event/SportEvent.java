package com.sportradar.scoreboard.domain.event;

import com.sportradar.scoreboard.domain.processing.SportEventVisitor;

import lombok.NonNull;


public interface SportEvent
{
    @NonNull
    EventCommon getEventCommon();

    void accept(SportEventVisitor visitor);
}

package com.sportradar.scoreboard.domain.util;

import java.time.Duration;

import com.sportradar.scoreboard.domain.event.EventCommon;
import com.sportradar.scoreboard.domain.game.GameKey;


public class EventCommonTestUtil
{
    public static EventCommon from(final GameKey gameKey, final int minutes, final Long teamId)
    {
        return EventCommon.builder()
            .withGameKey(gameKey)
            .withMatchTime(Duration.ofMinutes(minutes))
            .withAssociatedTeamId(teamId)
            .build();
    }
}

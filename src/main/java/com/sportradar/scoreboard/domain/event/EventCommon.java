package com.sportradar.scoreboard.domain.event;

import java.time.Duration;

import com.sportradar.scoreboard.domain.game.GameKey;

import lombok.Builder;
import lombok.NonNull;


@Builder(setterPrefix = "with")
public record EventCommon(@NonNull GameKey gameKey, @NonNull Duration matchTime, Long associatedTeamId)
{
}

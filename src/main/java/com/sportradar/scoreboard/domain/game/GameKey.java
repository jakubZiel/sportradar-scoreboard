package com.sportradar.scoreboard.domain.game;

import java.time.Instant;

import lombok.NonNull;


public record GameKey(long team1Id, long team2Id, @NonNull Instant scheduled)
{
}

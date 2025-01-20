package com.sportradar.scoreboard.util;

import com.sportradar.scoreboard.domain.game.GameKey;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionUtil
{
    public static void throwOnMissingSquad(final GameKey gameKey, final long teamId)
    {
        throw new IllegalStateException("There is no squad with ID: %s associated with a game key: %s".formatted(teamId, gameKey));
    }

    public static void throwOnPlayerMissingInSet(final String playerSet, final long teamId, final GameKey gameKey, final long missingPlayerId)
    {
        throw new IllegalArgumentException("%s of team: %s in game with a key: %s do not contain player %s"
            .formatted(playerSet, teamId, gameKey, missingPlayerId));
    }
}

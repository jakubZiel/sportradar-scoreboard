package com.sportradar.scoreboard.domain.util;

import java.util.Map;

import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.game.GameKey;
import com.sportradar.scoreboard.domain.team.Team;


public class TestUtil
{
    public static Team buildTeam(final String countryName)
    {
        return new Team(countryName);
    }

    public static GameKey buildGameKey(final String team1, final String team2)
    {
        return buildGameKey(team1, team2, null);
    }

    public static GameKey buildGameKey(final String team1, final String team2, final Long id)
    {
        return GameKey.builder()
            .withTeam1(buildTeam(team1))
            .withTeam2(buildTeam(team2))
            .withGameId(id)
            .build();
    }

    public static Game buildGame(final int score1, final int score2, final String team1, final String team2, final long id)
    {
        final var gameKey = GameKey.builder()
            .withTeam1(buildTeam(team1))
            .withTeam2(buildTeam(team2))
            .withGameId(id)
            .build();

        return Game.builder()
            .withTotalScore(score1 + score2)
            .withKey(gameKey)
            .withTeamIdToScore(Map.of(gameKey.team1(), score1, gameKey.team2(), score2))
            .build();
    }
}

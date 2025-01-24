package com.sportradar.scoreboard.infrastructure;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sportradar.scoreboard.domain.game.Game;
import com.sportradar.scoreboard.domain.game.GameKey;
import com.sportradar.scoreboard.domain.util.TestUtil;


@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
class GameStateInMemoryRepositoryTest
{
    private final Map<GameKey, Game> gameStore = new HashMap<>();
    private final GameStateInMemoryRepository tested = new GameStateInMemoryRepository(gameStore);

    @BeforeEach
    void setUp()
    {
        gameStore.clear();
    }

    @Test
    void testSave_givenTombstoneUpdate_shouldRemoveGame(final SoftAssertions softly)
    {
        // given
        final var game = TestUtil.buildGame(0, 0, "USA", "Canada", 1L);
        tested.save(game.key(), game);

        // when
        tested.save(game.key(), null);

        // then
        softly.assertThat(gameStore).doesNotContainKey(game.key());
    }

    @Test
    void testSave_givenCreateGameRequest_shouldSaveWithNewlyGenerateGameId(final SoftAssertions softly)
    {
        // given
        final var game = TestUtil.buildGame(0, 0, "USA", "Canada", null);

        // when
        final var savedGame = tested.save(game.key(), game);

        // then
        softly.assertThat(game.key().gameId()).isNull();
        softly.assertThat(savedGame.key().gameId()).isEqualTo(1L);
        softly.assertThat(gameStore).containsEntry(savedGame.key(), savedGame);
    }

    @Test
    void testSave_givenUpdateTeamRequest_shouldSaveWithOldGameId(final SoftAssertions softly)
    {
        // given
        final var game = TestUtil.buildGame(0, 0, "USA", "Canada", 12L);

        tested.save(game.key(), game);

        final var updatedGame = game.toBuilder()
            .withTeamIdToScore(Map.of(TestUtil.buildTeam("USA"), 1, TestUtil.buildTeam("Canada"), 0))
            .withTotalScore(1)
            .build();

        // when
        final var savedGame = tested.save(game.key(), updatedGame);

        // then
        softly.assertThat(savedGame.key().gameId()).isEqualTo(12L);
        softly.assertThat(savedGame.totalScore()).isEqualTo(1);
        softly.assertThat(gameStore).containsEntry(savedGame.key(), savedGame);
    }
}
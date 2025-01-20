package com.sportradar.scoreboard.domain.team;

import java.util.List;

import lombok.Builder;
import lombok.NonNull;


@Builder(setterPrefix = "with", toBuilder = true)
public record CardSet(@NonNull List<Long> yellowCards, @NonNull List<Long> redCards)
{
    public static CardSet EMPTY = new CardSet(List.of(), List.of());
}

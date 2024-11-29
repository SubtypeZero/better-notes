package dev.subtypezero.betternotes.ui.states;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortState {
    ALPHABETICAL("Alphabetical"),
    WEIGHTED("Weighted");

    private final String name;
}

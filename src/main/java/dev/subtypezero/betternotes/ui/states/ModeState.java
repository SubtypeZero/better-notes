package dev.subtypezero.betternotes.ui.states;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModeState {
    STANDARD("Standard"),
    COMPACT("Compact"),
    ICON("Icon");

    private final String name;
}

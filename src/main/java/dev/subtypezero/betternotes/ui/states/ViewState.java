package dev.subtypezero.betternotes.ui.states;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ViewState {
    LIST("List"),
    SECTION("Section");

    private final String name;
}

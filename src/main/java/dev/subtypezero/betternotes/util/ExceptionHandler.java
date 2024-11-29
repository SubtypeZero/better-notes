package dev.subtypezero.betternotes.util;

@FunctionalInterface
public interface ExceptionHandler {
    void handle(Exception e);
}

package com.parsecs;

@FunctionalInterface
public interface gameevent {
    void execute(gamestate state, IUserInterface ui);
}
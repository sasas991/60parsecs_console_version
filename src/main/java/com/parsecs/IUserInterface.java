package com.parsecs;

public interface IUserInterface {
    void println(String message);
    void print(String message);
    void typewriter(String message);
    int readInt();
    String readString();
    void clearScreen();
    void pause(int seconds);
}

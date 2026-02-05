package com.parsecs;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class gameui {
    private Terminal terminal;
    private TextGraphics textGraphics;
    private int currentLine = 0;

    public gameui(Terminal terminal) throws IOException {
        this.terminal = terminal;
        this.textGraphics = terminal.newTextGraphics();
    }

    private void advanceLine(int lines) {
        try {
            int rows = terminal.getTerminalSize().getRows();
            currentLine += lines;
            if (currentLine >= rows) {
                currentLine = rows - 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printTitle() {
        clearScreen();
        println("╔════════════════════════════════════════╗");
        println("║               60 PARSECS               ║");
        println("║     Выживание в космосе началось!      ║");
        println("╚════════════════════════════════════════╝");
        println("");
        pause(2);
    }

    public void displayStatus(gamestate state) {
        println("╔═══════════════ ДЕНЬ " + String.format("%-3d", state.day) + " ════════════════╗");
        println("║ [O2] Кислород: " + String.format("%-3d", state.oxygen) + "%                    ║");
        println("║ [FD] Еда:      " + String.format("%-3d", state.food) + "%                    ║");
        println("║ [HP] Корпус:   " + String.format("%-3d", state.ship) + "%                    ║");
        println("║ [CR] Экипаж:   " + String.format("%-3d", state.crew.size()) + " чел.               ║");
        println("╚════════════════════════════════════════╝");
        println("");
    }

    public void println(String message) {
        try {
            textGraphics.putString(0, currentLine, message);
            advanceLine(1);
            terminal.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void print(String message) {
        try {
            textGraphics.putString(0, currentLine, message);
            terminal.setCursorPosition(message.length(), currentLine);
            terminal.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int readInt() {
        String input = readString();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String readString() {
        try {
            terminal.setCursorVisible(true);
            StringBuilder sb = new StringBuilder();
            int startColumn = terminal.getCursorPosition().getColumn();

            while (true) {
                KeyStroke keyStroke = terminal.readInput();
                if (keyStroke.getKeyType() == KeyType.Enter) {
                    break;
                } else if (keyStroke.getKeyType() == KeyType.Backspace) {
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                        terminal.setCursorPosition(startColumn + sb.length(), currentLine);
                        terminal.putCharacter(' ');
                        terminal.setCursorPosition(startColumn + sb.length(), currentLine);
                        terminal.flush();
                    }
                } else if (keyStroke.getKeyType() == KeyType.Character) {
                    sb.append(keyStroke.getCharacter());
                    terminal.putCharacter(keyStroke.getCharacter());
                    terminal.flush();
                }
            }
            terminal.setCursorVisible(false);
            advanceLine(1);
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void clearScreen() {
        try {
            terminal.clearScreen();
            currentLine = 0;
            terminal.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Метод close() больше не нужен, так как терминал управляется в gamesession
}
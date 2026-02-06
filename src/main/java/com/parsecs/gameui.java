package com.parsecs;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.TextColor;
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
        try {
            println("   __   ___    ____   _    ____  ____  _____ ____ ____  ");
            println("  / /_ / _ \\  |  _ \\ / \\  |  _ \\/ ___|| ____/ ___/ ___| ");
            println(" | '_ \\ | | | | |_) / _ \\ | |_) \\___ \\|  _|| |   \\___ \\ ");
            println(" | (_) | |_| ||  __/ ___ \\|  _ < ___) | |__| |___ ___) |");
            println("  \\___/ \\___/ |_| /_/   \\_\\_| \\_\\____/|_____\\____|____/ ");
            println("");
            println("         Выживание в космосе началось!");
        } catch (Exception e) {}
        println("");
        pause(2);
    }

    public void displayStatus(gamestate state) {

        println("╔═══════════════ ДЕНЬ " + String.format("%-3d", state.day) + " ════════════════╗");
        
        
        printStatusRow("║ [O2] Кислород: ", state.oxygen, "%                     ║");
        printStatusRow("║ [FD] Еда:      ", state.food, "%                     ║");
        printStatusRow("║ [HP] Корпус:   ", state.ship, "%                     ║");
        
        
        println("║ [CR] Экипаж:   " + String.format("%-3d", state.crew.size()) + " чел.                 ║");
        
        println("╚═════════════════════════════════════════╝");
        println("");
    }

    public void println(String message) {
        printText(message, false);
    }

    public void typewriter(String message) {
        printText(message, true);
    }

    private void printText(String message, boolean animate) {
        
        TextColor color = TextColor.ANSI.DEFAULT;
        
        
        if (message.contains("[OK]") || message.contains("[SUCCESS]") || message.contains("[HEAL]") || message.contains("[LOOT]") || message.contains("[TRADE]") || message.contains("[SCIENCE]")) {
            color = TextColor.ANSI.GREEN;
        } else if (message.contains("[ERROR]") || message.contains("[FAIL]") || message.contains("[DAMAGE]") || message.contains("[DEATH]") || message.contains("[GAME OVER]") || message.contains("[!!!]")) {

            color = TextColor.ANSI.RED;

        } else if (message.contains("[WARNING]") || message.contains("[EVENT]") || message.contains("[ATTACK]") || message.contains("[LOCKED]")) {
            color = TextColor.ANSI.YELLOW;
        } else if (message.contains("[VICTORY]") || message.contains("[LAUNCH]") || message.contains("[ADMIN]") || message.contains("[ СИСТЕМА БЕЗОПАСНОСТИ ]")) {
            color = TextColor.ANSI.CYAN;
        } else if (message.trim().startsWith("╔") || message.trim().startsWith("║") || message.trim().startsWith("╚")) {
             color = TextColor.ANSI.CYAN; 
        }

        try {
            terminal.setCursorPosition(0, currentLine);
            textGraphics.setForegroundColor(color);            
            
            if (animate) {
                for (char c : message.toCharArray()) {
                    textGraphics.putString(terminal.getCursorPosition().getColumn(), currentLine, String.valueOf(c));
                    terminal.flush();
                    TimeUnit.MILLISECONDS.sleep(30); 
                }
            } else {
                textGraphics.putString(0, currentLine, message);
            }
            terminal.flush();
            textGraphics.setForegroundColor(TextColor.ANSI.DEFAULT); 
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            
            Thread.currentThread().interrupt();
        }
        
        advanceLine(1);
    }

    public void print(String message) {


        try {
            terminal.setCursorPosition(0, currentLine);
            textGraphics.setForegroundColor(TextColor.ANSI.DEFAULT);
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
            terminal.setForegroundColor(TextColor.ANSI.CYAN); 
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
            terminal.setForegroundColor(TextColor.ANSI.DEFAULT); 
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

    
    private void printStatusRow(String prefix, int value, String suffix) {
        try {
            
            textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
            textGraphics.putString(0, currentLine, "║");
            textGraphics.setForegroundColor(TextColor.ANSI.DEFAULT);
            textGraphics.putString(1, currentLine, prefix.substring(1));
            
            
            TextColor valColor = TextColor.ANSI.GREEN;
            if (value <= 25) valColor = TextColor.ANSI.RED;
            else if (value <= 50) valColor = TextColor.ANSI.YELLOW;
            
            textGraphics.setForegroundColor(valColor);
            textGraphics.putString(prefix.length(), currentLine, String.format("%-3d", value));
            
            
            textGraphics.setForegroundColor(TextColor.ANSI.DEFAULT);
            textGraphics.putString(prefix.length() + 3, currentLine, suffix.substring(0, suffix.length()-1));
            textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
            textGraphics.putString(prefix.length() + 3 + suffix.length() - 1, currentLine, "║");
            
            advanceLine(1);
            terminal.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
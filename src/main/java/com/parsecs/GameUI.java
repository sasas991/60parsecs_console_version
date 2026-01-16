package com.parsecs;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class GameUI {
    private final Scanner scanner = new Scanner(System.in);

    public void printTitle() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║        60 PARSECS: ТЕРМИНАЛ           ║");
        System.out.println("║     Выживание в космосе началось!     ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();
        pause(2);
    }

    public void displayStatus(GameState state) {
        System.out.println("╔═══════════════ ДЕНЬ " + state.day + " ═══════════════╗");
        System.out.println("║ 🫁 Кислород: " + state.oxygen + "%");
        System.out.println("║ 🍲 Еда: " + state.food + "%");
        System.out.println("║ 🛡️ Корпус: " + state.hull + "%");
        System.out.println("║ 👥 Экипаж: " + state.crew.size() + " человек(а)");
        System.out.println("╚════════════════════════════════════════╝\n");
    }

    public void println(String message) {
        System.out.println(message);
    }

    public void print(String message) {
        System.out.print(message);
    }

    public int readInt() {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();
            return choice;
        } catch (Exception e) {
            scanner.nextLine();
            return -1;
        }
    }

    public String readString() {
        return scanner.nextLine();
    }

    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void pause(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void close() {
        scanner.close();
    }
}
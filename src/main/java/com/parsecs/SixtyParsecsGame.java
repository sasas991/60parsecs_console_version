package com.parsecs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SixtyParsecsGame {
    private final GameUI ui = new GameUI();
    private final GameState state = new GameState();
    private final Random random = new Random();
    private final GameDatabase db = new GameDatabase();
    
    public void start() {
        ui.printTitle();
        
        ui.println("1. Новая игра");
        ui.println("2. Загрузить игру");
        ui.print("Ваш выбор: ");
        int choice = ui.readInt();

        if (choice == 2) {
            ui.print("Введите имя сохранения: ");
            String saveName = ui.readString();
            GameState loadedState = db.loadGame(saveName);
            
            if (loadedState != null) {
                state.oxygen = loadedState.oxygen;
                state.food = loadedState.food;
                state.hull = loadedState.hull;
                state.day = loadedState.day;
                state.crew = loadedState.crew;
                state.items = loadedState.items;
                state.gameOver = loadedState.gameOver;
                ui.println("Игра загружена!");
                ui.pause(1);
            } else {
                ui.println("Сохранение не найдено. Начинаем новую игру...");
                ui.pause(2);
                scavengePhase();
            }
        } else {
            scavengePhase();
        }
        
        if (!state.gameOver) {
            survivalPhase();
        }
        
        ui.close();
    }
    
    private void scavengePhase() {
        ui.println("🚨 ТРЕВОГА! ЯДЕРНАЯ АТАКА ЧЕРЕЗ 60 СЕКУНД!");
        ui.println("Быстро собирайте членов экипажа и предметы!\n");
        
        List<String> availableCrew = new ArrayList<>(Arrays.asList(
            "Капитан Джонс", "Инженер Эмили", "Учёный Макс", 
            "Медик Сара", "Солдат Том"
        ));
        
        List<String> availableItems = new ArrayList<>(Arrays.asList(
            "Аптечка", "Суповой порошок", "Атомная батарея",
            "Лазерный пистолет", "Скафандр", "Радио"
        ));
        
        int timeLeft = 60;
        
        while (timeLeft > 0 && (state.crew.size() < 3 || state.items.size() < 4)) {
            ui.println("⏱ Осталось: " + timeLeft + " секунд");
            ui.println("Экипаж: " + state.crew.size() + "/3 | Предметы: " + state.items.size() + "/4\n");
            
            if (state.crew.size() < 3 && !availableCrew.isEmpty()) {
                ui.println("ДОСТУПНЫЙ ЭКИПАЖ:");
                for (int i = 0; i < availableCrew.size(); i++) {
                    ui.println((i + 1) + ". " + availableCrew.get(i));
                }
            }
            
            if (state.items.size() < 4 && !availableItems.isEmpty()) {
                ui.println("\nДОСТУПНЫЕ ПРЕДМЕТЫ:");
                for (int i = 0; i < availableItems.size(); i++) {
                    ui.println((i + 6) + ". " + availableItems.get(i));
                }
            }
            
            ui.print("\nВыберите номер (или 0 для завершения): ");
            
            int choice = ui.readInt();
            
            if (choice == 0) break;
            
            if (choice >= 1 && choice <= 5 && state.crew.size() < 3) {
                int idx = choice - 1;
                if (idx < availableCrew.size()) {
                    state.crew.add(availableCrew.get(idx));
                    availableCrew.remove(idx);
                    timeLeft -= 8;
                }
            } else if (choice >= 6 && choice <= 11 && state.items.size() < 4) {
                int idx = choice - 6;
                if (idx < availableItems.size()) {
                    state.items.add(availableItems.get(idx));
                    availableItems.remove(idx);
                    timeLeft -= 5;
                }
            }
            
            ui.clearScreen();
        }
        
        if (state.crew.isEmpty()) {
            ui.println("\n💀 Вы не успели взять экипаж! ИГРА ОКОНЧЕНА.");
            state.gameOver = true;
        } else {
            ui.println("\n🚀 Вы успели! Шаттл отправляется в космос!");
            ui.println("Экипаж: " + state.crew);
            ui.println("Предметы: " + state.items);
            ui.pause(3);
        }
    }
    
    private void survivalPhase() {
        ui.println("\n═══════════════════════════════════════");
        ui.println("    НАЧИНАЕТСЯ ФАЗА ВЫЖИВАНИЯ");
        ui.println("═══════════════════════════════════════\n");
        ui.pause(2);
        
        while (!state.gameOver && state.day <= 30) {
            ui.clearScreen();
            ui.displayStatus(state);
            
            handleRandomEvent();
            
            if (state.gameOver) break;
            
            consumeResources();
            makeDecision();
            checkGameState();
            
            state.day++;
            ui.pause(1);
        }
        
        if (state.day > 30 && !state.gameOver) {
            ui.println("\n🎉 ПОБЕДА! Вы выжили 30 дней в космосе!");
            ui.println("Ваш экипаж достиг новой планеты!");
        }
    }
    
    private void handleRandomEvent() {
        String[] events = {
            "Метеоритный дождь повредил корпус!",
            "Обнаружен дрейфующий контейнер с припасами!",
            "Неизвестный сигнал из глубин космоса...",
            "Система жизнеобеспечения работает нормально.",
            "Член экипажа заболел!"
        };
        
        int eventChance = random.nextInt(100);
        
        if (eventChance < 30) {
            String event = events[random.nextInt(events.length)];
            ui.println("📡 СОБЫТИЕ: " + event);
            
            if (event.contains("Метеоритный")) {
                state.hull -= 15;
                ui.println("   Корпус повреждён! -15%");
            } else if (event.contains("контейнер")) {
                state.food += 20;
                ui.println("   Найдена еда! +20%");
            } else if (event.contains("заболел")) {
                if (state.items.contains("Аптечка")) {
                    ui.println("   Использована аптечка для лечения.");
                    state.items.remove("Аптечка");
                } else {
                    ui.println("   Нет аптечки! Член экипажа погиб.");
                    if (!state.crew.isEmpty()) state.crew.remove(0);
                }
            }
            ui.println("");
            ui.pause(2);
        }
    }
    
    private void consumeResources() {
        state.oxygen -= state.crew.size() * 3;
        state.food -= state.crew.size() * 2;
        
        if (state.oxygen < 0) state.oxygen = 0;
        if (state.food < 0) state.food = 0;
    }
    
    private void makeDecision() {
        ui.println("Выберите действие:");
        ui.println("1. Отдохнуть (восстановить здоровье экипажа)");
        ui.println("2. Починить корабль (восстановить корпус)");
        ui.println("3. Рационировать еду (сохранить еду)");
        ui.println("4. Исследовать космос (шанс найти ресурсы)");
        ui.println("5. Сохранить игру");
        
        ui.print("\nВаш выбор: ");
        int choice = ui.readInt();
        
        switch (choice) {
            case 1:
                ui.println("Экипаж отдыхает...");
                break;
            case 2:
                if (state.items.contains("Атомная батарея")) {
                    state.hull += 20;
                    ui.println("Корпус починен! +20%");
                } else {
                    state.hull += 5;
                    ui.println("Частичный ремонт. +5%");
                }
                break;
            case 3:
                state.food += 10;
                ui.println("Еда сохранена. +10%");
                break;
            case 4:
                if (random.nextBoolean()) {
                    state.oxygen += 15;
                    ui.println("Найден кислород! +15%");
                } else {
                    ui.println("Ничего не найдено...");
                }
                break;
            case 5:
                ui.print("Введите имя для сохранения: ");
                String saveName = ui.readString();
                db.saveGame(saveName, state);
                break;
        }
        
        ui.println("");
    }
    
    private void checkGameState() {
        if (state.oxygen <= 0) {
            ui.println("\n💀 Кислород закончился! Экипаж погиб от удушья.");
            state.gameOver = true;
        } else if (state.food <= 0) {
            ui.println("\n💀 Еда закончилась! Экипаж умер от голода.");
            state.gameOver = true;
        } else if (state.hull <= 0) {
            ui.println("\n💀 Корпус разрушен! Корабль развалился в космосе.");
            state.gameOver = true;
        } else if (state.crew.isEmpty()) {
            ui.println("\n💀 Весь экипаж погиб! Некому управлять кораблём.");
            state.gameOver = true;
        }
        
        // Ограничение ресурсов
        if (state.oxygen > 100) state.oxygen = 100;
        if (state.food > 100) state.food = 100;
        if (state.hull > 100) state.hull = 100;
    }
}
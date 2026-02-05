package com.parsecs;

import java.util.Random;

public class eventfactory {
    private static final Random random = new Random();

    public static gameevent createRandomEvent() {
        int eventType = random.nextInt(7);
        
        switch (eventType) {
            case 0: return (state, ui) -> {
                ui.println("");
                ui.println("      ");
                ui.println("  ");
                ui.typewriter("[EVENT] СОБЫТИЕ: Метеоритный дождь!");
                ui.println("Ваши действия?");
                ui.println("1. Использовать Лазерный пистолет (Безопасно)");
                ui.println("2. Маневрировать (Шанс 50% избежать урона)");
                ui.println("3. Принять удар (-15% Корпус)");
                
                ui.print("Выбор: ");
                int choice = ui.readInt();
                
                if (choice == 1) {
                    if (state.hasItem("Лазерный пистолет")) {
                        ui.typewriter("[ATTACK] Пиу-пиу! Метеориты уничтожены.");
                    } else {
                        ui.typewriter("[!] У вас нет пистолета! Пришлось маневрировать...");
                        choice = 2;
                    }
                }
                
                if (choice == 2) {
                    if (random.nextBoolean()) {
                        ui.typewriter("[SUCCESS] Отличный маневр! Урон избежан.");
                    } else {
                        ui.typewriter("[FAIL] Не удалось уклониться. Корпус задет.");
                        state.ship -= 10;
                        ui.println("   Корпус -10%");
                    }
                } else if (choice == 3) {
                    state.ship -= 15;
                    ui.typewriter("[DAMAGE] Удар принят. Корпус -15%");
                }
            };
            case 1: return (state, ui) -> {
                ui.println("");
                ui.println("      .___________.");
                ui.println("     /           /");
                ui.println("    /___________/|");
                ui.println("    |           |/");
                ui.println("    '-----------'");
                ui.typewriter("[EVENT] СОБЫТИЕ: Дрейфующий контейнер.");
                ui.println("1. Открыть (Шанс найти еду или ловушку)");
                ui.println("2. Просканировать (Нужен Учёный Макс)");
                ui.println("3. Игнорировать");
                
                ui.print("Выбор: ");
                int choice = ui.readInt();
                
                if (choice == 1) {
                    if (random.nextInt(100) < 70) {
                        state.food += 20;
                        ui.typewriter("[LOOT] Внутри оказалась еда! +20%");
                    } else {
                        state.ship -= 10;
                        ui.typewriter("[DAMAGE] Это была мина! Корпус -10%");
                    }
                } else if (choice == 2) {
                    if (state.crew.contains("Учёный Макс")) {
                        ui.typewriter("[SCIENCE] Макс определил, что контейнер безопасен.");
                        state.food += 20;
                        ui.println("   Еда +20%");
                    } else {
                        ui.typewriter("[!] У вас нет Учёного Макса.");
                    }
                } else {
                    ui.println("Вы пролетели мимо.");
                }
            };
            case 2: return (state, ui) -> {
                 ui.typewriter("[EVENT] СОБЫТИЕ: Торговец из глубокого космоса.");
                 ui.println("Он предлагает обменять 20% Еды на починку корпуса (+15%).");
                 ui.println("1. Согласиться");
                 ui.println("2. Отказаться");
                 
                 ui.print("Выбор: ");
                 int choice = ui.readInt();
                 
                 if (choice == 1) {
                     if (state.food >= 20) {
                         state.food -= 20;
                         state.ship += 15;
                         ui.typewriter("[TRADE] Сделка совершена.");
                     } else {
                         ui.typewriter("[!] Недостаточно еды для обмена.");
                     }
                 } else {
                     ui.println("Вы прервали связь.");
                 }
            };
            case 3: return (state, ui) -> {
                ui.typewriter("[EVENT] СОБЫТИЕ: Космическая пыль на фильтрах.");
                ui.println("Нужно очистить фильтры вручную.");
                ui.println("Напишите слово 'ЧИСТКА' быстро!");
                
                long start = System.currentTimeMillis();
                ui.print("Ввод: ");
                String input = ui.readString();
                long end = System.currentTimeMillis();
                
                if (input.equalsIgnoreCase("ЧИСТКА") && (end - start) < 4000) {
                    ui.typewriter("[SUCCESS] Фильтры чисты. Кислород в норме.");
                } else {
                    ui.typewriter("[FAIL] Слишком медленно или ошибка! Фильтры забились.");
                    state.oxygen -= 10;
                    ui.println("   Кислород -10%");
                }
            };
            case 4: return (state, ui) -> {
                ui.println("");
                ui.println("      .-.");
                ui.println("     (o.o)");
                ui.println("      |=|");
                ui.println("     '---'");
                ui.typewriter("[EVENT] СОБЫТИЕ: Член экипажа заболел!");
                ui.println("1. Использовать Аптечку (если есть)");
                ui.println("2. Народная медицина (Шанс 50/50, тратит 10 еды)");
                ui.println("3. Ничего не делать");
                
                ui.print("Выбор: ");
                int choice = ui.readInt();
                
                if (choice == 1) {
                    if (state.hasItem("Аптечка")) {
                        ui.typewriter("[HEAL] Аптечка использована. Экипаж здоров.");
                        state.removeItem("Аптечка");
                    } else {
                        ui.typewriter("[!] У вас нет аптечки!");
                        choice = 3; 
                    }
                } 
                
                if (choice == 2) {
                    if (state.food >= 10) {
                        state.food -= 10;
                        if (random.nextBoolean()) {
                            ui.typewriter("[FOOD] Горячий суп помог! Экипаж здоров.");
                        } else {
                            ui.typewriter("[DEATH] Не помогло. Член экипажа погиб.");
                            if (!state.crew.isEmpty()) state.crew.remove(0);
                        }
                    } else {
                        ui.typewriter("[!] Недостаточно еды.");
                        choice = 3;
                    }
                }
                
                if (choice == 3) {
                    ui.typewriter("[DEATH] Болезнь взяла свое. Член экипажа погиб.");
                    if (!state.crew.isEmpty()) state.crew.remove(0);
                }
            };
            case 5: return (state, ui) -> {
                ui.println("");
                ui.println("      / \\");
                ui.println("     / ! \\");
                ui.println("    '-----'");
                ui.typewriter("[WARNING] КРИТИЧЕСКИЙ СБОЙ! Навигационный компьютер завис!");
                int a = random.nextInt(40) + 10;
                int b = random.nextInt(40) + 10;
                int sum = a + b;
                ui.println("Решите уравнение для перезагрузки (у вас 5 секунд): " + a + " + " + b + " = ?");
                
                long startTime = System.currentTimeMillis();
                ui.print("Ваш ответ: ");
                int answer = ui.readInt();
                long endTime = System.currentTimeMillis();
                
                if (answer == sum && (endTime - startTime) <= 5000) {
                    ui.typewriter("[SUCCESS] Система перезагружена! Курс восстановлен.");
                } else {
                    if (answer != sum) ui.typewriter("[FAIL] Ошибка вычислений!");
                    else ui.typewriter("[FAIL] Время вышло!");
                    
                    state.ship -= 30;
                    ui.println("   Корабль потерял управление и задел астероид! Корпус -30%");
                }
            };
            case 6: return (state, ui) -> {
                ui.typewriter("[LOCKED] ЗАБЛОКИРОВАННЫЙ ОТСЕК! Введите код доступа (число от 1 до 3).");
                int code = random.nextInt(3) + 1;
                ui.print("Код: ");
                int guess = ui.readInt();
                
                if (guess == code) {
                    ui.typewriter("[SUCCESS] Доступ разрешен! Найдены запасы.");
                    state.food += 10;
                    ui.println("   Еда +10%");
                } else {
                    ui.typewriter("[FAIL] Ошибка доступа! Сработала система защиты.");
                    state.oxygen -= 5;
                    ui.println("   Утечка воздуха! Кислород -5%");
                }
            };
            default: return (state, ui) -> {};
        }
    }
}
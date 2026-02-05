package com.parsecs;

import java.util.Random;

public class eventfactory {
    private static final Random random = new Random();

    public static gameevent createRandomEvent() {
        int eventType = random.nextInt(7);
        
        switch (eventType) {
            case 0: return (state, ui) -> {
                ui.println("[EVENT] СОБЫТИЕ: Метеоритный дождь!");
                ui.println("Ваши действия?");
                ui.println("1. Использовать Лазерный пистолет (Безопасно)");
                ui.println("2. Маневрировать (Шанс 50% избежать урона)");
                ui.println("3. Принять удар (-15% Корпус)");
                
                ui.print("Выбор: ");
                int choice = ui.readInt();
                
                if (choice == 1) {
                    if (state.hasItem("Лазерный пистолет")) {
                        ui.println("[ATTACK] Пиу-пиу! Метеориты уничтожены.");
                    } else {
                        ui.println("[!] У вас нет пистолета! Пришлось маневрировать...");
                        choice = 2;
                    }
                }
                
                if (choice == 2) {
                    if (random.nextBoolean()) {
                        ui.println("[SUCCESS] Отличный маневр! Урон избежан.");
                    } else {
                        ui.println("[FAIL] Не удалось уклониться. Корпус задет.");
                        state.ship -= 10;
                        ui.println("   Корпус -10%");
                    }
                } else if (choice == 3) {
                    state.ship -= 15;
                    ui.println("[DAMAGE] Удар принят. Корпус -15%");
                }
            };
            case 1: return (state, ui) -> {
                ui.println("[EVENT] СОБЫТИЕ: Дрейфующий контейнер.");
                ui.println("1. Открыть (Шанс найти еду или ловушку)");
                ui.println("2. Просканировать (Нужен Учёный Макс)");
                ui.println("3. Игнорировать");
                
                ui.print("Выбор: ");
                int choice = ui.readInt();
                
                if (choice == 1) {
                    if (random.nextInt(100) < 70) {
                        state.food += 20;
                        ui.println("[LOOT] Внутри оказалась еда! +20%");
                    } else {
                        state.ship -= 10;
                        ui.println("[DAMAGE] Это была мина! Корпус -10%");
                    }
                } else if (choice == 2) {
                    if (state.crew.contains("Учёный Макс")) {
                        ui.println("[SCIENCE] Макс определил, что контейнер безопасен.");
                        state.food += 20;
                        ui.println("   Еда +20%");
                    } else {
                        ui.println("[!] У вас нет Учёного Макса.");
                    }
                } else {
                    ui.println("Вы пролетели мимо.");
                }
            };
            case 2: return (state, ui) -> {
                 ui.println("[EVENT] СОБЫТИЕ: Торговец из глубокого космоса.");
                 ui.println("Он предлагает обменять 20% Еды на починку корпуса (+15%).");
                 ui.println("1. Согласиться");
                 ui.println("2. Отказаться");
                 
                 ui.print("Выбор: ");
                 int choice = ui.readInt();
                 
                 if (choice == 1) {
                     if (state.food >= 20) {
                         state.food -= 20;
                         state.ship += 15;
                         ui.println("[TRADE] Сделка совершена.");
                     } else {
                         ui.println("[!] Недостаточно еды для обмена.");
                     }
                 } else {
                     ui.println("Вы прервали связь.");
                 }
            };
            case 3: return (state, ui) -> {
                ui.println("[EVENT] СОБЫТИЕ: Космическая пыль на фильтрах.");
                ui.println("Нужно очистить фильтры вручную.");
                ui.println("Напишите слово 'ЧИСТКА' быстро!");
                
                long start = System.currentTimeMillis();
                ui.print("Ввод: ");
                String input = ui.readString();
                long end = System.currentTimeMillis();
                
                if (input.equalsIgnoreCase("ЧИСТКА") && (end - start) < 4000) {
                    ui.println("[SUCCESS] Фильтры чисты. Кислород в норме.");
                } else {
                    ui.println("[FAIL] Слишком медленно или ошибка! Фильтры забились.");
                    state.oxygen -= 10;
                    ui.println("   Кислород -10%");
                }
            };
            case 4: return (state, ui) -> {
                ui.println("[EVENT] СОБЫТИЕ: Член экипажа заболел!");
                ui.println("1. Использовать Аптечку (если есть)");
                ui.println("2. Народная медицина (Шанс 50/50, тратит 10 еды)");
                ui.println("3. Ничего не делать");
                
                ui.print("Выбор: ");
                int choice = ui.readInt();
                
                if (choice == 1) {
                    if (state.hasItem("Аптечка")) {
                        ui.println("[HEAL] Аптечка использована. Экипаж здоров.");
                        state.removeItem("Аптечка");
                    } else {
                        ui.println("[!] У вас нет аптечки!");
                        choice = 3; 
                    }
                } 
                
                if (choice == 2) {
                    if (state.food >= 10) {
                        state.food -= 10;
                        if (random.nextBoolean()) {
                            ui.println("[FOOD] Горячий суп помог! Экипаж здоров.");
                        } else {
                            ui.println("[DEATH] Не помогло. Член экипажа погиб.");
                            if (!state.crew.isEmpty()) state.crew.remove(0);
                        }
                    } else {
                        ui.println("[!] Недостаточно еды.");
                        choice = 3;
                    }
                }
                
                if (choice == 3) {
                    ui.println("[DEATH] Болезнь взяла свое. Член экипажа погиб.");
                    if (!state.crew.isEmpty()) state.crew.remove(0);
                }
            };
            case 5: return (state, ui) -> {
                ui.println("[WARNING] КРИТИЧЕСКИЙ СБОЙ! Навигационный компьютер завис!");
                int a = random.nextInt(40) + 10;
                int b = random.nextInt(40) + 10;
                int sum = a + b;
                ui.println("Решите уравнение для перезагрузки (у вас 5 секунд): " + a + " + " + b + " = ?");
                
                long startTime = System.currentTimeMillis();
                ui.print("Ваш ответ: ");
                int answer = ui.readInt();
                long endTime = System.currentTimeMillis();
                
                if (answer == sum && (endTime - startTime) <= 5000) {
                    ui.println("[SUCCESS] Система перезагружена! Курс восстановлен.");
                } else {
                    if (answer != sum) ui.println("[FAIL] Ошибка вычислений!");
                    else ui.println("[FAIL] Время вышло!");
                    
                    state.ship -= 10;
                    ui.println("   Корабль потерял управление и задел астероид! Корпус -10%");
                }
            };
            case 6: return (state, ui) -> {
                ui.println("[LOCKED] ЗАБЛОКИРОВАННЫЙ ОТСЕК! Введите код доступа (число от 1 до 3).");
                int code = random.nextInt(3) + 1;
                ui.print("Код: ");
                int guess = ui.readInt();
                
                if (guess == code) {
                    ui.println("[SUCCESS] Доступ разрешен! Найдены запасы.");
                    state.food += 15;
                    ui.println("   Еда +15%");
                } else {
                    ui.println("[FAIL] Ошибка доступа! Сработала система защиты.");
                    state.oxygen -= 5;
                    ui.println("   Утечка воздуха! Кислород -5%");
                }
            };
            default: return (state, ui) -> {};
        }
    }
}
package com.parsecs;

import java.util.Random;

public class eventfactory {
    private static final Random random = new Random();

    public static gameevent createRandomEvent() {
        int eventType = random.nextInt(7);
        
        switch (eventType) {
            case 0: return (state, ui) -> {
                ui.println("📡 СОБЫТИЕ: Метеоритный дождь повредил корпус!");
                state.ship -= 15;
                ui.println("   Корпус повреждён! -15%");
            };
            case 1: return (state, ui) -> {
                ui.println("📡 СОБЫТИЕ: Обнаружен дрейфующий контейнер с припасами!");
                state.food += 20;
                ui.println("   Найдена еда! +20%");
            };
            case 2: return (state, ui) -> ui.println("📡 СОБЫТИЕ: Неизвестный сигнал из глубин космоса...");
            case 3: return (state, ui) -> ui.println("📡 СОБЫТИЕ: Система жизнеобеспечения работает нормально.");
            case 4: return (state, ui) -> {
                ui.println("📡 СОБЫТИЕ: Член экипажа заболел!");
                if (state.hasItem("Аптечка")) {
                    ui.println("   Использована аптечка для лечения.");
                    state.removeItem("Аптечка");
                } else {
                    ui.println("   Нет аптечки! Член экипажа погиб.");
                    if (!state.crew.isEmpty()) state.crew.remove(0);
                }
            };
            case 5: return (state, ui) -> {
                ui.println("⚠️ КРИТИЧЕСКИЙ СБОЙ! Навигационный компьютер завис!");
                int a = random.nextInt(40) + 10;
                int b = random.nextInt(40) + 10;
                int sum = a + b;
                ui.println("Решите уравнение для перезагрузки (у вас 5 секунд): " + a + " + " + b + " = ?");
                
                long startTime = System.currentTimeMillis();
                ui.print("Ваш ответ: ");
                int answer = ui.readInt();
                long endTime = System.currentTimeMillis();
                
                if (answer == sum && (endTime - startTime) <= 5000) {
                    ui.println("✅ Система перезагружена! Курс восстановлен.");
                } else {
                    if (answer != sum) ui.println("❌ Ошибка вычислений!");
                    else ui.println("❌ Время вышло!");
                    
                    state.ship -= 10;
                    ui.println("   Корабль потерял управление и задел астероид! Корпус -10%");
                }
            };
            case 6: return (state, ui) -> {
                ui.println("🔐 ЗАБЛОКИРОВАННЫЙ ОТСЕК! Введите код доступа (число от 1 до 3).");
                int code = random.nextInt(3) + 1;
                ui.print("Код: ");
                int guess = ui.readInt();
                
                if (guess == code) {
                    ui.println("✅ Доступ разрешен! Найдены запасы.");
                    state.food += 15;
                    ui.println("   Еда +15%");
                } else {
                    ui.println("❌ Ошибка доступа! Сработала система защиты.");
                    state.oxygen -= 5;
                    ui.println("   Утечка воздуха! Кислород -5%");
                }
            };
            default: return (state, ui) -> {};
        }
    }
}
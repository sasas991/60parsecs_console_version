package com.parsecs;

import java.util.Random;

public class eventfactory {
    private static final Random random = new Random();

    public static gameevent createRandomEvent() {
        int eventType = random.nextInt(5);
        
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
                if (state.items.contains("Аптечка")) {
                    ui.println("   Использована аптечка для лечения.");
                    state.items.remove("Аптечка");
                } else {
                    ui.println("   Нет аптечки! Член экипажа погиб.");
                    if (!state.crew.isEmpty()) state.crew.remove(0);
                }
            };
            default: return (state, ui) -> {};
        }
    }
}
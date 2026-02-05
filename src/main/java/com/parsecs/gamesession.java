package com.parsecs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.IOException;

public class gamesession {
    private gameui ui;
    private final gamestate state=new gamestate();
    private final Random random=new Random();
    private final gamedatabase db=gamedatabase.getInstance(); 
    
    public void start() {
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        try (Terminal terminal = defaultTerminalFactory.createTerminal()) {
            terminal.enterPrivateMode();
            this.ui = new gameui(terminal);

            ui.printTitle();    
            login();
            ui.println("1. Новая игра");
            ui.println("2. Загрузить игру");
            ui.print("Ваш выбор: ");
            int choice=ui.readInt();
            if (choice==2)
            {
                ui.print("Введите имя сохранения: ");
                String saveName=ui.readString();
                gamestate loadedState=db.loadGame(saveName);
                
                if (loadedState!=null)
                {
                    state.oxygen=loadedState.oxygen;
                    state.food=loadedState.food;
                    state.ship=loadedState.ship;
                    state.day=loadedState.day;
                    state.crew=loadedState.crew;
                    state.items=loadedState.items;
                    state.gameover=loadedState.gameover;
                    state.currentUser = this.state.currentUser; 
                    ui.println("Игра загружена!");
                    ui.pause(1);
                }
                else
                {
                    ui.println("Сохранение не найдено. Начинаем новую игру...");
                    ui.pause(2);
                    nuclearphase();
                }
            }
            else
                {
                nuclearphase();
            }
            
            if (!state.gameover) {
                survivalphase();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void login() {
        while (state.currentUser == null) {
            ui.println("[ СИСТЕМА БЕЗОПАСНОСТИ ]");
            ui.println("1. Вход");
            ui.println("2. Регистрация");
            ui.print("Выберите действие: ");
            int choice = ui.readInt();

            if (choice == 2) {
                ui.print("Придумайте имя пользователя: ");
                String newUsername = ui.readString();
                ui.print("Придумайте пароль: ");
                String newPassword = ui.readString();
                
                if (db.registerUser(newUsername, newPassword)) {
                    ui.println("[OK] Регистрация успешна! Теперь войдите в систему.");
                }
            } else if (choice == 1) {
                ui.print("Имя пользователя: ");
                String username = ui.readString();
                ui.print("Пароль: ");
                String password = ui.readString();

                userauth user = db.authenticate(username, password);
                if (user != null) {
                    state.currentUser = user;
                    ui.println("[OK] Добро пожаловать, " + user.getUsername() + " (" + user.getRole() + ")");
                } else {
                    ui.println("[!] Неверные данные.");
                }
            }
        }
    }

    private void nuclearphase()
    {
        ui.println("[!!!] ТРЕВОГА! ЯДЕРНАЯ АТАКА ЧЕРЕЗ 60 СЕКУНД!");
        ui.println("Быстро собирайте членов экипажа и предметы!\n");
        
        List<String> crewmembers = new ArrayList<>(Arrays.asList(
            "Капитан Джонс", "Инженер Эмили", "Учёный Макс", 
            "Медик Сара", "Солдат Том"
        ));
        
        List<mainitems> a_items = new ArrayList<>(Arrays.asList(
            new mainitems("Аптечка", itemcategory.MEDICAL),
            new mainitems("Суповой порошок", itemcategory.FOOD),
            new mainitems("Атомная батарея", itemcategory.TECHNOLOGY),
            new mainitems("Лазерный пистолет", itemcategory.MILITARY),
            new mainitems("Скафандр", itemcategory.SURVIVAL),
            new mainitems("Радио", itemcategory.TECHNOLOGY)
        ));
        
        int timeleft=60;
        
        while (timeleft>0&&(state.crew.size()<3 || state.items.size()<4))
            {
            ui.println("TIME: Осталось: " + timeleft + " секунд");
            ui.println("Экипаж: " + state.crew.size() + "/3 | Предметы: " + state.items.size() + "/4\n");
            
            if (state.crew.size() < 3 && !crewmembers.isEmpty())
                {
                ui.println("ДОСТУПНЫЙ ЭКИПАЖ:");
                for (int i = 0; i < crewmembers.size(); i++)
                    {
                    ui.println((i + 1) + ". " + crewmembers.get(i));
                }
            }
            
            if (state.items.size() < 4 && !a_items.isEmpty()) 
                {
                ui.println("\nДОСТУПНЫЕ ПРЕДМЕТЫ:");
                for (int i = 0; i < a_items.size(); i++) 
                    {
                    ui.println((i+6)+". "+a_items.get(i));
                }
            }
            
            ui.print("\nВыберите номер (или 0 для завершения): ");
            int choice=ui.readInt();
            if (choice==0) break;
            
            if (choice >=1 && choice <= 5 && state.crew.size()<3)
                {
                int idx=choice - 1;
                if (idx<crewmembers.size())
                    {
                    state.crew.add(crewmembers.get(idx));
                    crewmembers.remove(idx);
                    timeleft -= 8;
                }
            } else if (choice >= 6 && choice <= 11 && state.items.size() <4)
                {
                int idx=choice-6;
                if (idx<a_items.size())
                    {
                    state.items.add(a_items.get(idx));
                    a_items.remove(idx);
                    timeleft -= 5;
                }
            }
            
            ui.clearScreen();
        }
        
        if (state.crew.isEmpty()) {
            ui.println("\n[GAME OVER] Вы не успели взять экипаж! ИГРА ОКОНЧЕНА.");
            state.gameover = true;
        } else {
            ui.println("\n[LAUNCH] Вы успели! Шаттл отправляется в космос!");
            ui.print("Экипаж: ");
            state.crew.forEach(c -> ui.print(c + ", ")); 
            ui.println("");
            ui.println("Предметы: " + state.items); 
            ui.pause(3);
        }
    }
    
    private void survivalphase() {
        ui.println("\n═══════════════════════════════════════");
        ui.println("    НАЧИНАЕТСЯ ФАЗА ВЫЖИВАНИЯ");
        ui.println("═══════════════════════════════════════\n");
        ui.pause(2);
        
        while (!state.gameover && state.day <= 30) {
            ui.clearScreen();
            ui.displayStatus(state);
            
            rand_event();
            
            if (state.gameover) break;
            
            consume_resources();
            decision();
            checkgamestate();
            
            state.day++;
            ui.pause(1);
        }
        
        if (state.day > 30 && !state.gameover) {
            ui.println("\n[VICTORY] ПОБЕДА! Вы выжили 30 дней в космосе!");
            ui.println("Ваш экипаж достиг новой планеты!");
        }
    }
    
    private void rand_event() {
        if (random.nextInt(100) < 50) {
            gameevent event = eventfactory.createRandomEvent();
            event.execute(state, ui);
            
            ui.println("");
            ui.pause(2);
        }
    }
    
    private void consume_resources() {
        state.oxygen -= state.crew.size() * 3;
        state.food -= state.crew.size() * 2;
        
        if (state.oxygen < 0) state.oxygen = 0;
        if (state.food < 0) state.food = 0;
    }
    
    private void decision() {
        ui.println("Выберите действие:");
        ui.println("1. Отдохнуть (восстановить здоровье экипажа)");
        ui.println("2. Починить корабль (восстановить корпус)");
        ui.println("3. Рационировать еду (сохранить еду)");
        ui.println("4. Исследовать космос (шанс найти ресурсы)");
        ui.println("5. Сохранить игру");
        if (state.currentUser.isAdmin()) {
            ui.println("6. [ADMIN] Пополнить все ресурсы");
        }
        
        ui.print("\nВаш выбор: ");
        int choice = ui.readInt();
        
        switch (choice) {
            case 1:
                ui.println("Экипаж отдыхает...");
                break;
            case 2:
                if (state.hasItem("Атомная батарея")) {
                    state.ship += 20;
                    ui.println("Корпус починен! +20%");
                } else {
                    state.ship += 5;
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
            case 6:
                if (state.currentUser.isAdmin()) {
                    state.oxygen = 100;
                    state.food = 100;
                    state.ship = 100;
                    ui.println("[ADMIN] ЧИТ-КОД АКТИВИРОВАН: Ресурсы восстановлены.");
                }
                break;
        }
        
        ui.println("");
    }
    
    private void checkgamestate() {
        if (state.oxygen <= 0) {
            ui.println("\n[GAME OVER] Кислород закончился! Экипаж погиб от удушья.");
            state.gameover = true;
        } else if (state.food <= 0) {
            ui.println("\n[GAME OVER] Еда закончилась! Экипаж умер от голода.");
            state.gameover = true;
        } else if (state.ship <= 0) {
            ui.println("\n[GAME OVER] Корпус разрушен! Корабль развалился в космосе.");
            state.gameover = true;
        } else if (state.crew.isEmpty()) {
            ui.println("\n[GAME OVER] Весь экипаж погиб! Некому управлять кораблём.");
            state.gameover = true;
        }
        
        if (state.oxygen > 100) state.oxygen = 100;
        if (state.food > 100) state.food = 100;
        if (state.ship > 100) state.ship = 100;
    }
}
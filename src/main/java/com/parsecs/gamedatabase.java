package com.parsecs;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class gamedatabase
{
    private static final String URL="jdbc:postgresql://localhost:5432/postgres";
    private static final String USER="postgres";
    private static final String PASSWORD="123";

    public void saveGame(String saveName, gamestate state) {
        String sql = "INSERT INTO game_saves (save_name, oxygen, food, hull, day, crew, items, game_over) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON CONFLICT (save_name) DO UPDATE SET " +
                     "oxygen = EXCLUDED.oxygen, food = EXCLUDED.food, hull = EXCLUDED.hull, " +
                     "day = EXCLUDED.day, crew = EXCLUDED.crew, items = EXCLUDED.items, game_over = EXCLUDED.game_over";

        try (Connection conn=DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt=conn.prepareStatement(sql)) {

            pstmt.setString(1, saveName);
            pstmt.setInt(2, state.oxygen);
            pstmt.setInt(3, state.food);
            pstmt.setInt(4, state.ship);
            pstmt.setInt(5, state.day);
            pstmt.setString(6, String.join(",", state.crew));
            pstmt.setString(7, String.join(",", state.items));
            pstmt.setBoolean(8, state.gameover);

            pstmt.executeUpdate();
            System.out.println("✅ Игра успешно сохранена как '" + saveName + "'");
        } catch (SQLException e) {
            System.out.println("❌ Ошибка сохранения: " + e.getMessage());
        }
    }

    public gamestate loadGame(String saveName)
    {
        String sql = "SELECT * FROM game_saves WHERE save_name = ?";
        gamestate state=null;

        try (Connection conn=DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, saveName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                state=new gamestate();
                state.oxygen=rs.getInt("oxygen");
                state.food=rs.getInt("food");
                state.ship=rs.getInt("hull");
                state.day=rs.getInt("day");
                state.gameover=rs.getBoolean("game_over");

                String crewStr=rs.getString("crew");
                if (crewStr!=null && !crewStr.isEmpty()) {
                    state.crew=new ArrayList<>(Arrays.asList(crewStr.split(",")));
                }

                String itemsStr=rs.getString("items");
                if (itemsStr != null && !itemsStr.isEmpty()) {
                    state.items=new ArrayList<>(Arrays.asList(itemsStr.split(",")));
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Ошибка загрузки: " + e.getMessage());
        }
        return state;
    }
}
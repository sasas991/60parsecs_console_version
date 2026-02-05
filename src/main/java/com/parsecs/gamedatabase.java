package com.parsecs;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class gamedatabase
{
    private static final String URL="jdbc:postgresql://localhost:5432/postgres";
    private static final String USER="postgres";
    private static final String PASSWORD="123";
    
    private static gamedatabase instance;

    private gamedatabase() {} 

    public static synchronized gamedatabase getInstance() {
        if (instance == null) instance = new gamedatabase();
        return instance;
    }

    public void saveGame(String saveName, gamestate state) {
        String saveSql = "INSERT INTO game_saves (save_name, oxygen, food, hull, day, game_over) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON CONFLICT (save_name) DO UPDATE SET " +
                     "oxygen = EXCLUDED.oxygen, food = EXCLUDED.food, hull = EXCLUDED.hull, " +
                     "day = EXCLUDED.day, game_over = EXCLUDED.game_over " +
                     "RETURNING save_id";

        String deleteCrewSql = "DELETE FROM saved_crew WHERE save_id = ?";
        String deleteItemsSql = "DELETE FROM saved_items WHERE save_id = ?";
        String insertCrewSql = "INSERT INTO saved_crew (save_id, crew_member_name) VALUES (?, ?)";
        String insertItemsSql = "INSERT INTO saved_items (save_id, item_name) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(false);

            int saveId;
            try (PreparedStatement pstmt = conn.prepareStatement(saveSql)) {
                pstmt.setString(1, saveName);
                pstmt.setInt(2, state.oxygen);
                pstmt.setInt(3, state.food);
                pstmt.setInt(4, state.ship);
                pstmt.setInt(5, state.day);
                pstmt.setBoolean(6, state.gameover);
                ResultSet rs = pstmt.executeQuery();
                rs.next();
                saveId = rs.getInt(1);
            }

            try (PreparedStatement pstmt = conn.prepareStatement(deleteCrewSql)) {
                pstmt.setInt(1, saveId);
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(deleteItemsSql)) {
                pstmt.setInt(1, saveId);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(insertCrewSql)) {
                for (String crewMember : state.crew) {
                    pstmt.setInt(1, saveId);
                    pstmt.setString(2, crewMember);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(insertItemsSql)) {
                for (String item : state.items) {
                    pstmt.setInt(1, saveId);
                    pstmt.setString(2, item);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit(); 
            System.out.println("✅ Игра успешно сохранена как '" + saveName + "'");

        } catch (SQLException e) {
            System.out.println("❌ Ошибка сохранения: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public gamestate loadGame(String saveName)
    {
        
        String sql = "SELECT " +
                     "    gs.oxygen, gs.food, gs.hull, gs.day, gs.game_over, " +
                     "    array_agg(DISTINCT sc.crew_member_name) FILTER (WHERE sc.crew_member_name IS NOT NULL) as crew_members, " +
                     "    array_agg(DISTINCT si.item_name) FILTER (WHERE si.item_name IS NOT NULL) as item_names " +
                     "FROM " +
                     "    game_saves gs " +
                     "LEFT JOIN saved_crew sc ON gs.save_id = sc.save_id " +
                     "LEFT JOIN saved_items si ON gs.save_id = si.save_id " +
                     "WHERE " +
                     "    gs.save_name = ? " +
                     "GROUP BY " +
                     "    gs.save_id";
                     
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

                Array crewArray = rs.getArray("crew_members");
                if (crewArray != null) {
                    String[] crew = (String[]) crewArray.getArray();
                    state.crew = new ArrayList<>(Arrays.asList(crew));
                }

                Array itemsArray = rs.getArray("item_names");
                if (itemsArray != null) {
                    String[] items = (String[]) itemsArray.getArray();
                    state.items = new ArrayList<>(Arrays.asList(items));
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Ошибка загрузки: " + e.getMessage());
        }
        return state;
    }
}
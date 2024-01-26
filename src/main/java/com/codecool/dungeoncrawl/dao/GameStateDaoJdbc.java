package com.codecool.dungeoncrawl.dao;

import com.codecool.dungeoncrawl.model.GameState;
import com.codecool.dungeoncrawl.model.PlayerModel;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameStateDaoJdbc implements GameStateDao {

    private DataSource dataSource;

    public GameStateDaoJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void add(GameState state) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO game_state (current_map, discovered_maps, saved_at, player_id) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, state.getCurrentMap());
            statement.setString(2, state.getDiscoveredMaps().toString());
            statement.setDate(3, state.getSavedAt());
            statement.setInt(4, state.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(GameState state) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "UPDATE game_state SET current_map = ?, discovered_maps = ?, saved_at = ? WHERE player_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, state.getCurrentMap());
            statement.setString(2, state.getDiscoveredMaps().toString());
            statement.setDate(3, state.getSavedAt());
            statement.setInt(4, state.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameState get(int id) {
        return null;
    }

    @Override
    public List<GameState> getAll() {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM game_state INNER JOIN player ON player.id = game_state.player_id";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            List<GameState> states = new ArrayList<>();

            while (resultSet.next()) {
                int gameStateId = resultSet.getInt(1);
                String currentMap = resultSet.getString(2);
                String discoveredMap = resultSet.getString(3);
                Date savedAt = resultSet.getDate(4);
                int playerId = resultSet.getInt(5);
                String playerName = resultSet.getString(7);
                int hp = resultSet.getInt(8);
                int x = resultSet.getInt(9);
                int y = resultSet.getInt(10);
                String inventory = resultSet.getString(11);
                PlayerModel model = new PlayerModel( playerName, hp, x, y, inventory);
                model.setId(playerId);
                GameState state = new GameState(currentMap, savedAt, model) ;
                state.addDiscoveredMap(discoveredMap);
                states.add(state);
            }
            return states;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}

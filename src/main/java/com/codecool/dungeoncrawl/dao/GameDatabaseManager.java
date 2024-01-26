package com.codecool.dungeoncrawl.dao;

import com.codecool.dungeoncrawl.logic.actors.Player;
import com.codecool.dungeoncrawl.model.GameState;
import com.codecool.dungeoncrawl.model.PlayerModel;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class GameDatabaseManager {
    private PlayerDao playerDao;
    private GameStateDao gameStateDao;

    public void run() {
        try {
            setup();
        } catch (SQLException e) {
            System.err.println("Could not connect to the database.");
        }
    }

    public void setup() throws SQLException {
        DataSource dataSource = connect();
        playerDao = new PlayerDaoJdbc(dataSource);
        gameStateDao = new GameStateDaoJdbc(dataSource);
    }

    public boolean isNameValid(String username) {
        return playerDao.isNameValid(username);
    }

    public void saveGame(String currentMap, Date savedAt, Player player) {
        PlayerModel model = new PlayerModel(player);
        playerDao.add(model);
        GameState state = new GameState(currentMap, savedAt, model);
        state.addDiscoveredMap(player.getDiscoveredMap());
        gameStateDao.add(state);
    }

    public void updateGame(String currentMap, Date savedAt, Player player) {
        PlayerModel playerModel = new PlayerModel(player);
        playerDao.update(playerModel);
        GameState state = new GameState(currentMap, savedAt, playerModel);
        state.addDiscoveredMap(player.getDiscoveredMap());
        gameStateDao.update(state);
    }
    public List<GameState> getSavedStates() {
         return gameStateDao.getAll();
    }

    private DataSource connect() throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        String dbName = System.getenv("PSQL_DB_NAME");
        String user = System.getenv("PSQL_USER_NAME");
        String password = System.getenv("PSQL_PASSWORD");

        dataSource.setDatabaseName(dbName);
        dataSource.setUser(user);
        dataSource.setPassword(password);

        System.out.println("Trying to connect");
        dataSource.getConnection().close();
        System.out.println("Connection ok.");

        return dataSource;
    }
}

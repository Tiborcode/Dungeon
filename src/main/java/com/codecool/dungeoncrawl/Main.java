package com.codecool.dungeoncrawl;

import com.codecool.dungeoncrawl.dao.GameDatabaseManager;
import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.GameMap;
import com.codecool.dungeoncrawl.logic.MapLoader;
import com.codecool.dungeoncrawl.logic.MapWriter;
import com.codecool.dungeoncrawl.logic.items.Item;
import com.codecool.dungeoncrawl.model.GameState;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class Main extends Application {
    GameDatabaseManager DBManager = new GameDatabaseManager();
    GameMap map = MapLoader.loadMap("/map.txt");
    Canvas canvas = new Canvas(
            map.getWidth() * Tiles.TILE_WIDTH,
            map.getHeight() * Tiles.TILE_WIDTH);
    GraphicsContext context = canvas.getGraphicsContext2D();
    Label healthLabel = new Label();
    Button pickButton = new Button("Pick Up");
    Label itemLabel = new Label();
    Label resultLabel = new Label();
    Button playButton = new Button("Play Again");
    Button loadButton = new Button("Load");
    Button exportButton = new Button("Export");
    Button importButton = new Button("Import");
    Image ICON = new Image("logo.png");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(ICON);
        BorderPane borderPane = new BorderPane();
        GridPane ui = getSidebar(borderPane, primaryStage);

        borderPane.setCenter(canvas);
        borderPane.setRight(ui);

        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        refresh(context);

        scene.setOnKeyPressed(KeyEvent -> {
            try {
                onKeyPressed(KeyEvent, primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        primaryStage.setTitle("Dungeon Crawl");
        primaryStage.show();
        borderPane.requestFocus();
    }

    private GridPane getSidebar(BorderPane borderPane, Stage primaryStage) {
        GridPane ui = new GridPane();
        ui.setPrefWidth(200);
        ui.setPadding(new Insets(10));
        ui.setStyle("-fx-background-color: #b2ccb2");

        pickButton.setOnAction(e -> {
            map.addItems();
            refresh(context);
            borderPane.requestFocus();
        });

        loadButton.setOnAction(e-> {
            DBManager.run();
            List<GameState> stateList =  DBManager.getSavedStates();
            showStateWindow(stateList, primaryStage);
            borderPane.requestFocus();
        });

        exportButton.setOnAction(e -> {
            try {
                serialiseGameMap();
                borderPane.requestFocus();
                //jsonifyGameMap();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        importButton.setOnAction(e -> {
            map = getSerialisedGameMap();
            refresh(context);
            borderPane.requestFocus();
        });

        ui.add(loadButton, 0, 0);
        ui.add(getEmptySpace(30.0), 0, 1);
        ui.add(pickButton, 0, 2);
        ui.add(getEmptySpace(30.0), 0, 3);
        ui.add(new Label("Health: "), 0, 4);
        ui.add(healthLabel, 0, 5);
        ui.add(getEmptySpace(30.0), 0, 6);
        ui.add(new Label("Inventory: "), 0, 7);
        ui.add(itemLabel, 0, 8);
        ui.add(resultLabel, 0, 9);
        ui.add(getEmptySpace(30.0), 0, 10);
        ui.add(playButton, 0, 11);
        playButton.setVisible(false);
        ui.add(getEmptySpace(200),0,12);
        ui.add(importButton, 0,13);
        ui.add(getEmptySpace(30.0), 0, 14);
        ui.add(exportButton,0,15);
        return ui;
    }


    private void onKeyPressed(KeyEvent keyEvent, Stage primaryStage) throws Exception {
        switch (keyEvent.getCode()) {
            case UP:
                map.getPlayer().move(0, -1);
                map.moveGhost();
                refresh(context);
                break;
            case DOWN:
                map.getPlayer().move(0, 1);
                map.movePigs();
                map.moveGhost();
                refresh(context);
                break;
            case LEFT:
                map.getPlayer().move(-1, 0);
                map.moveGhost();
                refresh(context);
                break;
            case RIGHT:
                map.getPlayer().move(1, 0);
                map.moveGhost();
                refresh(context);
                break;
            case ENTER:
                if (map.getPlayer().isOnStairs()) {
                    loadNextMap("/map_2.txt", primaryStage);
                } else if (map.getPlayer().isAtExit()) {
                    handleExit(primaryStage);
                }
                break;
            case SPACE:
                if (map.getPlayer().hasWater() && map.getPlayer().isNearFire()) {
                    map.putOutFire();
                    refresh(context);
                }
                break;
            case S:
                if (keyEvent.isControlDown()) showSaveWindow(primaryStage);
                break;
        }
        if (map.getPlayer().getHealth() <= 0 && !map.getPlayer().isAtExit()) {
            endGame(primaryStage, "\n Oops! \n You lost!");
        }
    }


    private void showStateWindow(List<GameState> stateList, Stage primaryStage) {
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Saved games");

        ListView<GameState> listView = new ListView<>();
        stateList.forEach(e->listView.getItems().add(e));

        HBox buttonBox = getStateModalButtons( dialog, listView, primaryStage);

        VBox dialogBox = new VBox(20);
        dialogBox.getChildren().addAll(listView, buttonBox);
        dialogBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogBox, 500, 200);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private HBox getStateModalButtons(Stage dialog, ListView<GameState> listView, Stage primaryStage) {
        Button selectButton = new Button("Select");
        selectButton.setOnAction(e -> {
            GameState selectedState = listView.getSelectionModel().getSelectedItem();
            String currentMap = selectedState.getCurrentMap();
            String[] pathParts = currentMap.split("/");
            String path = "/" + pathParts[pathParts.length-1];
            loadNextMap(path, primaryStage, selectedState);
            dialog.close();
        });
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(selectButton, cancelButton);
        buttonBox.setSpacing(25);
        buttonBox.setAlignment(Pos.CENTER);
        return buttonBox;
    }

    private void showSaveWindow(Stage primaryStage) {
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Saving Game");

        Label inputLabel = new Label("Enter your username:");
        inputLabel.setStyle("-fx-font-weight: bold");
        TextField inputField = new TextField();
        inputField.setMaxWidth(200);

        HBox buttonBox = getSaveModalButtons(inputField, dialog, primaryStage);

        VBox dialogBox = new VBox(20);
        dialogBox.getChildren().addAll(inputLabel, inputField, buttonBox);
        dialogBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogBox, 450, 150);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private HBox getSaveModalButtons(TextField inputField, Stage dialog, Stage primaryStage) {
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            String userName = inputField.getText();
            DBManager.run();
            if (DBManager.isNameValid(userName)) {
                map.getPlayer().setName(userName);
                // "src/main/resources/current/"
                String currentMap = "target/classes/" + userName + ".txt";
                MapWriter.writeMap(map, currentMap);
                Date savedAt = Date.valueOf(LocalDate.now());
                DBManager.saveGame(currentMap, savedAt, map.getPlayer());
                dialog.close();
            } else {
                showWarningWindow(primaryStage, userName, dialog);
            }
        });
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(saveButton, cancelButton);
        buttonBox.setSpacing(25);
        buttonBox.setAlignment(Pos.CENTER);
        return buttonBox;
    }


    private void showWarningWindow(Stage primaryStage, String userName, Stage saveDialog) {
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Warning");

        Label warningLabel = new Label("Would you like to overwrite the already existing state?");
        warningLabel.setStyle("-fx-font-weight: bold");

        HBox buttonBox = getWarningModelButtons(dialog, userName, saveDialog);

        VBox dialogBox = new VBox(20);
        dialogBox.getChildren().addAll(warningLabel, buttonBox);
        dialogBox.setAlignment(Pos.CENTER);
        Scene dialogScene = new Scene(dialogBox, 450, 150);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private HBox getWarningModelButtons(Stage dialog, String userName, Stage saveDialog) {
        Button confirmButton = new Button("Yes");
        confirmButton.setOnAction(e -> {
            map.getPlayer().setName(userName);
            Date savedAt = Date.valueOf(LocalDate.now());
            String currentMap = "target/classes/" + userName + ".txt";
            MapWriter.writeMap(map, currentMap);
            DBManager.run();
            DBManager.updateGame(currentMap, savedAt, map.getPlayer());
            dialog.close();
            saveDialog.close();
        });
        Button cancelButton = new Button("No");
        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(confirmButton, cancelButton);
        buttonBox.setSpacing(25);
        buttonBox.setAlignment(Pos.CENTER);
        return buttonBox;
    }


    private void loadNextMap(String filePath, Stage primaryStage, GameState... state) {
        int health = map.getPlayer().getHealth();
        HashMap<String, List<Item>> items = map.getPlayer().getPlayerItems();

        map = MapLoader.loadMap(filePath);
        canvas = new Canvas(
                map.getWidth() * Tiles.TILE_WIDTH,
                map.getHeight() * Tiles.TILE_WIDTH);
        context = canvas.getGraphicsContext2D();
        start(primaryStage);

        if (filePath.equals("/map_2.txt")) {
            map.getPlayer().setHealth(health);
            map.getPlayer().setPlayerItems(items);
            map.getPlayer().removePlayerKey();
            map.getPlayer().setDiscoveredMap("/map.txt");
        } else if (!filePath.equals("/map.txt")) {
            map.getPlayer().setHealth(state[0].getPlayer().getHp());
            String inventory = state[0].getPlayer().getInventory();
            map.setInventory(inventory);
        }

        pickButton.setVisible(true);
        playButton.setVisible(false);
        resultLabel.setText("");
        refresh(context);
    }

    private void refresh(GraphicsContext context) {
        context.setFill(Color.BLACK);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        int centerX = map.getWidth() / 2;
        int visibilityRadius = 5;

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Cell cell = map.getCell(x, y);
                if (map.getPlayer().hasSurprise()) {
                    drawTileWithRestriction(cell, x, y, centerX, visibilityRadius);
                } else {
                    drawTileWithoutRestriction(cell, x, y);
                }
            }
        }

        healthLabel.setText("" + map.getPlayer().getHealth());
        setLabelStyle(healthLabel, 22, Color.RED);
        itemLabel.setText(map.getPickedItemNames());
        setLabelStyle(itemLabel, 18, Color.DARKGREEN);
    }

    private void drawTileWithoutRestriction(Cell cell, int x, int y) {
        if (cell.getActor() != null) {
            Tiles.drawTile(context, cell.getActor(), x, y);
        } else if (cell.getItem() != null) {
            Tiles.drawTile(context, cell.getItem(), x, y);
        } else {
            Tiles.drawTile(context, cell, x, y);
        }
    }

    private void drawTileWithRestriction(Cell cell, int x, int y, int centerX, int visibilityRadius) {
        if (cell.getActor() != null && cell.getActor().getTileName().equals("player") && vradiusViewTrue(x, y, visibilityRadius)) {
            Tiles.drawTile(context, cell.getActor(), centerX, y);
        } else if (cell.getActor() != null && vradiusViewTrue(x, y, visibilityRadius)) {
            Tiles.drawTile(context, cell.getActor(), shiftCellXPosition(centerX, x), y);
        } else if (cell.getItem() != null && vradiusViewTrue(x, y, visibilityRadius)) {
            Tiles.drawTile(context, cell.getItem(), shiftCellXPosition(centerX, x), y);
        } else if (vradiusViewTrue(x, y, visibilityRadius)) {
            Tiles.drawTile(context, cell, shiftCellXPosition(centerX, x), y);
        }
    }

    private int shiftCellXPosition(int center, int x) {
        //int center = (int)map.getWidth()/2;
        return center - map.getPlayer().getX() + x;
    }

    private boolean vradiusViewTrue(int x, int y, int radius) {
        int playerx = map.getPlayer().getX();
        int playery = map.getPlayer().getY();
        int playerXminusRadius = playerx - radius;
        int playerXplusRadius = playerx + radius;
        int playerYminusRadius = playery - radius;
        int playerYplusRadius = playery + radius;
        return playerXminusRadius < x && x < playerXplusRadius && playerYminusRadius < y && y < playerYplusRadius;

    }

    private void setLabelStyle(Label label, int fontSize, Color color) {
        label.setFont(Font.font(fontSize));
        label.setTextFill(color);
    }

    private Pane getEmptySpace(double height) {
        Pane empty = new Pane();
        empty.setMinHeight(height);
        return empty;
    }

    private void endGame(Stage primaryStage, String message) {
        refresh(context);

        resultLabel.setText(message);
        resultLabel.setStyle("-fx-font-weight: bold");
        setLabelStyle(resultLabel, 20, Color.DARKSLATEBLUE);

        playButton.setVisible(true);
        playButton.setOnAction(e -> {
            try {
                loadNextMap("/map.txt", primaryStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        pickButton.setVisible(false);
        playButton.requestFocus();
    }

    private void handleExit(Stage primaryStage) {
        map.getPlayer().getCell().setActor(null);
        if (map.getPlayer().hasFriend()) {
            endGame(primaryStage, "\n Congrats! \n You won!");
        } else {
            map.getPlayer().setHealth(0);
            endGame(primaryStage, "\n Awful friend! \n You lost!");
        }
    }

    public void serialiseGameMap() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("game_map.ser");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(map);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    public GameMap deserialiseGameMap() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("game_map.ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        GameMap map = (GameMap) objectInputStream.readObject();
        objectInputStream.close();
        return map;
    }

    public GameMap getSerialisedGameMap() {
        try {
            return deserialiseGameMap();
        } catch (IOException e) {
            System.out.println("IOException" + e);
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException" + e);
        }
        return null;
    }

    public void jsonifyGameMap() throws IOException {
        String jsonifiedMap = new Gson().toJson(map);
        FileOutputStream fileOutputStream = new FileOutputStream("game_state.json");
        fileOutputStream.write(jsonifiedMap.getBytes());
    }
}

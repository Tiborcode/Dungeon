package com.codecool.dungeoncrawl.logic;

import com.codecool.dungeoncrawl.logic.actors.Actor;
import com.codecool.dungeoncrawl.logic.actors.Pig;
import com.codecool.dungeoncrawl.logic.actors.Player;
import com.codecool.dungeoncrawl.logic.items.Item;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameMap implements Serializable {
    private int width;
    private int height;
    private Cell[][] cells;

    private Player player;
    private final List<Item> items = new ArrayList<>();
    private final List<Actor> enemies = new ArrayList<>();
    private String inventory;
    private boolean isSaved;

    public GameMap (int width, int height, CellType defaultCellType, boolean isSaved) {
        this.isSaved = isSaved;
        this.width = width;
        this.height = height;
        cells = new Cell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new Cell(this, x, y, defaultCellType);
            }
        }
    }


    public Cell getCell(int x, int y) {
        return cells[x][y];
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setEnemy(Actor actor) {
        if (!actor.getTileName().equals("player")) {
            enemies.add(actor);
        }
    }

    public void setItem(Item item) {
        items.add(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void moveGhost() {
        for (Actor enemy : enemies) {
            if (enemy.getTileName().equals("ghost")) {
                enemy.moveRandomly(this);
            }
        }
    }

    public void movePigs() {
        List<Pig> pigs = new ArrayList<>();
        for (Actor enemy : enemies) {
            if (enemy.getTileName().equals("pig") && getPlayer().hasHeart()) {
                pigs.add((Pig) enemy);
            }
        }
        if (pigs.size() == 2) {
            pigs.get(0).moveToSide("left");
            pigs.get(1).moveToSide("right");
        }
    }

    public void openDoor() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (cells[x][y].getType().equals(CellType.CLOSED_DOOR)) {
                    Cell closedDoor = cells[x][y];
                    closedDoor.setType(CellType.OPEN_DOOR);
                    break;
                }
            }
        }
    }

    public void putOutFire() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (cells[x][y].getType().equals(CellType.FIRE)) {
                    Cell fire = cells[x][y];
                    fire.setType(CellType.GROUND);
                    getPlayer().emptyWater();
                    break;
                }
            }
        }
    }

    public void addItems() {
        Cell playerCell = getPlayer().getCell();
        List<Item> allItems = getItems();
        for (Item item : allItems) {
            Cell itemCell = item.getCell();
            if (itemCell == playerCell) {
                if ((item.getTileName().equals("heart"))) {
                    getPlayer().setHealth(getPlayer().getHealth() + 2);
                    getPlayer().addHeart();
                } else {
                    getPlayer().addItems(item);
                    if (item.getTileName().equals("key")) {
                        openDoor();
                    }
                }
                itemCell.setItem(null);
            }
        }
    }

    public String getPickedItemNames() {
        if (!isSaved) {
            StringBuilder itemNames = new StringBuilder();
            int waterCount = 0;
            HashMap<String, List<Item>> playerItems = player.getPlayerItems();
            for (List<Item> items : playerItems.values()) {
                for (Item item : items) {
                    if (item.getTileName().equals("water")) {
                        waterCount ++;
                    } else {
                        itemNames.append(item.getTileName()).append("\n");
                    }
                }
            }
            if (waterCount > 0) {
                itemNames.append("water (").append(waterCount).append(")").append("\n");
            }
            return itemNames.toString();
        }
        return inventory;
    }
}

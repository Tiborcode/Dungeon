package com.codecool.dungeoncrawl.logic.actors;

import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.CellType;
import com.codecool.dungeoncrawl.logic.items.Item;

import java.util.*;

public class Player extends Actor {

    private int healthPoints = 10;
    private int waterCount = 0;
    private HashMap<String, List<Item>> items = new HashMap<>();
    private boolean hasWeapon = false;
    private boolean hasHeart = false;
    private boolean hasFriend = false;
    private boolean hasSurprise = false;
    private String name;
    private String discoveredMap = "";

    public Player(Cell cell) {
        super(cell);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiscoveredMap() {
        return discoveredMap;
    }

    public void setDiscoveredMap(String discoveredMap) {
        this.discoveredMap = discoveredMap;
    }

    public String getTileName() {
        return "player";
    }

    public void addItems(Item item) {
        String itemName = item.getTileName();

        switch (itemName) {
            case "sword": hasWeapon = true; break;
            case "friend": hasFriend = true; break;
            case "water": waterCount++; break;
            case "surprise": hasSurprise = true; break;
        }

        if (items.containsKey(itemName)) {
            items.get(itemName).add(item);
        } else {
            List<Item> valueList = new ArrayList<>();
            valueList.add(item);
            items.put(itemName, valueList);
        }
    }

    public void setPlayerItems(HashMap<String, List<Item>> items) {
        this.items = items;
        hasWeapon = items.containsKey("sword");
    }

    public HashMap<String, List<Item>> getPlayerItems() {
        return items;
    }

    public String getInventory() {

        StringBuilder inventory = new StringBuilder();

        for (String item : items.keySet()) {
            int itemCount = items.get(item).size();
            for (int i = 0; i < itemCount; i++) {
                inventory.append(item).append(" ");
            }
        }
        return inventory.toString();
    }

    public void attackEnemy(Skeleton skeleton, Cell playerCell, Cell skeletonCell) {
        skeleton.simulateAttack(hasWeapon, skeletonCell);
        healthPoints = healthPoints - 2;
        if (healthPoints <= 0) {
            playerCell.setActor(null);
        }
    }

    public void weakenHealth() {
        healthPoints = healthPoints-2;
    }

    public void setHealth(int health) {
        this.healthPoints = health;
    }

    @Override
    public int getHealth() {
        return this.healthPoints;
    }

    public void addHeart() {
        hasHeart = true;
    }

    public void removePlayerKey() {
        items.remove("key");
    }

    public void emptyWater() {
        items.remove("water");
    }

    public boolean hasHeart() {
        return hasHeart;
    }

    public boolean hasFriend() {
        return hasFriend;
    }

    public boolean hasWater() {
        return waterCount == 5;
    }

    public boolean hasSurprise() {
        return hasSurprise;
    }

    public boolean isNearFire() {
        return getCell().getNeighbor(0, 1).getType().equals(CellType.FIRE);
    }

    public boolean isAtExit() {
        return getCell().getType().equals(CellType.EXIT);
    }

    public boolean isOnStairs() {
        return getCell().getType().equals(CellType.STAIRS);
    }
}

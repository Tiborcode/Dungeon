package com.codecool.dungeoncrawl.logic.actors;

import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.CellType;
import com.codecool.dungeoncrawl.logic.GameMap;
import com.codecool.dungeoncrawl.logic.items.Item;
import com.codecool.dungeoncrawl.logic.items.Sword;
import com.codecool.dungeoncrawl.logic.items.Water;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    GameMap gameMap = new GameMap(30, 30, CellType.FLOOR, false);


    @Test
    void addItems_pickUpWater_IncreasedWaterNumberWhenPickedUp() {
        Player player = new Player(gameMap.getCell(1,1));
        Water water = new Water(gameMap.getCell(0,1));
        Water water2 = new Water(gameMap.getCell(2,1));
        player.addItems(water);
        player.addItems(water2);

        assertEquals(2, player.getPlayerItems().get("water").size());
    }

    @Test
    void attackEnemy_playerAttacks_playerHealthWeakenedBy2() {
        Player player = new Player(gameMap.getCell(1,1));
        gameMap.setPlayer(player);
        Skeleton skeleton = new Skeleton(gameMap.getCell(2,2));
        gameMap.setEnemy(skeleton);
        Cell skeletonCell = skeleton.getCell();
        Cell playerCell = gameMap.getPlayer().getCell();
        player.attackEnemy(skeleton, playerCell, skeletonCell);


         assertEquals(8, player.getHealth());

    }

    @Test
    void attackEnemy_playerAttacksWithoutSword_skeletonHealthWeakenedBy2() {
        Player player = new Player(gameMap.getCell(1,1));
        gameMap.setPlayer(player);
        Skeleton skeleton = new Skeleton(gameMap.getCell(2,2));
        Cell skeletonCell = skeleton.getCell();
        Cell playerCell = gameMap.getPlayer().getCell();
        player.attackEnemy(skeleton, skeletonCell, playerCell);

        assertEquals(3, skeleton.getHealthPoints());

    }

    @Test
    void attackEnemy_playerAttacksWithSword_skeletonIsNull() {
        Player player = new Player(gameMap.getCell(1,1));
        gameMap.setPlayer(player);
        Skeleton skeleton = new Skeleton(gameMap.getCell(2,2));
        Cell skeletonCell = skeleton.getCell();
        Cell playerCell = gameMap.getPlayer().getCell();
        Sword sword = new Sword(gameMap.getCell(2,1));
        player.addItems(sword);
        player.attackEnemy(skeleton, playerCell, skeletonCell);

        assertNull(skeletonCell.getActor());

    }


    @Test
    void addHeart() {
    }
}
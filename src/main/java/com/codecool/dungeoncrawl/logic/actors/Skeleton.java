package com.codecool.dungeoncrawl.logic.actors;

import com.codecool.dungeoncrawl.logic.Cell;

public class Skeleton extends Actor {

    private int healthPoints = 8;

    public Skeleton(Cell cell) {
        super(cell);
    }

    @Override
    public String getTileName() {
        return "skeleton";
    }

    public void simulateAttack(boolean hasWeapon, Cell skeletonCell) {
        healthPoints = hasWeapon ? healthPoints - 8 : healthPoints - 5;
        if (healthPoints <= 0) {
            skeletonCell.setActor(null);
        }
    }

    public int getHealthPoints (){
        return healthPoints;
    }
}

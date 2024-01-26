package com.codecool.dungeoncrawl.logic.actors;

import com.codecool.dungeoncrawl.logic.Cell;

public class Pig extends Actor {

    public Pig(Cell cell) {
        super(cell);
    }

    @Override
    public String getTileName() {
        return "pig";
    }

    public void attackPlayer(Cell playerCell) {
        if (playerCell.getActor().getTileName().equals("player")) {
            Player player = (Player) playerCell.getActor();
            player.setHealth(0);
            player.getCell().setActor(null);
        }
    }
}

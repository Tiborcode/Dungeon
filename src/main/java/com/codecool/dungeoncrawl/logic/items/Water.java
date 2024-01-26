package com.codecool.dungeoncrawl.logic.items;

import com.codecool.dungeoncrawl.logic.Cell;

public class Water extends Item {

    public Water(Cell cell) {
        super(cell);
    }

    @Override
    public String getTileName() {
        return "water";
    }
}

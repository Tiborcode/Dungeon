package com.codecool.dungeoncrawl.logic.items;

import com.codecool.dungeoncrawl.logic.Cell;

public class Surprise extends Item {

    public Surprise(Cell cell) {
        super(cell);
    }

    @Override
    public String getTileName() {
        return "surprise";
    }
}

package com.codecool.dungeoncrawl.logic.items;

import com.codecool.dungeoncrawl.logic.Cell;

public class Friend extends Item{

    public Friend(Cell cell) {
        super(cell);
    }

    @Override
    public String getTileName() {
        return "friend";
    }
}

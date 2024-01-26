package com.codecool.dungeoncrawl.logic.items;

import com.codecool.dungeoncrawl.logic.Cell;

public class Heart extends Item{

    public Heart(Cell cell) {
        super(cell);
    }

    @Override
    public String getTileName() {
        return "heart";
    }
}

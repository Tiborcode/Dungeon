package com.codecool.dungeoncrawl.logic;

public enum CellType {
    EMPTY("empty"),
    FLOOR("floor"),
    WALL("wall"),
    FENCE("fence"),
    FIRE("fire"),
    GROUND("ground"),
    CLOSED_DOOR("closed_door"),
    OPEN_DOOR("open_door"),
    STAIRS("stairs"),
    EXIT("exit");

    private final String tileName;

    CellType(String tileName) {
        this.tileName = tileName;
    }

    public String getTileName() {
        return tileName;
    }
}

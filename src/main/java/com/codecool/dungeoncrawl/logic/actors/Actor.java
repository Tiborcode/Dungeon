package com.codecool.dungeoncrawl.logic.actors;

import com.codecool.dungeoncrawl.logic.*;

import java.io.Serializable;

public abstract class Actor implements Drawable, Serializable {
    private Cell cell;
    private int health = 10;

    public Actor(Cell cell) {
        this.cell = cell;
        this.cell.setActor(this);
    }

    public void move(int dx, int dy) {
        Cell nextCell = cell.getNeighbor(dx, dy);
        boolean isValidCellType = isValidCellType(nextCell);

        if (isValidCellType && nextCell.getActor() == null) {
            cell.setActor(null);
            nextCell.setActor(this);
            cell = nextCell;
            if (isWaterCell() || isFriendCell()) {
                pickUpItem();
            }
        } else if (isValidCellType && isSkeletonNear(nextCell)) {
            Skeleton skeleton = (Skeleton) nextCell.getActor();
            Player player = (Player) cell.getActor();
            player.attackEnemy(skeleton, cell, nextCell);
        }
    }

    private boolean isValidCellType(Cell nextCell) {
        boolean isValidCellType;
        try {
            isValidCellType = nextCell.getType().equals(CellType.FLOOR) || nextCell.getType().equals(CellType.OPEN_DOOR) ||
                    nextCell.getType().equals(CellType.STAIRS) || nextCell.getType().equals(CellType.GROUND) || nextCell.getType().equals(CellType.EXIT);
        } catch (NullPointerException e) {
            isValidCellType = false;
        }
        return isValidCellType;
    }

    private void pickUpItem() {
        Player player = (Player) cell.getActor();
        player.addItems(cell.getItem());
        cell.setItem(null);
    }

    private boolean isWaterCell() {
        return cell.getItem() != null && cell.getItem().getTileName().equals("water");
    }

    private boolean isFriendCell() {
        return cell.getItem() != null && cell.getItem().getTileName().equals("friend");
    }

    private boolean isSkeletonNear (Cell nextCell) {
        return nextCell.getActor() != null && nextCell.getActor().getTileName().equals("skeleton");
    }

    public void moveToSide(String direction) {
        Cell nextCell = direction.equals("left") ? cell.getNeighbor(1, 0) : cell.getNeighbor(-1, 0);
        if (nextCell.getType().equals(CellType.FLOOR)) {
            if (nextCell.getActor() != null && nextCell.getActor().getTileName().equals("player")) {
                Pig pig = (Pig) cell.getActor();
                pig.attackPlayer(nextCell);
            }
            cell.setActor(null);
            nextCell.setActor(this);
            cell = nextCell;
        }
    }

    public void moveRandomly(GameMap map) {
        Cell randomCell = getRandomCell(map, 10, 6);
        while (randomCell.getActor() != null && this.getCell() != randomCell) {
            if (randomCell.getActor().getTileName().equals("player")) {
                Player player = (Player) randomCell.getActor();
                player.weakenHealth();
                break;
            } else {
                randomCell = getRandomCell(map, 10, 6);
            }
        }
        randomCell.setActor(this);
        this.getCell().setActor(null);
        this.setCell(randomCell);
    }

    private Cell getRandomCell(GameMap map, int width, int height) {
        int x = (int) Math.round(Math.random() * width + 1);
        int y = (int) Math.round(Math.random() * height + 1);
        return map.getCell(x, y);
    }

    public int getHealth() {
        return health;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public Cell getCell() {
        return cell;
    }

    public int getX() {
        return cell.getX();
    }

    public int getY() {
        return cell.getY();
    }
}

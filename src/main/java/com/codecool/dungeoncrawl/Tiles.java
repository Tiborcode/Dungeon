package com.codecool.dungeoncrawl;

import com.codecool.dungeoncrawl.logic.Drawable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class Tiles {
    public static int TILE_WIDTH = 32;

    private static Image tileset = new Image("/tiles.png", 543 * 2, 543 * 2, true, false);
    private static Map<String, Tile> tileMap = new HashMap<>();
    public static class Tile {
        public final int x, y, w, h;
        Tile(int i, int j) {
            x = i * (TILE_WIDTH + 2);
            y = j * (TILE_WIDTH + 2);
            w = TILE_WIDTH;
            h = TILE_WIDTH;
        }
    }

    static {
        tileMap.put("empty", new Tile(0, 0));
        tileMap.put("wall", new Tile(10, 17));
        tileMap.put("fence", new Tile(5, 3));
        tileMap.put("fire", new Tile(15, 10));
        tileMap.put("floor", new Tile(2, 0));
        tileMap.put("ground", new Tile(0, 2));
        tileMap.put("closed_door", new Tile(3, 3));
        tileMap.put("open_door", new Tile(4, 3));
        tileMap.put("stairs", new Tile(2, 6));
        tileMap.put("player", new Tile(18, 7));
        tileMap.put("skeleton", new Tile(29, 6));
        tileMap.put("pig", new Tile(29, 7));
        tileMap.put("ghost", new Tile(26, 6));
        tileMap.put("sword", new Tile(0, 30));
        tileMap.put("key", new Tile(16,23));
        tileMap.put("heart", new Tile(23, 22));
        tileMap.put("friend", new Tile(18, 8));
        tileMap.put("water", new Tile(14, 18));
        tileMap.put("exit", new Tile(13, 17));
        tileMap.put("surprise", new Tile(21, 25));
    }

    public static void drawTile(GraphicsContext context, Drawable d, int x, int y) {
        Tile tile = tileMap.get(d.getTileName());
        context.drawImage(tileset, tile.x, tile.y, tile.w, tile.h,
                x * TILE_WIDTH, y * TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
    }
}

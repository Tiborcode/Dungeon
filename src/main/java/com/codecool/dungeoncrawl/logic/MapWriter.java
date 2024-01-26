package com.codecool.dungeoncrawl.logic;

import com.codecool.dungeoncrawl.logic.actors.Actor;
import com.codecool.dungeoncrawl.logic.items.Item;

import java.io.*;

public class MapWriter {

    public static void writeMap(GameMap map, String filePath) {
        try {
            MapWriter.write(map, filePath);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private static void write(GameMap gameMap, String filePath) throws IOException {
        OutputStream os = new FileOutputStream(filePath);

        int width = gameMap.getWidth();
        int height = gameMap.getHeight();
        os.write((width + " " + height + "\n").getBytes());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                String tileName = gameMap.getCell(x,y).getTileName();
                Actor actor = gameMap.getCell(x,y).getActor();
                Item item = gameMap.getCell(x,y).getItem();

                if (actor != null) {
                    writeActors(actor.getTileName(), os);
                } else if (item != null) {
                    writeItems(item.getTileName(), os);
                } else {
                    writeTiles(tileName, os);
                }
            }
            os.write('\n');
        }
        os.flush();
        os.close();
    }

    private static void writeActors(String actorName, OutputStream os) throws IOException {
        switch (actorName) {
            case "player": os.write('@'); break;
            case "skeleton": os.write('s'); break;
            case "pig": os.write('p'); break;
            case "ghost": os.write('g'); break;
        }
    }

    private static void writeItems(String itemName, OutputStream os) throws IOException {
        switch (itemName) {
            case "sword": os.write('i'); break;
            case "key": os.write('k'); break;
            case "heart": os.write('h'); break;
            case "friend": os.write('f'); break;
            case "water": os.write('w'); break;
            case "surprise": os.write('?'); break;
        }
    }

    private static void writeTiles(String tileName, OutputStream os) throws IOException {
        switch (tileName){
            case "empty": os.write(' '); break;
            case "wall" : os.write('#'); break;
            case "fence": os.write('+'); break;
            case "fire": os.write('x'); break;
            case "floor": os.write('.'); break;
            case "ground" : os.write('\''); break;
            case "closed_door": os.write('-'); break;
            case "open_door": os.write('o'); break;
            case "stairs": os.write('|'); break;
            case "exit": os.write('e'); break;
        }
    }
}

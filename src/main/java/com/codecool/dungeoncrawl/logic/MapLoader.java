package com.codecool.dungeoncrawl.logic;

import com.codecool.dungeoncrawl.logic.actors.Ghost;
import com.codecool.dungeoncrawl.logic.actors.Pig;
import com.codecool.dungeoncrawl.logic.actors.Player;
import com.codecool.dungeoncrawl.logic.actors.Skeleton;
import com.codecool.dungeoncrawl.logic.items.*;

import java.io.InputStream;
import java.util.Scanner;

public class MapLoader {
    public static GameMap loadMap(String filePath) {
        InputStream is = MapLoader.class.getResourceAsStream(filePath);
        Scanner scanner = new Scanner(is);
        int width = scanner.nextInt();
        int height = scanner.nextInt();

        scanner.nextLine(); // empty line

        GameMap map = filePath.equals("/map.txt") || filePath.equals("/map_2.txt") ?
                new GameMap(width, height, CellType.EMPTY, false) : new GameMap(width, height, CellType.EMPTY, true);

        for (int y = 0; y < height; y++) {
            String line = scanner.nextLine();
            for (int x = 0; x < width; x++) {
                if (x < line.length()) {
                    Cell cell = map.getCell(x, y);
                    switch (line.charAt(x)) {
                        case ' ':
                            cell.setType(CellType.EMPTY);
                            break;
                        case '#':
                            cell.setType(CellType.WALL);
                            break;
                        case '+':
                            cell.setType(CellType.FENCE);
                            break;
                        case 'x':
                            cell.setType(CellType.FIRE);
                            break;
                        case '-':
                            cell.setType(CellType.CLOSED_DOOR);
                            break;
                        case 'o':
                            cell.setType(CellType.OPEN_DOOR);
                            break;
                        case 'e':
                            cell.setType(CellType.EXIT);
                            break;
                        case '|':
                            cell.setType(CellType.STAIRS);
                            break;
                        case '.':
                            cell.setType(CellType.FLOOR);
                            break;
                        case '\'':
                            cell.setType(CellType.GROUND);
                            break;
                        case 's':
                            cell.setType(CellType.FLOOR);
                            map.setEnemy(new Skeleton(cell));
                            break;
                        case 'p':
                            cell.setType(CellType.FLOOR);
                            map.setEnemy(new Pig(cell));
                            break;
                        case 'g':
                            cell.setType(CellType.FLOOR);
                            map.setEnemy(new Ghost(cell));
                            break;
                        case '@':
                            cell.setType(CellType.FLOOR);
                            map.setPlayer(new Player(cell));
                            break;
                        case 'i':
                            cell.setType(CellType.FLOOR);
                            map.setItem(new Sword(cell));
                            break;
                        case 'k':
                            cell.setType(CellType.FLOOR);
                            map.setItem(new Key(cell));
                            break;
                        case 'h':
                            cell.setType(CellType.FLOOR);
                            map.setItem(new Heart(cell));
                            break;
                        case 'f':
                            cell.setType(CellType.GROUND);
                            map.setItem(new Friend(cell));
                            break;
                        case 'w':
                            cell.setType(CellType.GROUND);
                            map.setItem(new Water(cell));
                            break;
                        case '?':
                            cell.setType(CellType.GROUND);
                            map.setItem(new Surprise(cell));
                            break;
                        default:
                            throw new RuntimeException("Unrecognized character: '" + line.charAt(x) + "'");
                    }
                }
            }
        }
        return map;
    }

}

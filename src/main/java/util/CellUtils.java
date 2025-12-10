package util;

import game.Cell;

import java.util.LinkedList;
import java.util.TreeMap;

public class CellUtils {
    public static TreeMap<Integer, TreeMap<Integer, Cell>> generateCells(int totalPlayers) {
        // first just generate cells
        TreeMap<Integer, TreeMap<Integer, Cell>> map = new TreeMap<>();
        for (int y = 4; y < 17; y++) {
            for (int x = y - 4; x < 25 - (y - 4); x += 2) {
                map.putIfAbsent(x, new TreeMap<>());
                map.get(x).put(y, new Cell(x, y));
            }
        }

        for (int y = 0; y < 13; y++) {
            for (int x = 12 - y; x < 25 - (12 - y); x += 2) {
                map.get(x).putIfAbsent(y, new Cell(x, y));
            }
        }

        // now set ownership and player
        // player 0 always set
        for (int y = 13; y < 17; y++) {
            for (int x = 9; x < 16; x++) {
                var cell = getCell(map, x, y);
                if (cell != null) {
                    cell.setInitialPlayer(0);
                }
            }
        }

        if (totalPlayers == 2 || totalPlayers == 6) {
            for (int y = 0; y < 4; y++) {
                for (int x = 9; x < 16; x++) {
                    var cell = getCell(map, x, y);
                    if (cell != null) {
                        cell.setInitialPlayer(1);
                    }
                }
            }
        }
        if (totalPlayers == 3 || totalPlayers == 6) {
            for (int y = 4; y < 8; y++) {
                for (int x = y % 2; x < 25; x++) {
                    if (x > 6 - (y - 4) && x < 18 + (y - 4)) {
                        continue;
                    }
                    var cell = getCell(map, x, y);
                    if (cell != null) {
                        cell.setInitialPlayer(totalPlayers == 3 ? (x < 12 ? 1 : 2) : (x < 12 ? 3 : 4));
                    }
                }
            }
        }
        if (totalPlayers == 6) {
            for (int y = 12; y > 8; y--) {
                for (int x = y % 2; x < 25; x++) {
                    if (x > 7 - (12 - y) && x < 17 + (12 - y)) {
                        continue;
                    }
                    var cell = getCell(map, x, y);
                    if (cell != null) {
                        cell.setInitialPlayer(x < 12 ? 5 : 2);
                    }
                }
            }
        }

        return map;
    }

    public static Cell getCell(TreeMap<Integer, TreeMap<Integer, Cell>> map, int x, int y) {
        if (!map.containsKey(x)) {
            return null;
        }

        return map.get(x).getOrDefault(y, null);
    }

    public static LinkedList<Cell> collectCells(TreeMap<Integer, TreeMap<Integer, Cell>> map) {
        var list = new LinkedList<Cell>();
        for (var eOuter : map.entrySet()) {
            for (var e : eOuter.getValue().entrySet()) {
                list.add(e.getValue());
            }
        }

        return list;
    }
}

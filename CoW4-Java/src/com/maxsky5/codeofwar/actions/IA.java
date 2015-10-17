package com.maxsky5.codeofwar.actions;

import com.maxsky5.codeofwar.world.Cell;
import com.maxsky5.codeofwar.world.GameWorld;

import java.util.ArrayList;
import java.util.List;

public class IA {

    public static Cell currentPosition = null;
    public static List<Cell> positions = new ArrayList<>();

    public static List<Order> executeTurn(GameWorld world) {

        Cell cell = world.getMyAI().getCell();

        currentPosition = cell;

        List<Order> orders = new ArrayList<>();
        Cell[][] labyrinth = world.getLabyrinth();

        Cell newCell = getMoveCell(labyrinth, cell);

        orders.add(new MoveOrder(newCell.getId()));

        positions.add(newCell);
        return orders;
    }

    public static Cell getMoveCell(Cell[][] labyrinth, Cell cell) {
        Cell orderCell = null;
        Integer orderLastIndex = null;
        List<Cell> posibleMoves = new ArrayList<>();

        if (!positions.isEmpty()) {
            if (cell.canRight() && !labyrinth[cell.getLine()][cell.getColumn() + 1].getId().equals(positions.get(positions.size() - 1).getId())) {
                posibleMoves.add(labyrinth[cell.getLine()][cell.getColumn() + 1]);
            }
            if (cell.canLeft() && !labyrinth[cell.getLine()][cell.getColumn() - 1].getId().equals(positions.get(positions.size() - 1).getId())) {
                posibleMoves.add(labyrinth[cell.getLine()][cell.getColumn() - 1]);
            }
            if (cell.canTop() && !labyrinth[cell.getLine() - 1][cell.getColumn()].getId().equals(positions.get(positions.size() - 1).getId())) {
                posibleMoves.add(labyrinth[cell.getLine() - 1][cell.getColumn()]);
            }
            if (cell.canBottom() && !labyrinth[cell.getLine() + 1][cell.getColumn()].getId().equals(positions.get(positions.size() - 1).getId())) {
                posibleMoves.add(labyrinth[cell.getLine() + 1][cell.getColumn()]);
            }

            for (Cell o : posibleMoves) {
                if (null == orderCell || posibleMoves.lastIndexOf(o) < orderLastIndex) {
                    orderCell = o;
                    orderLastIndex = posibleMoves.lastIndexOf(o);
                }
            }

            if (orderCell == null && !posibleMoves.isEmpty()) {
                Integer randomMove = new Double(Math.floor(Math.random() * posibleMoves.size())).intValue();
                orderCell = posibleMoves.get(randomMove);
            }

            System.out.println("Line : " + orderCell.getLine() + " --- Column : " + orderCell.getColumn());

        } else {
            if (cell.canRight()) {
                orderCell = labyrinth[cell.getLine()][cell.getColumn() + 1];
            } else if (cell.canLeft()) {
                orderCell = labyrinth[cell.getLine()][cell.getColumn() - 1];
            } else if (cell.canTop()) {
                orderCell = labyrinth[cell.getLine() - 1][cell.getColumn()];
            } else if (cell.canBottom()) {
                orderCell = labyrinth[cell.getLine() + 1][cell.getColumn()];
            }
        }

        if (orderCell == null && !positions.isEmpty()) {
            orderCell = positions.get(positions.size() - 1);
            positions.remove(positions.size() - 1);
        }

        return orderCell;
    }
}

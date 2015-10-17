package com.maxsky5.codeofwar.actions;

import com.maxsky5.codeofwar.world.Cell;
import com.maxsky5.codeofwar.world.GameWorld;

import java.util.ArrayList;
import java.util.List;


public class IA {
    public static List<Order> executeTurn(GameWorld world) {
        List<Order> orders = new ArrayList<>();
        Cell cell = world.getMyAI().getCell();
        Cell[][] labyrinth = world.getLabyrinth();
        int myAiColumn = cell.getColumn();
        int myAiLine = cell.getLine();
        Order order = null;
        if (cell.canRight()) {
            order = new MoveOrder(labyrinth[myAiLine][myAiColumn + 1].getId());
        } else if (cell.canLeft()) {
            order = new MoveOrder(labyrinth[myAiLine][myAiColumn - 1].getId());
        } else if (cell.canTop()) {
            order = new MoveOrder(labyrinth[myAiLine - 1][myAiColumn].getId());
        } else if (cell.canBottom()) {
            order = new MoveOrder(labyrinth[myAiLine + 1][myAiColumn].getId());
        }
        orders.add(order);
        return orders;
    }
}

package com.maxsky5.codeofwar.actions;

import com.maxsky5.codeofwar.utils.AwesomeList;
import com.maxsky5.codeofwar.world.Cell;
import com.maxsky5.codeofwar.world.GameWorld;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class IA {

    public static Cell currentPosition = null;
    public static List<Cell> positions = new ArrayList<>();
    public static Integer turn = 0;

    public static List<Order> executeTurn(GameWorld world) {
        turn++;
        System.out.println("---------------------");
        System.out.println("Tour : " + turn);

        Cell cell = world.getMyAI().getCell();
        Cell newCell;

        currentPosition = cell;

        List<Order> orders = new ArrayList<>();
        Cell[][] labyrinth = world.getLabyrinth();

        List<Cell> itineraryToChicken = itineraryToChicken(world);
        System.out.println("Nb stapes to chicken : " + itineraryToChicken.size());

        if (!CollectionUtils.isEmpty(itineraryToChicken)) {
            newCell = itineraryToChicken.get(0);
        } else {
            newCell = getMoveCell(labyrinth, cell);
        }

        orders.add(new MoveOrder(newCell.getId()));

        positions.add(newCell);
        return orders;
    }

    public static List<Cell> itineraryToChicken(GameWorld world) {
        List<Cell> itineraryToChicken = new AwesomeList<>();
        List<List<Cell>> itineraries = new AwesomeList<>();
        List<List<Cell>> newItineraries = new AwesomeList<>();
        List<Cell> tryedCells = new AwesomeList<>();
        Integer itineraryTurns = 0;

        Cell cell = world.getMyAI().getCell();
        Cell[][] labyrinth = world.getLabyrinth();

        Boolean foundChicken = false;

        tryedCells.add(cell);
        getPosibleMoves(labyrinth, cell).stream()
                .map(c -> {
                    List<Cell> l = new AwesomeList();
                    l.add(c);
                    return l;
                })
                .collect(Collectors.collectingAndThen(Collectors.toList(), itineraries::addAll));
        itineraries.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.collectingAndThen(Collectors.toList(), tryedCells::addAll));

        do {
            itineraryTurns++;

            for (List<Cell> itinerary : itineraries) {
                Cell lastCell = ((AwesomeList<Cell>)itinerary).getLastElement();
                List<Cell> posibleMoves = getPosibleNewMoves(labyrinth, tryedCells, lastCell);
                tryedCells.addAll(posibleMoves);

                for (Cell c : posibleMoves) {
                    List<Cell> newItinerary = itinerary;
                    newItinerary.add(c);
                    newItineraries.add(newItinerary);

                    if (world.getChicken().getCell().equals(c)) {
                        System.out.println("----- FOUND CHICKEN -----");
                        foundChicken = true;
                        itineraryToChicken = newItinerary;
                        break;
                    }
                }
            }

            itineraries = newItineraries;
            newItineraries.clear();
        } while (!foundChicken && itineraryTurns < 200);

        return itineraryToChicken;
    }

    public static List<Cell> getPosibleNewMoves(Cell[][] labyrinth, List<Cell> moves, Cell cell) {
        return getPosibleMoves(labyrinth, cell).stream()
                .filter(c -> moves.indexOf(c) == -1)
                .collect(Collectors.toList());
    }

    public static List<Cell> getPosibleMoves(Cell[][] labyrinth, Cell cell) {
        List<Cell> posibleMoves = new ArrayList<>();

        if (cell.canRight()) {
            posibleMoves.add(labyrinth[cell.getLine()][cell.getColumn() + 1]);
        }
        if (cell.canLeft()) {
            posibleMoves.add(labyrinth[cell.getLine()][cell.getColumn() - 1]);
        }
        if (cell.canTop()) {
            posibleMoves.add(labyrinth[cell.getLine() - 1][cell.getColumn()]);
        }
        if (cell.canBottom()) {
            posibleMoves.add(labyrinth[cell.getLine() + 1][cell.getColumn()]);
        }

        return posibleMoves;
    }

    public static Cell getMoveCell(Cell[][] labyrinth, Cell cell) {
        Cell orderCell = null;
        Integer orderLastIndex = 0;
        List<Cell> posibleMoves = new ArrayList<>();
        System.out.println("Cell : " + cell);

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
                System.out.println("Last Index : " + positions.lastIndexOf(o));
                if (null == orderCell || positions.lastIndexOf(o) < orderLastIndex) {
                    orderCell = o;
                    orderLastIndex = positions.lastIndexOf(o);
                }
            }

            if (orderCell == null && !posibleMoves.isEmpty()) {
                Integer randomMove = new Double(Math.floor(Math.random() * posibleMoves.size())).intValue();
                orderCell = posibleMoves.get(randomMove);
            }

            System.out.println("Posible Moves : " + posibleMoves.size());
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

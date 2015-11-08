package com.maxsky5.codeofwar.actions;

import com.maxsky5.codeofwar.utils.AwesomeList;
import com.maxsky5.codeofwar.world.Cell;
import com.maxsky5.codeofwar.world.GameWorld;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class IA {

    public static List<Cell> positions = new AwesomeList<>();
    public static List<Cell> chickenPositions = new AwesomeList<>();

    public static List<Order> executeTurn(GameWorld world) {
        System.out.println("---------------------");
        System.out.println("Tour : " + world.getGameTurn());
        System.out.println("Nb saved move : " + world.getMyAI().getMouvementPoints());

        Cell cell = world.getMyAI().getCell();
        Cell newCell;

        System.out.println("My Position : " + cell.getLine() + " / " + cell.getColumn());

        List<Order> orders = new ArrayList<>();

        if (null != cell.getItem()) {
            orders.add(new PickUpOrder());
        }

        chickenPositions.add(world.getChicken().getCell());

        if (world.getMyAI().getMouvementPoints() >= 5) {
            List<Cell> itineraryToChicken = getMyItineraryToChicken(world, world.getEnnemyAI().getCell());
            List<Cell> enemyItineraryToChicken = getMyEnemyItineraryToChicken(world, null);
            System.out.println("Stapes 1 to chicken : " + itineraryToChicken.size());
            System.out.println("Stapes 2 to chicken : " + enemyItineraryToChicken.size());

            if (itineraryToChicken.size() <= world.getMyAI().getMouvementPoints() && !itineraryToChicken.contains(world.getEnnemyAI().getCell())) {
                orders.addAll(itineraryToChicken.stream().map(c -> new MoveOrder(c.getId())).collect(Collectors.toList()));
                positions.addAll(itineraryToChicken);
                System.out.println("Catched !!");
            } else {
                Cell chickenTargetCell = getNextChickenTargetCell(world);

                if (null == chickenTargetCell) {
                    System.out.println("Chicken Target : NULL");
                } else {
                    System.out.println("Chicken Target : " + chickenTargetCell.getLine() + " / " + chickenTargetCell.getColumn());
                }

                if (enemyItineraryToChicken.size() < itineraryToChicken.size() && null != chickenTargetCell && !isOnChickenWay(world, chickenTargetCell)) {
                    List<Cell> itineraryToChickenBis = getItinerary(world.getLabyrinth(), cell, chickenTargetCell, Arrays.asList(world.getEnnemyAI().getCell()));

                    if (!CollectionUtils.isEmpty(itineraryToChickenBis)) {
                        System.out.println("----- Itinerary Bis -----");
                        newCell = itineraryToChickenBis.get(0);
                    } else {
                        System.out.println("----- Itinerary Bis Fail -----");
                        newCell = itineraryToChicken.get(0);
                    }
                } else {
                    newCell = itineraryToChicken.get(0);
                }

                orders.add(new MoveOrder(newCell.getId()));
                positions.add(newCell);
            }
        }

        return orders;
    }

    public static List<Cell> getMyItineraryToChicken(GameWorld world, Cell escape) {
        return getItinerary(world.getLabyrinth(), world.getMyAI().getCell(), world.getChicken().getCell(), Arrays.asList(escape));
    }

    public static List<Cell> getMyEnemyItineraryToChicken(GameWorld world, Cell escape) {
        return getItinerary(world.getLabyrinth(), world.getEnnemyAI().getCell(), world.getChicken().getCell(), Arrays.asList(escape));
    }

    public static List<Cell> getItinerary(Cell[][] labyrinth, Cell from, Cell to, List<Cell> escape) {
        List<Cell> itineraryToTarget = new AwesomeList<>();
        List<List<Cell>> itineraries = new AwesomeList<>();
        List<List<Cell>> newItineraries = new AwesomeList<>();
        List<Cell> tryedCells = new AwesomeList<>();
        Integer itineraryTurns = 0;

        Boolean foundTarget = false;

        tryedCells.add(from);
        getPosibleMoves(labyrinth, from).stream()
                .map(c -> {
                    List<Cell> l = new AwesomeList<>();
                    l.add(c);
                    return l;
                })
                .collect(Collectors.collectingAndThen(Collectors.toList(), itineraries::addAll));
        itineraries.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.collectingAndThen(Collectors.toList(), tryedCells::addAll));

        do {
            itineraryTurns++;

            itinerariesLoop:
            for (List<Cell> itinerary : itineraries) {
                Cell lastCell = ((AwesomeList<Cell>) itinerary).getLastElement();
                List<Cell> posibleMoves = getPosibleNewMoves(labyrinth, tryedCells, lastCell);
                tryedCells.addAll(posibleMoves);

                for (Cell c : posibleMoves) {
                    if (null == escape || !escape.contains(c)) {
                        List<Cell> newItinerary = new AwesomeList<>();
                        newItinerary.addAll(itinerary);
                        newItinerary.add(c);
                        newItineraries.add(newItinerary);

                        if (to.equals(c)) {
                            System.out.println("----- FOUND -----");
                            foundTarget = true;
                            itineraryToTarget = newItinerary;
                            break itinerariesLoop;
                        }
                    }
                }
            }

            itineraries.clear();
            newItineraries.stream()
                    .collect(Collectors.collectingAndThen(Collectors.toList(), itineraries::addAll));
            newItineraries.clear();
        } while (!foundTarget && itineraryTurns < 200);

        return itineraryToTarget;
    }

    public static Cell getNextChickenTargetCell(GameWorld world) {
        if (world.getChicken().getCell().isIntersection() || chickenPositions.size() < 2) {
            return null;
        }

        Cell[][] labyrinth = world.getLabyrinth();
        Boolean foundIntersection = false;
        Cell previousCell = chickenPositions.get(chickenPositions.size() - 2);
        Cell cell = chickenPositions.get(chickenPositions.size() - 1);
        Integer nbTry = 0;
        Integer nbMaxTry = 50;

        do {
            nbTry++;
            List<Cell> posibleMoves = new AwesomeList<>();
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

            for (Cell c : posibleMoves) {
                if (!c.equals(previousCell)) {
                    previousCell = cell;
                    cell = c;

                    if (cell.isIntersection()) {
                        foundIntersection = true;
                    }

                    break;
                }
            }

        } while (!foundIntersection && nbTry < nbMaxTry);

        return foundIntersection ? cell : null;
    }

    public static Boolean isOnChickenWay(GameWorld world, Cell chickenTarget) {
        List<Cell> chickenItinerary = getItinerary(world.getLabyrinth(), world.getChicken().getCell(), chickenTarget, null);

        return !CollectionUtils.isEmpty(chickenItinerary) && chickenItinerary.contains(world.getMyAI().getCell());
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

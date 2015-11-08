package com.maxsky5.codeofwar.actions;

import com.maxsky5.codeofwar.utils.AwesomeList;
import com.maxsky5.codeofwar.world.Cell;
import com.maxsky5.codeofwar.world.GameWorld;
import com.maxsky5.codeofwar.world.Item;
import com.maxsky5.codeofwar.world.ItemType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class IA {

    public static List<Cell> positions = new AwesomeList<>();
    public static List<Cell> chickenPositions = new AwesomeList<>();
    public static Boolean foundPotion = false;
    public static List<Cell> itineraryToPotion = null;

    private static void initializeGame() {
        positions.clear();
        chickenPositions.clear();
        foundPotion = false;
        itineraryToPotion = null;
    }

    public static List<Order> executeTurn(GameWorld world) {
        if (world.getGameTurn() == 0) {
            initializeGame();
        }

        System.out.println("---------------------");
        System.out.println("Tour : " + world.getGameTurn());
        System.out.println("Nb saved move : " + world.getMyAI().getMouvementPoints());

        Cell cell = world.getMyAI().getCell();
        Cell newCell;

        System.out.println("My Position : " + cell.getLine() + " / " + cell.getColumn());

        List<Order> orders = new ArrayList<>();

        if (null != cell.getItem()) {
            orders.add(new PickUpOrder());

            if (cell.getItem().getType().equals(ItemType.InvisibilityPotion)) {
                foundPotion = true;
                itineraryToPotion = null;
            }
        }

        if (!CollectionUtils.isEmpty(world.getMyAI().getItems()) && world.getMyAI().getItems().contains(new Item(ItemType.Trap))) {
            orders.add(new UseItemOrder(new Item(ItemType.Trap)));
        }

        chickenPositions.add(world.getChicken().getCell());

        if (world.getMyAI().getMouvementPoints() < 5) {

        } else if (foundPotion) {
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
                List<Cell> itineraryToChickenTarget = getItinerary(world.getLabyrinth(), cell, chickenTargetCell, Arrays.asList(world.getEnnemyAI().getCell()));
                List<Cell> chickenItineraryToTarget = getItinerary(world.getLabyrinth(), world.getChicken().getCell(), chickenTargetCell, Arrays.asList(world.getEnnemyAI().getCell()));

                if (canUsePotion(world, chickenItineraryToTarget, itineraryToChicken, itineraryToChickenTarget)) {
                    System.out.println("Invisible !!!!!");
                    orders.add(new UseItemOrder(new Item(ItemType.InvisibilityPotion)));
                }

                if (enemyItineraryToChicken.size() < itineraryToChicken.size() && null != chickenTargetCell && !isOnChickenWay(world, chickenTargetCell)) {

                    if (!CollectionUtils.isEmpty(itineraryToChickenTarget)) {
                        newCell = itineraryToChickenTarget.get(0);
                    } else {
                        newCell = itineraryToChicken.get(0);
                    }
                } else {
                    newCell = itineraryToChicken.get(0);
                }

                orders.add(new MoveOrder(newCell.getId()));
                positions.add(newCell);
            }
        } else {
            List<Cell> cellsWithItems = world.getCellsWithItems();

            if (null == itineraryToPotion) {
                Cell invisibilityPotion = cellsWithItems.stream()
                    .filter(c -> c.getItem().getType().equals(ItemType.InvisibilityPotion))
                    .map(c -> getItinerary(world.getLabyrinth(), world.getMyAI().getCell(), c, null))
                    .min((i1, i2) -> Integer.compare(i1.size(), i2.size()))
                    .map(i -> i.get(i.size() - 1))
                    .get();
                itineraryToPotion = getItinerary(world.getLabyrinth(), cell, invisibilityPotion, null);
            }

            newCell = itineraryToPotion.get(0);
            orders.add(new MoveOrder(newCell.getId()));
            positions.add(newCell);

            itineraryToPotion.remove(0);
        }

        return orders;
    }

    public static Boolean canUsePotion(GameWorld world, List<Cell> chickenItinerary, List<Cell> itineraryToChicken, List<Cell> itineraryToChickenTarget) {
        if (!world.getMyAI().getItems().contains(new Item(ItemType.InvisibilityPotion))) {
            return false;
        }

        if (world.getMyAI().getInvisibilityDuration() > 2) {
            return false;
        }

        Integer nbPotions = (int) world.getMyAI().getItems().stream()
            .filter(i -> i.equals(new Item(ItemType.InvisibilityPotion)))
            .count();

        if ((CollectionUtils.isEmpty(chickenItinerary) || chickenItinerary.size() <= 2) && itineraryToChicken.size() <= (nbPotions * 10 + 5)) {
            return true;
        }

        return false;
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
            return world.getChicken().getCell();
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

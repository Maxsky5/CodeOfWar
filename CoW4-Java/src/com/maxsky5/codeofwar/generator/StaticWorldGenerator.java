package com.maxsky5.codeofwar.generator;

/**
 * Created by Arsenik on 18/08/15.
 */

import com.maxsky5.codeofwar.actions.IA;
import com.maxsky5.codeofwar.socket.SocketManager;
import com.maxsky5.codeofwar.world.DynamicGameWorld;
import com.maxsky5.codeofwar.world.GameWorld;
import com.maxsky5.codeofwar.socket.CharacterSkin;

/**
 * en: This class generates the labyrinth data structure for StaticGameWorld class
 * fr: Cette classe génère la structure du labyrinthe pour la classe StaticGameWorld
 */
public class StaticWorldGenerator {

    public static void main(String[] args) {
        new SocketManager().connectToServer(
                "localhost",
                8127,
                "MyAI",
                "",
                "http://cdn5.coloritou.com/dessins/peindre/2011001/b03d302a8229d1f520dd950b130566be.png",
                CharacterSkin.BARBARIAN,
                IA::executeTurn,
                new DynamicGameWorld());
    }

    private static void printWorld(GameWorld world) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < world.getLabyrinth().length; i++) {
            sb.append("{\n");
            for (int j = 0; j < world.getLabyrinth()[i].length; j++) {
                sb.append("new Cell(");
                sb.append(world.getLabyrinth()[i][j].getId());
                sb.append("L,");
                sb.append(world.getLabyrinth()[i][j].canLeft());
                sb.append(",");
                sb.append(world.getLabyrinth()[i][j].canRight());
                sb.append(",");
                sb.append(world.getLabyrinth()[i][j].canTop());
                sb.append(",");
                sb.append(world.getLabyrinth()[i][j].canBottom());
                sb.append(",");
                sb.append(world.getLabyrinth()[i][j].getLine());
                sb.append(",");
                sb.append(world.getLabyrinth()[i][j].getColumn());
                sb.append(",");
                if (world.getLabyrinth()[i][j].getItem() != null) {
                    String itemTypeStr = "";
                    switch (world.getLabyrinth()[i][j].getItem().getType()) {
                        case InvisibilityPotion:
                            itemTypeStr = "ItemType.InvisibilityPotion";
                            break;
                        case PulletPerfume:
                            itemTypeStr = "ItemType.PulletPerfume";
                            break;
                        case Trap:
                            itemTypeStr = "ItemType.Trap";
                            break;
                    }
                    sb.append("new Item(" + itemTypeStr + ")");
                } else {
                    sb.append("null");
                }
                sb.append(")");
                if (j < (world.getLabyrinth()[i].length - 1)) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("}");
            if (i < (world.getLabyrinth().length - 1))  {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("}\n");
        System.out.flush();
        System.out.print(sb.toString());
    }
}

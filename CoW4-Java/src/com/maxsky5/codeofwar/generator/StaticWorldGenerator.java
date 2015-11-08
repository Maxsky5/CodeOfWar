package com.maxsky5.codeofwar.generator;

/**
 * Created by Arsenik on 18/08/15.
 */

import com.maxsky5.codeofwar.actions.IA;
import com.maxsky5.codeofwar.socket.CharacterSkin;
import com.maxsky5.codeofwar.socket.SocketManager;
import com.maxsky5.codeofwar.world.DynamicGameWorld;
import com.maxsky5.codeofwar.world.GameWorld;

/**
 * en: This class generates the labyrinth data structure for StaticGameWorld class
 * fr: Cette classe génère la structure du labyrinthe pour la classe StaticGameWorld
 */
public class StaticWorldGenerator {

    public static void main(String[] args) {
        new SocketManager().connectToServer(
                "localhost",
//                "qualif.codeofwar.net",
                8127,
                "Maxsky5",
                "45a574d77a65757e78dbaa401900ac0f",
                "http://icons.iconarchive.com/icons/martin-berube/flat-animal/256/chicken-icon.png",
                CharacterSkin.WIZARD,
                IA::executeTurn,
                new DynamicGameWorld());
    }
}

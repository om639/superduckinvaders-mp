/**
 * URL for executable: https://drive.google.com/file/d/0B4to7QAfaHDIeEE0TG4tR0dyRk0/view?usp=sharing
 */

package com.superduckinvaders.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.superduckinvaders.game.SuperDuckInvaders;

/**
 * Desktop launcher for Super Duck Invaders.
 */
public class DesktopLauncher {

    public static final int GAME_WIDTH = 1280;

    public static final int GAME_HEIGHT = 720;

    public static final String GAME_TITLE = "SDI Multiplayer Alpha";

    public static void main(String[] args) {
        String inetHost;
        int inetPort;

        if (args.length >= 1) {
            inetHost = args[0];

            if (args.length >= 2) {
                inetPort = Integer.parseInt(args[1]);
            } else {
                inetPort = 5577;
            }
        } else {
            System.err.println("no hostname specified");
            System.exit(1);

            // Will never be reached.
            return;
        }

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        //config.resizable = false;
        config.width = GAME_WIDTH;
        config.height = GAME_HEIGHT;
        config.title = GAME_TITLE;

        new LwjglApplication(new SuperDuckInvaders(inetHost, inetPort), config);
    }
}
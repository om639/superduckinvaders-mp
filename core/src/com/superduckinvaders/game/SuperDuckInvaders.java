package com.superduckinvaders.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.superduckinvaders.game.assets.Assets;

/**
 * Created by Oliver on 20/02/2016.
 */
public class SuperDuckInvaders extends Game {

    /**
     * The width of the game window.
     */
    public static final int GAME_WIDTH = 1280;

    /**
     * The height of the game window.
     */
    public static final int GAME_HEIGHT = 720;

    /**
     * The game engine responsible for network I/O.
     */
    private final Engine engine;

    /**
     * The sprite batch to render everything on.
     */
    private SpriteBatch batch;

    /**
     * The current world.
     */
    private World world;

    /**
     * The start screen.
     */
    private StartScreen startScreen;

    /**
     * The game screen.
     */
    private GameScreen gameScreen;

    public SuperDuckInvaders(String inetHost, int inetPort) {
        engine = new Engine(this, inetHost, inetPort);
    }

    public StartScreen getStartScreen() {
        return startScreen;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public void showStartScreen() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                setScreen(startScreen);
            }
        });
    }

    public void showGameScreen() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                setScreen(gameScreen);
            }
        });
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void create() {
        Assets.load();

        world = new World(Assets.levelOneMap);

        batch = new SpriteBatch();

        startScreen = new StartScreen(batch, "Connecting to server...");
        gameScreen = new GameScreen(batch, world, engine);

        setScreen(startScreen);

        try {
            engine.connect();
        } catch (Exception ex) {
            // This shouldn't ever happen.
            ex.printStackTrace();
        }
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        engine.updateInbound();

        // If we're on the game screen, update the world.
        if (screen == gameScreen)
            world.update(delta);

        // Renders the current screen.
        super.render();
        engine.updateOutbound();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}

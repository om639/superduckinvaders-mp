package com.superduckinvaders.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.superduckinvaders.game.net.packets.AttackOutboundPacket;
import com.superduckinvaders.game.net.packets.FlyOutboundPacket;
import com.superduckinvaders.game.net.packets.MovementOutboundPacket;

import java.util.BitSet;

/**
 * Created by Oliver on 20/02/2016.
 */
public class InputHandler implements InputProcessor {

    private static final int[] MOVEMENT_BITS = new int[]{
            Input.Keys.A,
            Input.Keys.D,
            Input.Keys.W,
            Input.Keys.S
    };

    private final GameScreen gameScreen;

    private final Engine engine;

    private BitSet movement = new BitSet(4);

    private boolean fly = false;

    private boolean attack = false;

    private double targetX, targetY;

    public InputHandler(GameScreen gameScreen, Engine engine) {
        this.gameScreen = gameScreen;
        this.engine = engine;
    }

    public void update() {
        if (!movement.isEmpty()) {
            engine.enqueueOutbound(new MovementOutboundPacket(movement));
        }

        if (fly) {
            engine.enqueueOutbound(new FlyOutboundPacket());
            fly = false;
        }

        if (attack) {
            engine.enqueueOutbound(new AttackOutboundPacket(targetX, targetY));
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE) {
            fly = true;
        } else {
            for (int i = 0; i < MOVEMENT_BITS.length; i++) {
                if (keycode == MOVEMENT_BITS[i]) {
                    movement.set(i);
                    break;
                }
            }
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        for (int i = 0; i < MOVEMENT_BITS.length; i++) {
            if (MOVEMENT_BITS[i] == keycode)
                movement.clear(i);
        }

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            Vector3 target = gameScreen.unproject(screenX, screenY);

            targetX = target.x;
            targetY = target.y;
            attack = true;
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            attack = false;
        }

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 target = gameScreen.unproject(screenX, screenY);

        targetX = target.x;
        targetY = target.y;

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}

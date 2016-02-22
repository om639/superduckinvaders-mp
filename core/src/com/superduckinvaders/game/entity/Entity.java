package com.superduckinvaders.game.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.superduckinvaders.game.assets.Assets;

/**
 * Represents an object in the game.
 */
public final class Entity {

    private static final int ANIM_COOLDOWN = 5;

    /**
     * The coordinates of this Entity.
     */
    private double x, y;

    /**
     * The direction this Entity is facing.
     */
    private Direction direction;

    /**
     * Which texture this Entity should use.
     */
    private int texture;

    /**
     * The state time for animation.
     */
    private float stateTime;

    /**
     * The previous values of x and y. Used for detecting a change in position to update state time.
     */
    private double oldX, oldY;

    private int animCounter = 0;

    /**
     * Creates a new Entity with the specified parameters.
     *
     * @param x         the x coordinate
     * @param y         the y coordinate
     * @param direction the direction
     * @param texture   the texture
     */
    public Entity(double x, double y, Direction direction, int texture) {
        this.x = oldX = x;
        this.y = oldY = y;
        this.direction = direction;
        this.texture = texture;
    }

    /**
     * Returns the x coordinate of this Entity.
     *
     * @return the x coordinate of this Entity
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the x coordinate of this Entity.
     *
     * @param x the new x coordinate of this Entity
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Returns the y coordinate of this Entity.
     *
     * @return the y coordinate of this Entity
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the y coordinate of this Entity.
     *
     * @param y the new y coordinate of this Entity
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Returns the direction this Entity is facing.
     *
     * @return the direction this Entity is facing
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Sets the direction this Entity is facing.
     *
     * @param direction the new direction this Entity is facing
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Returns the index of the texture for this Entity.
     *
     * @return the index of the texture for this Entity
     */
    public int getTexture() {
        return texture;
    }

    /**
     * Sets the index of the texture for this Entity.
     *
     * @param texture the new index of the texture for this Entity
     */
    public void setTexture(int texture) {
        this.texture = texture;
    }

    /**
     * Returns the state time of this Entity (i.e. how long it has been since the Entity started moving).
     *
     * @return the state time of this Entity
     */
    public float getStateTime() {
        return stateTime;
    }

    /**
     * Returns the c
     * urrent texture region to draw for this Entity.
     *
     * @return the current texture region to draw for this Entity
     */
    public TextureRegion getTextureRegion() {
        return Assets.textures[texture].getTexture(direction.ordinal(), stateTime);
    }

    /**
     * Updates the state time of this Entity.
     *
     * @param delta the tile elapsed since the last update
     */
    public void update(float delta) {
        if (x != oldX || y != oldY) {
            // Moved since last frame, increment state time.
            animCounter = ANIM_COOLDOWN;
        } else if (animCounter > 0) {
            animCounter--;
        }

        if (animCounter > 0) {
            stateTime += delta;
        } else {
            stateTime = 0;
        }

        oldX = x;
        oldY = y;
    }

    public void render(SpriteBatch batch) {
        batch.draw(getTextureRegion(), (float) x, (float) y);
    }

    /**
     * Represents the direction this Entity is facing.
     */
    public enum Direction {
        DOWN("down"),
        UP("up"),
        LEFT("left"),
        RIGHT("right");

        private static Direction[] values = values();

        private String name;

        Direction(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Direction forName(String name) {
            for (Direction direction : values()) {
                if (direction.getName().equalsIgnoreCase(name))
                    return direction;
            }

            throw new IllegalArgumentException();
        }

        public static Direction forOrdinal(int ordinal) {
            return values[ordinal];
        }
    }
}

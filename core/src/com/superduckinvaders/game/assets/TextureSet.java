package com.superduckinvaders.game.assets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represents a standard set of textures.
 */
public final class TextureSet {

    /**
     * Textures to use when the entity is idle.
     */
    private TextureRegion[] idle = new TextureRegion[4];

    /**
     * Animations to use when the entity is moving.
     */
    private Animation[] anim = new Animation[4];

    /**
     * Creates a new TextureSet, using the specified TextureRegion for everything.
     *
     * @param all the texture to use for everything
     */
    public TextureSet(TextureRegion all) {
        if (all == null)
            throw new IllegalArgumentException();

        idle[0] = all;
    }

    /**
     * Creates a new TextureSet with the specified idle textures and movement animations.
     *
     * @param idle the idle textures
     * @param anim the movement animations
     */
    public TextureSet(TextureRegion[] idle, Animation[] anim) {
        if (idle == null || idle[0] == null)
            throw new IllegalArgumentException();

        this.idle = idle;
        this.anim = anim;
    }

    /**
     * Gets the representative width of this TextureSet (the front idle texture).
     *
     * @return the width
     */
    public int getWidth() {
        return idle[0].getRegionWidth();
    }

    /**
     * Gets the representative height of this TextureSet (the front idle texture).
     *
     * @return the height
     */
    public int getHeight() {
        return idle[0].getRegionHeight();
    }

    /**
     * Gets the default texture.
     *
     * @return the default texture
     */
    public TextureRegion getTexture() {
        return idle[0];
    }

    /**
     * Gets the texture for the specified direction at the specified state time. A state time of 0 means not moving; idle.
     *
     * @param direction the direction
     * @param stateTime the state time
     * @return the appropriate texture
     */
    public TextureRegion getTexture(int direction, float stateTime) {
        return stateTime > 0 ? getAnim(direction, stateTime) : getIdle(direction);
    }

    /**
     * Gets the idle texture for the specified direction.
     *
     * @param direction the direction of the texture to get
     * @return the texture
     */
    private TextureRegion getIdle(int direction) {
        return idle[direction] == null ? idle[0] : idle[direction];
    }

    /**
     * Gets the movement animation for the specified direction.
     *
     * @param direction the direction of the texture to get
     * @param stateTime the state time of the animation
     * @return the texture
     */
    private TextureRegion getAnim(int direction, float stateTime) {
        return anim == null || anim[direction] == null ? idle[0] : anim[direction].getKeyFrame(stateTime, true);
    }
}

package com.superduckinvaders.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.superduckinvaders.game.assets.Assets;
import com.superduckinvaders.game.entity.Entity;

/**
 * Screen for interaction with the game.
 */
public class GameScreen implements Screen {

    /**
     * The gap between the health indicator and the left of the screen.
     */
    private static final float HEALTH_X_OFFSET = 5;

    /**
     * The gap between the health indicator and the bottom of the screen.
     */
    private static final float HEALTH_Y_OFFSET = 5;

    /**
     * The spacing between each heart on the health indicator.
     */
    private static final float HEALTH_SPACING = 5;

    /**
     * The maximum value the health indicator can have, in half hearts.
     */
    private static final float HEALTH_MAXIMUM = 6;

    /**
     * The gap between the flight bar and the right of the screen.
     */
    private static final float FLIGHT_CHARGE_X_OFFSET = 5;

    /**
     * The gap between the flight bar and the bottom of the screen.
     */
    private static final float FLIGHT_CHARGE_Y_OFFSET = 5;

    /**
     * The gap between the powerups and the right of the screen.
     */
    private static final float POWERUP_CHARGE_X_OFFSET = 5;

    /**
     * The gap between the powerups and the bottom of the screen.
     */
    private static final float POWERUP_CHARGE_Y_OFFSET = Assets.flightChargeEmpty.getRegionHeight() + FLIGHT_CHARGE_Y_OFFSET + 5;

    /**
     * How much the powerups should be scaled by.
     */
    private static final float POWERUP_CHARGE_SCALE = 3;

    /**
     * How much space should be left between powerups.
     */
    private static final float POWERUP_CHARGE_SPACING = 5;

    /**
     * The game camera.
     */
    private final OrthographicCamera camera;

    /**
     * Sprite batch for drawing.
     */
    private final SpriteBatch batch;

    /**
     * The world this GameScreen renders.
     */
    private World world;

    /**
     * The renderer for the tile map.
     */
    private final OrthogonalTiledMapRenderer mapRenderer;

    /**
     * The map layer to render over everything else. Contains things like tree tops and backs of roofs.
     */
    private MapLayer overlayLayer;

    /**
     * The input processor handling input from this GameScreen.
     */
    private final InputHandler inputHandler;

    /**
     * The health to be displayed.
     */
    private float health = 0;

    /**
     * The flight charge to be displayed.
     */
    private float flightCharge = 0;

    /**
     * The powerup charges to be displayed.
     */
    private float[] powerupCharge = new float[5];

    /**
     * Initialises this GameScreen for the specified world.
     *
     * @param world the world to be displayed
     */
    public GameScreen(SpriteBatch batch, World world, Engine engine) {
        this.batch = batch;
        this.world = world;

        inputHandler = new InputHandler(this, engine);

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.5f;

        mapRenderer = new OrthogonalTiledMapRenderer(world.getMap(), batch);

        // This will be null if there is no overlay layer.
        overlayLayer = world.getMap().getLayers().get("Overlay");
    }

    /**
     * Converts screen coordinates to world coordinates.
     *
     * @param x the x coordinate on screen
     * @param y the y coordinate on screen
     * @return a Vector3 containing the world coordinates (x and y)
     */
    public Vector3 unproject(int x, int y) {
        return camera.unproject(new Vector3(x, y, 0));
    }

    /**
     * Called when this GameScreen is displayed.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputHandler);
    }

    /**
     * Called when this GameScreen is hidden.
     */
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void setFlightCharge(float flightCharge) {
        this.flightCharge = flightCharge;
    }

    public void setPowerupCharge(float[] powerupCharge) {
        this.powerupCharge = powerupCharge;
    }

    /**
     * Centres the camera on the specified entity.
     *
     * @param entity the entity to centre the camera on
     */
    public void centreCameraOn(Entity entity) {
        if (entity == null)
            return;

        float cameraX = MathUtils.clamp((float) entity.getX(), camera.viewportWidth * camera.zoom / 2, world.getMapWidth() - camera.viewportWidth * camera.zoom / 2);
        float cameraY = MathUtils.clamp((float) entity.getY(), camera.viewportHeight * camera.zoom / 2, world.getMapHeight() - camera.viewportHeight * camera.zoom / 2);

        camera.position.set(cameraX, cameraY, 0);
        camera.update();
    }

    /**
     * Renders this GameScreen.
     *
     * @param delta the time elapsed since the last render
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Centre the camera on the player.
        centreCameraOn(world.getPlayer());

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        mapRenderer.setView(camera);

        for (MapLayer layer : world.getMap().getLayers()) {
            if (layer == overlayLayer)
                continue;

            mapRenderer.renderTileLayer((TiledMapTileLayer) layer);
        }

        // Draw all entities.
        for (Entity entity : world.getEntities()) {
            entity.render(batch);
        }

        // Render overlay layer if there is one.
        if (overlayLayer != null)
            mapRenderer.renderTileLayer((TiledMapTileLayer) overlayLayer);

        // Set projection matrix to UI.
        batch.setProjectionMatrix(camera.combined.cpy().setToOrtho2D(0, 0, camera.viewportWidth, camera.viewportHeight));

        renderHealth();
        renderFlightCharge();
        renderPowerupCharge();

        batch.end();

        // Process pending input.
        inputHandler.update();
    }

    private void renderHealth() {
        float width = Assets.heartFull.getRegionWidth();
        int hearts = MathUtils.ceil(HEALTH_MAXIMUM * health), count = 0;

        TextureRegion heartTexture;

        while (count < HEALTH_MAXIMUM) {
            if (count + 2 <= hearts) {
                heartTexture = Assets.heartFull;
            } else if (count + 1 <= hearts) {
                heartTexture = Assets.heartHalf;
            } else {
                heartTexture = Assets.heartEmpty;
            }

            batch.draw(heartTexture, (count / 2) * (width + HEALTH_SPACING) + HEALTH_X_OFFSET, HEALTH_Y_OFFSET);

            count += 2;
        }
    }

    private void renderFlightCharge() {
        float width = Assets.flightChargeEmpty.getRegionWidth();

        Assets.flightChargeFull.setRegionWidth(MathUtils.ceil(width * flightCharge));

        batch.draw(Assets.flightChargeEmpty, camera.viewportWidth - width - FLIGHT_CHARGE_X_OFFSET, FLIGHT_CHARGE_Y_OFFSET);
        batch.draw(Assets.flightChargeFull, camera.viewportWidth - width - FLIGHT_CHARGE_X_OFFSET, FLIGHT_CHARGE_Y_OFFSET);
    }

    private void renderPowerupCharge() {
        // Uses a shader to draw the powerup time outs.
        ShaderProgram shader = Assets.powerupShader;

        // Set the batch to use the shader.
        batch.setShader(shader);

        // The distance away from the right and bottom of the screen.
        float xOffset = POWERUP_CHARGE_X_OFFSET;

        for (int i = 0; i < powerupCharge.length; i++) {
            if (powerupCharge[i] > 0) {
                // Textures start at index 5 in the textures array.
                TextureRegion powerupTexutre = Assets.textures[i + 5].getTexture();

                float width = powerupTexutre.getRegionWidth() * POWERUP_CHARGE_SCALE;
                float height = powerupTexutre.getRegionHeight() * POWERUP_CHARGE_SCALE;
                float x = camera.viewportWidth - xOffset - width;
                float y = POWERUP_CHARGE_Y_OFFSET;

                shader.setUniformf("u_centre", x + 2 + width / 2, y - 1 + height / 2);
                shader.setUniformf("u_powerupWidth", width);
                shader.setUniformf("u_currentAngle", (float) (Math.PI * 2 * (1 - powerupCharge[i])));

                batch.draw(powerupTexutre, x, y, width, height);
                batch.flush();

                xOffset += width + POWERUP_CHARGE_SPACING;
            }
        }

        batch.setShader(null);
    }

    /**
     * Not implemented.
     */
    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    /**
     * Not implemented.
     */
    @Override
    public void pause() {
    }

    /**
     * Not implemented.
     */
    @Override
    public void resume() {
    }

    /**
     * Not implemented.
     */
    @Override
    public void dispose() {
    }
}

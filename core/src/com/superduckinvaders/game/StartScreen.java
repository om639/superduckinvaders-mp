package com.superduckinvaders.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.superduckinvaders.game.assets.Assets;

public class StartScreen implements Screen {
    /**
     * The viewport of the stage.
     */
    private ScreenViewport viewport = new ScreenViewport();

    /**
     * Sprite batch for drawing.
     */
    private SpriteBatch batch;

    /**
     * Stage for containing the button.
     */
    private Stage stage;

    /**
     * The label displaying the message.
     */
    private Label messageLabel;

    /**
     * The message to be displayed.
     */
    private String message;

    /**
     * The color of the message to be displayed.
     */
    private Color color = Color.WHITE;

    /**
     * Creates a new StartScreen with the specified message.
     *
     * @param message the message to be displayed
     */
    public StartScreen(SpriteBatch batch, String message) {
        this.batch = batch;
        this.message = message;

        messageLabel = new Label(message, new Label.LabelStyle(Assets.font, Color.WHITE));
    }

    /**
     * Called when this StartScreen is displayed.
     */
    @Override
    public void show() {
        stage = new Stage(viewport, batch);

        Image logoImage = new Image(Assets.logo);
        logoImage.setPosition((stage.getWidth() - logoImage.getPrefWidth()) / 2, 400);

        messageLabel.setPosition((stage.getWidth() - messageLabel.getPrefWidth()) / 2, 200);

        stage.addActor(logoImage);
        stage.addActor(messageLabel);
    }

    /**
     * Sets the message to be displayed on this StartScreen.
     *
     * @param message the message to be displayed
     */
    public void setMessage(String message) {
        setMessage(message, Color.WHITE);
    }

    /**
     * Sets the message to be displayed on this StartScreen.
     *
     * @param message the message to be displayed
     * @param color   the colour to display the message in
     */
    public void setMessage(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setColor(color);
        messageLabel.setPosition((stage.getWidth() - messageLabel.getPrefWidth()) / 2, 200);
    }

    /**
     * Called when this StartScreen is hidden.
     */
    @Override
    public void hide() {
        stage.dispose();
    }

    /**
     * Renders this StartScreen.
     *
     * @param delta the time elapsed since the last render
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }

    /**
     * Not implemented.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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

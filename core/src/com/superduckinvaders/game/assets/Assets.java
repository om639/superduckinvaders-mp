package com.superduckinvaders.game.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.superduckinvaders.game.entity.Entity;

import java.io.FileNotFoundException;

/**
 * Responsible for loading game assets.
 */
public class Assets {

    /**
     * The names of the texture sets to load, in the order they will be in the textures array.
     */
    public static final String[] TEXTURE_SETS = new String[]{
            "player",
            "player_flying",
            "badguy",
            "projectile",
            "flag",
            "powerup_score_multiplier",
            "powerup_super_speed",
            "powerup_rate_of_fire",
            "powerup_invulnerable",
            "powerup_regeneration"
    };

    /**
     * The number of frames in each movement animation texture.
     */
    public static final int TEXTURE_ANIM_FRAMES = 4;

    /**
     * The duration of each frame in the movement animations.
     */
    public static final float TEXTURE_ANIM_DURATION = 0.25f;

    /**
     * The textures.
     */
    public static TextureSet[] textures = new TextureSet[TEXTURE_SETS.length];

    /**
     * Textures for Hearts
     */
    public static TextureRegion heartFull, heartHalf, heartEmpty;

    /**
     * Textures for stamina.
     */
    public static TextureRegion flightChargeFull, flightChargeEmpty;

    /**
     * Shader to use for powerup timers.
     */
    public static ShaderProgram powerupShader;

    /**
     * Tile map for level one.
     */
    public static TiledMap levelOneMap;

    /**
     * The font for the UI.
     */
    public static BitmapFont font;

    /**
     * The texture for the button.
     */
    public static TextureRegion button;

    /**
     * Texture for the game logo.
     */
    public static TextureRegion logo;

    /**
     * Responsible for loading maps.
     */
    private static TmxMapLoader mapLoader = new TmxMapLoader();

    /**
     * Loads all assets.
     */
    public static void load() {
        loadTextureSets();
        loadUIAssets();

        levelOneMap = loadTiledMap("maps/map.tmx");
    }

    /**
     * Loads the texture sets defined in TEXTURE_SETS into the textures array.
     */
    private static void loadTextureSets() {
        for (int i = 0; i < TEXTURE_SETS.length; i++) {
            try {
                textures[i] = loadTextureSet("textures/" + TEXTURE_SETS[i]);
            } catch (FileNotFoundException e) {
                System.err.println("texture set " + TEXTURE_SETS[i] + " does not exist");
            }
        }
    }

    /**
     * Loads textures relating to game UI.
     */
    private static void loadUIAssets() {
        font = loadFont("font/font.fnt", "font/font.png");

        Texture hearts = loadTexture("textures/hearts.png");
        heartFull = new TextureRegion(hearts, 0, 0, 32, 28);
        heartHalf = new TextureRegion(hearts, 32, 0, 32, 28);
        heartEmpty = new TextureRegion(hearts, 64, 0, 32, 28);

        Texture stamina = loadTexture("textures/stamina.png");
        flightChargeFull = new TextureRegion(stamina, 0, 0, 192, 28);
        flightChargeEmpty = new TextureRegion(stamina, 0, 28, 192, 28);

        ShaderProgram.pedantic = false;
        powerupShader = loadShaderProgram("shaders/powerup_shader.vsh", "shaders/powerup_shader.fsh");

        button = new TextureRegion(loadTexture("textures/button.png"));

        logo = new TextureRegion(loadTexture("textures/logo.png"));
    }

    /**
     * Loads the specified texture set.
     *
     * @param path the directory containing the texture set
     * @return the texture set
     * @throws FileNotFoundException if the directioy does not exist
     */
    private static TextureSet loadTextureSet(String path) throws FileNotFoundException {
        FileHandle directory = Gdx.files.internal(path);

        if (!directory.exists() || !directory.isDirectory())
            throw new FileNotFoundException();

        // Check if there is a single texture.
        FileHandle all = directory.child("all.png");

        if (all.exists()) {
            Texture texture = loadTexture(all);

            return new TextureSet(new TextureRegion(texture));
        } else {
            TextureRegion[] idle = new TextureRegion[4];
            Animation[] anim = new Animation[4];

            for (FileHandle child : directory.list()) {
                if (child.name().equals("idle.png")) {
                    // Load the idle texture.
                    Texture texture = loadTexture(child);
                    int width = texture.getWidth() / 4;
                    int height = texture.getHeight();

                    idle[Entity.Direction.DOWN.ordinal()] = new TextureRegion(texture, 0, 0, width, height);
                    idle[Entity.Direction.UP.ordinal()] = new TextureRegion(texture, width, 0, width, height);
                    idle[Entity.Direction.LEFT.ordinal()] = new TextureRegion(texture, width * 2, 0, width, height);
                    idle[Entity.Direction.RIGHT.ordinal()] = new TextureRegion(texture, width * 3, 0, width, height);
                } else if (child.name().startsWith("anim_")) {
                    // Load the animation textures.
                    String name = child.nameWithoutExtension().substring(5);

                    anim[Entity.Direction.forName(name).ordinal()] = loadAnimation(child, TEXTURE_ANIM_FRAMES, TEXTURE_ANIM_DURATION);
                }
            }

            return new TextureSet(idle, anim);
        }
    }

    /**
     * Loads the texture from the specified file.
     *
     * @param file the file to load from
     * @return the texture
     */
    public static Texture loadTexture(String file) {
        return loadTexture(Gdx.files.internal(file));
    }

    /**
     * Loads the texture from the specified file handle.
     *
     * @param file the file handle to load from
     * @return the texture
     */
    public static Texture loadTexture(FileHandle file) {
        return new Texture(file);
    }

    /**
     * Loads the tile map from the specifed file.
     *
     * @param file the file to load from
     * @return the tile map
     */
    public static TiledMap loadTiledMap(String file) {
        return mapLoader.load(file);
    }

    /**
     * Loads the animation from the specified texture.
     *
     * @param texture       the texture containing the animation frames
     * @param count         the number of animation frames
     * @param frameWidth    the width of each animation frame
     * @param frameDuration the duration of each frame
     * @return the animation
     */
    public static Animation loadAnimation(Texture texture, int count, int frameWidth, float frameDuration) {
        Array<TextureRegion> keyFrames = new Array<>();

        for (int i = 0; i < count; i++) {
            keyFrames.add(new TextureRegion(texture, i * frameWidth, 0, frameWidth, texture.getHeight()));
        }

        return new Animation(frameDuration, keyFrames);
    }

    /**
     * Loads the animation from the specified file handle. The width of each frame is calculated based on the width of the texture and the count.
     *
     * @param file          the texture containing the animation frames
     * @param count         the number of animation frames
     * @param frameDuration the duration of each frame
     * @return the animation
     */
    public static Animation loadAnimation(FileHandle file, int count, float frameDuration) {
        Texture texture = loadTexture(file);

        return loadAnimation(texture, count, texture.getWidth() / count, frameDuration);
    }

    /**
     * Loads the bitmap font from the specified files.
     *
     * @param fontFile  the file containing information about the glyphs stored on the image file
     * @param imageFile the file containing the actual glyphs
     * @return the bitmap font
     */
    public static BitmapFont loadFont(String fontFile, String imageFile) {
        return new BitmapFont(Gdx.files.internal(fontFile), Gdx.files.internal(imageFile), false);
    }

    public static ShaderProgram loadShaderProgram(String vertexShaderFile, String fragmentShaderFile) {
        return new ShaderProgram(Gdx.files.internal(vertexShaderFile), Gdx.files.internal(fragmentShaderFile));
    }
}

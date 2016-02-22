package com.superduckinvaders.game;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.superduckinvaders.game.entity.Entity;
import com.superduckinvaders.game.entity.EntityList;

/**
 * Represents a round of the game played on one level with a single objective.
 */
public final class World {

    /**
     * The World's map.
     */
    private TiledMap map;

    /**
     * Size of the map in tiles.
     */
    private int mapWidth, mapHeight;

    /**
     * Size of each tile.
     */
    private int tileWidth, tileHeight;

    /**
     * List of all entities currently in the World.
     */
    private EntityList entities;

    /**
     * The ID of the player entity.
     */
    private int player;

    /**
     * Initialises a new World with the specified map.
     *
     * @param map the World's map
     */
    public World(TiledMap map) {
        this.map = map;

        mapWidth = map.getProperties().get("width", Integer.class);
        mapHeight = map.getProperties().get("height", Integer.class);
        tileWidth = map.getProperties().get("tilewidth", Integer.class);
        tileHeight = map.getProperties().get("tileheight", Integer.class);

        entities = new EntityList(2048);
    }

    /**
     * Returns this World's map
     *
     * @return this World's map
     */
    public TiledMap getMap() {
        return map;
    }

    public int getMapWidth() {
        return mapWidth * tileWidth;
    }

    public int getMapHeight() {
        return mapWidth * tileWidth;
    }

    /**
     * Returns a list of all the entities in this World
     *
     * @return a list of all the entities in this World
     */
    public EntityList getEntities() {
        return entities;
    }

    /**
     * Returns the entity that represents the player.
     *
     * @return the entity that represents the player
     */
    public Entity getPlayer() {
        return entities.get(player);
    }

    /**
     * Sets the ID of the entity that represents the player
     *
     * @param player the ID of the entity that represents the player
     */
    public void setPlayer(int player) {
        this.player = player;
    }

    public void createEntity(int id, double x, double y, Entity.Direction direction, int texture) {
        entities.set(id, new Entity(x, y, direction, texture));
    }

    public void destroyEntity(int id) {
        entities.remove(id);
    }

    public void updateEntityPosition(int id, double x, double y) {
        System.out.println("update position for entity " + id);
        Entity entity = entities.get(id);

        if (entity != null) {
            entity.setX(x);
            entity.setY(y);
        }
    }

    public void updateEntityDirection(int id, Entity.Direction direction) {
        Entity entity = entities.get(id);

        if (entity != null)
            entity.setDirection(direction);
    }

    public void updateEntityTexture(int id, int texture) {
        Entity entity = entities.get(id);

        if (entity != null)
            entity.setTexture(texture);
    }

    /**
     * Updates all entities in this World.
     *
     * @param delta the time elapsed since the last update
     */
    public void update(float delta) {
        for (Entity entity : entities) {
            entity.update(delta);
        }
    }
}

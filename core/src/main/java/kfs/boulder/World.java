package kfs.boulder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import kfs.boulder.comp.*;
import kfs.boulder.ecs.Entity;
import kfs.boulder.ecs.KfsSystem;
import kfs.boulder.ecs.KfsWorld;
import kfs.boulder.sys.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Consumer;


public class World extends KfsWorld {
    public static final int TILE_SIZE = 32;
    public static final float PLAYER_MOVE_SPEED = 6f;
    public static final float CAMERA_SMOOTH = 0.15f;
    public static final float GEM_EXPLOSION_TIME = 0.55f;
    public static final float STONE_ROLLING_TIME = 1.6f;

    private final Consumer<Boolean> gameOverCallback;
    private final Map<Tile, Texture> textures;
    private Tile[][] tiles;

    public World(Consumer<Boolean> gameOverCallback) {
        this.gameOverCallback = gameOverCallback;
        textures = loadTextures();
        loadMap("maps/map1.txt");

        addSys(new InputSys(this));
        addSys(new MovingSys(this));
        addSys(new GravitySys(this));
        addSys(new RenderMoving(this));
        addSys(new ExplosionSys(this));
        addSys(new LevelSys(this));
    }

    public void loadMap(String file) {
        reset();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
            Gdx.files.internal(file).read()))) {
            String[] lines = reader.lines().toArray(String[]::new);
            int height = lines.length;
            int width = lines[0].length();
            tiles = new Tile[width][height];

            for (int y = 0; y < height; y++) {
                String line = lines[height - 1 - y]; // převrátíme Y
                for (int x = 0; x < width; x++) {
                    char c = line.charAt(x);
                    Tile tile = Tile.fromCode(c);
                    tiles[x][y] = tile;
                    switch (tile) {
                        case GEM:
                            Entity gem = createEntity();
                            addComponent(gem, new ItemComp(true));
                            addComponent(gem, new PositionComp(x, y));
                            addComponent(gem, new RenderComp(Tile.GEM));
                            break;
                        case STONE:
                            Entity stone = createEntity();
                            addComponent(stone, new ItemComp(false));
                            addComponent(stone, new PositionComp(x, y));
                            addComponent(stone, new RenderComp(Tile.STONE));
                            break;
                        case PLAYER:
                            Entity p = createEntity();
                            addComponent(p, new PlayerComp());
                            addComponent(p, new RenderComp(Tile.PLAYER));
                            addComponent(p, new PositionComp(x,y));
                            break;
                        case DOOR_CLOSED:
                            Entity door = createEntity();
                            addComponent(door, new DoorComp());
                            addComponent(door, new PositionComp(x,y));
                            addComponent(door, new RenderComp(Tile.DOOR_CLOSED));
                            break;
                    }
                }
            }
        } catch (Exception e) {
            Gdx.app.error("World", "Failed to load map: " + e.getMessage());
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(textures.get(Tile.EMPTY), 0, 0, tiles.length * TILE_SIZE, tiles[0].length * TILE_SIZE);
        for (int x = 0; x < tiles.length; x++)
            for (int y = 0; y < tiles[0].length; y++) {
                if ((tiles[x][y] != Tile.PLAYER)
                    && (tiles[x][y] != Tile.STONE)
                    && (tiles[x][y] != Tile.GEM)
                    && (tiles[x][y] != Tile.DOOR_CLOSED)
                    && (tiles[x][y] != Tile.DOOR_OPENED)
                ) { // player & items will draw on other place
                    batch.draw(textures.get(tiles[x][y]), x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        super.render(batch);
    }

    public void dispose() {
        textures.values().forEach(Texture::dispose);
        runSystems(KfsSystem::done);
    }

    public Tile [][]getTiles() {
        return tiles;
    }

    public void setTile(int x, int y, Tile type) {
        if (x>0 && y>0 && x<tiles.length && y<tiles[0].length) {
            tiles[x][y] = type;
        }
    }

    public Tile getTileType(int x, int y) {
        if (x < 0 || y < 0 || x >= tiles.length || y >= tiles[0].length) return Tile.EMPTY;
        return tiles[x][y];
    }

    public Texture getTexture(Tile tile) {
        return textures.get(tile);
    }

    public int getScore() {
        for (Entity e : getEntitiesWith(PlayerComp.class)) {
            return getComponent(e, PlayerComp.class).gemsEaten * 100;
        }
        return 0;
    }

    public void gameOver(boolean win) {
        gameOverCallback.accept(win);
    }

    public static Map<Tile, Texture> loadTextures() {
        return Map.of(
            Tile.PLAYER, new Texture("tiles/player.png"),
            Tile.DIRT, new Texture("tiles/dirt.png"),
            Tile.ROCK, new Texture("tiles/rock.png"),
            Tile.EMPTY, new Texture("tiles/empty.png"),
            Tile.GEM, new Texture("tiles/gem.png"),
            Tile.STONE, new Texture("tiles/stone.png"),
            Tile.DOOR_CLOSED, new Texture("tiles/door_c.png"),
            Tile.DOOR_OPENED, new Texture("tiles/door_o.png")
        );
    }
}

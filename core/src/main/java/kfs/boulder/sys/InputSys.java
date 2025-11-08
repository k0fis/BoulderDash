package kfs.boulder.sys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Array;
import kfs.boulder.Tile;
import kfs.boulder.World;
import kfs.boulder.comp.*;
import kfs.boulder.ecs.Entity;
import kfs.boulder.ecs.KfsSystem;

/** Handles player input: keyboard + touch D-pad + swipe/drag for tablets. */
public class InputSys implements KfsSystem {

    private final World world;

    // touch-based virtual D-pad
    private int touchDx = 0;
    private int touchDy = 0;

    // swipe/drag detection
    private int swipeDx = 0;
    private int swipeDy = 0;

    public InputSys(World world) {
        this.world = world;

        // setup gesture detector
        GestureDetector gd = new GestureDetector(new GestureDetector.GestureAdapter() {
            private final float minFling = 50f; // minimum fling distance in pixels

            @Override
            public boolean fling(float velocityX, float velocityY, int button) {
                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    swipeDx = velocityX > 0 ? 1 : -1;
                    swipeDy = 0;
                } else {
                    swipeDx = 0;
                    swipeDy = velocityY > 0 ? 1 : -1;
                }
                return true;
            }

            @Override
            public boolean touchDown(float x, float y, int pointer, int button) {
                swipeDx = 0;
                swipeDy = 0;
                return false;
            }
        });
        Gdx.input.setInputProcessor(gd);
    }

    /** Optional: call once to setup on-screen buttons for tablet. */
    public void setupTouchControls(Stage stage, Skin skin) {
        int size = 80; // button size
        int padding = 10;
        int baseX = padding;
        int baseY = padding;

        TextButton up = new TextButton("U", skin);
        up.setBounds(baseX + size, baseY + size * 2, size, size);
        up.addListener(new ClickListener(){
            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                touchDy = 1;
                return true;
            }
            @Override public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                touchDy = 0;
            }
        });

        TextButton down = new TextButton("D", skin);
        down.setBounds(baseX + size, baseY, size, size);
        down.addListener(new ClickListener(){
            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                touchDy = -1;
                return true;
            }
            @Override public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                touchDy = 0;
            }
        });

        TextButton left = new TextButton("L", skin);
        left.setBounds(baseX, baseY + size, size, size);
        left.addListener(new ClickListener(){
            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                touchDx = -1;
                return true;
            }
            @Override public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                touchDx = 0;
            }
        });

        TextButton right = new TextButton("R", skin);
        right.setBounds(baseX + size * 2, baseY + size, size, size);
        right.addListener(new ClickListener(){
            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                touchDx = 1;
                return true;
            }
            @Override public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                touchDx = 0;
            }
        });

        stage.addActor(up);
        stage.addActor(down);
        stage.addActor(left);
        stage.addActor(right);
    }

    @Override
    public void update(float delta) {
        for (Entity e : world.getEntitiesWith(PlayerComp.class, PositionComp.class)) {
            if (world.getComponent(e, MovingComp.class) != null) continue;

            PositionComp pos = world.getComponent(e, PositionComp.class);
            int dx = 0;
            int dy = 0;

            // keyboard input
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) dx = -1;
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dx = 1;
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) dy = 1;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) dy = -1;

            // combine with touch input (touch overrides 0)
            if (touchDx != 0) dx = touchDx;
            if (touchDy != 0) dy = touchDy;

            // combine with swipe input (fling overrides 0)
            if (swipeDx != 0) dx = swipeDx;
            if (swipeDy != 0) dy = swipeDy;
            swipeDx = 0; // reset after applying
            swipeDy = 0;

            if (dx != 0 || dy != 0) handleMovement(e, pos, dx, dy);
        }
    }

    private void handleMovement(Entity e, PositionComp pos, int dx, int dy) {
        int newX = pos.x + dx;
        int newY = pos.y + dy;

        Tile target = world.getTileType(newX, newY);

        if (target == Tile.EMPTY || target == Tile.DIRT || target == Tile.GEM || target == Tile.DOOR_CLOSED) {
            move(e, pos, newX, newY, Tile.PLAYER);
        }

        // gem collection
        if (target == Tile.GEM) {
            for (Entity gem : world.getEntitiesWith(ItemComp.class, PositionComp.class)) {
                PositionComp pc = world.getComponent(gem, PositionComp.class);
                if (pc.x == newX && pc.y == newY) {
                    world.addComponent(gem, new ExplosionComp(pc.x * World.TILE_SIZE, pc.y * World.TILE_SIZE,
                        World.GEM_EXPLOSION_TIME, Tile.GEM));
                    world.removeComponent(gem, PositionComp.class);
                    world.removeComponent(gem, ItemComp.class);
                    world.getComponent(e, PlayerComp.class).gemsEaten++;
                    break;
                }
            }
        } else if (target == Tile.STONE && dy == 0) {
            PositionComp pc = new PositionComp(newX, newY);
            for (Entity stone : world.getEntitiesWith(PositionComp.class, a -> a.equals(pc))) {
                int newNewX = newX + dx;
                if (newNewX >= 0 && newNewX < world.getTiles().length &&
                    Tile.EMPTY == world.getTileType(newNewX, newY)) {
                    move(stone, world.getComponent(stone, PositionComp.class), newNewX, newY, Tile.STONE);
                    move(e, pos, newX, newY, Tile.PLAYER);
                }
            }
        }

        if (dx != 0) {
            RenderComp r = world.getComponent(e, RenderComp.class);
            if (r != null) r.dx = dx;
        }
    }

    private void move(Entity e, PositionComp pos, int newX, int newY, Tile newTile) {
        world.addComponent(e, new MovingComp(pos.x * World.TILE_SIZE, pos.y * World.TILE_SIZE,
            newX * World.TILE_SIZE, newY * World.TILE_SIZE, World.PLAYER_MOVE_SPEED));
        world.setTile(pos.x, pos.y, Tile.EMPTY);
        pos.x = newX;
        pos.y = newY;
        world.setTile(pos.x, pos.y, newTile);
    }
}

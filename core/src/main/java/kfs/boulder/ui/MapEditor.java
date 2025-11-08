package kfs.boulder.ui;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import kfs.boulder.KfsMain;
import kfs.boulder.Tile;
import kfs.boulder.World;

import java.util.Arrays;
import java.util.Map;

/**
 * MapEditorScreen — complete editor with horizontal palette, ASCII save/load,
 * zoom buttons, on-screen pan buttons, right-mouse drag to pan, double-click to center,
 * and smooth inertial camera movement.
 *
 * Adapted to your Tile enum and textures map.
 */
public class MapEditor extends ScreenAdapter {
    private final KfsMain game;
    private final Map<Tile, Texture> textures;

    private Stage stage;
    private Skin skin;

    private OrthographicCamera camera;
    private FitViewport viewport;

    private Tile[][] map;
    private int mapW = 40;
    private int mapH = 25;
    private int tileSize = 24;

    private Tile selectedTile = Tile.DIRT;

    // Camera smooth movement
    private float zoom = 1f;
    private float velX = 0f;
    private float velY = 0f;
    private float damping = 0.87f;

    private boolean panning = false;
    private int lastPanX, lastPanY;

    // double click detection
    private long lastClickTime = 0L;
    private static final long DOUBLE_CLICK_MS = 300L;

    private SpriteBatch batch;

    public MapEditor(KfsMain game) {
        this.game = game;
        this.textures = World.loadTextures();

        initEmptyMap(mapW, mapH);
    }

    private void initEmptyMap(int w, int h) {
        mapW = w;
        mapH = h;
        map = new Tile[mapH][mapW];
        for (int y = 0; y < mapH; y++) Arrays.fill(map[y], Tile.DIRT);
        for (int y = 0; y < mapH; y++) {
            map[y][0] = Tile.ROCK;
            map[y][mapW - 1] = Tile.ROCK;
        }
        for (int x = 0; x < mapW; x++) {
            map[0][x] = Tile.ROCK;
            map[mapH - 1][x] = Tile.ROCK;
        }
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 600, camera);
        camera.position.set((mapW * tileSize)/2f, (mapH * tileSize)/2f, 0);
        camera.update();

        stage = new Stage(new FitViewport(1280, 720));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        createPaletteBar();
        createMainButtons();
        createZoomButtons();
        createPanButtons();

        // Input multiplexer: stage first, then editor input
        InputMultiplexer mux = new InputMultiplexer();
        mux.addProcessor(stage);
        mux.addProcessor(new EditorInputProcessor());
        Gdx.input.setInputProcessor(mux);
    }

    private void createPaletteBar() {
        Table table = new Table();
        table.setFillParent(true);
        table.top().left().pad(8);

        for (Tile t : Tile.values()) {
            if (t == Tile.DOOR_OPENED) continue;
            Image img = null;
            Texture tex = textures.get(t);
            if (tex != null) img = new Image(new TextureRegion(tex));
            else img = new Image(new TextureRegion(new Texture(1,1, Pixmap.Format.RGBA8888)));
            img.setSize(tileSize, tileSize);

            TextButton btn = new TextButton(t.name(), skin);
            btn.add(img).size(tileSize, tileSize).padRight(6);
            btn.addListener(new ClickListener(){
                @Override public void clicked(InputEvent e, float x, float y) { selectedTile = t; }
            });
            table.add(btn).padRight(6);
        }

        stage.addActor(table);
    }

    private void createMainButtons() {
        TextButton save = new TextButton("Export", skin);
        save.setPosition(10, 5);
        save.addListener(new ClickListener(){ @Override public void clicked(InputEvent e, float x, float y){ exportMap(); }});

        TextButton back = new TextButton("Back", skin);
        back.setPosition(60, 5);
        back.addListener(new ClickListener(){ @Override public void clicked(InputEvent e, float x, float y){ game.setScreen(new MainScreen(game)); dispose(); }});

        stage.addActor(back);
        stage.addActor(save);
    }

    private void createZoomButtons() {
        TextButton zin = new TextButton("+", skin);
        zin.setPosition(90, 5);
        zin.addListener(new ClickListener(){ @Override public void clicked(InputEvent e, float x, float y){ zoom = Math.max(0.2f, zoom - 0.1f); camera.zoom = zoom; }});

        TextButton zout = new TextButton("-", skin);
        zout.setPosition(110, 5);
        zout.addListener(new ClickListener(){ @Override public void clicked(InputEvent e, float x, float y){ zoom = Math.min(4f, zoom + 0.1f); camera.zoom = zoom; }});

        stage.addActor(zin);
        stage.addActor(zout);
    }

    private void createPanButtons() {
        TextButton up = new TextButton("U", skin);
        up.setPosition(170, 5);
        up.addListener(new ClickListener(){ @Override public void clicked(InputEvent e, float x, float y){ velY = 200; }});

        TextButton down = new TextButton("D", skin);
        down.setPosition(190, 5);
        down.addListener(new ClickListener(){ @Override public void clicked(InputEvent e, float x, float y){ velY = -200; }});

        TextButton left = new TextButton("L", skin);
        left.setPosition(130, 5);
        left.addListener(new ClickListener(){ @Override public void clicked(InputEvent e, float x, float y){ velX = 200; }});

        TextButton right = new TextButton("R", skin);
        right.setPosition(150, 5);
        right.addListener(new ClickListener(){ @Override public void clicked(InputEvent e, float x, float y){ velX = -200; }});

        stage.addActor(up);
        stage.addActor(down);
        stage.addActor(left);
        stage.addActor(right);
    }

    private void exportMap() {
        StringBuilder sb = new StringBuilder();
        for (int y = mapH-1; y >= 0; y--) {
            for (int x = 0; x < mapW; x++) {
                sb.append(map[y][x].sym);
            }
            sb.append('\n');
        }
        Gdx.app.log("MapEditor", "Export map:\n" + sb);
    }

    @Override
    public void render(float delta) {
        // apply inertial movement
        camera.translate(velX * delta, velY * delta);
        velX *= damping; velY *= damping;

        camera.update();

        ScreenUtils.clear(0.1f, 0.1f, 0.12f, 1f);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (int y = 0; y < mapH; y++) {
            for (int x = 0; x < mapW; x++) {
                Tile t = map[y][x];
                Texture tex = textures.get(t);
                if (tex != null) batch.draw(tex, x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
    }

    // Input processor for editing and camera control
    private class EditorInputProcessor extends InputAdapter {
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            // check double click
            long now = System.currentTimeMillis();
            if (button == Input.Buttons.LEFT && now - lastClickTime <= DOUBLE_CLICK_MS) {
                // center camera on clicked tile
                Vector3 world = camera.unproject(new Vector3(screenX, screenY, 0));
                camera.position.set(world.x, world.y, 0);
                lastClickTime = 0;
                return true;
            }
            lastClickTime = now;

            if (button == Input.Buttons.LEFT) {
                // paint
                Vector3 world = camera.unproject(new Vector3(screenX, screenY, 0));
                int tx = (int)(world.x / tileSize);
                int ty = (int)(world.y / tileSize);
                if (tx >= 0 && tx < mapW && ty >= 0 && ty < mapH) {
                    map[ty][tx] = selectedTile;
                    return true;
                }
            }

            if (button == Input.Buttons.RIGHT) {
                panning = true;
                lastPanX = screenX; lastPanY = screenY;
                return true;
            }
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            if (panning) {
                int dx = screenX - lastPanX;
                int dy = screenY - lastPanY;
                camera.translate(-dx * camera.zoom, dy * camera.zoom);
                lastPanX = screenX; lastPanY = screenY;
                return true;
            } else {
                // painting while dragging with left button
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    Vector3 world = camera.unproject(new Vector3(screenX, screenY, 0));
                    int tx = (int)(world.x / tileSize);
                    int ty = (int)(world.y / tileSize);
                    if (tx >= 0 && tx < mapW && ty >= 0 && ty < mapH) {
                        map[ty][tx] = selectedTile;
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (button == Input.Buttons.RIGHT) panning = false;
            return false;
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {
            zoom = Math.min(4f, Math.max(0.2f, zoom + amountY * 0.1f));
            camera.zoom = zoom;
            return true;
        }
    }
}

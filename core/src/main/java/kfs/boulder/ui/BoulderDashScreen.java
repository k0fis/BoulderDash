package kfs.boulder.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import kfs.boulder.KfsMain;
import kfs.boulder.World;
import kfs.boulder.sys.*;

/** First screen of the application. Displayed after the application is created. */
public class BoulderDashScreen extends BaseScreen {

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final SpriteBatch batch;
    private World world;
    private Stage uiStage;
    private Label scoreLabel;

    public BoulderDashScreen(KfsMain game, String mapName) {
        super(game, true);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        viewport = new FitViewport(800, 600, camera);
        viewport.apply();

        batch = new SpriteBatch();
        world = new World(win -> {
            if (win) {
                game.setScreen(new GameOverScreen(game, world.getScore(), mapName));
            } else {
                game.setScreen(new LevelDoneScreen(game, false, "Try it again", mapName));
            }
        });

        world.addSys(new CameraUpdateSys(camera, world));

        setupUI();

        if (Gdx.app.getType() == Application.ApplicationType.WebGL) {
            world.getSystem(InputSys.class).setupTouchControls(uiStage, skin);
        }

        world.loadMap(mapName);

    }

    private void setupUI() {
        uiStage = new Stage(new FitViewport(800, 600));
        Gdx.input.setInputProcessor(uiStage);

        Table table = new Table();
        table.setFillParent(true);
        table.top().pad(10);
        uiStage.addActor(table);

        // Score label

        Label.LabelStyle scoreStyle = new Label.LabelStyle(fontSmall, Color.YELLOW);
        scoreLabel = new Label("Score: 0", scoreStyle);
        table.add(scoreLabel).expandX().left();

        // Exit to Menu button
        TextButton.TextButtonStyle buttonStyle = getTextButtonStyle(fontSmall, Color.WHITE);
        TextButton exitButton = new TextButton("Exit to Menu", buttonStyle);
        exitButton.addListener(e -> {
            if (exitButton.isPressed()) {
                game.setScreen(new LevelSelectScreen(game));
            }
            return false;
        });
        table.add(exitButton).right();


    }

    @Override
    public void render(float delta) {
        // --- Game logic ---
        world.update(delta);

        // --- Clear & draw world ---
        ScreenUtils.clear(0, 0, 0, 1);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();
        float bx = camera.position.x - worldW / 2f;
        float by = camera.position.y - worldH / 2f;

        // Darken background using color tint (RGB < 1 = darker)
        batch.setColor(0.3f, 0.3f, 0.3f, 1f); // 40% brightness
        batch.draw(background, bx, by, worldW, worldH); // match your viewport/world size
        batch.setColor(1, 1, 1, 1); // reset color for next draws

        world.render(batch);
        batch.end();

        // --- Update UI (score) ---
        scoreLabel.setText("Score: " + world.getScore());

        // --- Draw UI ---
        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        uiStage.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiStage.getViewport().update(width, height, true);
    }

}

package kfs.boulder.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import kfs.boulder.KfsConst;
import kfs.boulder.KfsMain;

public class MainScreen extends BaseScreen {

    public MainScreen(KfsMain game) {
        super(game, false);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);

        ScrollPane scrollPane = new ScrollPane(table, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFillParent(true);

        stage.addActor(scrollPane);

        TextButton.TextButtonStyle buttonStyle = getTextButtonStyle(fontBig, Color.BLACK);

        TextButton playButton = new TextButton("Play", buttonStyle);

        playButton.getColor().a = KfsConst.BUTTON_TRANSPARENCY;
        TextButton helpButton = new TextButton("Info / Help", buttonStyle);
        helpButton.getColor().a = KfsConst.BUTTON_TRANSPARENCY;
        TextButton musicButton = new TextButton("Music play", buttonStyle);
        musicButton.getColor().a = KfsConst.BUTTON_TRANSPARENCY;
        TextButton music2Button = new TextButton("Music stop", buttonStyle);
        music2Button.getColor().a = KfsConst.BUTTON_TRANSPARENCY;
        TextButton editorButton = new TextButton("Map Editor", buttonStyle);
        editorButton.getColor().a = KfsConst.BUTTON_TRANSPARENCY;
        TextButton leaderboardButton = new TextButton("Leaderboard", buttonStyle);
        leaderboardButton.getColor().a = KfsConst.BUTTON_TRANSPARENCY;

        TextButton quitButton = new TextButton("Quit", buttonStyle);
        quitButton.getColor().a = KfsConst.BUTTON_TRANSPARENCY;

        float buttonWidth = 350f;
        float buttonHeight = 80f;

        table.defaults().width(buttonWidth).height(buttonHeight).pad(15f);

        table.add(playButton).row();
        table.add(leaderboardButton).row();
        table.add(helpButton).row();
        table.add(musicButton).row();
        table.add(music2Button).row();
        if (Gdx.app.getType() != Application.ApplicationType.WebGL) {
            table.add(quitButton).row();
            table.add(editorButton).row();
        }

        playButton.addListener(e -> {
            if (playButton.isPressed()) {
                game.setScreen(new LevelSelectScreen(game));
            }
            return false;
        });

        leaderboardButton.addListener(e -> {
            if (leaderboardButton.isPressed()) {
                game.setScreen(new LeaderboardScreen(game));
            }
            return false;
        });

        helpButton.addListener(e -> {
            if (helpButton.isPressed()) {
                game.setScreen(new DrawSimpleMDScreen(game,"info/rules.md"));
            }
            return false;
        });

        musicButton.addListener(e -> {
            if (musicButton.isPressed()) {
                Gdx.app.log("music", "music play");
                game.music.play();
            }
            return false;
        });

        music2Button.addListener(e -> {
            if (music2Button.isPressed()) {
                Gdx.app.log("music", "music stop");
                game.music.stop();
            }
            return false;
        });

        quitButton.addListener(e -> {
            if (quitButton.isPressed()) {
                Gdx.app.exit();
            }
            return false;
        });

        editorButton.addListener(e->{
            if (editorButton.isPressed()) {
                game.setScreen(new MapEditor(game));
            }
            return false;
        });

        stage.setScrollFocus(scrollPane);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }
}

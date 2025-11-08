package kfs.boulder.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import kfs.boulder.KfsConst;
import kfs.boulder.KfsMain;

public class LevelSelectScreen extends BaseScreen {

    public LevelSelectScreen(KfsMain game) {
        super(game, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.defaults().pad(5);
        stage.addActor(table);

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = fontBig;
        Label title = new Label("Select a Level", style);
        table.add(title).colspan(2).padBottom(20).row();

        // Scrollable area for many levels
        Table levelList = new Table();
        ScrollPane scrollPane = new ScrollPane(levelList, skin);
        scrollPane.setFadeScrollBars(false);

        var buttonStyle = getTextButtonStyle(fontSmall, Color.WHITE);
        for (FileHandle file : game.getMaps()) {
            TextButton levelButton = new TextButton(file.nameWithoutExtension(), buttonStyle);
            levelButton.getColor().a = KfsConst.BUTTON_TRANSPARENCY;
            levelButton.addListener(e -> {
                if (levelButton.isPressed()) {
                    Gdx.app.log("LevelSelectScreen", "Selected level: " + file.path());
                    // Load the selected level here
                    game.setScreen(new BoulderDashScreen(game, file.path())); // example
                }
                return false;
            });

            levelList.add(levelButton).width(300).height(24).pad(2).row();
        }

        table.add(scrollPane).expand().fill().colspan(2).row();

        // Back button
        TextButton backButton = new TextButton("Main Menu", buttonStyle);
        backButton.getColor().a = KfsConst.BUTTON_TRANSPARENCY;
        backButton.addListener(e -> {
            if (backButton.isPressed()) {
                game.setScreen(new MainScreen(game));
            }
            return false;
        });

        table.add(backButton).width(300).height(24).padTop(10);
        stage.setScrollFocus(scrollPane);
    }

}


package kfs.boulder.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import kfs.boulder.KfsConst;
import kfs.boulder.KfsMain;

public class DrawSimpleMDScreen extends BaseScreen {

    private final String text;
    private final BitmapFont font;

    public DrawSimpleMDScreen(KfsMain game, String file) {
        super(game, true);
        this.text = parseMarkdown(Gdx.files.internal(file).readString());
        this.font = new BitmapFont(Gdx.files.internal("fonts/PressStart2P-10.fnt"));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = new Label(text, labelStyle);
        label.setWrap(true);
        label.setAlignment(Align.topLeft);
        font.getData().markupEnabled = true;

        ScrollPane scrollPane = new ScrollPane(label, skin);
        scrollPane.setFadeScrollBars(false);

        Table table = new Table();
        table.setFillParent(true);
        table.pad(20);
        table.add(scrollPane).expand().fill().row();

        TextButton backButton = new TextButton("Main Menu", getTextButtonStyle(fontSmall, Color.BLACK));
        backButton.getColor().a = KfsConst.BUTTON_TRANSPARENCY;
        backButton.setWidth(300f);

        backButton.addListener(e -> {
            if (backButton.isPressed()) {
                game.setScreen(new MainScreen(game));
            }
            return false;
        });

        table.add(backButton).width(Gdx.graphics.getWidth() * 0.6f).padTop(10);
        stage.addActor(table);
        stage.setScrollFocus(scrollPane);
    }

    private String parseMarkdown(String md) {
        StringBuilder sb = new StringBuilder();
        String[] lines = md.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("##")) {
                sb.append("[#00ffff]").append(line.substring(2).trim()).append("[]\n");
            } else if (line.startsWith("#")) {
                sb.append("[#ffff00]").append(line.substring(1).trim()).append("[]\n");
            } else {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public void dispose() {
        super.dispose();
        font.dispose();
    }
}

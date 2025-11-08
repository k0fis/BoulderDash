package kfs.boulder;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import kfs.boulder.ui.BoulderDashScreen;
import kfs.boulder.ui.MainScreen;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class KfsMain extends Game {

    public MusicManager music;

    @Override
    public void create() {
        music = new MusicManager("music/");
        //music.play();

        setScreen(new MainScreen(this));
    }


    public List<FileHandle> getMaps() {
        FileHandle levelDir = Gdx.files.internal("maps");
        if (!levelDir.exists() || !levelDir.isDirectory()) {
            Gdx.app.log("KfsMain", "No levels found in /assets/maps/");
            return List.of();
        }

        return Stream.of(levelDir.list())
            .filter(file -> file.name().endsWith(".txt"))
            .sorted((a, b) -> a.name().compareToIgnoreCase(b.name()))
            .toList();
    }

    public String getMap(String before) {
        String prev = "";
        for (FileHandle file : getMaps()) {
            if (before.equals(prev)) {
                return file.path();
            }
            prev = file.path();
        }
        Gdx.app.log("KfsMain", "No next map after " + before + " found");
        return null;
    }

}

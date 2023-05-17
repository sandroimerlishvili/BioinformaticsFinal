package com.bioinformatics.toolbox;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ProteinScreen extends ScreenAdapter {

    private MainClass parent;

    //screen

    private Camera camera;
    private Viewport viewport;
    private Stage stage;
    private Skin skin;
    private Table mainTable;
    private Label proteinLabel;

    //graphics

    private SpriteBatch batch;

    private Texture background;

    // timing

    private float backgroundOffset = 0;
    private float backgroundMaxScrollingSpeed;

    // world parameters

    private final int WORLD_WIDTH = 1280;
    private final int WORLD_HEIGHT = 720;

    // HUD

    BitmapFont font1;

    // font Generators

    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;

    // sequence values

    String dnaSequence;
    String proteinSequence = "";
    int readingFrame = 1;
    boolean readingFrameChanged = false;
    String[][] codonChart = { { "aug", "M" },
            { "uuu", "uuc", "F" },
            { "uua", "uug", "cuu", "cuc", "cua", "cug", "L" },
            { "auu", "auc", "aua", "I" },
            { "guu", "guc", "gua", "gug", "V" },
            { "ucu", "ucc", "uca", "ucg", "agu", "agc", "S" },
            { "ccu", "ccc", "cca", "ccg", "P" },
            { "acu", "acc", "aca", "acg", "T" },
            { "gcu", "gcc", "gca", "gcg", "A" },
            { "uau", "uac", "Y" },
            { "cau", "cac", "H" },
            { "caa", "cag", "Q" },
            { "aau", "aac", "N" },
            { "aaa", "aag", "K" },
            { "gau", "gac", "D" },
            { "gaa", "gag", "E" },
            { "ugu", "ugc", "C" },
            { "ugg", "W" },
            { "cgu", "cgc", "cga", "cgg", "aga", "agg", "R" },
            { "ggu", "ggc", "gga", "ggg", "G" },
            { "uaa", "uag", "uga", "*" } };

    public ProteinScreen(MainClass parentClass, String dnaSequence) {

        // GameClass Setup

        parent = parentClass;

        this.dnaSequence = dnaSequence;

        // initialize backgrounds

        Assets.manager.load(Assets.background, Texture.class);

        background = Assets.manager.get(Assets.background, Texture.class);

        backgroundMaxScrollingSpeed =  (float)(WORLD_HEIGHT) / 2;

        // FONTS & HUD

        initializeFonts();

        skin = Assets.manager.get(Assets.SKIN);

        // audio

        parent.mainMusic = Assets.manager.get(Assets.mainMusic, Music.class);

        parent.mainMusic.setVolume(1f);
        parent.mainMusic.play();

        parent.clickSound = Assets.manager.get(Assets.clickSound, Sound.class);

        batch = new SpriteBatch();

    }

    private void initializeFonts() {

        // create bitmap fonts from file

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font/EdgeFont.otf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        font1 = fontGenerator.generateFont(fontParameter);

    }

    @Override
    public void render(float deltaTime) {

        batch.begin();

        detectInput(deltaTime);

        renderBackgrounds(deltaTime);

        font1.draw(batch, " ", WORLD_WIDTH * 1/4, WORLD_HEIGHT * 9/10 - font1.getXHeight() * 0, (float)WORLD_WIDTH * 1/2, Align.center, false);

        stage.act();
        stage.draw();

        if (readingFrameChanged) {

            proteinLabel.setText(convertToProtein(dnaSequence, readingFrame));

        }

        readingFrameChanged = false;

        batch.end();

    }

    private void renderBackgrounds(float deltaTime) {

        backgroundOffset += (deltaTime * backgroundMaxScrollingSpeed * 1/2);

        batch.draw(background, 0, -backgroundOffset, WORLD_WIDTH, WORLD_HEIGHT);
        batch.draw(background, 0, -backgroundOffset + WORLD_HEIGHT, WORLD_WIDTH, WORLD_HEIGHT);

        if (backgroundOffset > WORLD_HEIGHT) {
            backgroundOffset = 0;
        }

    }

    private void detectInput(float deltaTime) {

        // escape

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {

            System.exit(0);

        }

    }

    @Override
    public void show() {

        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        stage = new Stage(viewport);

        createTable();

        addLabel("Pick a reading frame:", 0, 25);

        addFrameTextButton("Frame 1", "laser").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                parent.clickSound.play(0.7f);
                readingFrame = 1;
                readingFrameChanged = true;
            }
        });

        addFrameTextButton("Frame 2", "laser").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                parent.clickSound.play(0.7f);
                readingFrame = 2;
                readingFrameChanged = true;
            }
        });

        addFrameTextButton("Frame 3", "laser").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                parent.clickSound.play(0.7f);
                readingFrame = 3;
                readingFrameChanged = true;
            }
        });

        proteinLabel = addLabel(convertToProtein(dnaSequence, readingFrame), 100, 0);
        proteinLabel.setWrap(true);

        addMainTextButton("Try Another Sequence", "warning", 100, 25).addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                parent.clickSound.play(0.7f);
                dispose();
                parent.changeScreen(parent.MAINMENU);
            }
        });





        Gdx.input.setInputProcessor(stage);

    }

    private void createTable() {

        mainTable = new Table();
        mainTable.setFillParent(true);

        stage.addActor(mainTable);

        mainTable.setPosition(0, 0);

        //mainTable.setDebug(true);

    }

    private Label addLabel(String name, int padTop, int padBottom) {

        Label label = new Label(name, skin);
        label.setColor(77f / 255f, 210f / 255f, 219f / 255f, 255f / 255f);

        label.setAlignment(Align.center);

        mainTable.add(label).width(1000).height(50).padTop(padTop).padBottom(padBottom);
        mainTable.row();

        return label;

    }

    private ImageTextButton addMainTextButton(String name, String styleName, int padTop, int padBottom) {

        ImageTextButton button = new ImageTextButton(name, skin, styleName);

        button.setColor(77f / 255f, 210f / 255f, 219f / 255f, 100);

        mainTable.add(button).width(1000).height(100).padTop(padTop).padBottom(padBottom);
        mainTable.row();
        return button;

    }

    private ImageTextButton addFrameTextButton(String name, String styleName) {

        ImageTextButton button = new ImageTextButton(name, skin, styleName);

        button.setColor(77f / 255f, 210f / 255f, 219f / 255f, 100);


        mainTable.add(button).width(175).height(50);
        mainTable.row();
        return button;

    }

    private String convertToProtein(String dnaSequence, int readingFrame) {

        System.out.println("DNA: " + dnaSequence);

        dnaSequence = dnaSequence.replace("t", "u");

        System.out.println("\nRNA: " + dnaSequence);

        ArrayList<String> codons = new ArrayList<String>();

        for (int i = readingFrame - 1; i < dnaSequence.length() - 2; i += 3) {

            codons.add(dnaSequence.substring(i, i + 3));

        }

        System.out.println("\n" + codons);

        for (int i = 0; i < codons.size(); i++) {

            for (int j = 0; j < codonChart.length; j++) {

                for (int k = 0; k < codonChart[j].length - 1; k++) {

                    if (codons.get(i).equals(codonChart[j][k])) {

                        codons.set(i, codonChart[j][codonChart[j].length - 1]);

                    }

                }

            }

        }

        proteinSequence = codons.toString().replaceAll("[,\\[\\]]", "").replace(" ", "");

        System.out.println("\nProtein: " + proteinSequence);

        return proteinSequence;

    }

    @Override
    public void resize(int width, int height) {

        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);

    }

    @Override
    public void dispose() {

        //screen

        stage.dispose();

        //graphics

        batch.dispose();

        background = null;

        // HUD

        font1.dispose();

        // font Generators

        fontGenerator.dispose();
        fontParameter = null;


    }

}
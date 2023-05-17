package com.bioinformatics.toolbox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import jdk.tools.jmod.Main;

public class ProteinScreen extends ScreenAdapter {

    private MainClass parent;

    //screen

    private Camera camera;
    private Viewport viewport;
    private Stage stage;
    private Skin skin;
    private Table mainTable;

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

    // binary values

    String dnaSequence;
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
            { "uaa", "uag", "uga", "STOP" } };

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

        addLabel("Pick a reading frame");

        addMainTextButton("Try Another Sequence").addListener(new ClickListener() {
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

    }

    private Label addLabel(String name) {

        Label label = new Label(name, skin);
        label.setColor(77f / 255f, 210f / 255f, 219f / 255f, 255f / 255f);

        label.setAlignment(Align.center);

        mainTable.add(label).width(1000).height(100).padBottom(10);
        mainTable.row();
        return label;

    }

    private ImageTextButton addMainTextButton(String name) {

        ImageTextButton button = new ImageTextButton(name, skin, "warning");

        button.setColor(77f / 255f, 210f / 255f, 219f / 255f, 100);

        mainTable.add(button).width(1000).height(100).padBottom(25);
        mainTable.row();
        return button;

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
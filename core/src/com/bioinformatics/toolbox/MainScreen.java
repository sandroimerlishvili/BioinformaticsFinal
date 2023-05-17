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

public class MainScreen extends ScreenAdapter {

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

    private TextField textField;

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

    // audio

    public MainScreen(MainClass parentClass) {

        // GameClass Setup

        parent = parentClass;

        // initialize backgrounds

        background = Assets.manager.get(Assets.background, Texture.class);

        backgroundMaxScrollingSpeed =  (float)(WORLD_HEIGHT) / 2;

        // FONTS & HUD

        initializeFonts();

        skin = Assets.manager.get(Assets.SKIN);

        // audio

        parent.mainMusic = Assets.manager.get(Assets.mainMusic, Music.class);

        parent.mainMusic.setVolume(1f);
        parent.mainMusic.setLooping(true);
        parent.mainMusic.play();

        parent.clickSound = Assets.manager.get(Assets.clickSound, Sound.class);
        parent.errorSound = Assets.manager.get(Assets.errorSound, Sound.class);

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

        // restrict textField input

        if (textField != null) {

            String text = textField.getText();

            text = text.replaceAll("[^atgc]", "");

            textField.setText(text);
            textField.setCursorPosition(textField.getText().length());

        }

        renderBackgrounds(deltaTime);

        font1.draw(batch, " ", WORLD_WIDTH * 1/4, WORLD_HEIGHT * 5/6 - font1.getXHeight() * 0, (float)WORLD_WIDTH * 1/2, Align.center, false);

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

        addLabel("Welcome to the unofficial DSAP toolbox! Type in your DNA sequence below and it will be " +
                "\nconverted to a protein sequence with a chosen reading frame." +
                "\n(ONLY input valid base pair letters and at least 15 letters)");

        textField = addTextField("");

        addTextButton("Submit").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {

                if (textField.getText().length() >= 15) {

                    parent.clickSound.play(1f);
                    parent.dnaSequence = textField.getText().toLowerCase();
                    dispose();
                    parent.changeScreen(parent.PROTEIN);

                } else {

                    parent.errorSound.play(1f);

                }
            }
        });

        addTextButton("Exit").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                parent.clickSound.play(0.7f);
                System.exit(0);
            }
        });

        Gdx.input.setInputProcessor(stage);

    }

    private void createTable() {

        mainTable = new Table();
        mainTable.setFillParent(true);

        stage.addActor(mainTable);

        //mainTable.setPosition(0, 0);

    }

    private Label addLabel(String name) {

        Label label = new Label(name, skin);
        label.setColor(77f / 255f, 210f / 255f, 219f / 255f, 255f / 255f);

        label.setAlignment(Align.center);

        mainTable.add(label).width(1000).height(100).padBottom(10);
        mainTable.row();
        return label;

    }

    private ImageTextButton addTextButton(String name) {

        ImageTextButton button = new ImageTextButton(name, skin, "default");

        button.setColor(77f / 255f, 210f / 255f, 219f / 255f, 255f / 255f);

        mainTable.add(button).width(1000).height(100).padBottom(25);
        mainTable.row();
        return button;

    }

    private TextField addTextField(String name) {

        TextField textField = new TextField(name, skin);

        mainTable.add(textField).width(700).height(50).padBottom(25);
        mainTable.row();
        return textField;

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
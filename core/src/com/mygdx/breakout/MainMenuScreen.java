package com.mygdx.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MainMenuScreen implements Screen {

    private final Breakout game;
    private final OrthographicCamera camera;
    private Label label3;

    public MainMenuScreen(Breakout game) {
        this.game = game;
        camera = new OrthographicCamera();

        camera.setToOrtho(false, 200, 200);

        Skin mySkin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        label3 = new Label("This is a Label (skin) on  5 columns ", mySkin,"default");
        label3.setSize(0.3f,60);
        label3.setPosition(0,10);
//        addActor(label3);

//        Label label = new Label();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        label3.draw(game.batch, 1);

        game.font.draw(game.batch, "Welcome to Breakout!!! ", 25, 25);
        game.font.draw(game.batch, "Tap anywhere to begin!", 20, 20);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}

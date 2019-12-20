/*
 * Created by Vadim Dück on 20.12.19 19:40
 * Copyright (c) 2019 . All rights reserved.
 * Last modified 20.12.19 19:40
 *
 */

package de.hhn.aib.swlab.wise1920.group06.exercise3.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

import de.hhn.aib.swlab.wise1920.group06.exercise3.MyUnoGame;

public class PlayScreen implements Screen {
    private MyUnoGame game;
    Texture texture;

    public PlayScreen(MyUnoGame game) {
        this.game = game;
        texture = new Texture("Wild_Draw.png");
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // render the stage
        Gdx.gl.glClearColor(1, 0,0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.batch.draw(texture, 0, 0);
        game.batch.end();
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

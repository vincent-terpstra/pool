package com.vdt.poolgame.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.vdt.poolgame.game.draw.PoolBallShader;
import com.vdt.poolgame.game.table.PoolBall;
import com.vdt.poolgame.library.ShaderProgram;
import com.vdt.poolgame.library.SpriteArray;

/**
 * Vincent Terpstra
 * Aug 8 2019
 * Allows a single poolball to be rendered
 */

public class SingleBall extends ApplicationAdapter {
    private PoolBallShader batch;
    private PoolBall ball;

    @Override
    public void create(){
        SpriteArray array = new SpriteArray("images");
        array.getTexture().bind();
        batch = new PoolBallShader(array);
        ball = new PoolBall(10, 0, 0);
        batch.set(1.2f, 1.2f);
    }

    @Override
    public void render(){
        ShaderProgram.clearScreen();
        batch.begin();
        batch.drawBall(ball);
    }


}

package com.vdt.poolgame.game.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.vdt.poolgame.game.table.PoolBall;
import com.vdt.poolgame.library.SpriteArray;


public class PoolBallBatch implements PoolBallDraw {
    private final ShaderProgram shaderProgram;
    private final TableBatch batch;

    TextureRegion[] regions;

    public PoolBallBatch(TableBatch batch, SpriteArray draw){
        shaderProgram = new ShaderProgram(
                Gdx.files.internal("poolball.vert").readString(),
                Gdx.files.internal("poolball.frag").readString()
        );

        if(shaderProgram.getLog().length() != 0){
            System.out.print(shaderProgram.getLog());
        }

        this.batch = batch;

        regions = new TextureRegion[16];

        for(int i = 0; i < regions.length; i++){
            regions[i] = draw.getTextureRegion(i != 0 ? Integer.toString(i) : "cue");
        }

        //light source
        final float x = 2, y = 5, z = 10;
        float mag = (float)Math.sqrt(x * x + y * y + z * z);
        light = new float[]{x/mag, y/mag, z/mag};
    }

    final float[] light;

    @Override
    public void dispose() {
        shaderProgram.dispose();
    }

    @Override
    public void begin() {
        batch.setShader(shaderProgram);

    }

    @Override
    public void drawBall(PoolBall ball) {
        batch.superBegin();
        TextureRegion region = regions[ball.id()];
        Gdx.gl.glUniform3f(shaderProgram.getUniformLocation("u_pos"),
                batch.x(ball.x(), 0f), batch.y(ball.y(), 0f), batch.getRatio() /2);
        Gdx.gl.glUniform3f(shaderProgram.getUniformLocation("u_light"),
                    light[0], light[1], light[2]
                );
        Gdx.gl.glUniformMatrix3fv(shaderProgram.getUniformLocation("u_mat3"),
                1, false, ball.matrix.getMatrix(), 0);

        Gdx.gl.glUniform3f(shaderProgram.getUniformLocation("u_uv"),
                (region.getU() + region.getU2()) / 2,
                (region.getV() + region.getV2()) / 2,
                (region.getV2() - region.getV()) / 2);

            batch.draw(region, ball.x(), ball.y(), 1.1f, 1.1f);
        batch.end();
    }
}

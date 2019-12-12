package com.vdt.poolgame.game.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.vdt.poolgame.game.PoolGame;
import com.vdt.poolgame.game.table.Pocket;
import com.vdt.poolgame.game.table.PoolBall;
import com.vdt.poolgame.game.table.PoolTable;
import com.vdt.poolgame.library.PointXY;
import com.vdt.poolgame.library.SpriteArray;

public class TableBatch extends SpriteBatch implements TableDraw {
    private ShaderProgram shaderProgram;

    private final TextureRegion loop, circle, stripe, solid, arrow, line, pocket,left, side, black, wood;
    public final float ratio;
    private final float WIDTH, HEIGHT;

    public TableBatch(SpriteArray array){
        loop    = array.getTextureRegion("loop");
        circle  = array.getTextureRegion("circle");
        stripe  = array.getTextureRegion("stripe");
        solid   = array.getTextureRegion("solid");
        arrow   = array.getTextureRegion("arrow");
        line    = array.getTextureRegion("rect");
        pocket  = array.getTextureRegion("pocket"); //, Pocket.RADIUS * 2, 0),
        left    = array.getTextureRegion("left"); //, 0, 4, 0, 0),
        side    = array.getTextureRegion("right");//, 0, 4, -1, 0),
        black   = array.getTextureRegion("black");//, 1, 1, -.5f, -.5f);
        wood    = array.getTextureRegion("wood");

        ratio   = Gdx.graphics.getWidth() / PoolGame.getWidth();
        WIDTH   = Gdx.graphics.getWidth() / 2;
        HEIGHT  = Gdx.graphics.getHeight() / 2;

        magic = (left.getU2() - left.getU()) * 2 / (left.getV2() - left.getV());
        magic2 = (side.getU2() - side.getU()) * 2 / (side.getV2() - side.getV());

        shaderProgram = createDefaultShader();

    }

    float magic;
    float magic2;
    @Override
    public void dispose() {
        super.dispose();
        shaderProgram.dispose();
    }

    public void superBegin(){
        super.begin();
    }

    @Override
    public void begin() {
        setShader(shaderProgram);
        super.begin();
        float width = PoolGame.getWidth();
        float delta = (PoolGame.getHeight() - PoolTable.HEIGHT)/2;
        float b_y = PoolTable.HEIGHT + delta + 2;
        draw(wood, 0, b_y,  width, delta);
        draw(wood, 0, -b_y,  -width, delta);

        float scaleX = 1, scaleY = 1;
        for(int j = 0; j < 4; j++) {
            draw(pocket, scaleX * corner.x(), scaleY * corner.y(), Pocket.RADIUS, Pocket.RADIUS);

            draw(side, scaleX * (PoolTable.WIDTH - 5.5f - Pocket.RADIUS * 2 * .7071f), scaleY * (PoolTable.HEIGHT + 2),  scaleX * magic2, scaleY * 2);
            //draw(side, scaleX * (PoolTable.WIDTH), scaleY * (PoolTable.HEIGHT + 4 - Pocket.RADIUS * 2 * .7071f), 0, 1,-scaleY, scaleX);

            draw(left, scaleX * magic, scaleY *( PoolTable.HEIGHT + 2),  scaleX * magic, scaleY * 2);
            if(j==0) scaleX = -1;
            if(j==1) scaleY = -1;
            if(j==2) scaleX =  1;
        }
        draw(side, WIDTH+(2 + PoolTable.WIDTH/2)*ratio,HEIGHT-.16f*ratio,0,0, magic2, 2, ratio, ratio, 90 );
        draw(side, WIDTH+(2 + PoolTable.WIDTH/2)*ratio,HEIGHT+.16f*ratio,0,0, magic2, 2,-ratio, ratio, 90 );
        draw(side, WIDTH-(2 + PoolTable.WIDTH/2)*ratio,HEIGHT+.16f*ratio,0,0, magic2, 2,-ratio,-ratio, 90 );
        draw(side, WIDTH-(2 + PoolTable.WIDTH/2)*ratio,HEIGHT-.16f*ratio,0,0, magic2, 2, ratio,-ratio, 90 );
        //Head and Foot spot
        draw(pocket, PoolTable.HEIGHT, 0,  .5f, .5f);
        draw(pocket, -PoolTable.HEIGHT, 0,  .5f, .5f);

        //headline
        draw(black, -PoolTable.HEIGHT, 0, .07f, PoolTable.WIDTH / 2);
        //draw(black, -PoolTable.HEIGHT, 0, PoolTable.WIDTH / 2, .07f);

    }
    PointXY corner;
    @Override
    public void drawArrow(PoolBall cue, PointXY angle, float length, float rotation) {
        length *= ratio / 2;
        float x = cue.x(), y = cue.y();
        draw(arrow, WIDTH + x * ratio / 2, HEIGHT - length * angle.x() - y * ratio / 2, 0,0, length, length, 1, 1, rotation );
    }

    @Override
    public void drawEdge(SpriteArray array, PointXY corner) {
        this.corner = corner;
    }

    @Override
    public void drawLine(PointXY move, PointXY delta, float width, float height) {
        float x = move.x(), y = move.y();
        double rotation = Math.toDegrees(Math.atan2(delta.y(), -delta.x()));
        draw(line, WIDTH + x * ratio /2, HEIGHT - y * ratio / 2, 0,  0.5f , 1, 1, -width * ratio / 2, height * ratio / 2 , (float)rotation);
    }

    @Override
    public void drawLoop(PointXY cue, float rad) {
        draw(loop, cue.x(), cue.y(), rad, rad);
    }

    @Override
    public void drawCircle(PointXY cue, float rad) {
        draw(circle, cue.x(), cue.y(), rad/2, rad/2);
    }

    @Override
    public void draw(TextureRegion region, float x, float y, float width, float height){
        super.draw(region, x(x, width), y(y, height), width * ratio, height * ratio);
    }

    public float getRatio(){
        return ratio;
    }

    public float x(float x, float width){
       return  WIDTH + (x - width)* ratio / 2;
    }

    public float y(float y, float height){
        return HEIGHT - (height + y) * ratio / 2;
    }

    @Override
    public void drawInd(int id, PoolBall ball, float rad) {
        TextureRegion type = loop;
        if(id == 8) type = solid;
        if(id  > 8) type = stripe;
        draw(type, ball.x(), ball.y(), 2, 2);
    }

    @Override
    public void drawSunk(int id, float x, float y, float rad) {
        rad /= 2;
        TextureRegion type = solid;
        if(id == 8) type = circle;
        if(id  > 8) type = stripe;
        draw(type, x, y, rad, rad);
    }

    @Override
    public void end() {
        super.end();
    }
}

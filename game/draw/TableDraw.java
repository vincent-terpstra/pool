package com.vdt.poolgame.game.draw;

import com.vdt.poolgame.game.table.PoolBall;
import com.vdt.poolgame.library.PointXY;
import com.vdt.poolgame.library.SpriteArray;

public interface TableDraw {
    void dispose();

    void begin();

    void drawArrow(PoolBall cue, PointXY angle, float length, float rotation);

    void drawEdge(SpriteArray array, PointXY corner);

    void drawLine(PointXY move, PointXY delta, float width, float height);

    void drawLoop(PointXY cue, float rad);

    void drawCircle(PointXY cue, float rad);

    void drawInd(int id, PoolBall ball, float rad);

    void drawSunk(int idx, float x, float y, float rad);

    void end();
}

package com.vdt.poolgame.game.draw;

import com.vdt.poolgame.game.table.PoolBall;

public interface PoolBallDraw {

    void dispose();

    void begin();

    void drawBall(PoolBall ball);
}

package com.vdt.poolgame.game.table;

import com.vdt.poolgame.library.PointXY;

public class Pocket extends PointXY {
	public static final float RADIUS = 2.3f;

	public final boolean checkCollide(PoolBall ball){
		PointXY diff = new PointXY().set(this).move(-1, ball);
		float rad = diff.normalize();
		if(rad < .5f){
		    ball.resetSpeed();
		    return true;
        } else if(rad < RADIUS - 1){
			ball.dropPocket(diff);
			return true;
		} else if(rad < RADIUS){
			ball.setSpeed(diff, 1);
		}
		return false;
	}
}

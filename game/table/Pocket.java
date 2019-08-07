package com.vdt.poolgame.game.table;

import com.vdt.poolgame.game.draw.DefaultShader;
import com.vdt.poolgame.library.PointXY;

public class Pocket extends PointXY {
	public static final float radius = 2.3f;
	public Pocket(float x, float y){
		set(x * (PoolTable.width -.3f)* 2, y * (PoolTable.height - .3f) * 2);
	}
	public Pocket(float y) {
		set(0, y * (PoolTable.height + .5f) * 2);
	}

	public final boolean checkCollide(PoolBall ball){
		PointXY diff = new PointXY().set(this).move(-1, ball);
		float rad = diff.normalize();
		if(rad < .5f) {
			ball.velocity.set(0,0);
		} else if(rad < radius - 1){
			ball.dropPocket(diff);
		} else if(rad < radius){
			ball.setSpeed(diff, 1);
		} else {
		    return false;
        }
		return true;
	}
	
	public final void draw(DefaultShader shader) {
		shader.draw(shader.drawPocket, this );
	}
}

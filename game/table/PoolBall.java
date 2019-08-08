package com.vdt.poolgame.game.table;

import com.vdt.poolgame.library.Matrix3;
import com.vdt.poolgame.library.PointXY;

public final class PoolBall extends PointXY {
	public final PointXY velocity = new PointXY();
	public final int id;
	private float speed;
	float pocket = 0;
	public final Matrix3 matrix;

	public boolean checkCollide(PoolBall ball) {
		if(ball.pocket != 0 || pocket != 0) return false;
		PointXY normal = new PointXY().set(this).move( -1, ball);
		if(normal.dot(normal) < 4){ //distance of balls 
			normal.normalize();		//direction of the impulse
			float dot = new PointXY() 
				.move(speed, velocity) 			  //current velocity
				.move(-ball.speed, ball.velocity) //relative velocity
				.dot(normal);					  //magnitude of the impulse
			if(dot > 0) return false; //balls moving away from each other
			normal.scale(dot); 	//impulse / change in velocity
			ball.setSpeed(normal, 1);
			setSpeed(normal, -1);
			return true;
		}
		return false;
	}
	public void dropPocket(PointXY diff) {
		pocket = 1;
		velocity.set(diff);
	}
	/*
	public void setRandomSpeed() {
		double rad = Math.random() * Math.PI * 2;
		setSpeed((float)Math.cos(rad), (float)Math.sin(rad), 8);
	}
	*/
	void setSpeed(PointXY _normal, float dt){
		speed = velocity.scale(speed).move(dt, _normal).normalize();
	}
	public void setSpeed(PointXY _speed) {
		speed = velocity.set(_speed).normalize();
	}

	public void resetSpeed(){
		speed = 0;
		velocity.set(0,0);
	}

	public PoolBall(int id, float x, float y) {
		set(x, y);
		this.id = id;
		matrix = new Matrix3();
	}
	
	public final boolean update(float delta) {
		if(speed  <= 0){ 
			speed = 0;
		} else {
			move(delta * speed, velocity);
			matrix.rotateY(-velocity.x() * speed * delta, -velocity.y() * speed * delta);
			speed -= 2 * delta;
		}
		return speed != 0;
	}

}

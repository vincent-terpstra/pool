package com.vdt.poolgame.game.table;

import com.vdt.poolgame.library.Matrix3;
import com.vdt.poolgame.library.PointXY;

public final class PoolBall extends PointXY {
	public final PointXY velocity = new PointXY();
	public final Matrix3 matrix = new Matrix3();
	private int id;
	private float speed;

	public int id(){
		return id;
	}

	public boolean checkCollide(PoolBall ball) {
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
		velocity.set(diff);
		speed *= .8f;
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

	public void reset(int id, float x, float y){
		set(x, y);
		this.id = id;
		matrix.identity();
		resetSpeed();
	}
	
	public final boolean update(float delta) {
		if(speed  <= 0){ 
			speed = 0;
		} else {
			move(delta * speed, velocity);
			matrix.rotateY(-velocity.x() * speed * delta, -velocity.y() * speed * delta);
			speed -= 3f * delta;
		}
		return speed != 0;
	}

	public final PointXY positive(){
		return new PointXY().set( x > 0 ? 1 : -1, y > 0 ? 1 : -1);
	}

	void scale(PointXY pos){
		scale(pos.x(), pos.y());
		velocity.scale(pos.x(), pos.y());
	}

}

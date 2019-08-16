package com.vdt.poolgame.game.table;

import java.util.ArrayList;
import java.util.List;

import com.vdt.poolgame.game.PoolControl;
import com.vdt.poolgame.game.draw.DefaultShader;
import com.vdt.poolgame.game.draw.PoolBallShader;
import com.vdt.poolgame.library.PointXY;
import com.vdt.poolgame.library.SpriteArray;

public class PoolTable {
	public static final float height = 7.5f, width = 2 * height;
	private static final float boundX = width * 2 - 2.5f, boundY = height * 2 - 2.5f;
	
	private final float[] left, side, mask;
	public boolean locked = false;
	public PoolTable(SpriteArray array){
		left = array.get("left", 0, 4, 0, 0);
		side = array.get("right", 0, 4, -1, -1);
		mask = array.get("mask", 2, 2,-.5f, 0);

		balls.add(new PoolBall(0, -10, 0)); //add cue ball
		rack();
		centre = new Pocket(1);
		corner = new Pocket(1,1);

		final float rad = 1;
		final float sqrt = (float)Math.sqrt(2) * rad / 2;
		
		final float p_rsqrt = Pocket.radius * sqrt;
		final float diff = p_rsqrt + 1;
		PointXY p1 = new PointXY().set(width * 2 + p_rsqrt, height * 2 - p_rsqrt);
		PointXY p2 = p1.clone().move(-diff, -diff);
		Circle c = (Circle)new Circle(rad).set(p2).move(sqrt, -sqrt);
		PointXY p3 = new PointXY().set(c).move(-rad,  0);
		float delta = 4 - (p1.x() - p3.x());
		p1.move(delta, delta);
		right = new TableObject[] {
			new Edge(p3.clone(), p3.move(0, -width)),
			new Edge(p1, p2),
			c
		};
		s_right = p1.clone();
		p3.set(Pocket.radius, width +.5f);
		final float dx = 2f - sqrt;
		PointXY p4 = p3.clone().move(dx/2, -dx);
		p1.move(-width, width).invert();
		p2.move(-width, width).invert();
		Circle 
			c2 = (Circle)new Circle(rad).set(p4).move((float)Math.sqrt(3)/2, .5f),
			c3 = (Circle)new Circle(rad).set(c).move(-width, width).invert();
		top = new TableObject[] {
			new Edge(c2.clone().move(0, -rad), c3.clone().move(0, -rad)),
			new Edge(p1, p2),
			new Edge(p3, p4),
			c2,
			c3
			
		};
		b_y = ((Edge)top[0]).first.y();
		b_right = p1.clone();
		
	}
	public final List<PoolBall>
			balls = new ArrayList<PoolBall>(),
			inPocket = new ArrayList<PoolBall>();
	private final Pocket centre, corner;
	private final TableObject[] right, top;
	
	private void rack(){
        float[] rackPos = {
                0, 0,	1, 1,	2,-2,	3, 3,
                3,-1,	4,-4,	4, 2,
                2, 0,
                1,-1,	2, 2,	3,-3,	3, 1,
                4, 4,	4,-2,	4, 0
        };
		float delta = (float)Math.sqrt(3);
		for(int i = 0; i < rackPos.length;){
			balls.add(new PoolBall(i / 2 + 1,10 + rackPos[i++] * delta, rackPos[i++]));
		}
	}

	public final void update(float delta) {
		locked = false;
		for(PoolBall ball : inPocket){
			ball.update(delta);
			checkShift(ball, cornerCheck);
		}
		for(PoolBall ball : balls) {
			locked = ball.update(delta) || locked;
		}
		if(locked) {
			for (int idx = 0; idx < balls.size(); ) {
				PoolBall current = balls.get(idx);
				for (int j = idx+1; j < balls.size(); j++)
					current.checkCollide(balls.get(j));

				if(checkShift(current, edgeCheck)){
					balls.remove(idx);
					inPocket.add(current);
				} else {
					idx++;
				}
			}
		}
	}

	public final void predict(DefaultShader shader, PointXY angle, float velocity){
		if(!locked){
			PoolBall cue = balls.get(0);
			PointXY tmp = new PointXY().set(cue);
			cue.setSpeed(new PointXY().set(angle).scale(velocity));

			findCollision(shader, cue);

			shader.drawCircle(cue, 2f);
			cue.resetSpeed();
			cue.set(tmp);
		}

	}

	private final void findCollision(DefaultShader shader, PoolBall cue){
		for(int i = 0; i < 100; i++){
			cue.update(.02f);
			for(int j = 1; j < balls.size(); j++){
				PoolBall at = balls.get(j);
				if(at.checkCollide(cue)){
					at.resetSpeed();
					return;
				}
			}
			if(checkShift(cue, findCollide))
				return;
		}
	}

	final BoolShift
			cornerCheck = new BoolShift() {
		@Override
		public boolean check(PoolBall ball) {
			return corner.checkCollide(ball) || centre.checkCollide(ball);
		}
	}, 		edgeCheck = new BoolShift() {
		@Override
		public boolean check(PoolBall ball) {
			boolean pocket = corner.checkCollide(ball);

			if (ball.y() > boundY) {
				checkCollide(ball, top);
				pocket = centre.checkCollide(ball) || pocket;
			}
			if(ball.x() > boundX) {
				checkCollide(ball, right);
			}
			return pocket;
		}
	},		findCollide = new BoolShift() {
		@Override
		public boolean check(PoolBall ball) {
			return corner.checkCollide(ball) || (ball.y() > boundY && (checkCollide(ball, top) || centre.checkCollide(ball))) ||
					ball.x() > boundX && checkCollide(ball, right) ;
		}
	};



	private interface BoolShift {
		boolean check(PoolBall ball);
	}

	private boolean checkShift(PoolBall current, BoolShift func){
		float
				shiftX = current.x() < 0 ? -1 : 1,
				shiftY = current.y() < 0 ? -1: 1;
		current.scale(shiftX, shiftY);
		current.velocity.scale(shiftX, shiftY);

		boolean check = func.check(current);

		current.scale(shiftX, shiftY);
		current.velocity.scale(shiftX, shiftY);

		return check;

	}

	private boolean checkCollide(PoolBall ball, TableObject[] objs) {
		for(TableObject o : objs) {
			if(o.checkCollide(ball)) return true;
		}
		return false;
	}
	private final PointXY b_right, s_right;
	private final float b_y;

	public void draw(DefaultShader shader, PoolControl control, PoolBallShader batch) {

		DefaultShader.clearScreen();
		shader.begin();
		drawCentre(shader);
		shader.setScale(-1,-1);
		drawCentre(shader);
		drawEdge(shader, - 1, 1);
		drawEdge(shader, 1, -1);
		drawEdge(shader, -1, -1);
		drawEdge(shader, 1, 1);

		control.draw(shader);
		shader.end();
		batch.begin();
		for(PoolBall ball: balls)
			batch.drawBall(ball);

		for(PoolBall ball: inPocket)
			batch.drawBall(ball);
	}

	private void drawCentre(DefaultShader shader){
	    float d_y = DefaultShader.Height() - b_y -2;
	    if(d_y > 0)
		    shader.draw(mask, 0, b_y + 2, 1, 0, DefaultShader.Width(), d_y);
	    float d_x = DefaultShader.Width() - s_right.x() + 2;
	    if(d_x > 0)
		    shader.draw(mask, s_right.x() - 2 , 0, 0, 1, DefaultShader.Height(), d_x);
		centre.draw(shader);
		shader.draw(mask, 0, b_y + 4, 1, 0, 5, 1);
	}

	
	private void drawEdge(DefaultShader shader, float x, float y) {
		shader.setScale(x, y);
		corner.draw(shader);
		shader.draw(side, b_right);
		shader.draw(side, s_right.x(), s_right.y(), 0, 1, -1, 1);
		shader.draw(left , 0, b_y);
		/**
		for(TableObject e : right) {
			e.draw(shader);
		}
		for(TableObject e : top) {
			e.draw(shader);
		}
		/**/
	}
	
	public interface TableObject {
		boolean checkCollide(PoolBall ball);
		void draw(DefaultShader shader);
	}
	
}

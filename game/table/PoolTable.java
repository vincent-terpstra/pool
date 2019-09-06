package com.vdt.poolgame.game.table;

import java.util.ArrayList;
import java.util.List;

import com.vdt.poolgame.game.draw.SunkDisplay;
import com.vdt.poolgame.game.draw.TableShader;
import com.vdt.poolgame.game.draw.PoolBallShader;
import com.vdt.poolgame.library.PointXY;
import com.vdt.poolgame.library.ShaderProgram;
import com.vdt.poolgame.library.SpriteArray;

public final class PoolTable {
	private final SunkDisplay sunk;

	public static final float HEIGHT = 18f, WIDTH = 2 * HEIGHT;
	private static final float boundX = WIDTH - 1, boundY = HEIGHT - 1;
    public int showType = 0;
    public PointXY sign = new PointXY().set(- (WIDTH - 4), -HEIGHT + 4);

	private boolean locked = false, cuePocket = false;


	public PoolTable(SpriteArray array, TableShader shader){
		for(int i = 0; i < 16; i++)
			balls.add(new PoolBall());

		centre = new Pocket();
		corner = new Pocket();

		corner.set(WIDTH +.6f, HEIGHT + .6f);
		centre.set(0, HEIGHT + Pocket.RADIUS);

		//MAGIC NUMBERS for the collision edge objects (Table is unchanging)
		final float rad = 1;
		right = new TableObject[] {
			new Edge(36.0f, 14.3331f, 36.0f, -2),
			new Edge(40.0f, 18.747316f, 36.2929f, 15.04021f),

			(Circle)new Circle(rad).set(37, 14.3331f)
		};

		top = new TableObject[] {
			new Edge(3.8f, 18.0f, 32.3f, 18f),
			new Edge(36.75f, 22.0f, 33.0f, 18.3f),
			new Edge(2.3f, 19.8f, 2.95f, 18.5f),

			(Circle)new Circle(rad).set(3.81f	, 19),
			(Circle)new Circle(rad).set(32.33f, 19)
		};
		//add edges to the shader buffer to draw the table
		shader.drawEdge(array, corner);
		//only need to add these edges once

		sunk = new SunkDisplay(array);

		rack();
	}

	public final List<PoolBall>
			balls = new ArrayList<PoolBall>(),
			inPocket = new ArrayList<PoolBall>();
	private final Pocket centre, corner;
	private final TableObject[] right, top;
	
	public void rack(){
	    kitchen = true; //can move the cue ball
		showType = 0;
		sunk.reset();
        float[] rackPos = {
                0, 0,	1, 1,	2,-2,	4, 2,
                3,-1,	4,-4,	3, 3,
                2, 0,	//8 BALL
                4, 0,	4,-2,  3, 1,	1,-1,
				2, 2, 	4, 4, 	3, -3,
        };
		float delta = (float)Math.sqrt(3);

		while(inPocket.size() > 0){
			balls.add(inPocket.remove(0));
		}


		balls.get(0).reset(0, -HEIGHT, 0); //add cue ball

		for(int i = 0; i < rackPos.length;){
			balls.get(i/2 +1).reset(i / 2 + 1,HEIGHT + rackPos[i++] * delta, rackPos[i++]);
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
				//prevent the cue from colliding while in pocket
				if(idx != 0 || !cuePocket) {
					for (int j = idx + 1; j < balls.size(); j++)
						current.checkCollide(balls.get(j));
				}

				if(checkShift(current, edgeCheck)){
					if(current.id() != 0) {
						balls.remove(idx);
						inPocket.add(current);
						sunk.add(current.id());
						if(showType == 0)
							showType = current.id() > 8 ? 2 : 1;
					} else {
						//Cue ball in pocket
						cuePocket = true;
						idx++;
					}
				} else {
					idx++;
				}
			}
		}
	}

	public final void predict(TableShader shader, PointXY  velocity){
		if(!locked){
			PoolBall cue = balls.get(0);
			PointXY tmp = new PointXY().set(cue);
			cue.setSpeed(velocity);

			findCollision( cue);

			shader.draw(shader.circle, cue, 2f);
			cue.resetSpeed();
			cue.set(tmp);
		}
	}

	public final boolean isLocked(){
	    return locked;
    }

	public final boolean canMoveCue(float x, float y){
	    return !locked && balls.get(0).range(x, y, 4);
    }

	public final void moveCue(float x, float y){
		if( locked ) return;

		cuePocket = false;
		PoolBall cue = balls.get(0);
		PointXY tmp = cue.clone();
		cue.set(x, y);
		for(int i = 1; i < balls.size(); i++){
			if(balls.get(i).checkCollide(cue)) {
					cue.set(tmp);
					return;
				}
		}
		cue.limit(kitchen ? -HEIGHT :  boundX, - boundX, boundY, - boundY);

    }

    public boolean cuePocket(){
		return cuePocket;
	}

    boolean kitchen = true; // ball must be in kitchen

    public final void fireCue(PointXY speed){
    	if(locked || cuePocket) return;

		balls.get(0).setSpeed(speed);

		kitchen = false;
	}

	private final void findCollision(PoolBall cue){
		for(int i = 0; i < 100; i++){
			cue.update(.01f);
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

	private interface BoolShift {
		boolean check(PoolBall ball);
	}

	private final BoolShift
	cornerCheck = new BoolShift() {
		@Override
		public boolean check(PoolBall ball) {
			return corner.checkCollide(ball) || centre.checkCollide(ball);
		}
	},
	edgeCheck = new BoolShift() {
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
			float h = ShaderProgram.getHeight(), w = ShaderProgram.getWidth();
			ball.limit(w, -w, h, -h);
			return pocket; //return true if ball is in a pocket
		}
	},
	findCollide = new BoolShift() {
		@Override
		public boolean check(PoolBall ball) {
			return corner.checkCollide(ball) ||
                    (ball.y() > boundY && (checkCollide(ball, top) || centre.checkCollide(ball))) ||
					(ball.x() > boundX &&  checkCollide(ball, right)) ;
		}
	};

	private boolean checkShift(PoolBall current, BoolShift func){
		PointXY pos = current.positive();
		current.scale(pos);

		boolean check = func.check(current);

		current.scale(pos);

		return check;
	}

	private boolean checkCollide(PoolBall ball, TableObject[] objs) {
		for(TableObject o : objs) {
			if(o.checkCollide(ball))
			    return true;
		}
		return false;
	}


	public void draw(PoolBallShader batch, TableShader shader){
		/**
			shader.drawCircle(centre, Pocket.RADIUS * 2);
			shader.drawCircle(corner, Pocket.RADIUS * 2);
		/**
		for(TableObject obj : top)
			obj.draw(shader);

		for(TableObject obj : right)
			obj.draw(shader);
		/**/
        if(showType != 0) {
            shader.draw((showType == 1 ? shader.solid : shader.stripe), sign, 6 );
        }

		for(PoolBall ball: balls) {

			if(ball.id() != 0 && ((showType == 1 && ball.id() < 8) || (showType == 2 && ball.id() > 8))){
				shader.draw(shader.loop, ball, 2);
			}
		}

		sunk.draw(shader);


		shader.end();
		batch.begin();
		for(PoolBall ball: inPocket)
			batch.drawBall(ball);

		for(PoolBall ball: balls)
			batch.drawBall(ball);
	}

	
	public interface TableObject {
		boolean checkCollide(PoolBall ball);
		void draw(TableShader shader);
	}
	
}

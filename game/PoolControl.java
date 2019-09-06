package com.vdt.poolgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.vdt.poolgame.game.draw.TableShader;
import com.vdt.poolgame.game.table.PoolBall;
import com.vdt.poolgame.game.table.PoolTable;
import com.vdt.poolgame.library.PointXY;
import com.vdt.poolgame.library.ShaderProgram;
import com.vdt.poolgame.library.SpriteArray;

public class PoolControl implements InputProcessor {
	private final PoolBall  cue;
	private final PoolTable table;
	private final PointXY down  = new PointXY();
	private final PointXY delta = new PointXY();
	private float speed;
	private final float
			MIN_SPEED = 8,
			MAX_SPEED = 120f,
			SCALE =  -2f;

	private boolean
			aiming = false,
			moveCue = false,
			allowCueMove = false;

	public PoolControl(PoolTable table, PoolBall cue, SpriteArray array) {
		this.cue = cue;
		this.table = table;
		sign = new PointXY().set(- (table.WIDTH - 4), table.HEIGHT - 4);
	}

	private final PointXY sign;


	public void draw(TableShader shader){
		shader.draw(shader.loop, sign, 3);

		if(table.isLocked()){
			shader.draw(shader.timer, sign, 3);
			allowCueMove = false;
		} else {
			if(table.cuePocket())
				allowCueMove = true;

			if(allowCueMove){
				PointXY angle = new PointXY().degrees(45);
				shader.draw(shader.circle2, sign, 2.7f);
				shader.draw(shader.arrow, sign, angle, 3f);
				shader.draw(shader.arrow, cue,  angle, 4.1f);

			} else {
				shader.draw(shader.lock, sign, 3);
			}

			shader.draw(shader.loop, cue, 2f);
		}

	    if(aiming) {

	    	shader.draw(shader.loop, down, 2f);

	    	if(speed > 2) {
				shader.drawLine(cue.clone().move(-1.95f, delta), delta, speed * SCALE + 4.4f, .5f);
				shader.drawLine(down.clone().move(1.95f, delta), delta, speed - 1.99f, .5f);
			}

            if(speed > MIN_SPEED) {
				table.predict(shader, delta.clone().scale(speed * SCALE));
			}
        }
	}



	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		float x = screenX(screenX);
		float y = screenY(screenY);
		if(button == 1 || pointer == 2 ) {
			moveCue = aiming = false;
			table.rack();
		} else if (allowCueMove && table.canMoveCue(x, y)){
			moveCue = true;
		} else if (sign.range(x,y, 3)){
			allowCueMove = true;
		} else{
			aiming = true;
			down.set(x, y);
			delta.set(0, 0);
		}
		return true;
	}

	public void reset(){
	    aiming = false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if( aiming && speed > MIN_SPEED){
        	table.fireCue(delta.scale( Math.max(speed * SCALE, -MAX_SPEED)));
        }
		moveCue = aiming = false;
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		float x = screenX(screenX);
		float y = screenY(screenY);
		if(moveCue ){
			table.moveCue(x, y);
		} else if( aiming ) {
			speed = delta.set(x, y).move(-1, down).normalize();
		}
		return true;
	}

	private static final float screenX(float _x){
		return (_x /(float)Gdx.graphics.getWidth() - .5f) * ShaderProgram.getWidth() * 2;
	}

	private static final float screenY(float _y){
		return (_y /(float)Gdx.graphics.getHeight() - .5f) * ShaderProgram.getHeight() * 2;
	}


	
	//Unhandled inputs
	@Override public boolean keyDown(int keycode) { return false; }
	@Override public boolean keyUp  (int keycode) { return false; }
	@Override public boolean scrolled(int amount) { return false; }
	@Override public boolean keyTyped(char input) { return false; }
	@Override public boolean mouseMoved(int screenX, int screenY) { return false; }
}

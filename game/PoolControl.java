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
	private final float[] loop;
	private final PointXY down  = new PointXY();
	private final PointXY delta = new PointXY();
	private float speed;
	private final float
			MIN_SPEED = 8,
			SCALE = -1.5f;

	private boolean
			aiming = false,
			moveCue = false;

	public PoolControl(PoolTable table, PoolBall cue, SpriteArray array) {
		this.cue = cue;
		this.table = table;
		this.loop = array.get("loop", 2, 2);
	}



	public void draw(TableShader shader){
	    if(aiming) {
            shader.drawLine(cue, delta, speed * SCALE, .2f);
            //shader.drawRatio(loop, down.x(), down.y());
            shader.draw(loop, down);
            shader.drawLine(down, delta, speed, .2f);
            shader.drawCircle(cue, 3f);
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
		} else if( table.canMoveCue(x, y)){
			moveCue = true;
		} else {
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
        	table.fireCue(delta.scale(speed * SCALE));
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

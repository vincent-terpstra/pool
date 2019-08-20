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
	private final PoolBall cue;
	private final PoolTable table;
	private final float[] loop;
	private final PointXY down = new PointXY();
	private final PointXY delta = new PointXY();
	private float speed;

	public PoolControl(PoolTable table, PoolBall cue, SpriteArray array) {
		this.cue = cue;
		this.table = table;
		this.loop = array.get("loop", 2, 2);
	}

	private static final float MINSPEED = 10;

	public void draw(TableShader shader){
	    if(touched) {
            shader.drawLine(cue, delta, speed, .2f);
            shader.drawRatio(loop, down.x(), down.y());
            shader.drawCircle(cue, 3f);
            if(speed > MINSPEED) {
				table.predict(shader, delta, speed);
			}
        }
	}

	private boolean touched = false,
            moveCue = false;

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		float dist = cue.dist(screenX(screenX), screenY(screenY));
		if(dist < 2){
			moveCue = true;
		} else {
			touched = true;
			down.set(screenX, screenY);
			delta.set(0, 0);
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if( !moveCue && speed > MINSPEED // && table.locked
        ){
            cue.setSpeed(delta.scale(speed));
        }
		moveCue = touched = false;
		return true;
	}

	private static float screenX(float _x){
		return (_x /(float)Gdx.graphics.getWidth() - .5f) * ShaderProgram.getWidth() * 2;
	}

	private static float screenY(float _y){
		return (_y /(float)Gdx.graphics.getHeight() - .5f) * ShaderProgram.getHeight() * 2;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
	    if(moveCue && !table.locked){
			cue.set(screenX(screenX), screenY(screenY));
        } else {
            speed = delta.set(screenX, screenY).move(-1, down).scale(-.15f).normalize();
        }
		return true;
	}
	
	//Unhandled inputs
	@Override public boolean keyDown(int keycode) { return false; }
	@Override public boolean keyUp  (int keycode) { return false; }
	@Override public boolean scrolled(int amount) { return false; }
	@Override public boolean keyTyped(char input) { return false; }
	@Override public boolean mouseMoved(int screenX, int screenY) { return false; }
}

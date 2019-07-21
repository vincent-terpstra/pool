package com.vdt.poolgame.game;

import com.badlogic.gdx.InputProcessor;
import com.vdt.poolgame.game.draw.DefaultShader;
import com.vdt.poolgame.game.table.PoolBall;
import com.vdt.poolgame.library.PointXY;
import com.vdt.poolgame.library.SpriteArray;

class PoolControl implements InputProcessor {
	private final PoolBall cue;
	private final float[] loop;
	private final PointXY down = new PointXY();
	private final PointXY delta = new PointXY();
	public PoolControl(PoolBall cue, SpriteArray array) {
		this.cue = cue;
		this.loop = array.get("loop", 2, 2);
	}
	void draw(DefaultShader shader){
	    if(touched) {
            shader.drawSpeed(cue, delta);
            shader.drawRatio(loop, downX, downY);
        }
	}
	
	private int downX, downY;
	private boolean touched = false;

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		touched = true;
		this.downX = screenX;
		this.downY = screenY;
		down.set(screenX, screenY);
		delta.set(0,0);
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
	    //if(!PoolTable.locked) {
            cue.setSpeed(delta);
        //}
		touched = false;
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		delta.set(screenX, screenY).move(-1, down).scale(-.1f);
		return true;
	}
	
	//Unhandled inputs
	@Override public boolean keyDown(int keycode) { return false; }
	@Override public boolean keyUp  (int keycode) { return false; }
	@Override public boolean scrolled(int amount) { return false; }
	@Override public boolean keyTyped(char input) { return false; }
	@Override public boolean mouseMoved(int screenX, int screenY) { return false; }
}

package com.vdt.poolgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.vdt.poolgame.game.draw.TableDraw;
import com.vdt.poolgame.game.table.PoolBall;
import com.vdt.poolgame.game.table.PoolTable;
import com.vdt.poolgame.library.PointXY;

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
			draggingCue = false,
			allowCueMove = true,
			onCue = false;

	public PoolControl(PoolTable table, PoolBall cue) {
		this.cue = cue;
		this.table = table;
	}

	public void reset(){
		aiming = false;
	}

	public void draw(TableDraw shader){
		if(allowCueMove || table.cuePocket()){
			PointXY angle = new PointXY().degrees(45);
			shader.drawArrow(cue, angle, 4.1f, 45);

			if(!table.isLocked()){
				allowCueMove = true;
			}
		}

		shader.drawLoop( cue, 2f);

	    if(aiming) {
	    	shader.drawLoop( down, 2f);

	    	if(speed > 2) {
				shader.drawLine(cue.clone().move(-1.95f, delta), delta, speed * SCALE + 4.4f, .5f);
				shader.drawLine(down.clone().move(1.95f, delta), delta, speed - 1.99f, .5f);
			}

            if(speed > MIN_SPEED) {
				table.predict(shader, delta.clone().scale(speed * SCALE));
			}
        }
	}

	int totalTouches = 0;

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		float x = screenX(screenX);
		float y = screenY(screenY);

		if(++totalTouches == 2){ //second touch
			aiming = false;
			table.flipDisplay(); //show or hide type indicators
		} else if(button == 1 || totalTouches == 3 ) { //three touch (reset)
			draggingCue = aiming = false;
			allowCueMove = true;
			table.rack();
		} else if (allowCueMove && table.canMoveCue(x, y)){ //allow move
			draggingCue = true;
		} else {
			onCue = !table.isLocked() && cue.range(x, y, 2);
			aiming = true;
			down.set(x, y);
			delta.set(0, 0);
		}
		return true;
	}


	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

	    totalTouches--;

        if(totalTouches == 0 && aiming && speed > MIN_SPEED){
        	table.fireCue(delta.scale( Math.max(speed * SCALE, -MAX_SPEED)));
        	allowCueMove = false;
        }

        if(!Gdx.input.isTouched())
			totalTouches = 0;

		allowCueMove = allowCueMove || (onCue && cue.range(screenX(screenX), screenY(screenY), 2));

		onCue = draggingCue = aiming = false;
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		float x = screenX(screenX);
		float y = screenY(screenY);
		if(draggingCue){
			table.moveCue(x, y);
		} else if( aiming ) {
			speed = delta.set(x, y).move(-1, down).normalize();
		}
		return true;
	}

	private static final float screenX(float _x){
		return (_x /(float)Gdx.graphics.getWidth() - .5f) * PoolGame.getWidth() * 2;
	}

	private static final float screenY(float _y){
		return (_y /(float)Gdx.graphics.getHeight() - .5f) * PoolGame.getHeight() * 2;
	}

	//Unhandled inputs
	@Override public boolean keyDown(int keycode) {
	    if(keycode == Input.Keys.SPACE){
            table.flipDisplay();
        }
	    return true;
	}
	@Override public boolean keyUp  (int keycode) { return false; }
	@Override public boolean scrolled(int amount) { return false; }
	@Override public boolean keyTyped(char input) { return false; }
	@Override public boolean mouseMoved(int screenX, int screenY) { return false; }
}

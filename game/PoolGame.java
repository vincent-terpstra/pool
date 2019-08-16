package com.vdt.poolgame.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.vdt.poolgame.game.draw.DefaultShader;
import com.vdt.poolgame.game.draw.PoolBallShader;
import com.vdt.poolgame.game.table.PoolBall;
import com.vdt.poolgame.game.table.PoolTable;
import com.vdt.poolgame.library.ShaderProgram;
import com.vdt.poolgame.library.SpriteArray;
import com.vdt.poolgame.library.TimeStep;

public class PoolGame extends ApplicationAdapter {
	private PoolBallShader batch;

	private DefaultShader shader;
	private Texture texture;
	private PoolControl control;
	private PoolTable table;

	private TimeStep timer;
	
	@Override public void pause() {
		Gdx.input.setInputProcessor(null);
	}
	@Override public void resume() {
		Gdx.input.setInputProcessor(control);
		
	}
	@Override public void resize(int width, int height){
		shader.resize(width, height);
	}
	
	@Override
	public void create () {
		SpriteArray pool = new SpriteArray("images");
		table = new PoolTable(pool);
	
		control  = new PoolControl(table, table.balls.get(0), pool);
		texture  = pool.getTexture();
			texture.bind();
		batch	= new PoolBallShader(pool);
		shader	= new DefaultShader(pool);

		timer = new TimeStep(.01f);
		resume();
	}
	@Override
	public void render () {
		while(timer.update())
			table.update(timer.delta);
		renderScreen();
		
	}
	
	private void renderScreen() {
		table.draw(shader, control, batch);

	}
	
	@Override
	public void dispose () {
		texture.dispose();
		batch.dispose();
	}
}

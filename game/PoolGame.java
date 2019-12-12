package com.vdt.poolgame.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.vdt.poolgame.game.draw.PoolBallBatch;
import com.vdt.poolgame.game.draw.PoolBallDraw;
import com.vdt.poolgame.game.draw.TableBatch;
import com.vdt.poolgame.game.draw.TableDraw;
import com.vdt.poolgame.game.draw.TableShader;
import com.vdt.poolgame.game.draw.PoolBallShader;
import com.vdt.poolgame.game.table.PoolTable;
import com.vdt.poolgame.library.SpriteArray;
import com.vdt.poolgame.library.TimeStep;

public class PoolGame extends ApplicationAdapter {
	protected static float height = 1, width = 1, ratio = 1;

	public static float getHeight(){
		return height;
	}

	public static float getWidth(){
		return width;
	}

	public static void set(float _height, float _width){
		height = _height;
		width  = _width;
	}

	private PoolBallDraw batch;

	private TableDraw shader;
	private Texture texture;
	private PoolControl control;
	private PoolTable table;

	private TimeStep timer;

	public PoolControl getControl(){
		return control;
	}


	@Override public void pause() {
		Gdx.input.setInputProcessor(null);
	}
	@Override public void resume() {
		Gdx.input.setInputProcessor(control);
	}

	@Override public void resize(int _width, int _height){
		height = Math.max(PoolTable.HEIGHT + 1f, (PoolTable.WIDTH + 1f ) * (float)_height / (float)_width);
		width = height * (float)_width / (float)_height;
		ratio = 2 * width / _width;

		//redraw wooden panels on top or side of the board
		//drawIdx = 0;

		if (height < PoolTable.HEIGHT + 1){
			float s_x = PoolTable.WIDTH, d_x = width - s_x;
			//draw(wood,  s_x+=1.8f, 0, 0, 1,  height, d_x);
			//draw(wood, -s_x		 , 0, 0, 1, -height,-d_x);
		} else {
			float b_y = PoolTable.HEIGHT, d_y = height - b_y;
			//draw(wood, 0, b_y += 1.8f, 1, 0, width, d_y);
			//draw(wood, 0, -b_y, 1, 0, -width, -d_y);
		}
	}
	
	@Override
	public void create () {
		SpriteArray pool = new SpriteArray("images");

		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		shader	= new TableBatch(pool);
		batch	= new PoolBallBatch((TableBatch)shader, pool);

		table = new PoolTable(pool, shader);
	
		control  = new PoolControl(table, table.balls.get(0));

		texture  = pool.getTexture();
		texture.bind();

		timer = new TimeStep(.005f);

		TableShader.setClearColor(.15f, .45f, 1f);
		resume();



	}

	@Override
	public void render () {
		while(timer.update())
			table.update(timer.delta);
		renderScreen();
		
	}
	
	private void renderScreen() {
		TableShader.clearScreen();
		shader.begin();
		control.draw(shader);
		table.draw(batch, shader);
	}
	
	@Override
	public void dispose () {
		texture.dispose();
		shader.dispose();
		batch.dispose();
	}
}

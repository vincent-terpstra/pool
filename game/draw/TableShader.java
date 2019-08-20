package com.vdt.poolgame.game.draw;

import com.badlogic.gdx.Gdx;
import com.vdt.poolgame.game.table.Pocket;
import com.vdt.poolgame.game.table.PoolTable;
import com.vdt.poolgame.library.PointXY;
import com.vdt.poolgame.library.ShaderProgram;
import com.vdt.poolgame.library.SpriteArray;

public class TableShader extends ShaderProgram {
	public final float[] circle;
	public final float[] rect;
	private final float[]  wood;

	public void draw(float[] draw, PointXY point){
		draw(draw, point.x(), point.y());
	}

	public void drawRatio(float[] draw, float x, float y){
		draw(draw, x * ratio - width, y * ratio - height);
	}

	public void drawCircle(PointXY point, float diameter){
		draw(circle, point.x(), point.y(), diameter, 0, 1, 1);
	}

	public void drawLine(PointXY point, PointXY angle, float length, float scaleY){
		draw(rect, point.x(), point.y(), -angle.x(), angle.y(), length, scaleY);
	}

	public void draw(float[] draw, float x, float y){
		draw(draw, x, y, 1, 0, 1, 1);
	}

	public void draw(float[] draw, float x, float y, float cos, float sin, float scaleX, float scaleY){
		if(drawIdx + draw.length > drawValues.length) end();
		for(int i = 0; i < draw.length; ){
			final float drawX = draw[i++] * scaleX;
			final float drawY = draw[i++] * scaleY;
			drawValues[drawIdx++] = drawX * cos + drawY * sin + x;
			drawValues[drawIdx++] = drawY * cos - drawX * sin + y;
			drawValues[drawIdx++] = draw[i++];
			drawValues[drawIdx++] = draw[i++];
		}
	}

	public void end(){
		if(drawIdx == 0) return;
		bind(drawIdx, drawValues, -1);
		drawIdx = 0;
	}


	@Override
	protected final void derivedBegin(){
		drawIdx = finalIDX;
		//create the board (centered at 0,0)
		float[] matrix2 =  { 1/width, 0,
							 0,-1/height };
		Gdx.gl.glUniformMatrix2fv( uniformIDS[1], 1, false, matrix2, 0);
	}

	private final float[] drawValues;
	private int drawIdx = 0;

	private int finalIDX;

	public TableShader(SpriteArray array) {
		super("attribute vec2 a_xy;" 							+
				"attribute vec2 a_uv;" 								+
				"uniform mat2 u_mat;" 								+
				"varying vec2 v_uv;" 								+

				"void main(){" 										+
				"   v_uv = a_uv;" 									+
				"	gl_Position = vec4(u_mat * a_xy, 1.0, 1.0);" 	+
				"}",

				"varying vec2 v_uv;" 							+
				"uniform sampler2D u_texture;" 						+
				"void main(){" 										+
				"		gl_FragColor = texture2D(u_texture, v_uv);" +
				"}",
				40,
				new String[] {"u_texture", "u_mat" },
				new String[]{"a_xy", "a_uv" },
				new int[] {2, 2});
		//create a fixed amount of vertices set to the square to display the ball
		drawValues = new float[vertices.capacity()];


		circle = array.get("circle");
		rect = array.get("rect",1,1, -1, 0);

		wood = array.get("wood", 2, 2,-.5f, 0);

		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}


	public void resize(int _width, int _height){
		height = Math.max(PoolTable.HEIGHT + .8f, (PoolTable.WIDTH + .8f ) * (float)_height / (float)_width);
		width = height * (float)_width / (float)_height;
		ratio = 2 * width / _width;

	//redraw wooden panels on top or side of the board
		drawIdx = 0;

		if (height < PoolTable.HEIGHT + 1){
			float s_x = PoolTable.WIDTH, d_x = width - s_x;
			draw(wood,  s_x+=1.8f, 0, 0, 1,  height, d_x);
			draw(wood, -s_x		 , 0, 0, 1, -height,-d_x);
		} else {
			float b_y = PoolTable.HEIGHT, d_y = height - b_y;
			draw(wood, 0, b_y += 1.8f, 1, 0, width, d_y);
			draw(wood, 0, -b_y, 1, 0, -width, -d_y);
		}
	}

	public void drawEdge(SpriteArray array, PointXY corner) {

		final float[]
			drawPocket = array.get("pocket", Pocket.RADIUS * 2, 0),
			left = array.get("left", 0, 4, 0, 0),
			side = array.get("right", 0, 4, -1, 0),
			line = array.get("black", 1, 1, -.5f, -.5f);
		float scaleX = 1, scaleY = 1;
		for(int j = 0; j < 4; j++) {
			draw(drawPocket, scaleX * corner.x(), scaleY * corner.y());

			draw(side, scaleX * (PoolTable.WIDTH + 4 - Pocket.RADIUS * 2 * .7071f)	, scaleY * (PoolTable.HEIGHT), 1, 0, scaleX, scaleY);
			draw(side, scaleX * (PoolTable.WIDTH), scaleY * (PoolTable.HEIGHT + 4 - Pocket.RADIUS * 2 * .7071f), 0, 1,-scaleY, scaleX);

			draw(left, 0, scaleY * PoolTable.HEIGHT, 1, 0, scaleX, scaleY);
			if(j==0) scaleX = -1;
			if(j==1) scaleY = -1;
			if(j==2) scaleX =  1;
		}

		//Head and Foot spot
		draw(drawPocket, PoolTable.HEIGHT, 0, 1, 0, .2f, .2f);
		draw(drawPocket, -PoolTable.HEIGHT, 0, 1, 0, .2f, .2f);

		//headline
		draw(line, -PoolTable.HEIGHT, 0, 1, 0, .15f, PoolTable.WIDTH);


		//draw(circle, 0,0 );
		//draw(line, -PoolTable.HEIGHT, 0, 0, 1, .15f, PoolTable.WIDTH);


		finalIDX = drawIdx;
	}
}

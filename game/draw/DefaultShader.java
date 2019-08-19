package com.vdt.poolgame.game.draw;

import com.badlogic.gdx.Gdx;
import com.vdt.poolgame.game.table.Pocket;
import com.vdt.poolgame.game.table.PoolTable;
import com.vdt.poolgame.library.PointXY;
import com.vdt.poolgame.library.ShaderProgram;
import com.vdt.poolgame.library.SpriteArray;

public class DefaultShader extends ShaderProgram {
	public final float[] drawPocket;
	public final float[] circle;
	public final float[] rect;
	private final float[] left, side, mask;

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
	public void setScale(float x, float y) {
		scaleX = x;
		scaleY = y;
	}


	public void drawCentre() {
		float b_y = 13.7071f, s_x = 32.7071f;
		float d_x = width - s_x + 2;
		float d_y = height - b_y - 2;
		if (d_y > 0) {
			draw(mask, 0, b_y + 1.8f, 1, 0, width, d_y);
		} else if (d_x > 0) {
			draw(mask, s_x - 2.2f, 0, 0, 1, height, d_x);
		}
	}

	public void drawEdge(float x, float y, Pocket corner, PointXY b_right, PointXY s, float b_y) {
		setScale(x, y);
		corner.draw(this);
		draw(side, b_right);
		draw(side, s.x(), s.y(), 0, 1, -1, 1);
		draw(left , 0, b_y);
		/**
		 for(TableObject e : right) {
		 e.draw(shader);
		 }
		 for(TableObject e : top) {
		 e.draw(shader);
		 }
		 /**/
	}

	private float scaleX = 1, scaleY = 1;
	public void draw(float[] draw, float x, float y, float cos, float sin, float scaleX, float scaleY){
		if(drawIdx + draw.length > drawValues.length) end();
		for(int i = 0; i < draw.length; ){
			final float drawX = draw[i++] * scaleX;
			final float drawY = draw[i++] * scaleY;
			drawValues[drawIdx++] = (drawX * cos + drawY * sin + x)*this.scaleX;
			drawValues[drawIdx++] = (drawY * cos - drawX * sin + y)*this.scaleY;
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
	public void lock(){ finalIDX = drawIdx; }

	public DefaultShader(SpriteArray array) {
		super("attribute vec2 a_xy;" 							+
				"attribute vec2 a_uv;" 								+
				"uniform mat2 u_mat;" 								+
				"varying vec2 v_uv;" 								+ //texture coordinates

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

		drawPocket = array.get("pocket", Pocket.radius * 2, 0);
		circle = array.get("circle");
		rect = array.get("rect",1,1, -1, 0);

		left = array.get("left", 0, 4, 0, 0);
		side = array.get("right", 0, 4, -1, -1);
		mask = array.get("wood", 2, 2,-.5f, 0);

		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}


	public void resize(int _width, int _height){

		height = Math.max(14.8f, (PoolTable.width * 2 - .5f) * (float)_height / (float)_width);
		width = height * (float)_width / (float)_height;
		ratio = 2 * width / _width;
		drawIdx = 0;
		drawCentre();
		setScale(-1, -1);
		drawCentre();
		setScale(1, 1);
	}
}

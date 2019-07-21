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
	
	public void draw(float[] draw, PointXY point){
		draw(draw, point.x(), point.y());
	}
	public void drawRatio(float[] draw, float x, float y){
		draw(draw, x * ratio - width, y * ratio - height);
	}
	public void drawSpeed(PointXY ball, PointXY delta){
		PointXY b = new PointXY().set(ball);
		drawCircle(b, 1f);
		for(int i = 0; i < 5; i++){
			drawCircle(b.move(.2f, delta), .5f);
		}

	}
	public void drawCircle(PointXY point, float diameter){
		draw(circle, point.x(), point.y(), diameter, 0, 1, 1);
	}
	
	public void draw(float[] draw, float x, float y){
		draw(draw, x, y, 1, 0, 1, 1);
	}
	
	public void setScale(float x, float y) {
		scaleX = x;
		scaleY = y;
	}
	public void reset() {
		scaleX = 1;
		scaleY = 1;
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
		drawIdx = 0;
		//create the board (centered at 0,0)
		float[] matrix2 =  { 1/width, 0,
							 0,-1/height };
		Gdx.gl.glUniformMatrix2fv( uniformIDS[1], 1, false, matrix2, 0);
	}
	
	private final float[] drawValues;
	private int drawIdx = 0;
	
	public DefaultShader(SpriteArray array) {
		super(vert, frag, 10, uniforms, attributes, new int[] {2, 2});
		width = PoolTable.width * 2;
		height = width * (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();
		ratio = 2 * width / Gdx.graphics.getWidth();
		//create a fixed amount of vertices set to the square to display the ball
		drawValues = new float[vertices.capacity()];
		drawPocket = array.get("pocket", Pocket.radius * 2, 0);
		circle = array.get("circle");
		rect = array.get("rect");
	}
	
	private static final String[] 
			uniforms   = {"u_texture", "u_mat" }, 
			attributes = {"a_xy", "a_uv" };
	private static final String 
	vert =
			"attribute vec2 a_xy;"
		+	"attribute vec2 a_uv;"
			
		+	"uniform mat2 u_mat;"
		
		+	"varying vec2 v_uv;" //texture coordinates
		
		+	"void main(){"
		+	"   v_uv = a_uv;"
		+	"	gl_Position = vec4(u_mat * a_xy, 1.0, 1.0);"
		+	"}"
		,
	frag = 
			"varying vec2 v_uv;"
					
		+	"uniform sampler2D u_texture;"
		
		+	"void main(){"
		+	"   gl_FragColor = texture2D(u_texture, v_uv );"
		+	"}"
		;

}

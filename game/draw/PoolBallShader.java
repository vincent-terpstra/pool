package com.vdt.poolgame.game.draw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.vdt.poolgame.game.table.PoolBall;
import com.vdt.poolgame.library.ShaderProgram;
import com.vdt.poolgame.library.SpriteArray;

public class PoolBallShader extends ShaderProgram {
	public void drawBall(PoolBall ball) {
		GL20 gl  = Gdx.gl;
		//supply the location of the ball and of the texture UV to the shader
		gl.glUniform2f(uniformIDS[2], ball.x(), ball.y());
		int id = ball.id();
		gl.glUniform3f(uniformIDS[3], poolBalls[id * 2], poolBalls[id*2+1], ballWidth);
		gl.glUniformMatrix3fv(uniformIDS[4], 1, false, ball.matrix.getMatrix(), 0);
		//Load vertex for points XY
		bind(8, -1);
	}
	
	@Override
	protected final void derivedBegin(){

		Gdx.gl.glUniform3f(uniformIDS[5], light[0], light[1], light[2] );
	}

	private final float[] light;
	private final float[] poolBalls;
	private final float ballWidth;

	public PoolBallShader(SpriteArray array) {
		super(
			"attribute vec2 a_xy;"

			+	"uniform mat2 u_mat;"
			+	"uniform vec2 u_loc;"

			+	"varying vec2 v_xy;" //used to draw circles

			+	"void main(){"
			+	"   v_xy = a_xy;"
			+	"	gl_Position = vec4(u_mat * (a_xy + u_loc), 1.0, 1.0);"
			+	"}",

			"varying vec2 v_xy;"

			+	"uniform sampler2D u_texture;"
			+	"uniform mat3 u_mat3;" //rotation matrix
			+	"uniform vec3 u_uv;"   //center of the texture & length of the texture
			+ 	"uniform vec3 u_light;"

			+	"void main(){"
			+	"   float size = dot(v_xy, v_xy);"
			+	"	if(size > 1.2) discard;"
			+   "   else if(size > 1.0) gl_FragColor = vec4(0,0,0,1);" //outline
			+   "   else {"
			+   "   	vec3 val3 = vec3(v_xy.xy, sqrt(1.0-size));"
			+	"   	float diffuse = dot(val3, u_light);"
			+   "   	val3 *= u_mat3;"
			+   "   	if(val3.z < 0.0) val3.x *= -1.0;" //flip the texture if on the bottom of the ball
			+	"   	gl_FragColor = vec4(texture2D(u_texture, val3.xy * u_uv.z + u_uv.xy ).rgb * "
			+ 	"			(.04 + diffuse) + pow(diffuse, 16.0) * .6 + .02, 1.0);"
			+   "   }"
			+	"}",
			1,
			new String[]{"u_texture", "u_mat", "u_loc", "u_uv", "u_mat3", "u_light" },
			new String[]{"a_xy" },
			new int[] {2}
		);

		poolBalls = new float[16 * 2];
		float width = 0;
		for(int i = 0; i <= 15; i++){
			SpriteArray.Region  r = array.region(i == 0 ? "cue" : Integer.toString(i));
			poolBalls[2*i]   = (r.u + r.u2) / 2;
			poolBalls[2*i+1] = (r.v + r.v2) / 2;
			width = r.u2 - poolBalls[2*i];
		}
		this.ballWidth = width;

		//create a fixed amount of vertices set to the square to display
		float r = 1.2f;
		vertices.put(new float[]{-r,-r, -r, r, r,r, r, -r});
		vertices.position(0);

		indices.position(0).limit(6);

		//light source
		final float x = 2, y = 5, z = 10;
		float mag = (float)Math.sqrt(x * x + y * y + z * z);
		light = new float[]{x/mag, y/mag, z/mag};
		
	}
}

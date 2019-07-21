package com.vdt.poolgame.library;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static com.badlogic.gdx.Gdx.gl20;

public abstract class ShaderProgram {
	protected static float height = 1, width = 1, ratio = 1;
	
	public static void clearScreen(){
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
	}
	
	protected final int 	programHandle, fragment, vertex;
	protected final int[] 	uniformIDS;
	
	private static final int BYTES_PER_FLOAT = 4, BYTES_PER_SHORT = 2;
	protected final int STRIDE;
	protected final FloatBuffer	vertices;
	protected final ByteBuffer buffer;
	protected final ShortBuffer	indices;
	protected final int[] attrSize;
	
	public void dispose(){
		Gdx.gl.glUseProgram(0);
		Gdx.gl.glDeleteProgram(programHandle);
		Gdx.gl.glDeleteShader(fragment);
		Gdx.gl.glDeleteShader(vertex);
	}

	public final void begin(){
		//use this shader
		Gdx.gl.glUseProgram		 ( programHandle	   );
		//apply the texture to the shader
		Gdx.gl.glUniform1f		 ( uniformIDS[0], 0 );
		
		derivedBegin(); //call derived class method
	}
	
	protected final void bind(int totalIndices, float[] drawValues, int clrIdx) {
		if(drawValues != null) {
			vertices.position(0);
			vertices.put(drawValues);
		}
		int idx = 0;
		int offset = 0;
		for(int i : attrSize) {
			if(idx == clrIdx) {
				Gdx.gl.glVertexAttribPointer(idx, 4, GL20.GL_UNSIGNED_BYTE, true, STRIDE, buffer.position(offset * 4));
			} else {
				Gdx.gl.glVertexAttribPointer(idx, i, GL20.GL_FLOAT, false, STRIDE, vertices.position(offset));
			}
			Gdx.gl.glEnableVertexAttribArray(idx++);
			offset += i;
		}
		totalIndices = totalIndices / offset / 4 * 6;
		Gdx.gl.glDrawElements(GL20.GL_TRIANGLES, totalIndices, GL20.GL_UNSIGNED_SHORT, indices.position(0).limit(totalIndices));
		
		
	}
	//
	protected abstract void derivedBegin();
	protected ShaderProgram(String vert, String frag, int quads, 
			String[] uniforms, String[] attributes, int[] attrSize){
		int sum = 0;
		for(int i : attrSize) {
			sum += i;
		}
		int POINTSPERVERTICE = sum;
		this.attrSize = attrSize;
		//Setup GL constants
		GL20 gl = Gdx.gl;
		gl.glClearColor (0, 0, 1, 1 );
		//enable blending
		gl.glEnable	  (	GL20.GL_BLEND );
		gl.glBlendFunc( GL20.GL_SRC_ALPHA, 
						GL20.GL_ONE_MINUS_SRC_ALPHA );
		//Set up OpenGL Shaders 
		this.vertex   = ShaderPart(GL20.GL_VERTEX_SHADER, vert );
		this.fragment = ShaderPart(GL20.GL_FRAGMENT_SHADER, frag);
		int programHandle = gl.glCreateProgram();
		if(programHandle != 0) {
			gl.glAttachShader(programHandle, vertex);
			gl.glAttachShader(programHandle, fragment);	
			//Bind attributes
			int idx = 0;
			for(String s : attributes){
				gl.glBindAttribLocation(programHandle, idx++, s);
			}
			gl.glLinkProgram(programHandle);
			final java.nio.IntBuffer linkStatus = BufferUtils.newIntBuffer(1);
			gl.glGetProgramiv(programHandle, GL20.GL_LINK_STATUS, linkStatus);
			if(linkStatus.get(0) == 0){
				gl.glDeleteProgram(programHandle);
				programHandle = 0;
			}
		}
		if(programHandle == 0){
			throw new RuntimeException("Error creating Shader Program");
		}	
		this.programHandle = programHandle;
		uniformIDS = new int[uniforms.length];
		int uni = 0;
		for(String uniform : uniforms){
			uniformIDS[uni++] = gl.glGetUniformLocation(programHandle, uniform);
		}
		STRIDE = POINTSPERVERTICE * BYTES_PER_FLOAT;
		
		buffer = ByteBuffer
					.allocateDirect(quads * 4 * POINTSPERVERTICE * BYTES_PER_FLOAT)
					.order(ByteOrder.nativeOrder());
		vertices = buffer
					.asFloatBuffer();
		indices = ByteBuffer
					.allocateDirect(quads * 6 * BYTES_PER_SHORT)
					.order(ByteOrder.nativeOrder())
					.asShortBuffer();
		short j = 0;
		short[] offset = {0,1,2,2,3,0};
		for(int i = 0 ; i < quads * 6; ){
			indices.put(i,  (short) (j + offset[i%6]));
			if(++i % 6 == 0) j+=4;
		}
	}

	private int ShaderPart(int type, final String source ){
		GL20 gl = gl20;
		int shaderHandle = gl.glCreateShader(type);
		boolean check = false;
		if (shaderHandle != 0){
			gl.glShaderSource ( shaderHandle, source);
			gl.glCompileShader( shaderHandle );
			final java.nio.IntBuffer compileCheck = BufferUtils.newIntBuffer(1);
			gl.glGetShaderiv(shaderHandle, GL20.GL_COMPILE_STATUS, compileCheck);
			check = compileCheck.get(0) == 0;
		}
		if ( check ) {
			System.out.println(gl.glGetShaderInfoLog(shaderHandle));
			System.out.println(source);
			gl.glDeleteShader(shaderHandle);
		}
		return shaderHandle;
	}
}

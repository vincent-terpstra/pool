package com.vdt.poolgame.library;
/**
 * @author Vincent Terpstra
 * @date Jan 26, 2019
 * @file Matrix.java
 * @class Matrix
 *	Implements a 3x3 matrix
 */
public class Matrix3 {
	//row Major order for matrix
	private float[] matrix;
	
	public Matrix3(){
		matrix = new float[9];
		identity();
	}
	
	public Matrix3 identity(){
		for(int i = 0; i < 3; i++){
			for(int c = 0; c < 3; c++){
				matrix[i* 3 + c] = (i == c ? 1 : 0);
			}
		}
		return this;
	}
	
	public final float[] getMatrix(){
		return matrix;
	}
	
	public void rotateY(float deltaX, float deltaY){
		float cosX = (float)Math.cos(deltaX);
		float sinX = (float)Math.sin(deltaX);
		float cosY = (float)Math.cos(deltaY);
		float sinY = (float)Math.sin(deltaY);
		multiply(new float[]{cosX, -sinX * sinY, sinX * cosY,
								 0, cosY, sinY, 
							-sinX, -cosX * sinY, cosX * cosY});
	}
	
	public Matrix3 multiply(float[] multi){
		float[] result = new float[9];
		for(int r = 0; r < 3; r++){
			for(int c = 0; c < 3; c++){
				float sum = 0;
				for(int dot = 0; dot < 3; dot++){
					sum +=  multi[dot*3 + c] * matrix[r*3 + dot]; 
				}
				result[r*3 + c] = sum;
			}
		}
		this.matrix = result;
		return this;
	}
	
	public void display(){
		for(int i = 0; i < matrix.length; i++){
			System.out.print(matrix[i] + " ");
			if(i % 3 == 2) System.out.println();
		}
	}
}

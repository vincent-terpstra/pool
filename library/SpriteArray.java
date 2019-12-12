package com.vdt.poolgame.library;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class SpriteArray{
	private final Array<Region> regions = new Array<Region>();
	public SpriteArray(String atlas){
		FileHandle packFile =  com.badlogic.gdx.Gdx.files.internal("images/"+ atlas + ".atlas");
		texture = new Texture( com.badlogic.gdx.Gdx.files.internal("images/"+ atlas + ".png"  ));
		BufferedReader reader = new BufferedReader(new InputStreamReader(packFile.read()), 64);
		try {
			while (true) {
				String name = reader.readLine();
				if (name == null) break;
					readTuple(reader);
					regions.add(new Region(name, tuple[0], tuple[1], tuple[2], tuple[3]));
			}
		} catch (Exception ex) {
			throw new GdxRuntimeException("Error reading pack file: " + packFile, ex);
		} finally {
			StreamUtils.closeQuietly(reader);
		}
	}
	private Texture texture;
	public Texture getTexture(){
		return texture;
	}
	public Region region(String name) {
		for (int i = 0, n = regions.size; i < n; i++)
			if (regions.get(i).name.equals(name)) return regions.get(i);
		return null;
	}
	
	private static final int TEXTURE_WIDTH = 1024;
	public final class Region {
		private float[] saved;
		private final String name;
		public final float u, v, u2, v2;
		public Region(String name, float x, float y, float width, float height){
			this.name = name;
			float ratio = 1f / TEXTURE_WIDTH;
			this.u  = x * ratio; 
			this.v2  = (y +height)* ratio;
			this.u2 = (x + width) * ratio; 
			this.v = y * ratio;
		}
	}
	private static final int[] tuple = new int[4];
	private void readTuple (BufferedReader reader) throws IOException {
		String line = reader.readLine();
		int colon = line.indexOf(':');
		if (colon == -1) throw new GdxRuntimeException("Invalid line: " + line);
		int i = 0, lastMatch = colon + 1;
		for (i = 0; i < 4; i++) {
			int comma = line.indexOf(',', lastMatch);
			if (comma == -1) {
				if (i == 0) throw new GdxRuntimeException("Invalid line: " + line);
				break;
			}
			tuple[i] = Integer.parseInt(line.substring(lastMatch, comma).trim());
			lastMatch = comma + 1;
		}
	}
	public final TextureRegion getTextureRegion(String region){
		Region r = region(region);

		return new TextureRegion(texture, r.u, r.v, r.u2, r.v2);
	}
	public final float[] get(String region){
		return get(region, 1, 1);
	}
	
	public final float[] get(String region, float x, float y) {
		return get(region, x, y, -.5f, -.5f);
	}
	
	public final float[] get(String region, float x, float y, float dx, float dy){
		try {
		Region r = region(region);
		float u = r.u, v = r.v, u2 = r.u2, v2 = r.v2;
		
		if( x== 0 ) x = y * (u2 - u) / (v2 - v);
		if( y== 0 ) y = x * (v2 - v) / (u2 - u);
		dx *= x;
		dy *= y;
		x+= dx;
		y+= dy;
		
			return new float[] {
				// X, Y, U, V,
				dx, y, u , v2,
			    dx, dy   , u , v ,
			     x, dy   , u2, v ,
			     x, y, u2, v2,
			};
		} catch (NullPointerException ex) {
			System.out.println("Unable to find region " + region);
			return null;
		}
	}
	/**
	public final float[][] getString(String name){
		int length = name.length();
		float[][] str = new float[name.length()][];
		for(int i = 0; i < length; i++){
			String sub =  name.substring(i, i+1);
			Region r = region(sub);
			if( r.saved == null){
				r.saved = get(sub,0,0);
			}
			str[i] = r.saved;
		}
		return str;
	}
	/**
	public float[] getRatio(String name){
		return get(name, 0, .0125f * (float)TEXTURE_WIDTH);
	}
	public float[] getRatio(String name, float ratio){
		return	get(name, 0, ratio);
	}
	*/
	
	/**
	public final float[][] getArray( String name){
		return getArray(name, .0125f * (float)TEXTURE_WIDTH);
	}
	public final float[][] getArray( String name, float ratio ){
		int size = 1;
		while(region(name + size) != null){
			size++;
		}
		float[][] array = new float[size][];
		for(int i = 0; i < size; i ++){
			array[i] = getRatio( name + i, ratio );
		}
		return array;
	}
	/**/
}

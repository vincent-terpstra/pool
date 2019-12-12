package com.vdt.poolgame.library;

public class PointXY implements Cloneable {
	protected float x, y;
	
	public float x() { return x; }
	public float y() { return y; }
	
	public final PointXY degrees(float deg) {
		return radians(deg * (float)Math.PI / 180);
	}
	
	public final PointXY radians(float rad) {
		return set((float)Math.cos(rad), (float)Math.sin(rad));
	}
	
	public final PointXY rotateCC(PointXY angle) {
		return rotate(angle.x,-angle.y);
	}
	public final PointXY rotate(PointXY angle) {
		return rotate(angle.x, angle.y);
	}
	public final PointXY rotate(float cos, float sin) {
		return set(x * cos + y * sin,  y * cos - x * sin);
	}
	
	public final PointXY set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	public final PointXY set(PointXY point) {
		return set(point.x, point.y);
	}
	public final PointXY move(float dx, float dy) {
		return set(x + dx, y + dy);
	}
	
	public final PointXY move(float delta, PointXY dir) {
		return move(delta * dir.x, delta * dir.y);
	}
	
	public final PointXY scale(float multi) {
		return set(x * multi, y * multi);
	}

	public final PointXY scale(float multiX, float multiY){
	    return set(x * multiX, y * multiY);
    }
	
	public final float dot(PointXY point) {
		return x * point.x + y * point.y;
	}
	
	public final float normalize() {
		float dist = (float)Math.sqrt(x * x + y * y);
		if(dist != 0) {
			x /=  dist;
			y /=  dist;
		}
		return dist;
	}

	public final boolean range(float _x, float _y, float dist){
		_x -= this.x;
		_y -= this.y;
		return _x * _x  + _y * _y < dist * dist;
	}

	public PointXY clone() {
			return new PointXY().set(x, y);
	}

	public void limit(float maxX, float minX, float maxY, float minY){
	    x = Math.min(maxX, Math.max(minX, x));
	    y = Math.min(maxY, Math.max(minY, y));
    }
	
	public final void print() {
		System.out.println(x + " : " + y);
	}
	public PointXY invert() {
		return set(y, x);
	}
}

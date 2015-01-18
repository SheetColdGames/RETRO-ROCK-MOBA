package me.sheetcoldgames.topdownengine.engine.collision;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class SheetPoint {
	
	public Vector2 pos;
	public Circle radius;
	
	public SheetPoint(float x, float y) {
		pos = new Vector2(x, y);
		radius = new Circle(x, y, .25f);
	}
	
	public void set(float x, float y) {
		pos.set(x, y);
	}
	
	public float getX() {
		return pos.x;
	}
	
	public float getY() {
		return pos.y;
	}
	
	/** Checks if the point located in (x,y) is inside the radius of this SheetPoint */
	public boolean contains(float x, float y) {
		if (x < pos.x + radius.radius && x > pos.x - radius.radius) {
			if (y < pos.y + radius.radius && y > pos.y - radius.radius) {
				return true;
			}
		}
		return false;
		// check why the fuck this is wrong
		// return radius.contains(x, y);
	}
	
	public boolean overlaps(float x, float y) {
		return radius.contains(new Circle(x, y, 1.f));
	}
}

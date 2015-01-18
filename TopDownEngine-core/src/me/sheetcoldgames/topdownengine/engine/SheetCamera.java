package me.sheetcoldgames.topdownengine.engine;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class SheetCamera extends OrthographicCamera {
	
	Entity target;
	
	public SheetCamera() {
		super();
	}
	
	public SheetCamera(float w, float h) {
		super(w, h);
	}
	
	public void update() {
		if (hasTarget()) {
			position.set(target.position.x, target.position.y, 0f);
		}
		super.update();
	}
	
	public Entity getTarget() {
		return target;
	}
	
	public boolean hasTarget() {
		return getTarget() != null;
	}
	
	public void setTarget(Entity ent) {
		this.target = ent;
	}
}

package me.sheetcoldgames.topdownengine.engine;

import com.badlogic.gdx.math.Vector2;

public class Entity {
	
	public Vector2 position;
	public Vector2 velocity;
	
	public float width;
	public float height;
	
	public float offset;
	
	public float minSpeed;
	
	public Entity() {
		position = new Vector2();
		velocity = new Vector2();
		width = .8f;
		height = 1f;
		offset = .2f;
		minSpeed = offset / 4f;
	}
	
}

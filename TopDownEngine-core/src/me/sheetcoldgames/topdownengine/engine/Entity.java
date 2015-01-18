package me.sheetcoldgames.topdownengine.engine;

import com.badlogic.gdx.math.Vector2;

public class Entity {
	
	public Vector2 position;
	public Vector2 velocity;
	
	public float radius;
	public float sqrRadius;
	
	public Entity() {
		position = new Vector2();
		velocity = new Vector2();
		radius = .5f;
		sqrRadius = radius * radius;
	}
	
}

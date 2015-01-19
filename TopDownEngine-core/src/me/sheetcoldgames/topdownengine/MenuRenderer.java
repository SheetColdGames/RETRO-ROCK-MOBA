package me.sheetcoldgames.topdownengine;

import me.sheetcoldgames.topdownengine.engine.MenuController;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

public class MenuRenderer {
	
	MenuController controller;
	ShapeRenderer sr;
	
	public MenuRenderer(MenuController controller) {
		this.controller = controller;
		sr = new ShapeRenderer();
	}
	
	public void dispose() {
		sr.dispose();
	}
	
	public void render() {
		debugRender();
	}
	
	public void debugRender() {
		sr.begin(ShapeType.Filled);
		
		// sr.rect(controller.button.x, controller.button.y, controller.button.width, controller.button.height);
		for (int i = 0; i < controller.buttons.size(); i++) {
			sr.setColor(Color.RED);
			sr.rect(controller.buttons.get(i).x, controller.buttons.get(i).y, controller.buttons.get(i).width, controller.buttons.get(i).height);
		}
		sr.end();
		
		sr.begin(ShapeType.Line);
		sr.end();
	}
}

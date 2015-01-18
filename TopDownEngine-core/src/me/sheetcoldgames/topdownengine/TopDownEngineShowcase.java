package me.sheetcoldgames.topdownengine;

import me.sheetcoldgames.topdownengine.engine.Controller;

import com.badlogic.gdx.ApplicationAdapter;

public class TopDownEngineShowcase extends ApplicationAdapter {
	
	Controller controller;
	GameRenderer renderer;
	
	public void create() {
		controller = new Controller();
		renderer = new GameRenderer(controller);
	}
	
	public void dispose() {
		renderer.dispose();	
	}
	
	public void render() {
		controller.update();
		renderer.render();
	}

}

package me.sheetcoldgames.topdownengine;

import me.sheetcoldgames.topdownengine.engine.Input;
import me.sheetcoldgames.topdownengine.engine.MenuController;
import me.sheetcoldgames.topdownengine.engine.GameController;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

public class TopDownEngineShowcase extends ApplicationAdapter {
	Input input;
	
	MenuController jvController;
	GameController rgController;
	
	MenuRenderer jvRenderer;
	GameRenderer rgRenderer;
	
	public void create() {
		input = new Input();
		
		jvController = new MenuController(input);
		rgController = new GameController(input);
		
		jvRenderer = new MenuRenderer(jvController);
		rgRenderer = new GameRenderer(rgController);
		
		Gdx.input.setInputProcessor(input);
		
		jvController.isFinished = true;
	}
	
	public void dispose() {
		rgController.dispose();
		jvController.dispose();
		
		jvRenderer.dispose();
		rgRenderer.dispose();
	}
	
	public void render() {
		if (jvController.isFinished) {
			rgController.update();
			rgRenderer.render();
		} else {
			jvController.update();
			jvRenderer.render();
		}
	}

}

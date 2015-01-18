package me.sheetcoldgames.topdownengine;

import me.sheetcoldgames.topdownengine.engine.Input;
import me.sheetcoldgames.topdownengine.engine.JVController;
import me.sheetcoldgames.topdownengine.engine.RGController;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

public class TopDownEngineShowcase extends ApplicationAdapter {
	Input input;
	
	JVController jvController;
	RGController rgController;
	
	JVRenderer jvRenderer;
	RGRenderer rgRenderer;
	
	public void create() {
		input = new Input();
		
		jvController = new JVController(input);
		rgController = new RGController(input);
		
		jvRenderer = new JVRenderer(jvController);
		rgRenderer = new RGRenderer(rgController);
		
		Gdx.input.setInputProcessor(input);
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

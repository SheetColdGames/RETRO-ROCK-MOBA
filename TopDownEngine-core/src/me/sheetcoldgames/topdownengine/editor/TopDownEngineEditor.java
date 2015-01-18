package me.sheetcoldgames.topdownengine.editor;

import com.badlogic.gdx.ApplicationAdapter;

public class TopDownEngineEditor extends ApplicationAdapter {
	
	EditorController controller;
	EditorRenderer renderer;
	
	public void create() {
		controller = new EditorController();
		renderer = new EditorRenderer(controller);
	}
	
	public void dispose() {
		controller.dispose();
		renderer.dispose();
	}
	
	public void render() {
		controller.update();
		renderer.render();
	}
}
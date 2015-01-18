package me.sheetcoldgames.topdownengine;

import me.sheetcoldgames.topdownengine.editor.TopDownEngineEditor;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class EditorLauncher {
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.resizable = false;
		config.width = 640;
		config.height = 480;
		config.title = "SheetColdGames 2D Top Down Editor";
		new LwjglApplication(new TopDownEngineEditor(), config);
	}
}

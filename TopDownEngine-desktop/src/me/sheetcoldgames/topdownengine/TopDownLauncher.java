package me.sheetcoldgames.topdownengine;

import me.sheetcoldgames.topdownengine.Constants;
import me.sheetcoldgames.topdownengine.TopDownEngineShowcase;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class TopDownLauncher {
	
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Constants.WINDOW_WIDTH;
		config.height = Constants.WINDOW_HEIGHT;
		config.resizable = false;
		config.title = Constants.WINDOW_TITLE;
		
		new LwjglApplication(new TopDownEngineShowcase(), config);
	}

}

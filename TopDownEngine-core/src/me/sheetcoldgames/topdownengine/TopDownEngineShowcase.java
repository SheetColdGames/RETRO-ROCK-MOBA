package me.sheetcoldgames.topdownengine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;

public class TopDownEngineShowcase extends ApplicationAdapter {
	
	public void create() {
		
	}
	
	public void dispose() {
		
	}
	
	public void render() {
		Gdx.gl.glClearColor(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

}

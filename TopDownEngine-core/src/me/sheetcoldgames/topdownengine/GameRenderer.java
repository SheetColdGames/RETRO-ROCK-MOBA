package me.sheetcoldgames.topdownengine;

import me.sheetcoldgames.topdownengine.engine.GameController;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * Renderer for the game. The only thing it needs is a controller
 * 
 * @author Rafael Giordanno
 *
 */
public class GameRenderer {
	
	GameController controller;
	ShapeRenderer sr;
	
	public GameRenderer(GameController controller) {
		this.controller = controller;
		sr = new ShapeRenderer();
	}
	
	public void dispose() {
		sr.dispose();
	}
	
	public void render() {
		clearScreen();
		sr.setProjectionMatrix(controller.camera.combined);
		
		
		sr.begin(ShapeType.Filled);
		renderPlayer();
		sr.end();
		
		sr.begin(ShapeType.Line);
		renderMapPoints();
		sr.end();
	}
	
	private void clearScreen() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	private void renderPlayer() {
		sr.setColor(Colors.PLAYER);
		sr.circle(controller.player.position.x, controller.player.position.y, .5f, 16);
	}
	
	private void renderMapPoints() {
		sr.setColor(Colors.COLLISION_POINT);
		for (int groupIndex = 0; groupIndex < controller.groupPoints.size(); groupIndex++) {
			
			for (int currentIndex = 0; currentIndex < controller.groupPoints.get(groupIndex).size()-1; currentIndex++) {
				// Let's render a circle
				sr.circle(controller.groupPoints.get(groupIndex).get(currentIndex).pos.x,
						controller.groupPoints.get(groupIndex).get(currentIndex).pos.y,
						.25f, 16);
				// Let's render a line
				sr.line(controller.groupPoints.get(groupIndex).get(currentIndex).pos,
						controller.groupPoints.get(groupIndex).get(currentIndex+1).pos);
			}
			// if this is the last point, then render a circle for it
			sr.circle(controller.groupPoints.get(groupIndex).getLast().pos.x,
					controller.groupPoints.get(groupIndex).getLast().pos.y,
					.25f, 16);
		}
	}
}

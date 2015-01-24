package me.sheetcoldgames.topdownengine;

import me.sheetcoldgames.topdownengine.engine.Entity;
import me.sheetcoldgames.topdownengine.engine.GameController;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;

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
		
		entColors = new Color[controller.aEntity.size()];
		for (int k = 0; k < controller.aEntity.size(); k++) {
			entColors[k] = new Color(
					MathUtils.random()*.3f+.3f,
					MathUtils.random()*.3f+.4f,
					MathUtils.random()*.3f+.4f, 1f);
		}
	}
	
	public void dispose() {
		sr.dispose();
	}
	
	public void render() {
		clearScreen();
		sr.setProjectionMatrix(controller.camera.combined);
		
		
		sr.begin(ShapeType.Filled);
		renderEntities();
		sr.end();
		
		sr.begin(ShapeType.Line);
		entityCollisionRenderer();
		renderMapPoints();
		sr.end();
	}
	
	private void clearScreen() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	Color[] entColors;
	
	private void renderEntities() {
		sr.setColor(Colors.PLAYER);
		int i = 0;
		for (Entity ent : controller.aEntity) {
			sr.setColor(entColors[i]);
			sr.rect(ent.position.x-ent.width/2f,
					ent.position.y-ent.height/2f,
					ent.width, ent.height);
			++i;
		}
	}
	
	private void entityCollisionRenderer() {
		for (Entity ent : controller.aEntity) {
			renderCollisionBounds(ent);
		}
	}
	
	private void renderCollisionBounds(Entity ent) {
		sr.setColor(Color.RED);
		// horizontal top line
		sr.line(ent.position.x - ent.width/2f - Math.abs(ent.velocity.x) - ent.offset,
				ent.position.y + ent.height/2f,
				ent.position.x + ent.width/2f + Math.abs(ent.velocity.x) + ent.offset,
				ent.position.y + ent.height/2f);
		
		// horizontal bottom line
		sr.line(ent.position.x - ent.width/2f - Math.abs(ent.velocity.x) - ent.offset,
				ent.position.y - ent.height/2f,
				ent.position.x + ent.width/2f + Math.abs(ent.velocity.x) + ent.offset,
				ent.position.y - ent.height/2f);
		
		// vertical left line
		sr.line(ent.position.x - ent.width/2f,
				ent.position.y - ent.height/2f - Math.abs(ent.velocity.y) - ent.offset,
				ent.position.x - ent.width/2f,
				ent.position.y + ent.height/2f + Math.abs(ent.velocity.y) + ent.offset);
		
		// vertical right line
		sr.line(ent.position.x + ent.width/2f,
				ent.position.y - ent.height/2f - Math.abs(ent.velocity.y) - ent.offset,
				ent.position.x + ent.width/2f,
				ent.position.y + ent.height/2f + Math.abs(ent.velocity.y) + ent.offset);
		
		/*
		sr.rect(controller.player.position.x - controller.player.width/2f - Math.abs(controller.player.velocity.x), 
				controller.player.position.y - controller.player.height/2f - Math.abs(controller.player.velocity.y),
				controller.player.width + Math.abs(controller.player.velocity.x),
				controller.player.height + Math.abs(controller.player.velocity.y));
		/*
		sr.line(controller.player.position.x - Math.abs(controller.player.velocity.x) - controller.player.width/2f,
				controller.player.position.y,
				controller.player.position.x + Math.abs(controller.player.velocity.x) + controller.player.width/2f,
				controller.player.position.y);
		sr.line(controller.player.position.x,
				controller.player.position.y - Math.abs(controller.player.velocity.y) - controller.player.height/2f,
				controller.player.position.x,
				controller.player.position.y + Math.abs(controller.player.velocity.y) + controller.player.height/2f);
		*/
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

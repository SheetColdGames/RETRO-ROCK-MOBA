package me.sheetcoldgames.topdownengine.editor;

import me.sheetcoldgames.topdownengine.Colors;
import me.sheetcoldgames.topdownengine.engine.collision.SheetPoint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class EditorRenderer {
	
	EditorController controller;
	
	ShapeRenderer sr;
	
	/** For now, I'm only adding this batch because of the font **/
	SpriteBatch sb;
	BitmapFont font;
	
	OrthogonalTiledMapRenderer mapRenderer;
	
	public EditorRenderer(EditorController controller) {
		this.controller = controller;
		
		sr = new ShapeRenderer();
		
		sb = new SpriteBatch();
		font = new BitmapFont();
		
		mapRenderer = new OrthogonalTiledMapRenderer(controller.map, 1f/16f);
	}
	
	public void dispose() {
		sr.dispose();
		sb.dispose();
		font.dispose();
		mapRenderer.dispose();
	}
	
	public void render() {
		clearScreen();
		renderMap();
		
		sr.begin(ShapeType.Line);
		sr.setProjectionMatrix(controller.camera.combined);
		renderGrid(sr);
		renderPoints();
		sr.end();
		
		renderGUI(sr);
	}
	
	private void clearScreen() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	int[] background = {0};
	// int[] foreground = {1, 2};
	
	private void renderMap() {
		if (controller.toggleMap) {
			mapRenderer.setView(controller.camera);
			mapRenderer.render(background);
			// mapRenderer.render(foreground);
		}
	}
	
	/**
	 * Renders a grid that follows the editor no matter what
	 * It has 1 meter of size
	 * @param renderer a initialized renderer to draw the grid
	 */
	private void renderGrid(ShapeRenderer renderer) {
		int posX = (int) (controller.camera.position.x - controller.camera.viewportWidth/2f - 1f);
		int posY = (int) (controller.camera.position.y - controller.camera.viewportHeight/2f - 1f);
		
		renderer.setColor(controller.toggleGridColor ? Colors.LIGHT_GRID : Colors.DARK_GRID);
		for (int x = posX; x < posX + controller.camera.viewportWidth+2f; x++) {
			for (int y = posY; y < posY + controller.camera.viewportHeight+2f; y++) {
				renderer.rect(x, y, 1, 1);
			}
		}
	}
	
	/**
	 * Render all the connected points 
	 * @param renderer a initialized renderer to draw the points
	 */
	private void renderPoints() {
		for (int groupIndex = 0; groupIndex < controller.groupPoints.size(); groupIndex++) {
			controller.points = controller.groupPoints.get(groupIndex);
			Color currentColor = (groupIndex == controller.currentGroup ? Colors.SELECTED_GROUP : Colors.NON_SELECTED_GROUP);
			sr.setColor(currentColor);
			for (int i = 0; i < controller.groupPoints.get(groupIndex).size()-1; i++) {
				if (controller.selectedPointIndex == i && controller.currentGroup == groupIndex) {
					sr.setColor(Colors.SELECTED_POINT);
					renderPoint(controller.points.get(i));
					sr.setColor(currentColor);
				} else {
					renderPoint(controller.points.get(i));
				}
				
				sr.line(controller.points.get(i).pos, controller.points.get(i+1).pos);
			}
			if (controller.selectedPointIndex == controller.points.size()-1 && controller.currentGroup == groupIndex) {
				sr.setColor(Colors.SELECTED_POINT);
			}
			renderPoint(controller.points.getLast());
		}
		
	}
	
	private void renderPoint(SheetPoint point) {
		sr.circle(point.pos.x, point.pos.y, point.radius.radius, 32);
	}
	
	/**
	 * Here, we'll insert info about:
	 * - the id of the selected group
	 * - the current position of the mouse
	 * - the the position of the last selected point (not necessarily of the selected group)
	 * 
	 * @param renderer
	 */
	private void renderGUI(ShapeRenderer renderer) {
		renderer.begin(ShapeType.Filled);
		renderer.setProjectionMatrix(controller.guiCamera.combined);
		renderer.setColor(Color.BLACK);
		renderer.rect(0f, controller.guiCamera.viewportHeight-40, controller.guiCamera.viewportWidth*2f, controller.guiCamera.viewportHeight);
		renderer.end();
		
		sb.setProjectionMatrix(controller.guiCamera.combined);
		sb.begin();
		font.setScale(1f);
		font.setColor(Color.WHITE);
		// id of the selected group
		font.draw(sb, "Group: " + controller.currentGroup, 10f, controller.guiCamera.viewportHeight-16f);
		// Mouse moving position til' being clicked
		font.draw(sb,
				String.format("(%.2f, %.2f)", controller.input.movingMousePosition.x, controller.input.movingMousePosition.y),
				120f, controller.guiCamera.viewportHeight-16f);
		// position of the mouse dragging
		font.draw(sb,
				String.format("(%.2f, %.2f)", controller.input.currentRawPoint.x, controller.input.currentRawPoint.y),
				controller.guiCamera.viewportWidth-120f, controller.guiCamera.viewportHeight-16f);
		sb.end();
	}
}

package me.sheetcoldgames.topdownengine.engine;

import java.util.ArrayList;
import java.util.LinkedList;

import me.sheetcoldgames.topdownengine.Constants;
import me.sheetcoldgames.topdownengine.engine.collision.SheetPoint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * This variable contains all the logic that handles inside of a game loop
 * There's only one copy of it and it is used by the renderer to draw stuff on
 * the screen
 * @author Rafael Giordanno
 *
 */
public class RGController {
	
	public SheetCamera camera;
	Input input;
	
	public Entity player;
	
	public ArrayList<LinkedList<SheetPoint>> groupPoints;
	LinkedList<SheetPoint> currentPoints;
	
	public RGController(Input input) {
		// Initializing camera
		camera = new SheetCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
		camera.position.set(camera.viewportWidth/2f, camera.viewportHeight/2f, 0f);
		camera.update();
		// =======
		
		// initializing keyboard input
		this.input = input;
		// =======
		
		// Initializing the points for collision detection
		groupPoints = new ArrayList<LinkedList<SheetPoint>>();
		currentPoints = new LinkedList<SheetPoint>();
		currentPoints.add(new SheetPoint(1f, 1f));
		currentPoints.add(new SheetPoint(1f, 4f));
		currentPoints.add(new SheetPoint(3f, 4f));
		groupPoints.add(currentPoints);
		// =======
		
		player = new Entity();
		player.position.set(2f, 2f);
		
		camera.setTarget(player);
		camera.update();
	}
	
	public void dispose() {
		
	}
	
	float dt; // deltaTime
	
	public void update() {
		dt = Gdx.graphics.getDeltaTime();
		handleInput();
		updatePlayer();
		
		camera.update();
	}
	
	private void handleInput() {
		playerInput();
	}
	
	private void playerInput() {
		float walkSpeed = 1f;
		
		// horizontal motion
		if (input.buttons[Input.RIGHT]) {
			player.velocity.x += walkSpeed * dt;  
		} else if (input.buttons[Input.LEFT]) {
			player.velocity.x -= walkSpeed * dt;
		} else {
			player.velocity.x = MathUtils.lerp(player.velocity.x, 0f, .3f);
		}
		
		// vertical motion
		if (input.buttons[Input.UP]) {
			player.velocity.y += walkSpeed * dt;
		} else if (input.buttons[Input.DOWN]) {
			player.velocity.y -= walkSpeed * dt;
		} else {
			player.velocity.y = MathUtils.lerp(player.velocity.y, 0f, .3f);
		}
		
		// clamping the maximum speed
		player.velocity.x = MathUtils.clamp(player.velocity.x, -.2f, .2f);
		player.velocity.y = MathUtils.clamp(player.velocity.y, -.2f, .2f);
		
		System.out.println(Gdx.graphics.getFramesPerSecond());
	}
	
	/**
	 * Crucial fact about this method: It only works with perpendicular walls.
	 * It treats any entity as a cross for its collision detection.
	 * Any wall variation collision coming soon
	 */
	private void updatePlayer() {
		// This variable should be outside to avoid constant creation of new objects
		Vector2 newEntPosition = new Vector2(
				player.position.x + player.velocity.x, 
				player.position.y + player.velocity.y);
		
		Vector2 intersectedPoint = new Vector2();
		
		// walk through all the walls
		for (int currentGroup = 0; currentGroup < groupPoints.size(); currentGroup++) {
			for (int currentPoint = 0; currentPoint < groupPoints.get(currentGroup).size()-1; currentPoint++) {
				SheetPoint p1 = groupPoints.get(currentGroup).get(currentPoint);
				SheetPoint p2 = groupPoints.get(currentGroup).get(currentPoint+1);
				// does this wall intersects the player?
//				if (Intersector.intersectSegmentCircle(
//						groupPoints.get(currentGroup).get(currentPoint).pos, 
//						groupPoints.get(currentGroup).get(currentPoint+1).pos, 
//						newEntPosition, player.sqrRadius)) {

				if (Intersector.intersectSegments(
						p1.pos.x, p1.pos.y, p2.pos.x, p2.pos.y,
						player.position.x-player.radius, player.position.y,
						player.position.x+player.radius, player.position.y,
						intersectedPoint)) {
					// Checking horizontal collision
					// Cancel horizontal velocity
					player.velocity.x = 0;
					// Update the horizontal position with a slight offset
					// p1 and p2 have the same x position
					if (p1.pos.x < player.position.x) {
						// this value is smaller than the radius of the player (else, it wouldn't
						float tmp = player.position.x - newEntPosition.x;
						player.position.x = p1.pos.x + player.radius - tmp;
					} else {
						float tmp = newEntPosition.x - player.position.x ;
						player.position.x = p1.pos.x - player.radius + tmp;
					}
				} else if (Intersector.intersectSegments(
						p1.pos.x, p1.pos.y, p2.pos.x, p2.pos.y,
						player.position.x, player.position.y-player.radius,
						player.position.x, player.position.y+player.radius,
						intersectedPoint)) {
					// Checking vertical collision
					player.velocity.y = 0;
					// Update the vertical position with a slight offset
					if (p1.pos.y < player.position.y) {
						// this value is smaller than the radius of the player (else, it wouldn't
						float tmp = player.position.y - newEntPosition.y;
						player.position.y = p1.pos.y + player.radius - tmp;
					} else {
						float tmp = newEntPosition.y - player.position.y ;
						player.position.y = p1.pos.y - player.radius + tmp;
					}	
				}	
			}
		}
		player.position.x += player.velocity.x;
		player.position.y += player.velocity.y;
	}
}

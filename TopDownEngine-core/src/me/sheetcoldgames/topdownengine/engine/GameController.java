package me.sheetcoldgames.topdownengine.engine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import me.sheetcoldgames.topdownengine.Constants;
import me.sheetcoldgames.topdownengine.engine.collision.SheetPoint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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
public class GameController {
	
	public SheetCamera camera;
	Input input;
	
	public Entity player;
	
	public ArrayList<LinkedList<SheetPoint>> groupPoints;
	LinkedList<SheetPoint> currentPoints;
	
	public GameController(Input input) {
		// Initializing camera
		camera = new SheetCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
		camera.position.set(camera.viewportWidth/2f, camera.viewportHeight/2f, 0f);
		camera.update();
		// =======
		
		// initializing keyboard input
		this.input = input;
		// =======
		
		// Initializing the points for collision detection
		initTestMap("test.fis");
		// =======
		
		player = new Entity();
		player.position.set(90f, 12f);
		
		camera.setTarget(player);
		camera.update();
	}
	
	public void dispose() {
		
	}
	
	private void initTestMap(String filename) {
		groupPoints = new ArrayList<LinkedList<SheetPoint>>();
//		currentPoints = new LinkedList<SheetPoint>();
//		currentPoints.add(new SheetPoint(1f, 1f));
//		currentPoints.add(new SheetPoint(1f, 4f));
//		currentPoints.add(new SheetPoint(3f, 4f));
//		groupPoints.add(currentPoints);
		// here we'll load the map inside the ArrayList<LinkedList<>>
		FileHandle handle = Gdx.files.internal(filename);
		Scanner scan = new Scanner(handle.readString());
		int i = -1;
		
		while (scan.hasNext()) {
			StringBuffer line = new StringBuffer(scan.nextLine());
			String[] sa = line.toString().split(" ");
			if (sa.length == 1) {
				// we're dealing with a new group
				groupPoints.add(new LinkedList<SheetPoint>());
				i++;
			} else {
				// we're dealing with new points
				groupPoints.get(i).add(
						new SheetPoint(Float.parseFloat(sa[0]), Float.parseFloat(sa[1])));
			}
			
		}
		scan.close();
		if (i == -1) {
			// We don't have any points, we must initialize with a group with no points
			groupPoints.add(new LinkedList<SheetPoint>());
		}
	}	
	
	float dt; // deltaTime
	
	public void update() {
		dt = Gdx.graphics.getDeltaTime();
		handleInput();
		updatePlayer();
		
		camera.update();
		// printLog();
	}
	
	private void handleInput() {
		playerInput();
	}
	
	private void playerInput() {
		float walkSpeed = 1f;
		
		// horizontal motion
		if (input.buttons[Input.RIGHT] ^ input.buttons[Input.LEFT]) {
			if (input.buttons[Input.RIGHT]) {
				if (player.velocity.x == 0) {
					player.velocity.x = player.minSpeed;
				}
				player.velocity.x += walkSpeed * dt;  
			} else if (input.buttons[Input.LEFT]) {
				if (player.velocity.x == 0) {
					player.velocity.x = -player.minSpeed;
				}
				player.velocity.x -= walkSpeed * dt;
			}
		} else {
			player.velocity.x = MathUtils.lerp(player.velocity.x, 0f, .4f);
		}
		
		// vertical motion
		if (input.buttons[Input.UP] ^ input.buttons[Input.DOWN]) {
			if (input.buttons[Input.UP]) {
				if (player.velocity.y == 0) {
					player.velocity.y = player.minSpeed;
				}
				player.velocity.y += walkSpeed * dt;
			} else if (input.buttons[Input.DOWN]) {
				if (player.velocity.y == 0) {
					player.velocity.y = -player.minSpeed;
				}
				player.velocity.y -= walkSpeed * dt;
			}
		} else {
			player.velocity.y = MathUtils.lerp(player.velocity.y, 0f, .4f);
		}
		
		// clamping the maximum speed
		player.velocity.x = MathUtils.clamp(player.velocity.x, -.2f, .2f);
		player.velocity.y = MathUtils.clamp(player.velocity.y, -.2f, .2f);
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
				// Let's check horizontally
				if (Intersector.intersectSegments(
						p1.pos.x, p1.pos.y, p2.pos.x, p2.pos.y,
						player.position.x - player.width/2f - Math.abs(player.velocity.x) - player.offset,
						player.position.y + player.height/2f,
						player.position.x + player.width/2f + Math.abs(player.velocity.x) + player.offset,
						player.position.y + player.height/2f,
						intersectedPoint)) { // TOP
					// Checking horizontal collision
					// Cancel horizontal velocity
					player.velocity.x = 0;
					// Update the horizontal position with a slight offset
					// p1 and p2 have the same x position
					if (intersectedPoint.x < player.position.x) {
						// this value is smaller than the radius of the player (else, it wouldn't
						float tmp = player.position.x - newEntPosition.x;
						player.position.x = p1.pos.x + player.width/2f - tmp + player.offset + player.minSpeed;
					} else {
						float tmp = newEntPosition.x - player.position.x ;
						player.position.x = p1.pos.x - player.width/2f + tmp - player.offset - player.minSpeed;
					}
				}
				if (Intersector.intersectSegments(
						p1.pos.x, p1.pos.y, p2.pos.x, p2.pos.y,
						player.position.x - player.width/2f - Math.abs(player.velocity.x) - player.offset,
						player.position.y - player.height/2f,
						player.position.x + player.width/2f + Math.abs(player.velocity.x) + player.offset,
						player.position.y - player.height/2f,
						intersectedPoint)) { // BOTTOM
					player.velocity.x = 0;
					// Update the horizontal position with a slight offset
					// p1 and p2 have the same x position
					if (intersectedPoint.x < player.position.x) {
						// this value is smaller than the radius of the player (else, it wouldn't
						float tmp = player.position.x - newEntPosition.x;
						player.position.x = intersectedPoint.x + player.width/2f - tmp + player.offset + player.minSpeed;
					} else {
						float tmp = newEntPosition.x - player.position.x ;
						player.position.x = intersectedPoint.x - player.width/2f + tmp - player.offset - player.minSpeed;
					}
				} 
				if (Intersector.intersectSegments(
						p1.pos.x, p1.pos.y, p2.pos.x, p2.pos.y,
						player.position.x - player.width/2f,
						player.position.y - player.height/2f - Math.abs(player.velocity.y) - player.offset,
						player.position.x - player.width/2f,
						player.position.y + player.height/2f + Math.abs(player.velocity.y) + player.offset,
						intersectedPoint)) { // LEFT
					// Checking vertical collision
					// Update the vertical position with a slight offset
					if (intersectedPoint.y < player.position.y) {
						// this value is smaller than the radius of the player (else, it wouldn't
						float tmp = player.position.y - newEntPosition.y;
						player.position.y = intersectedPoint.y + player.height/2f - tmp + player.offset + player.minSpeed;
						// player.position.y = intersectedPoint.y + player.height/2f + player.offset + player.minSpeed;
					} else {
						float tmp = newEntPosition.y - player.position.y;
						player.position.y = intersectedPoint.y - player.height/2f + tmp - player.offset - player.minSpeed;
						// player.position.y = intersectedPoint.y - player.height/2f - player.offset - player.minSpeed;
					}
					// Do not forget to reset the velocity
					player.velocity.y = 0;
					
				}
				if (Intersector.intersectSegments(
						p1.pos.x, p1.pos.y, p2.pos.x, p2.pos.y,
						player.position.x + player.width/2f,
						player.position.y - player.height/2f - Math.abs(player.velocity.y) - player.offset,
						player.position.x + player.width/2f,
						player.position.y + player.height/2f + Math.abs(player.velocity.y) + player.offset,
						intersectedPoint)) { // RIGHT
					// Checking vertical collision
					
					// Update the vertical position with a slight offset
					if (intersectedPoint.y < player.position.y) {
						// this value is smaller than the radius of the player (else, it wouldn't
						float tmp = player.position.y - newEntPosition.y;
						player.position.y = intersectedPoint.y + player.height/2f - tmp + player.offset + player.minSpeed;
						// player.position.y = intersectedPoint.y + player.height/2f + player.offset + player.minSpeed;
					} else {
						float tmp = newEntPosition.y - player.position.y;
						player.position.y = intersectedPoint.y - player.height/2f + tmp - player.offset - player.minSpeed;
						// player.position.y = intersectedPoint.y - player.height/2f - player.offset - player.minSpeed;
					}
					// Don't forget to reset the velocity to 0
					player.velocity.y = 0;
				}
			}
		}
		player.position.x += player.velocity.x;
		player.position.y += player.velocity.y;
	}
	
	private void printLog() {
		System.out.println(Gdx.graphics.getFramesPerSecond());
	}
}

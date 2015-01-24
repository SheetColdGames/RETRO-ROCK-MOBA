package me.sheetcoldgames.topdownengine.engine;

import java.util.ArrayList;
import java.util.Collections;
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
 * DEFINING THE ID'S FOR THE ENTITIES:
 * 
 * @author Rafael Giordanno
 *
 */
public class GameController {
	
	public SheetCamera camera;
	Input input;
	
	public ArrayList<Entity> aEntity;
	public int playerId = Constants.PLAYER_ID;
	public int currentPlayerIndex = -1;
	
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
		
		initEntities();
		
		camera.setTarget(aEntity.get(currentPlayerIndex));
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
	
	private void initEntities() {		
		// let's make an array of entities
		aEntity = new ArrayList<Entity>();
		
		// This is the player
		aEntity.add(new Entity());
		aEntity.get(0).id 		= Constants.PLAYER_ID;
		aEntity.get(0).width 	= Constants.PLAYER_WIDTH;
		aEntity.get(0).height 	= Constants.PLAYER_HEIGHT;
		aEntity.get(0).maxSpeed = Constants.PLAYER_WALK_SPEED;
		aEntity.get(0).position.set(90f, 12f);
		currentPlayerIndex = 0;
		
		// Then, for a new set of enemies, we keep adding them in for loops
		/*
		for (int k = aEntity.size(); k < aEntity.size()+2; k++) {
			aEntity.add(new Entity());
			aEntity.get(k).id 		= Constants.AI_ID;
			aEntity.get(k).width 	= Constants.AI_WIDTH/2f;
			aEntity.get(k).height 	= Constants.AI_HEIGHT/2f;
			aEntity.get(k).maxSpeed = Constants.AI_WALK_SPEED;
			
		}
		*/
		
		aEntity.add(new Entity());
		aEntity.get(1).id 		= Constants.AI_ID;
		aEntity.get(1).width 	= Constants.AI_WIDTH;
		aEntity.get(1).height 	= Constants.AI_HEIGHT;
		aEntity.get(1).maxSpeed = Constants.AI_WALK_SPEED;
		aEntity.get(1).position.set(94f, 16f);
		aEntity.get(1).followPoints.add(new Vector2(98f, 16f));
		aEntity.get(1).followPoints.add(new Vector2(94f, 16f));
		
		/*
		aEntity.add(new Entity());
		aEntity.get(2).id 		= Constants.AI_ID;
		aEntity.get(2).width 	= Constants.PLAYER_WIDTH/2f;
		aEntity.get(2).height 	= Constants.PLAYER_HEIGHT/2f;
		aEntity.get(2).maxSpeed = Constants.PLAYER_WALK_SPEED;
		aEntity.get(2).position.set(90f, 16f);
		
		aEntity.add(new Entity());
		aEntity.get(3).id 		= Constants.AI_ID;
		aEntity.get(3).width 	= Constants.AI_WIDTH;
		aEntity.get(3).height 	= Constants.AI_HEIGHT;
		aEntity.get(3).maxSpeed = Constants.AI_WALK_SPEED;
		aEntity.get(3).position.set(94f, 12f);
		*/
	}
	
	float dt; // deltaTime
	
	public void update() {
		dt = Gdx.graphics.getDeltaTime();
		reorganizeEntities(playerId);
		handleAI();
		handleInput();
		updateEntities();
		
		camera.update();
		// printLog();
	}
	
	private void reorganizeEntities(int id) {
		// Since we're dealing with few entities, bubble sort
		// Reorganizing the enemies
		for (int i = 0; i < aEntity.size(); i++) {
			for (int j = i+1; j < aEntity.size(); j++) {
				// We want the guys with higher y values to be rendered first
				if (aEntity.get(j).position.y > aEntity.get(i).position.y) {
					Collections.swap(aEntity, i, j);
				}
			}
		}
		
		// Let's find the player
		for (int k = 0; k < aEntity.size(); k++) {
			if (aEntity.get(k).id == id) {
				currentPlayerIndex = k;
				break;
			}
		}
	}
	
	private void handleAI() {
		/*
		 * How to handle with AI? First, let's address the problems
		 * We have to make the AI follow a certain path to reach its destiny,
		 * we can do that by simply drawing a straight line to its destiny and
		 * change its path to something more accordingly.
		 * I'm feeling dizzy to try this now, so I'll try that tomorrow 
		 */
		for (int k = 0; k < aEntity.size(); k++) {
			if (k == currentPlayerIndex) {
				continue;
			}
			
			
		}
	}
	
	private void handleInput() {
		playerInput(aEntity.get(currentPlayerIndex));
	}
	
	private void playerInput(Entity player) {
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
	
	private void updateEntities() {
		for (Entity ent : aEntity) {
			updateEntity(ent);
		}
	}
	
	/**
	 * Crucial fact about this method: It only works with perpendicular walls.
	 * It treats any entity as a cross for its collision detection.
	 * Any wall variation collision coming soon
	 */
	private void updateEntity(Entity player) {
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

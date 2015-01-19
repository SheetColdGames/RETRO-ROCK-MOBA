package me.sheetcoldgames.topdownengine.editor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import me.sheetcoldgames.topdownengine.engine.Input;
import me.sheetcoldgames.topdownengine.engine.MOUSE_BUTTON;
import me.sheetcoldgames.topdownengine.engine.collision.SheetPoint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;

/**
 * Collision detection editor
 * 
 * This editor will be heavily commented and will be thought of using linked
 * lists.
 * 
 * Let's define something about the controls, so you might call this section as
 * --- INSTRUCTIONS ---
 * --- LMB -> Left Mouse Button
 * --- RMB -> Right Mouse Button
 * - WASD updates the camera position
 * - LMB adds a new point and connects it with the last point of the group
 * - Holding CTRL after LMB places the point on a number divisible by 5 (1.0, 1.5, 2.0, etc...)
 * - Holding SHIFT and LMB on an: 
 * 		* existing point from active group: inserts a new point after the selected one
 * 		* empty space: creates a new group with that new starting point
 * - RMB deletes an existing point
 * - Deleting a point connects its previous point with its next point
 * - Deleting the only point of a group, deletes the group as well
 * - Q and E navigates through the available groups of points.
 * - R toggles the grid color to be brighter/darker
 * - SPACE BAR toggles the background reference map
 * - CTRL+X prints the map coordinates on the console
 * - WARNING! THERE IS NO UNDO KEY TO THIS EDITOR, BE CAREFUL WHILE EDITING!
 * 
 * @author Rafael Giordanno
 *
 */
public class EditorController {
	/** The editor camera, nothing special **/
	OrthographicCamera camera;
	
	/** The GUI camera, the name says it all **/
	OrthographicCamera guiCamera;
	
	/** Our Input class, it uses an array, pretty dope **/
	Input input;
	
	ArrayList<LinkedList<SheetPoint>> groupPoints;
	
	/** It might not seem interesting, but we keep the points in a LinkedList */
	LinkedList<SheetPoint> points;
	
	/** Prevent us from saving tons of times because of constant input */
	boolean saved = false;
	
	/** This is the index of the latest selected point */
	int selectedPointIndex = -1;
	
	int currentGroup;
	
	/* Tiled map related variables */
	TiledMap map;
	
	// Released Keys booleans
	boolean saveReleased = false;
	
	boolean previousReleased = false;
	boolean nextReleased = false;
	
	boolean toggleMapReleased = false;
	boolean toggleGridColorReleased = false;
	
	// constants
	final float cameraSpeed = .4f;
	
	public EditorController() {
	
		camera = new OrthographicCamera(20f, 15f);
		camera.position.set(camera.viewportWidth/2f, camera.viewportHeight/2f, 0f);
		camera.update();
		
		guiCamera = new OrthographicCamera(640, 480);
		guiCamera.position.set(guiCamera.viewportWidth/2f, guiCamera.viewportHeight/2f, 0f);
		guiCamera.update();
		
		input = new Input();
		Gdx.input.setInputProcessor(input);
		
		groupPoints = new ArrayList<LinkedList<SheetPoint>>();
		points = new LinkedList<SheetPoint>();
		points.add(new SheetPoint(1f, 12f));
		groupPoints.add(points);
		
		// We're adding this points for debugging purposes
		loadMap("test.fis");
		
		// Map
		map = new TmxMapLoader().load("test.tmx");	
	}
	
	public void dispose() {
		map.dispose();
		for (int i = 0; i < groupPoints.size(); i++) {
			groupPoints.get(i).clear();
			groupPoints.remove(i);
		}
		groupPoints.clear();
	}
	
	private void loadMap(String filename) {
		groupPoints = new ArrayList<LinkedList<SheetPoint>>();
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
	
	float dt;
	
	public void update() {
		dt = Gdx.graphics.getDeltaTime();
		
		handleInput();
		
		camera.update();
	}
	
	private void handleInput() {
		cameraControls();
		editorControls();
		checkMouse();
		checkSave();
	}
	
	/** Moves the camera around for simplicity */
	private void cameraControls() {
		// === begin camera controls
		if (input.buttons[Input.CAM_UP]) camera.position.y += cameraSpeed;
		else if (input.buttons[Input.CAM_DOWN]) camera.position.y -= cameraSpeed;
		
		if (input.buttons[Input.CAM_RIGHT]) camera.position.x += cameraSpeed;
		else if (input.buttons[Input.CAM_LEFT]) camera.position.x -= cameraSpeed;
		// === end of camera controls
	}
	
	boolean toggleGridColor = true;
	boolean toggleMap = true;
	
	private void editorControls() {
		if (input.buttons[Input.PREVIOUS_GROUP]) {
			previousReleased = true;
		} else if (input.buttons[Input.NEXT_GROUP]) {
			nextReleased = true;
		}
		
		if (!input.buttons[Input.PREVIOUS_GROUP] && previousReleased) {
			if (currentGroup > 0) {
				currentGroup--;
			}
			previousReleased = false;
		}
		
		if (!input.buttons[Input.NEXT_GROUP] && nextReleased) {
			if (currentGroup < groupPoints.size()-1) {
				currentGroup++;
			}
			nextReleased = false;
		}
		
		// toggle dark/light grid color
		if (input.buttons[Input.TOGGLE_GRID]) {
			toggleGridColorReleased = true;
		}
		
		if (!input.buttons[Input.TOGGLE_GRID] && toggleGridColorReleased) {
			toggleGridColor = !toggleGridColor;
			toggleGridColorReleased = false;
		}
		
		// toggle map
		if (input.buttons[Input.TOGGLE_MAP]) {
			toggleMapReleased = true;
		}
		
		if (!input.buttons[Input.TOGGLE_MAP] && toggleMapReleased) {
			toggleMap = !toggleMap;
			toggleMapReleased = false;
		}
		
	}
	
	private void checkMouse() {
		// check if the mouse's been pressed down
		// if true, unproject the mouse position using the camera
		// check if the mouse position is the same of some existing point
		// if true, select that point
		// else, create new point and select this new point
		
		// else if the mouse is being dragged, update the position of the selected point
		
		// else if the mouse's been released, update the point
		// ---
		
		camera.unproject(input.movingMousePosition);
		
		if (input.mouseDown) {
			camera.unproject(input.currentRawPoint);
			
			// System.out.println("Mouse position on mouse down: " + input.currentRawPoint.x + ", " + input.currentRawPoint.y);
			
			selectedPointIndex = -1;
			points = groupPoints.get(currentGroup);
			
			// look for some existing point
			for (int i = 0; i < groupPoints.get(currentGroup).size(); i++) {
				// if true, select that point
				if (groupPoints.get(currentGroup).get(i).contains(input.currentRawPoint.x, input.currentRawPoint.y)) {
					selectedPointIndex = i;
					break;
				}
			}
			
			// else, create a new point and select it
			if (input.lastMouseButton == MOUSE_BUTTON.LEFT) {
				// If we're pressing shift we want...
				if (input.buttons[Input.SHIFT]) {
					// ...to create a new point if we're clicking an empty space
					// (in a new group, tho...)
					if (selectedPointIndex == -1) {
						selectedPointIndex = 0;
						points = new LinkedList<SheetPoint>();
						points.add(new SheetPoint(input.currentRawPoint.x, input.currentRawPoint.y));
						groupPoints.add(points);
						currentGroup = groupPoints.size()-1;
						selectedPointIndex = 0;
					} else { // we'll create a new point on the current group
						++selectedPointIndex;
						groupPoints.get(currentGroup).add(
								selectedPointIndex, new SheetPoint(input.currentRawPoint.x, input.currentRawPoint.y));
					}
				} else {
					// if we're not pressing shift, we just want to create a new point
					if (selectedPointIndex == -1) {
						selectedPointIndex = points.size();
						groupPoints.get(currentGroup).add(new SheetPoint(input.currentRawPoint.x, input.currentRawPoint.y));
					}
				}
				
			}
			input.mouseDown = false;
		} else if (input.mouseDragged) {
			camera.unproject(input.currentRawPoint);
			
			if (input.lastMouseButton == MOUSE_BUTTON.LEFT) {
				// update the selected point
				if (input.buttons[Input.CTRL]) {
					float x = input.currentRawPoint.x - MathUtils.floor(input.currentRawPoint.x);
					float y = input.currentRawPoint.y - MathUtils.floor(input.currentRawPoint.y);
					groupPoints.get(currentGroup).get(selectedPointIndex).set(
							(int)input.currentRawPoint.x + (x > .5f ? .5f : 0f), 
							(int)input.currentRawPoint.y + (y > .5f ? .5f : 0f));
				} else {
					groupPoints.get(currentGroup).get(selectedPointIndex).set(input.currentRawPoint.x, input.currentRawPoint.y);
				}
			}
			
			// System.out.println("Mouse position on mouse drag: " + input.currentRawPoint.x + ", " + input.currentRawPoint.y);
			
			input.mouseDragged = false;
		} else if (input.mouseReleased){
			camera.unproject(input.currentRawPoint);
			
			if (input.lastMouseButton == MOUSE_BUTTON.LEFT) {
				// update the selected point
				if (input.buttons[Input.CTRL]) {
					float x = input.currentRawPoint.x - MathUtils.floor(input.currentRawPoint.x);
					float y = input.currentRawPoint.y - MathUtils.floor(input.currentRawPoint.y);
					groupPoints.get(currentGroup).get(selectedPointIndex).set(
							(int)input.currentRawPoint.x + (x > .5f ? .5f : 0f), 
							(int)input.currentRawPoint.y + (y > .5f ? .5f : 0f));
				} else {
					groupPoints.get(currentGroup).get(selectedPointIndex).set(input.currentRawPoint.x, input.currentRawPoint.y);
				}
			} else if (input.lastMouseButton == MOUSE_BUTTON.RIGHT) {
				groupPoints.get(currentGroup).remove(selectedPointIndex);
				if (groupPoints.get(currentGroup).size() == 0) {
					groupPoints.remove(currentGroup);
					if (currentGroup >= groupPoints.size())
						--currentGroup;
				}
			}
			
			input.mouseReleased = false;
		}
	}
	
	private void printLog() {
		System.out.println("--- Log ---");
		System.out.println("Total points: " + points.size());
		System.out.println("Points positions: ");
		for (int i = 0; i < points.size(); i++) {
			System.out.println(i + " " + points.get(i).getX() + " " + points.get(i).getY());
		}
		
		System.out.println("");
	}
	
	private void checkSave() {
		// saving options TODO make it work with byte array (safer)
		if (!input.buttons[Input.CTRL] && !input.buttons[Input.SAVE_POINTS] && saveReleased) {
			System.out.println("\n");
			for (int groupIndex = 0; groupIndex < groupPoints.size(); groupIndex++) {
				System.out.println(groupIndex);
				points = groupPoints.get(groupIndex);
				for (int i = 0; i < groupPoints.get(groupIndex).size(); i++) {
					// System.out.println(Float.floatToRawIntBits(points.get(i).getX()) + " " + Float.floatToRawIntBits(points.get(i).getY()));
					System.out.printf("%.1f %.1f\n", points.get(i).getX(), points.get(i).getY());
				}
				// System.out.println(selectedPointIndex);
			}
			saveReleased = false;
		}
		if (input.buttons[Input.CTRL] && input.buttons[Input.SAVE_POINTS]) {
			saveReleased = true;
		}
	}
}

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
public class NetworkController {
	
	public SheetCamera camera;
	Input input;
	
	public Entity player;
	public Entity player2;
	
	public ArrayList<LinkedList<SheetPoint>> groupPoints;
	LinkedList<SheetPoint> currentPoints;
	
	public HostController host;
	public ClientController client;
	String hostAddr = "192.168.1.10"; 
	public int hostCmd = 99998;
	public int clientCmd = 99999;
	public String[] clientStatus = {"0","90f","12f"}; // "cmd/playerPosX/playerPosY"
	public float clientPosX = 9999999f;
	public float clientPosY = 9999999f;
	
	public boolean isHost = false; //set false to use as client (as host it will wait for connection)
	public boolean connected = false;
	
	
	
	public NetworkController(Input input) {
		
		// Initializing connection
		if (isHost){
			host = new HostController();
		}
		else{
			client = new ClientController(hostAddr);
			connected = client.isConnected();
		}
		// =======
		
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
		player2 = new Entity();
		player2.position.set(70f, 12f);
		
		if (isHost){
			camera.setTarget(player2);
		} else{
			camera.setTarget(player);
		}
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
		
		// wait for client to connect
		if(isHost && !connected){
			connected = host.clientConnected();
		}
	
		handleInput();
		updatePlayer();
		updatePlayer2();
		
		camera.update();
		
		connectionSendUpdate();
		connectionReceiveUpdate();
	}
	
	public void connectionSendUpdate(){
		if (connected){
			if(isHost){
				host.sendToClient(hostCmd);
			}
			else{
				client.sendToHost(clientStatus);
			}
			
		}
	}
	
	public void connectionReceiveUpdate(){
		if(connected){
			if(isHost){
				clientStatus = host.getFromClientSTR();// aqui
				clientCmd = Integer.parseInt(clientStatus[0]);
				clientPosX = Float.parseFloat(clientStatus[1]);
				clientPosY = Float.parseFloat(clientStatus[2]);
				
			}
			else{
				hostCmd = client.getFromHost();
			}
		}
	}
	
	
	private void handleInput() {
		if(isHost){
			player2Input();
		}
		else{
			playerInput();
		}
	}
	
	private void playerInput() {
		float walkSpeed = 1f;
		
		// horizontal motion
		if (input.buttons[Input.RIGHT]) {
			player.velocity.x += walkSpeed * dt;  
			
		}else if (input.buttons[Input.LEFT]) {
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
		
		//System.out.println(Gdx.graphics.getFramesPerSecond());
	}
	
	private void player2Input() {
		float walkSpeed = 1f;
		
		// horizontal motion
		if (input.buttons[Input.RIGHT]) {
			player2.velocity.x += walkSpeed * dt;  
			clientCmd = 1;
		} else if (input.buttons[Input.LEFT]) {
			player2.velocity.x -= walkSpeed * dt;
		} else {
			player2.velocity.x = MathUtils.lerp(player2.velocity.x, 0f, .3f);
			clientCmd = 0;
		}
		
		// vertical motion
		if (input.buttons[Input.UP]) {
			player2.velocity.y += walkSpeed * dt;
		} else if (input.buttons[Input.DOWN]) {
			player2.velocity.y -= walkSpeed * dt;
		} else {
			player2.velocity.y = MathUtils.lerp(player2.velocity.y, 0f, .3f);
		}
		
		// clamping the maximum speed
		player2.velocity.x = MathUtils.clamp(player2.velocity.x, -.2f, .2f);
		player2.velocity.y = MathUtils.clamp(player2.velocity.y, -.2f, .2f);
		
		//System.out.println(Gdx.graphics.getFramesPerSecond());
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
	}
		
		private void updatePlayer2() {
			// This variable should be outside to avoid constant creation of new objects
			Vector2 newEntPosition = new Vector2(
					player2.position.x + player2.velocity.x, 
					player2.position.y + player2.velocity.y);
			
			Vector2 intersectedPoint = new Vector2();
			
			// walk through all the walls
			for (int currentGroup = 0; currentGroup < groupPoints.size(); currentGroup++) {
				for (int currentPoint = 0; currentPoint < groupPoints.get(currentGroup).size()-1; currentPoint++) {
					SheetPoint p1 = groupPoints.get(currentGroup).get(currentPoint);
					SheetPoint p2 = groupPoints.get(currentGroup).get(currentPoint+1);
					// does this wall intersects the player?
//					if (Intersector.intersectSegmentCircle(
//							groupPoints.get(currentGroup).get(currentPoint).pos, 
//							groupPoints.get(currentGroup).get(currentPoint+1).pos, 
//							newEntPosition, player.sqrRadius)) {

					if (Intersector.intersectSegments(
							p1.pos.x, p1.pos.y, p2.pos.x, p2.pos.y,
							player2.position.x-player2.radius, player2.position.y,
							player2.position.x+player2.radius, player2.position.y,
							intersectedPoint)) {
						// Checking horizontal collision
						// Cancel horizontal velocity
						player2.velocity.x = 0;
						// Update the horizontal position with a slight offset
						// p1 and p2 have the same x position
						if (p1.pos.x < player2.position.x) {
							// this value is smaller than the radius of the player (else, it wouldn't
							float tmp = player2.position.x - newEntPosition.x;
							player2.position.x = p1.pos.x + player2.radius - tmp;
						} else {
							float tmp = newEntPosition.x - player2.position.x ;
							player2.position.x = p1.pos.x - player2.radius + tmp;
						}
					} else if (Intersector.intersectSegments(
							p1.pos.x, p1.pos.y, p2.pos.x, p2.pos.y,
							player2.position.x, player2.position.y-player2.radius,
							player2.position.x, player2.position.y+player2.radius,
							intersectedPoint)) {
						// Checking vertical collision
						player2.velocity.y = 0;
						// Update the vertical position with a slight offset
						if (p1.pos.y < player2.position.y) {
							// this value is smaller than the radius of the player (else, it wouldn't
							float tmp = player2.position.y - newEntPosition.y;
							player2.position.y = p1.pos.y + player2.radius - tmp;
						} else {
							float tmp = newEntPosition.y - player2.position.y ;
							player2.position.y = p1.pos.y - player2.radius + tmp;
						}	
					}	
				}
			}
		
			player.position.x += player.velocity.x;
			player.position.y += player.velocity.y;
			player2.position.x += player2.velocity.x;
			player2.position.y += player2.velocity.y;
		if(!isHost){
			clientStatus[1] = String.valueOf(player.position.x+"f");
			clientStatus[2] = String.valueOf(player.position.y+"f");
		}
		else{
			player.position.x = clientPosX;
			player.position.y = clientPosY;
		}
		
	}
}

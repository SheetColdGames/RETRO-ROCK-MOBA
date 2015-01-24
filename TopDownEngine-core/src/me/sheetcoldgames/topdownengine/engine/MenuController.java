package me.sheetcoldgames.topdownengine.engine;

import java.util.ArrayList;

import me.sheetcoldgames.topdownengine.NetworkRenderer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;

public class MenuController {
	
	public boolean isFinished = false;
	public NetworkController netController;
	public NetworkRenderer netRenderer;
	public boolean mp = false;
	
	OrthographicCamera camera;
	
	Input input;
	
	public ArrayList<Rectangle> buttons;
	
	public MenuController(Input input) {
		camera = new OrthographicCamera(640f, 480f);
		camera.position.set(camera.viewportWidth/2f, camera.viewportHeight/2f, 0f);
		camera.update();
		
		this.input = input;
		
		buttons = new ArrayList<Rectangle>();
		for (int i = 0; i < 3; i++) {
			buttons.add(
					new Rectangle(
							camera.viewportWidth/2f-64f,
							camera.viewportHeight/2f+64f-i*64f, 
							128f, 48f)
					);
		}
	}
	
	public void initMP(boolean host){
		netController = new NetworkController(input,host);
		netController.isHost = host;
		netRenderer = new NetworkRenderer(netController);
	}
	
	public void dispose() {
		
	}
	
	public void update() {
		handleInput();
	}
	
	boolean mouseReleased = false;
	
	private void handleInput() {
		camera.unproject(input.currentRawPoint);
		
		if (input.mouseDown) {
			mouseReleased = true;
		}
		
		if (!input.mouseDown && input.mouseReleased && mouseReleased) {
			mouseReleased = false;
			
			for (int i = 0; i < buttons.size(); i++) {
				if (buttons.get(i).contains(input.currentRawPoint.x, input.currentRawPoint.y)) {
					if (i == 0){
						isFinished = true;
					}
					
					if (i == 1){
						initMP(true);
						mp = true;
						isFinished = true;
					} else if(i == 2){
						initMP(false);
						mp = true;
						isFinished = true;
					}
					System.out.println("Index of button: " + i);
				}
			}
		}
	}
}

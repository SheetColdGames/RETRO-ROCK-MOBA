package me.sheetcoldgames.topdownengine;

public class Constants {
	// Most of these stuff should be on a config file, but I have other priorities
	// right now
	public static final int WINDOW_WIDTH = 640;
	public static final int WINDOW_HEIGHT = 480;
	public static final String WINDOW_TITLE = "SheetColdGames Top Down Engine Showcase";
	
	public static final int TILE_SIZE = 32; 
	
	public static final float VIEWPORT_WIDTH = WINDOW_WIDTH / TILE_SIZE;
	public static final float VIEWPORT_HEIGHT= WINDOW_HEIGHT/ TILE_SIZE;
	
	public static final int PLAYER_ID = 1;
	public static final float PLAYER_WALK_SPEED = .6f;
	public static final float PLAYER_WIDTH = 1f;
	public static final float PLAYER_HEIGHT = 1.5f;
	
	public static final int AI_ID = 2;
	public static final float AI_WALK_SPEED = .3f;
	public static final float AI_WIDTH = 1.5f;
	public static final float AI_HEIGHT = 2f;
}

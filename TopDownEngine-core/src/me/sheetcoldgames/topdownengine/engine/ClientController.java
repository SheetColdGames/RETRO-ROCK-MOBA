package me.sheetcoldgames.topdownengine.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ClientController {
	
	private static final int PORT = 30480;
	private Socket clientSocket;
	private PrintWriter printWriter;
	private boolean connected = false;
	private BufferedReader bufferedReader; 
	private int hostCmd;
	
	public ClientController(InetAddress addr){
		
		try{
			clientSocket = new Socket(addr, PORT);
			this.connected = true;
		} catch(Exception e){
			System.out.println(e);
			return;
		}
		
		System.out.println("Client connected to "+addr+":"+PORT);
	}
	
	public boolean isConnected(){
		if (this.connected){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void sendToHost(int cmd){ // TODO:overload with serializable
		try {
			printWriter = new PrintWriter(clientSocket.getOutputStream(),true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		printWriter.println(cmd);
	}
	
	public int getFromHost(){ // TODO:overload with serializable
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			hostCmd = bufferedReader.read();
			return hostCmd;
		} catch (IOException e) {
			System.out.println("host -> client error");
			e.printStackTrace();
			return 9999;//connection lost
		}
	}
	
	
}

package me.sheetcoldgames.topdownengine.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import me.sheetcoldgames.topdownengine.TopDownEngineShowcase;

public class ClientController {
	
	private static final int PORT = 30480;
	private Socket clientSocket;
	private PrintWriter printWriter;
	private boolean connected = false;
	private BufferedReader bufferedReader; 
	private int hostCmd;
	public String clientStatus;
	private String hostStatus;
	private String[] hostHash = {"0","70f", "12f"};
	
	public ClientController(String addr){
		
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
	
	public void sendToHost(String[] clientHash){ // TODO:overload with serializable // string with " cmd, playerPosX, playerPosY"
		try {
			//send client cmd
			clientStatus = String.join("/", clientHash);
			//clientStatus = clientStatus+"\n";
			printWriter = new PrintWriter(clientSocket.getOutputStream(),true);
			printWriter.println(clientStatus);
			System.out.println("sent: "+clientStatus);
			printWriter = null;
			
			/*//send client player position
			printWriter = new PrintWriter(clientSocket.getOutputStream(),true);
			printWriter.println(cmd);
			System.out.println("sent: "+cmd);*/
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
	
	InputStream is;
	
	public String[] getFromHostSTR(){// TODO:overload with serializable
		try {
			is = clientSocket.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(is));
			if(bufferedReader.ready()){
				hostStatus = bufferedReader.readLine();
				System.out.println("received: "+hostStatus);
				hostHash = hostStatus.split("/");
			}
			
				
			return hostHash;
			
		} catch (IOException e) {
			System.out.println(e);
			return hostHash;// connection lost
		}
	}
	
	
}

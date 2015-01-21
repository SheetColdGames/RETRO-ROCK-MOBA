package me.sheetcoldgames.topdownengine.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;




public class HostController {
	
	private static final int PORT = 30480;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private BufferedReader bufferedReader;
	//private String inputLine;
	private int clientCmd;
	private InetAddress hostAddress;
	private PrintWriter printWriter;

	
	public HostController(){
		try{
			hostAddress = InetAddress.getLocalHost();
		} catch(UnknownHostException e){
			System.out.println("Cold not get the host address.");
			return;
		}
		
		System.out.println("Server host address is: "+hostAddress);
		
		try{
			serverSocket = new ServerSocket(PORT,0,hostAddress);
		} catch(IOException e){
			System.out.println("Could not open server socket");
			return;
		}
		
		System.out.println("Socket "+serverSocket+ "created.");
	}
	
	public boolean clientConnected(){
			try{
				clientSocket = serverSocket.accept();
			} catch(IOException e){
				System.out.println("Could not get a client");
				return false;
			}
			
			System.out.println("Client "+clientSocket+" has connected.");
			return true;
	}
	
	public void sendToClient(int cmd){ // TODO:overload with serializable
		try {
			printWriter = new PrintWriter(clientSocket.getOutputStream(),true);
			printWriter.println(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
		printWriter.println(cmd);
	}
	
	public int getFromClient(){// TODO:overload with serializable
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			clientCmd = bufferedReader.read();
			return clientCmd;
		} catch (IOException e) {
			System.out.println(e);
			return 99999;// connection lost
		}
	}
	
}

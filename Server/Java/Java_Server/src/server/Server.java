package server;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.*;
import clientThread.ClientThread;

public class Server implements Runnable {
	Thread t;
	StringBuilder outBuilder;
	
	public CopyOnWriteArrayList<ClientThread> clients;
	
	public Server(){
		clients = new CopyOnWriteArrayList<ClientThread>();
	}
	
	private void WAIT(int millis){
		try{
			Thread.sleep(millis);
		}catch(Exception e){
			
		}
	}
	
	private String condense(String a, int b){
		StringBuilder sb = new StringBuilder(String.valueOf(a));
		sb.append(b);
		return sb.toString();
	}
	
	@Override
	public void run(){
		int portNumber = 50007;
		int totalMsgs = 0;
		boolean waterstatus = false;
		Integer numClients = 0;
		
		Thread checkForDead = new Thread(new Runnable(){
			public void run(){
				while(true){
					Iterator<ClientThread> clientIterator = clients.iterator();
					while (clientIterator.hasNext()) {
						ClientThread check = clientIterator.next();
						if(!check.isConnected()){
							System.out.println("Removed dead client with index: " + check.toString());
							check.disconnect();
							clients.remove(check);
						}
					}
					WAIT(5000);
				}
			}
		});
		
		checkForDead.start();
		
		while(true){
			System.out.println("connecting...");
			try ( 
			    ServerSocket serverSocket = new ServerSocket(portNumber);
			) {
				System.out.println("Opening Socket Nr.:" + numClients);
				Socket clientSocket = serverSocket.accept();
				ClientThread client = new ClientThread(clientSocket, numClients);
				clients.add(client);
				clients.get(clients.size()-1).start();
				numClients++;
				
			}catch(Exception e){
				System.out.println("could not connect...");
				WAIT(500);
			}
		}
		
	}
	
	public void start(){
		t = new Thread(this);
		t.start();
	}

}

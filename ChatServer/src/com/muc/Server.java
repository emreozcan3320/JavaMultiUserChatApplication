package com.muc;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

	private int serverPort;
	
	private List<ServerWorker> workerList = new ArrayList<>();

	public List<ServerWorker> getWorkerList() {
		return workerList;
	}

	public Server(int serverPort) {
		this.serverPort = serverPort;
	}
	
	@Override
	public void run() {
	try {
		ServerSocket serverSocket = new ServerSocket(serverPort);
		while(true) {
			// Accept any client connections 
			System.out.println("About to Accept client connection ...");
			Socket clientSocket = serverSocket.accept();
			System.out.println("Accepted connection from" + clientSocket);
			
			// We create a new thread EVERY TIME we get a connection from client
			ServerWorker worker = new ServerWorker(this, clientSocket);
			workerList.add(worker);
			worker.start();
		}
		
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	}

}

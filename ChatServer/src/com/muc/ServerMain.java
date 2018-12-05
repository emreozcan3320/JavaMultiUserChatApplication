package com.muc;


import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {

	public static void main(String[] args) {
		//  in order for us to create a network server 
		//  we have to create something called a server socket
	
		int port = 8818;
		
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while(true) {
				// Accept any client connections 
				System.out.println("About to Accept client connection ...");
				Socket clientSocket = serverSocket.accept();
				System.out.println("Accepted connection from" + clientSocket);
				
				// We create a new thread EVERY TIME we get a connection from client
				ServerWorker worker = new ServerWorker(clientSocket);
				worker.start();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	
	
	
}

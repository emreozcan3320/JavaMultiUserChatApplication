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
				
				Thread t = new Thread() {
					@Override
					public void run() {
						try {
							handleClient(clientSocket);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				};
				
				t.start();	
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	private static void handleClient(Socket clientSocket) throws IOException {
		
		OutputStream outputStream = clientSocket.getOutputStream();
		for(int i=0; i<10; i++) {
			outputStream.write("hello worldn".getBytes());
			try {
				Thread.sleep(1000);	
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		clientSocket.close();
	}
	
	
}

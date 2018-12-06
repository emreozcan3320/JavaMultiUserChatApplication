package com.muc;

public class ServerMain {

	public static void main(String[] args) {
		//  in order for us to create a network server 
		//  we have to create something called a server socket
		int port = 8818;
		Server server = new Server(port);
		server.start();
		
		
	}

	
	
	
}

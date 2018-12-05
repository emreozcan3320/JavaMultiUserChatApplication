package com.muc;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ServerWorker extends Thread {

	private Socket clientSocket;

	public ServerWorker(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	@Override
	public void run() {
		try {
			handleClient();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleClient() throws IOException {
			
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

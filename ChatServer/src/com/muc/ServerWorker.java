package com.muc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
		// getting access to the input stream for reading the data from the client
		InputStream inputStream = clientSocket.getInputStream();				
		// output stream that we can access from the client socket to get data from client
		OutputStream outputStream = clientSocket.getOutputStream();
		
		// we create a buffer reader to read line by line
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while((line = reader.readLine()) != null) {
			if("quit".equalsIgnoreCase(line)) {
				break;
			}
			String msg = "You typed : "+ line+"\n";
			outputStream.write(msg.getBytes());
		}
		clientSocket.close();
	}
}

package com.muc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.lang3.StringUtils;

public class ServerWorker extends Thread {

	private Socket clientSocket;
	private String login = null;

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
		outputStream.write("Program started ...\n".getBytes());
		
		String line;
		while((line = reader.readLine()) != null) {
			String[] tokens = StringUtils.split(line);
			
			if(tokens != null && tokens.length > 0 ) {
				String cmd  = tokens[0];
				if("quit".equalsIgnoreCase(cmd)) {
					break;
				}else if(cmd.equals("login")){
					handleLogin(outputStream, tokens);
				}else {
					String msg = "unknown command :" + cmd +"\n";
					outputStream.write(msg.getBytes());
				}
			}
			
		}
		clientSocket.close();
	}

	private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
		if(tokens.length == 3 ) {
			String login = tokens[1];
			String password = tokens[2];
			
			if(login.equals("guest")&&password.equals("guest")
					|| (login.equals("emre")&&password.equals("emre"))) {
				String msg = "ok login";
				outputStream.write(msg.getBytes());
				this.login = login;
				System.out.println("User Loged In Succesfully : " + login);
			}else {
				String msg = "error login fail";
				outputStream.write(msg.getBytes());
			}
		}
		
	}
}

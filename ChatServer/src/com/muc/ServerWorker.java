package com.muc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class ServerWorker extends Thread {
	private final Server server;
	private final Socket clientSocket;
	private String login = null;
	private OutputStream outputStream;
	private HashSet<String> topicSet = new HashSet<>();

	public ServerWorker(Server server, Socket clientSocket) {
		this.server = server;
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
		this.outputStream = clientSocket.getOutputStream();
		
		// we create a buffer reader to read line by line
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		outputStream.write("Program started ...\n".getBytes());
		
		String line;
		while((line = reader.readLine()) != null) {
			String[] tokens = StringUtils.split(line);
			
			if(tokens != null && tokens.length > 0 ) {
				String cmd  = tokens[0];
				if("logoff".equalsIgnoreCase(cmd) || "quit".equalsIgnoreCase(cmd)) {
					handleLogoff();
					break;
				}else if(cmd.equals("login")){
					String[] tokensMsg = StringUtils.split(line,null,3);
					handleLogin(outputStream, tokens);
				}else if ("msg".equalsIgnoreCase(cmd)){
					handleMessage(tokens);
				}else if("join".equalsIgnoreCase(cmd)){
					handleJoin(tokens);					
				}else {
					String msg = "unknown command :" + cmd +"\n";
					outputStream.write(msg.getBytes());
				}
			}
			
		}
		clientSocket.close();
	}
	
	public boolean isMemberOfTopic(String topic) {
		return topicSet.contains(topic);
	}
	
	private void handleJoin(String[] tokens) {
		if(tokens.length > 1) {
			String topic = tokens[1];
			topicSet.add(topic);
		}
		
	}

	// format: "msg" "login" msg...
	// format: "msg" "#topic" msg...
	private void handleMessage(String[] tokens) throws IOException {
			String sendTo = tokens[1];
			String body = tokens[2];
			
			boolean isTopic = sendTo.charAt(0) == '#';
			
			List<ServerWorker> workerList = server.getWorkerList();
			for(ServerWorker worker : workerList) {
				if(isTopic) {
					if(worker.isMemberOfTopic(sendTo)) {
						String outMsg = "msg" +sendTo +":"+login+" "+body+"\n";
						worker.send(outMsg);
					}
				}else {
					if(sendTo.equalsIgnoreCase(worker.getLogin())) {
						String outMsg = "msg" +login +""+body+"\n";
						worker.send(outMsg);
					}
				}
				
			}
	}

	private void handleLogoff() throws IOException {
		server.removeWorker(this);
		List<ServerWorker> workerList = server.getWorkerList();
		
		// send other online users current user's status
		String onlineMsg = "offline"+login+"\n";
		for(ServerWorker worker : workerList) {
			if(!login.equals(worker.getLogin())){
				worker.send(onlineMsg);
			}
		}
		clientSocket.close();
		
	}

	public String getLogin() {
		return login;
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
				
				List<ServerWorker> workerList = server.getWorkerList();
				
				//send current user all other online logins
				for(ServerWorker worker : workerList) {					
					if(worker.getLogin() != null) {
						if(!login.equals(worker.getLogin())){
							String msg2 = "online" + worker.getLogin();
							send(msg2);	
						}
					}						
									
				}
				
				// send other online users current user's status
				String onlineMsg = "online"+login+"\n";
				for(ServerWorker worker : workerList) {
					if(!login.equals(worker.getLogin())){
						worker.send(onlineMsg);
					}
				}
			}else {
				String msg = "error login fail";
				outputStream.write(msg.getBytes());
			}
		}
		
	}

	private void send(String msg) throws IOException {
		if(login != null) {
			outputStream.write(msg.getBytes());
		}
		
	}
}

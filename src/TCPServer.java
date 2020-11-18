import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends Thread {
	
	String servernm;
	String sharedroot;
	private String currentDir;
	int port;
	Users users;			//pass value to this global variable in startup
	
	
	
	public void Server() throws IOException{
		currentDir = sharedroot;
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println(servernm+" listening at port " + port);
		while(true) {
			Socket clientSocket = serverSocket.accept();
			System.out.printf("Connected client (%s:%d)\n", clientSocket.getInetAddress(), clientSocket.getPort());
			new Thread(()-> {
				serve(clientSocket, users);
			}).start();
		}
	}
	
	//copypasted the file server from lab, needs to be modified
//	public TCPServer(int port, User users) throws IOException {			//use linkedlist to make a tcpserver class so that can use login method
//		ServerSocket serverSocket = new ServerSocket(port);
//		System.out.println("Listening at port " + port);
//		while(true) {
//			Socket clientSocket = serverSocket.accept();
//			System.out.printf("Connected client (%s:%d)\n", clientSocket.getInetAddress(), clientSocket.getPort());
//			new Thread(()-> {
//				serve(clientSocket, users);
//			}).start();
//		}
//	}
	
	//serve needs to be rewritten to accept commands and then call methods to fulfill the command	
	private void serve(Socket socket, Users users) {
		byte[] buffer = new byte[1024];
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			
			int userlogin = in.readInt();
			in.read(buffer, 0, userlogin);
			String namepw = new String(buffer, 0, userlogin);
			//System.out.println(namepw);
			
			String[] cmd = namepw.split(" ");
			if (users.logIn(cmd[0], cmd[1])) {
				sendOut("success", out);
			}else {
				sendOut("Failure", out);
			}
			
			String access = users.getAccess();
			boolean connected = true;
			
			while (connected) {
				if (access.trim().equalsIgnoreCase("basic")) {
					sendOut("Your available actions: ", out);
					sendOut("1. Read file list [DIR]", out);
					sendOut("3. Upload and download files [UPL]/[DWL] [name]", out);
					sendOut("7. Read file information [READ] [name]", out);
					sendOut("\nPlease input command", out);
					sendOut("end", out);
				}else if (access.trim().equalsIgnoreCase("partial")) {
					sendOut("Your available actions: ", out);
					sendOut("1. Read file list [DIR]", out);
					sendOut("2. Create sub-directory [MKDIR] [name]", out);
					sendOut("3. Upload and download files [UPL]/[DWL] [name]", out);
					sendOut("6. Change file/target name [RENAME] [name] [new name]", out);
					sendOut("7. Read file information [READ] [name]", out);
					sendOut("\nPlease input command", out);
					sendOut("end", out);
				}else if (access.trim().equalsIgnoreCase("full")) {
					sendOut("Your available actions: ", out);
					sendOut("1. Read file list [DIR]", out);
					sendOut("2. Create sub-directory [MKDIR][name]", out);
					sendOut("3. Upload and download files [UPL]/[DWL] [name]", out);
					sendOut("4. Delete files [DEL] [name]", out);
					sendOut("5. Delete sub-directory [DELDIR] [name]", out);
					sendOut("6. Change file/target name [RENAME] [name] [new name]", out);
					sendOut("7. Read file information [READ] [name]", out);
					sendOut("\nPlease input command", out);
					sendOut("end", out);
				}
				
				String usercmd = receiveCmd(in);
				System.out.println("Received cmd: "+usercmd);			//test if receive cmd
				interpretCmd(usercmd, out, in);
			}
			
//			System.out.print("Downloading file %s " + name);
//
//			long size = in.readLong();
//			System.out.printf("(%d)", size);
//
//			
//			File file = new File(name);
//			FileOutputStream out = new FileOutputStream(file);
//
//			while(size > 0) {
//				int len = in.read(buffer, 0, buffer.length);
//				out.write(buffer, 0, len);
//				size -= len;
//				System.out.print(".");
//			}
//			System.out.println("\nDownload completed.");
//			
//			in.close();
//			out.close();
		} catch (Exception e) {
			System.err.println("Server Error");
		}
	}
	
	public void sendOut(String str, DataOutputStream out) throws IOException {
		out.writeInt(str.length());
		out.write(str.getBytes(), 0, str.length());
	}
	
	public String receiveCmd(DataInputStream in) throws IOException {			
		//receivedCmd doesnt need to return String we can take input of access level and handle the methods for each user inside
		byte[] buffer = new byte[1024];
		String cmd ="";
		try {
			while (true) {
				int len = in.readInt();
				in.read(buffer, 0, len);	//read into the buffer the len amount of data from the inputstream
				
				cmd = (new String(buffer, 0, len));
				return cmd;
			}
		} catch (IOException ex) {
			System.out.println("Server connection dropped");
			System.exit(-1);
		}
		
		return cmd;
	}
	
	public void interpretCmd(String cmd, DataOutputStream out, DataInputStream in) throws IOException {
		String[] cmdTokens = cmd.trim().split(" ");
		String Tokenpara = "";
		
		if (cmdTokens.length >= 2) {
			for (int i=1; i<cmdTokens.length; i++) {
				if (i==1) {
					Tokenpara = cmdTokens[i];
				}else {
					Tokenpara = Tokenpara+" "+cmdTokens[i];
				}
			}
		}
		
		if (cmdTokens[0].equals("")) {
			System.err.println("No input received!\n");
		}else {
			switch (cmdTokens[0].toLowerCase()) {
			case "dir":
				directory(out);
				break;
			case "mkdir":
				makeDir(Tokenpara, out);
				break;
			case "upl":
				//System.out.println("upload requested");          //debugging purposes
				download(in);
				break;
			case "dwl":
				break;
			case "del":
				break;
			case "deldir":
				break;
			case "rename":
				break;
			case "read":
				break;
			case "help":     //help and add does not need any server action
				break;
			case "add":
//				String input = receiveCmd(in);
//				if (input.equalsIgnoreCase("start")) {
//					users.print();						//testing to see if updated linkedlist will pass onto server
//				}
				break;
			case "remove":
				break;
			default: 
				//System.out.println("Please input a valid command");
				sendOut("Please input a valid command", out);
			}
		}
	}
	
	public void directory(DataOutputStream out) throws IOException {				// function 1
		File dFile = new File(sharedroot);
		if (dFile.exists()) {
			String[] files = dFile.list();
			
			for (String file : files) {
				sendOut(file, out);		
			}
			sendOut("end", out);
		}else {
//			System.err.println("Error in sharedroot directory");
			sendOut("Error in sharedroot directory", out);
		}
		return;
	}
	
	public void makeDir(String cmd, DataOutputStream out) throws IOException {							//function 2
		if (cmd.contains(sharedroot)) {
			File newDir = new File(cmd);
			newDir.mkdirs();
		}else {
			cmd = sharedroot+"\\"+cmd;
			try {
				File newDir = new File(cmd);
				newDir.mkdirs();
			}catch (Exception e) {
				sendOut("Path provided not valid", out);
				sendOut("end", out);
				return;
			}
		}
		
		sendOut("Sub-directories created successfully!", out);
		sendOut("end", out);
	}
	
	public void download(DataInputStream in) {			//receive upload (one side upload, other side download)
		byte[] buffer = new byte[1024];
		try {
			int nameLen = in.readInt();
			in.read(buffer, 0, nameLen);			
			String name = new String(buffer, 0, nameLen);
			
			if (name.equals("404 not found")) {
				return;
			}

			System.out.print("Downloading file %s " + name);
			System.out.println(name);
//			String[] nameTokens = name.trim().split("\\");			//for some reason, the name of file is already parsed from the path provided...?
//			name = nameTokens[nameTokens.length-1];			//from the client path given, take the name of the file only
			

			long size = in.readLong();
			System.out.printf("(%d)", size);

			name = currentDir+"/"+name;
			File file = new File(name);
			FileOutputStream out = new FileOutputStream(file);

			while(size > 0) {
				int len = in.read(buffer, 0, buffer.length);
				out.write(buffer, 0, len);
				size -= len;
				System.out.print(".");
			}
			out.close();
			System.out.println("\nDownload completed.");
		} catch (IOException e) {
			System.err.println("unable to download file.");
		}
	}
	
	public static void main(String[] args) {

		//please do not delete this.
//		int port = 0;
//		try {
//			if (args.length != 1)
//				throw new NumberFormatException();
//			
//			port = Integer.parseInt(args[0]);
//			
//		} catch (NumberFormatException e) {
//			System.err.println("Invalid port number.");
//			System.err.println("Usage: java FileServer portNumber");
//			System.exit(-1);
//		}
//		
//		try {
//			new TCPServer(port);
//		} catch (IOException e) {
//			System.err.println("Unable to start server with port " + port);
//		}
	}
	
	public void run() {
		try {
			Server();
			//TCPServer server = new TCPServer(port, users);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in launching server");
		}
	}
	


}

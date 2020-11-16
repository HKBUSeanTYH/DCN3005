import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
	int port;
	Users users;			//pass value to this global variable in startup
	
	
	
	public void Server() throws IOException{
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("Listening at port " + port);
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
			
			if (access.trim().equalsIgnoreCase("basic")) {
				sendOut("Your available actions: ", out);
				sendOut("1. Read file list", out);
				sendOut("3. Upload and download files", out);
				sendOut("7. Read file information", out);
				sendOut("\nPlease input a number", out);
				sendOut("end", out);
				
				String usercmd = receiveCmd(in);
				System.out.println("Received cmd: "+usercmd);			//test if receive cmd
			}else if (access.trim().equalsIgnoreCase("partial")) {
				sendOut("Your available actions: ", out);
				sendOut("1. Read file list", out);
				sendOut("2. Create sub-directory", out);
				sendOut("3. Upload and download files", out);
				sendOut("6. Change file/target name", out);
				sendOut("7. Read file information", out);
				sendOut("\nPlease input a number", out);
				sendOut("end", out);
				
				String usercmd = receiveCmd(in);
				System.out.println("Received cmd: "+usercmd);			//test if receive cmd
			}else if (access.trim().equalsIgnoreCase("full")) {
				sendOut("Your available actions: ", out);
				sendOut("1. Read file list", out);
				sendOut("2. Create sub-directory", out);
				sendOut("3. Upload and download files", out);
				sendOut("4. Delete files", out);
				sendOut("5. Delete sub-directory", out);
				sendOut("6. Change file/target name", out);
				sendOut("7. Read file information", out);
				sendOut("\nPlease input a number", out);
				sendOut("end", out);
				
				String usercmd = receiveCmd(in);
				System.out.println("Received cmd: "+usercmd);			//test if receive cmd
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
			System.err.println("Failure in logging in");
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
	
	public void executeCmd(String cmd, DataOutputStream out) {
		
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

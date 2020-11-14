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
	
	int port;
	User users;			//pass value to this global variable in startup
	
	//copypasted the file server from lab, needs to be modified
	public TCPServer(int port, User users) throws IOException {			//use linkedlist to make a tcpserver class so that can use login method
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
	
	//serve needs to be rewritten to accept commands and then call methods to fulfill the command	
	private void serve(Socket socket, User users) {
		byte[] buffer = new byte[1024];
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			
			int userlogin = in.readInt();
			in.read(buffer, 0, userlogin);
			String namepw = new String(buffer, 0, userlogin);
			
			String[] cmd = namepw.split(" ");
			if (users.logIn(cmd[0], cmd[1])) {
				String result = "success";
				out.writeInt(result.length());
				out.write(result.getBytes(), 0, result.length());
			}else {
				String result = "fail";
				out.writeInt(result.length());
				out.write(result.getBytes(), 0, result.length());
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
			TCPServer server = new TCPServer(port, users);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in launching server");
		}
	}
	


}

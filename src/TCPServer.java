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

public class TCPServer {			//copypasted the file server from lab, needs to be modified

	public TCPServer(int port) throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("Listening at port " + port);
		while(true) {
			Socket clientSocket = serverSocket.accept();
			System.out.printf("Connected client (%s:%d)\n", clientSocket.getInetAddress(), clientSocket.getPort());
			new Thread(()-> {
				serve(clientSocket);
			}).start();
		}
	}
	
	private void serve(Socket socket) {
		byte[] buffer = new byte[1024];
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			
			int nameLen = in.readInt();
			in.read(buffer, 0, nameLen);
			String name = new String(buffer, 0, nameLen);

			System.out.print("Downloading file %s " + name);

			long size = in.readLong();
			System.out.printf("(%d)", size);

			
			File file = new File(name);
			FileOutputStream out = new FileOutputStream(file);

			while(size > 0) {
				int len = in.read(buffer, 0, buffer.length);
				out.write(buffer, 0, len);
				size -= len;
				System.out.print(".");
			}
			System.out.println("\nDownload completed.");
			
			in.close();
			out.close();
		} catch (IOException e) {
			System.err.println("unable to download file.");
		}
	}

	public static void main(String[] args) {

		int port = 0;
		try {
			if (args.length != 1)
				throw new NumberFormatException();
			
			port = Integer.parseInt(args[0]);
			
		} catch (NumberFormatException e) {
			System.err.println("Invalid port number.");
			System.err.println("Usage: java FileServer portNumber");
			System.exit(-1);
		}
		
		try {
			new TCPServer(port);
		} catch (IOException e) {
			System.err.println("Unable to start server with port " + port);
		}
	}
	


}

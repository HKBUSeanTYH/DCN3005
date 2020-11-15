import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class TCPClient extends Thread {
	
	String serverIP;
	int port;
	
	public void Client() throws IOException{
		Scanner scanner = new Scanner(System.in); // take user input and send to server
		boolean login = false;
		
		while (!login) {
			Socket socket = new Socket(serverIP, port);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());
			
			System.out.println("Please input your username");
			String username = scanner.nextLine();
			System.out.println("Please input your password");
			String pw = scanner.nextLine();
			System.out.println("Connecting to server...");
			
			if (sendLogin(username, pw, out, in)) {		//if login method returns true break out of while loop (maybe change to a do-while loop?)
				login = true;
			}			
		}
		
		System.out.println("Log in success!");
	}

//	public TCPClient(String serverIP, int port) throws IOException{
//		
//		Scanner scanner = new Scanner(System.in); // take user input and send to server
//		boolean login = false;
//		
//		while (!login) {
//			Socket socket = new Socket(serverIP, port);
//			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//			DataInputStream in = new DataInputStream(socket.getInputStream());
//			
//			System.out.println("Please input your username");
//			String username = scanner.nextLine();
//			System.out.println("Please input your password");
//			String pw = scanner.nextLine();
//			System.out.println("Connecting to server...");
//			
//			if (sendLogin(username, pw, out, in)) {		//if login method returns true break out of while loop (maybe change to a do-while loop?)
//				login = true;
//			}			
//		}
//		
//		System.out.println("Log in success!");
//		
//	}
	
	public boolean sendLogin(String username, String pw, DataOutputStream out, DataInputStream in) throws IOException {
		String send = username+" "+pw;
		out.writeInt(send.length());
		out.write(send.getBytes(), 0, send.length());
		
		byte[] buffer = new byte[1024];
		try {
			while (true) {
				int len = in.readInt();
				in.read(buffer, 0, len);	//read into the buffer the len amount of data from the inputstream
				
				if ((new String(buffer, 0, len)).equalsIgnoreCase("sucess")) {				//write a method to check if the login is success
					return true;
				}else {
					System.out.println("Username and password not accepted");						//how to terminate system or terminate login??
					return false;
				}
			}
		} catch (IOException ex) {
			System.out.println("Server connection dropped");
			System.exit(-1);
		}
		
		return false;
	}

	public void sendCommand(DataOutputStream out) throws IOException {

	}
	
	//we need a way to receive the socket's inputstream (DataInputStream input = new DataInputStream(socket.getInputStream())
	public void receive() throws IOException {
		
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
	}
	
	public void run() {
		try {
			Client();
			//TCPClient client = new TCPClient(serverIP, port);		previous class constructor
		}catch (Exception e) {
			System.out.println("Error in launching client");
		}
		
	}
	
}

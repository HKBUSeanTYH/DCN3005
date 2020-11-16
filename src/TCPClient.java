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
		
		Socket socket = null;
		DataOutputStream out = null;
		DataInputStream in = null;
		
		while (!login) {
			socket = new Socket(serverIP, port);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			
			System.out.println("Please input your username");
			String username = scanner.nextLine();
			System.out.println("Please input your password");
			String pw = scanner.nextLine();
			System.out.println("Connecting to server...");
			
			if (sendLogin(username, pw, out, in)) {		//if login method returns true break out of while loop (maybe change to a do-while loop?)
				login = true;
			}			
		}
		
		System.out.println("Log in success!\n");
		receive(in);			//receive user actions for user's access level
		
		boolean loggedIn = true;
		
		while (loggedIn) {
			String cmd = scanner.nextLine();
			sendCmd(cmd, out, in);
//			if (!cmd.equals("3")) {
//				receive(in);
//			}else if(cmd.equalsIgnoreCase("quit")) {
//				loggedIn = false;
//				break;
//			}else {		//if uploading or downloading files, use a different receive method
//				
//			}
		}
		
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
				
				String result = (new String(buffer, 0, len));
				//System.out.println(result);
				
				if (result.equalsIgnoreCase("success")) {				//write a method to check if the login is success
					return true;
				}else {
					return false;
				}
			}
		} catch (IOException ex) {
			System.out.println("Server connection dropped");
			System.exit(-1);
		}
		
		return false;
	}

	public void sendCmd(String str, DataOutputStream out, DataInputStream in) throws IOException {
		out.writeInt(str.length());
		out.write(str.getBytes(), 0, str.length());
		receive(in);
	}
	
	//we need a way to receive the socket's inputstream (DataInputStream input = new DataInputStream(socket.getInputStream())
	public void receive(DataInputStream in) throws IOException {
		byte[] buffer = new byte[1024];
		try {
			while (true) {
				int len = in.readInt();
				in.read(buffer, 0, len);	//read into the buffer the len amount of data from the inputstream
				
				String result = (new String(buffer, 0, len));
				if (result.equals("end")) {					//end of the returned result
					break;
				}
				System.out.println(result);		//print out the result
			}
		} catch (IOException ex) {
			System.out.println("Server connection dropped");
			System.exit(-1);
		}
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

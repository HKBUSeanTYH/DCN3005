import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient extends Thread {
	
	String serverIP;
	int port;
	
	public void Client() throws IOException, InterruptedException{
		Scanner scanner = new Scanner(System.in); // take user input and send to server
		boolean login = false;
		
		Socket socket = null;
		DataOutputStream out = null;
		DataInputStream in = null;
		
		while (!login) {
			socket = new Socket(serverIP, port);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			
			Thread.sleep(100);
			
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
			sendCmd(cmd, out);

			String[] cmdTokens = cmd.trim().split(" ");
			if (cmdTokens[0].equals("")) {
				System.err.println("No input received!\n");
			}else {
				switch (cmdTokens[0].toLowerCase()) {
				case "dir":
					receive(in);
					break;
				case "mkdir":
					break;
				case "upl":
					String name ="";
					for (int i=1; i<cmdTokens.length; i++) {
						if (i == 1) {
							name = cmdTokens[i];
						}else {
							name += " "+cmdTokens[i];
						}
					}
					upload(name, out);
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
				default: 
					System.out.println("Please input a valid command");
				}
			}
			
			receive(in);			//receive next set of actions
		}
		
	}
	
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

	public void sendCmd(String str, DataOutputStream out) throws IOException {
		out.writeInt(str.length());
		out.write(str.getBytes(), 0, str.length());
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
	
	public void upload(String filename, DataOutputStream out) throws IOException {
		File fname = new File(filename);
		if (fname.exists()) {
			try {
				FileInputStream in = new FileInputStream(fname);
				
				byte[] buffer = new byte[1024];
				
				out.writeInt(fname.getName().length());
				out.write(fname.getName().getBytes(), 0, fname.getName().length());
				
				long size = fname.length();
				out.writeLong(size);
				
				while(size >0) {
					int len = in.read(buffer, 0, buffer.length);
					out.write(buffer, 0, len);
					size -= len;
				}
			}catch (Exception e) {
				System.out.println("Error occurred in uploading file to server. Please try again");
			}
		}else {
			System.out.println("File not found");
			String quit = "404 not found";
			sendCmd(quit, out);
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

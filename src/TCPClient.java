import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient extends Thread {
	
	sharedRoot sroot;
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
			String name ="";
			
			if (cmdTokens.length >= 2) {
				for (int i=1; i<cmdTokens.length; i++) {
					if (i == 1) {
						name = cmdTokens[i];
					}else {
						name += " "+cmdTokens[i];
					}
				}
			}
			if (cmdTokens[0].equals("")) {
				System.err.println("No input received!\n");
			}else {
				switch (cmdTokens[0].toLowerCase()) {
				case "dir":
					receive(in);
					break;
				case "mkdir":
					receive(in);    //receive success or failure to make directory/directories
					break;
				case "upl":
					upload(name, out);
					break;
				case "dwl":
					download(in);
					receive(in);
					break;
				case "del":
					receive(in);
					break;
				case "deldir":
					receive(in);  //do i recursively delete all?
					break;
				case "rename":
					renameFile(out);
					receive(in);
					break;
				case "read":
					receive(in);
					break;
				case "help":
					receive(in);
					break;
				case "add":
					sroot.addUsers();
					//sendCmd("start", out);
					break;
				case "remove":
					Thread.sleep(100);
					System.out.println("Please input user to remove");
					String username = scanner.nextLine();
					sroot.removeUsers(username);
					break;
				case "cd":
					receive(in);
					break;
				case "recurdir":
					receive(in);
					break;
				case "shutdown":
					System.exit(0);
					break;
				default: 
					System.out.println("Please input a valid command");
				}
			}
			//System.out.println("receiving next actions");        //debugging
			receive(in);			//receive next set of actions
		}
		
	}
	
	public boolean sendLogin(String username, String pw, DataOutputStream out, DataInputStream in) throws IOException {
		String send = username;
		out.writeInt(send.length());
		out.write(send.getBytes(), 0, send.length());
		
		send = pw;
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
		if (filename.equals("")) {
			System.out.println("Please provide a file name");
			String quit = "404 not found";
			sendCmd(quit, out);
			return;
		}
		File fname = new File(filename);
		if (fname.exists()) {
			try {
				FileInputStream in = new FileInputStream(fname);
				
				byte[] buffer = new byte[1024];
				
				out.writeInt(fname.getName().length());
				out.write(fname.getName().getBytes(), 0, fname.getName().length());			//writes file name only, not including the path
				
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
	
	public void download(DataInputStream in) throws IOException {			//receive upload (one side upload, other side download)
		byte[] buffer = new byte[1024];
		try {
			int nameLen = in.readInt();
			in.read(buffer, 0, nameLen);			
			String name = new String(buffer, 0, nameLen);
			
			if (name.equals("404 not found") || name.equals("start")) {
				return;
			}

			System.out.print("Downloading file %s " + name);
			//System.out.println(name);
//			String[] nameTokens = name.trim().split("\\");			//for some reason, the name of file is already parsed from the path provided...?
//			name = nameTokens[nameTokens.length-1];			//from the client path given, take the name of the file only
			

			long size = in.readLong();
			System.out.printf("(%d)", size);

			//name = currentDir+"/"+name;
			File file = new File(name);
			FileOutputStream out = new FileOutputStream(file);

			while(size > 0) {
				if (size <= buffer.length) {
					int sizelong = (int) size;
					int len = in.read(buffer, 0, sizelong);
					out.write(buffer, 0, len);
					size -= len;
					System.out.print(".");
				}else {
					int len = in.read(buffer, 0, buffer.length);
					out.write(buffer, 0, len);
					size -= len;
					System.out.print(".");
				}
//				int len = in.read(buffer, 0, buffer.length);
//				out.write(buffer, 0, len);
//				size -= len;
//				System.out.print(".");
			}
			out.close();
			System.out.println("\nDownload completed.");
		} catch (IOException e) {
			System.err.println("unable to download file.");
		}
	}
	
	public void renameFile(DataOutputStream out) throws IOException {
		Scanner in = new Scanner(System.in);
		while (true) {
			System.out.println("Please provide a new name for the file");
			String newname = in.nextLine();
			
			if (newname.equals("")) {
				System.out.println("No input detected!");
			}else {
				out.writeInt(newname.length());
				out.write(newname.getBytes(), 0, newname.length());
				break;
			}
		}
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		String shared = "D:\\seant\\2uniFiles";
//		String current2 = "D:\\seant\\2uniFiles";
//		String current = "D:\\seant";		
//		
//		if (current2.contains(shared)) {
//			System.out.println("Yes");
//		}
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

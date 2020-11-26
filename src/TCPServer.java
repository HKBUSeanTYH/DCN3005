import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
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
	Users users; // pass value to this global variable in startup

	public void Server() throws IOException {
		currentDir = sharedroot;
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println(servernm + " listening at port " + port);
		while (true) {
			Socket clientSocket = serverSocket.accept();
			System.out.printf("Connected client (%s:%d)\n", clientSocket.getInetAddress(), clientSocket.getPort());
			new Thread(() -> {
				serve(clientSocket, users);
			}).start();
		}
	}

	private void serve(Socket socket, Users users) {
		byte[] buffer = new byte[1024];
		
		ArrayList<String> useraccess = users.getAccess();
		ArrayList<String> loggedin = users.loggedIn();
		String access = null;
		String loggedIn = null;
		
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());

			int userlogin = in.readInt();
			in.read(buffer, 0, userlogin);
			String name = new String(buffer, 0, userlogin);
			
			userlogin = in.readInt();
			in.read(buffer, 0, userlogin);
			String pw = new String(buffer, 0, userlogin);

			
			synchronized (loggedin){
				if (users.logIn(name, pw)) {
					sendOut("success", out);
					loggedIn = name;
					access = useraccess.get(loggedin.indexOf(name));  
					//getting the value based on index of loggedIn because there are duplicate values in arraylist
				} else {
					sendOut("Failure", out);
				}
			}
			
			
			boolean connected = true;

			while (connected) {
				if (access.trim().equalsIgnoreCase("basic")) {
					sendOut("Your available actions: ", out);
					sendOut("1. Read file list [DIR]", out);
					sendOut("3. Upload and download files [UPL]/[DWL] [name]", out);
					sendOut("7. Read file information [READ] [name]", out);
					sendOut("\nPlease input command", out);
					sendOut("end", out);
				} else if (access.trim().equalsIgnoreCase("partial")) {
					sendOut("Your available actions: ", out);
					sendOut("1. Read file list [DIR]", out);
					sendOut("2. Create sub-directory [MKDIR] [name]", out);
					sendOut("3. Upload and download files [UPL]/[DWL] [name]", out);
					sendOut("6. Change file/target name [RENAME] [name]", out);
					sendOut("7. Read file information [READ] [name]", out);
					sendOut("\nPlease input command", out);
					sendOut("end", out);
				} else if (access.trim().equalsIgnoreCase("full")) {
					sendOut("Your available actions: ", out);
					sendOut("1. Read file list [DIR]", out);
					sendOut("2. Create sub-directory [MKDIR][name]", out);
					sendOut("3. Upload and download files [UPL]/[DWL] [name]", out);
					sendOut("4. Delete files [DEL] [name]", out);
					sendOut("5. Delete sub-directory [DELDIR]/[RECURDIR] [name]", out);
					sendOut("6. Change file/target name [RENAME] [name]", out);
					sendOut("7. Read file information [READ] [name]", out);
					sendOut("\nPlease input command", out);
					sendOut("end", out);
				}

				String usercmd = receiveCmd(in);
				System.out.println("Received cmd: " + usercmd); // test if receive cmd
				interpretCmd(usercmd, access.trim(), out, in);
			}
		} catch (Exception e) {
			System.err.println("Server Error\n");
			if (loggedIn != null) {
				synchronized (loggedin) {
					useraccess.remove(loggedin.indexOf(loggedIn));  //loggedIn is the string of username get the index of this and use it to remove access
					loggedin.remove(loggedIn);
				}
			}
		}
	}

	public void sendOut(String str, DataOutputStream out) throws IOException {
		out.writeInt(str.length());
		out.write(str.getBytes(), 0, str.length());
	}

	public String receiveCmd(DataInputStream in) throws IOException {
		// receivedCmd doesnt need to return String we can take input of access level
		// and handle the methods for each user inside
		byte[] buffer = new byte[1024];
		String cmd = "";
		try {
			while (true) {
				int len = in.readInt();
				in.read(buffer, 0, len); // read into the buffer the len amount of data from the inputstream

				cmd = (new String(buffer, 0, len));
				return cmd;
			}
		} catch (IOException ex) {
			System.out.println("Server connection dropped");
			System.exit(-1);
		}

		return cmd;
	}

	public void interpretCmd(String cmd, String access, DataOutputStream out, DataInputStream in) throws IOException {
		String[] cmdTokens = cmd.trim().split(" ");
		String Tokenpara = "";

		if (cmdTokens.length >= 2) {
			for (int i = 1; i < cmdTokens.length; i++) {
				if (i == 1) {
					Tokenpara = cmdTokens[i];
				} else {
					Tokenpara = Tokenpara + " " + cmdTokens[i];
				}
			}
		}

		if (cmdTokens[0].equals("")) {
			System.err.println("No input received!\n");
		} else {
			switch (cmdTokens[0].toLowerCase()) {
			case "dir":
				directory(out);
				break;
			case "mkdir":
				if (access.equals("basic")) {
					sendOut("You do not have access to this function!", out);
					sendOut("end", out);
				} else {
					if (Tokenpara.equals("")) {
						sendOut("Please provide a filename!", out);
						sendOut("end", out);
					}else {
						makeDir(Tokenpara, out);
					}
				}
				break;
			case "upl":
				download(in);
				break;
			case "dwl":
				if (Tokenpara.equals("")) {
					sendOut("404 not found", out);
					sendOut("Please provide a filename!", out);
					sendOut("end", out);
				}else {
					upload(Tokenpara, out);
				}
				break;
			case "del":
				if (access.equals("full")) {
					deleteFile(Tokenpara, out);
				} else {
					sendOut("You do not have access to this function!", out);
					sendOut("end", out);
				}
				break;
			case "deldir":
				if (access.equals("full")) {
					deldir(Tokenpara, out);
				} else {
					sendOut("You do not have access to this function!", out);
					sendOut("end", out);
				}
				break;
			case "rename":
				if (access.equals("basic")) {
					sendOut("You do not have access to this function!", out);
					sendOut("end", out);
				} else {
					if (Tokenpara.equals("")) {
						sendOut("Please provide a filename!", out);
						sendOut("end", out);
					}else {
						renameFile(Tokenpara, in, out);
					}
				}
				break;
			case "read":
				readFile(Tokenpara, out);
				break;
			case "help": // help and add does not need any server action
				getHelp(access, out);
				break;
			case "add":
//				String input = receiveCmd(in);
//				if (input.equalsIgnoreCase("start")) {
//					users.print();						//testing to see if updated linkedlist will pass onto server
//				}
				break;
			case "remove":
				break;
			case "cd":
				changeDir(Tokenpara, out);
				break;
			case "recurdir":
				if (access.equals("full")) {
					recursiveDel(Tokenpara, out);
				}else {
					sendOut("You do not have access to this function!", out);
					sendOut("end", out);
				}
				
				break;
			default:
				// System.out.println("Please input a valid command");
				sendOut("Please input a valid command", out);
				sendOut("end", out);
			}
		}
	}

	public void directory(DataOutputStream out) throws IOException { // function 1
		File dFile = new File(currentDir);
		if (dFile.exists()) {
			String[] files = dFile.list();

			for (String file : files) {
				sendOut(file, out);
			}
		} else {
//			System.err.println("Error in sharedroot directory");
			sendOut("Error in sharedroot directory", out);
		}
		sendOut("end", out);
	}

	public void makeDir(String cmd, DataOutputStream out) throws IOException { // function 2
		if (cmd.startsWith(currentDir)) {
			File newDir = new File(cmd);
			newDir.mkdirs();
		} else if (cmd.startsWith(sharedroot)) {
			File newDir = new File(cmd);
			newDir.mkdirs();
		} else {
			cmd = currentDir + "\\" + cmd;
			try {
				File newDir = new File(cmd);
				newDir.mkdirs();
			} catch (Exception e) {
				sendOut("Path provided not valid", out);
				sendOut("end", out);
				return;
			}
		}

		sendOut("Sub-directories created successfully!", out);
		sendOut("end", out);
	}

	public void upload(String filename, DataOutputStream out) throws IOException {
		File fname = null;
		if (filename.startsWith(sharedroot)) { // if the path provided starts with sharedroot
			fname = new File(filename);

			if (!fname.exists()) {
				System.out.println("File not exists");
				String quit = "404 not found";
				sendOut(quit, out);
				return;
			}

			if (fname.isFile()) {
				try {
					FileInputStream in = new FileInputStream(fname);

					byte[] buffer = new byte[1024];

					out.writeInt(fname.getName().length());
					out.write(fname.getName().getBytes(), 0, fname.getName().length()); // writes file name only, not
																						// including the path

					long size = fname.length();
					out.writeLong(size);

					while (size > 0) {
						int len = in.read(buffer, 0, buffer.length);
						out.write(buffer, 0, len);
						size -= len;
					}
				} catch (Exception e) {
					System.out.println("Error occurred in uploading file to client. Please try again");
				}
			} else {
				System.out.println("File is a directory");
				String quit = "404 not found";
				sendOut(quit, out);
			}
		} else {
			// recursively traverse subdirectories and printout all paths of files that
			// match name and ask user to specify the path?
			File rootfile = new File(sharedroot);
			sendOut("start", out); // stop client from downloading and prepare to receive possible paths
			displayFiles(rootfile, filename, out);
			sendOut("Please specify a path in order to download an existing file!\n", out);
			sendOut("end", out);
		}
	}

	public void download(DataInputStream in) { // receive upload (one side upload, other side download)
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

			name = currentDir + "/" + name;
			File file = new File(name);
			FileOutputStream out = new FileOutputStream(file);

			while (size > 0) {
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

	public void deleteFile(String filename, DataOutputStream out) throws IOException {
		if (filename.equals("")) {
			sendOut("Please provide a filename!", out);
			sendOut("end", out);
			return;
		}
		
		if (filename.startsWith(sharedroot)) {
			File fname = new File(filename);
			if (fname.exists() && fname.isFile()) {
				fname.delete();
				sendOut("File successfully deleted!", out);
				sendOut("end", out);
			} else if (fname.isDirectory()) {
				sendOut("Please use deldir method to delete directories", out);
				sendOut("end", out);
			} else {
				sendOut("File does not exist", out);
				sendOut("end", out);
			}
		} else {
			File rootfile = new File(sharedroot);
			displayFiles(rootfile, filename, out);
			sendOut("Please specify a path in order to delete an existing file!\n", out);
			sendOut("end", out);

		}
	}

	public void deldir(String filename, DataOutputStream out) throws IOException {
		if (filename.equals("")) {
			sendOut("Please provide a filename!", out);
			sendOut("end", out);
			return;
		}
		
		if (filename.startsWith(sharedroot)) {
			File fname = new File(filename);
			if (fname.exists() && fname.isDirectory()) {
				File[] fnameList = fname.listFiles();
				if (fnameList.length == 0) {
					fname.delete();
					sendOut("Sub-directory deleted successfully!", out);
					sendOut("end", out);
				} else {
					sendOut("Sub-directory is not empty! Unable to delete!", out);
					sendOut("end", out);
				}
			} else if (fname.isFile()) {
				sendOut("Please use del method to delete files", out);
				sendOut("end", out);
			} else {
				sendOut("Directory does not exist", out);
				sendOut("end", out);
			}
		} else {
			File rootfile = new File(sharedroot);
			displayDirs(rootfile, filename, out);
			sendOut("Please specify a path in order to delete an existing directory!\n", out);
			sendOut("end", out);
		}

	}
	
	public void recursiveDel(String filename, DataOutputStream out) throws IOException {
		if (filename.equals("")) {
			sendOut("Please provide a filename!", out);
			sendOut("end", out);
			return;
		}
		
		if (filename.startsWith(sharedroot) || filename.startsWith(currentDir)) {
			File fname = new File(filename);
			if (fname.exists() && fname.isDirectory()) {
				boolean result = recursiveDirectory(fname);
				
				if (result) {
					sendOut("Directory deleted successfully!\n", out);
					sendOut("end", out);
				}else {
					sendOut("Error occured in recursively deleting files inside directory!\n", out);
					sendOut("end", out);
				}
			}else {
				sendOut("File is not a directory!", out);
				sendOut("Please use del method to delete files", out);
				sendOut("end", out);
			}
		}else {
			File rootfile = new File(sharedroot);
			displayDirs(rootfile, filename, out);
			sendOut("Please specify a path in order to delete an existing directory!\n", out);
			sendOut("end", out);
		}
	}
	
	public boolean recursiveDirectory(File fname) {
		File[] contents = fname.listFiles();
		if (contents.length != 0) {
			for (File file : contents) {
				recursiveDirectory(file);
			}
		}
		
		return fname.delete();
	}

	public void renameFile(String filename, DataInputStream in, DataOutputStream out) throws IOException {

		if (filename.startsWith(sharedroot)) {
			File file = new File(filename);

			byte[] buffer = new byte[1024];
			try {
				int nameLen = in.readInt();
				in.read(buffer, 0, nameLen);
				String name = new String(buffer, 0, nameLen);

				if (name.equals("404 not found")) {
					return;
				}
				
				if (getFileExtension(name).equals("")) {				//if file extension is not provided, defaults back to original file extension type
					name = name + getFileExtension(filename);
				}

				File newFile = new File(file.getParent() + "\\" + name); // rename back to same directory
				
				

				if (file.renameTo(newFile)) {
					sendOut("File renamed successfull!", out);
					sendOut("end", out);
				}else {
					sendOut("Error occurred in renaming file!", out);
					sendOut("end", out);
				}

			} catch (IOException e) {
				System.err.println("unable to rename file.");
			}
		} else {
			File rootfile = new File(sharedroot);
			displayFiles(rootfile, filename, out);
			sendOut("Please specify a path in order to rename an existing file!\n", out);
			sendOut("end", out);
		}
	}

	public void readFile(String filename, DataOutputStream out) throws IOException {
		if (filename.equals("")) {
			sendOut("Please provide a filename!", out);
			sendOut("end", out);
			return;
		}
		if (filename.startsWith(sharedroot)) {
			File fname = new File(filename);
			if (fname.exists()) {
				sendOut("name : " + fname.getName(), out);
				sendOut("size (bytes) : " + fname.length(), out);
				sendOut("absolute path? : " + fname.isAbsolute(), out);
				sendOut("exists? : " + fname.exists(), out);
				sendOut("hidden? : " + fname.isHidden(), out);
				sendOut("dir? : " + fname.isDirectory(), out);
				sendOut("file? : " + fname.isFile(), out);
				sendOut("modified (timestamp) : " + fname.lastModified(), out);
				sendOut("readable? : " + fname.canRead(), out);
				sendOut("writable? : " + fname.canWrite(), out);
				sendOut("executable? : " + fname.canExecute(), out);
				sendOut("parent : " + fname.getParent(), out);
				sendOut("absolute file : " + fname.getAbsoluteFile(), out);
				sendOut("absolute path : " + fname.getAbsolutePath(), out);
				sendOut("canonical file : " + fname.getCanonicalFile(), out);
				sendOut("canonical path : " + fname.getCanonicalPath(), out);
			} else {
				sendOut("File does not exist!", out);
			}

		} else {
			File rootfile = new File(sharedroot);
			displayFiles(rootfile, filename, out);
			sendOut("Please specify a path to the existing file to read file details!\n", out);
		}
		
		sendOut("end", out);
	}
	
	public void changeDir(String filename, DataOutputStream out) throws IOException {
		if (filename.equals("")) {
			sendOut("Please provide a filename", out);
			sendOut("end", out);
			return;
		}
		
		if (filename.startsWith(currentDir) || filename.startsWith(sharedroot)) {
			File newfile = new File(filename);
			if (newfile.exists() && newfile.isDirectory()) {
				currentDir = filename;
				sendOut("Directory changed.", out);
				sendOut("Current directory: "+newfile.getPath(), out);
				sendOut("end", out);
			}else {
				sendOut("File provided is not a directory!", out);
				sendOut("end", out);
			}
		}else {
			File rootfile = new File(sharedroot);
			displayFiles(rootfile, filename, out);
			sendOut("Please specify a valid path to the existing directory!\n", out);
			sendOut("end", out);
		}
	}

	public void getHelp(String access, DataOutputStream out) throws IOException {
		if (access.trim().equalsIgnoreCase("basic")) {
			sendOut("1. Read file list [DIR] - takes no input besides command", out);
			sendOut("3. Upload and download files [UPL]/[DWL] [name] - takes one file name as input besides command",
					out);
			sendOut("7. Read file information [READ] [name] - takes one file name as input besides command", out);
		} else if (access.trim().equalsIgnoreCase("partial")) {
			sendOut("Your available actions: ", out);
			sendOut("1. Read file list [DIR]", out);
			sendOut("2. Create sub-directory [MKDIR] [name] - takes one file name as input besides command", out);
			sendOut("3. Upload and download files [UPL]/[DWL] [name] - takes one file name as input besides command",
					out);
			sendOut("6. Change file/target name [RENAME] [name] - takes one file name as input besides command", out);
			sendOut("7. Read file information [READ] [name] - takes one file name as input besides command", out);
		} else if (access.trim().equalsIgnoreCase("full")) {
			sendOut("Your available actions: ", out);
			sendOut("1. Read file list [DIR]", out);
			sendOut("2. Create sub-directory [MKDIR][name] - takes one file name as input besides command", out);
			sendOut("3. Upload and download files [UPL]/[DWL] [name] - takes one file name as input besides command",
					out);
			sendOut("4. Delete files [DEL] [name] - takes one file name as input besides command", out);
			sendOut("5. Delete sub-directory [DELDIR] [name] - takes one file name as input besides command", out);
			sendOut("6. Change file/target name [RENAME] [name] - takes one file name as input besides command", out);
			sendOut("7. Read file information [READ] [name] - takes one file name as input besides command", out);
		}

		sendOut("add - initiates process to add new users to login list of personal server", out);
		sendOut("remove - initiates process to remove users from login list of personal server", out);
		sendOut("cd - takes one file name as input besides command to change the current directory", out);
		sendOut("shutdown - takes no inputs besides command to terminate program running", out);
		sendOut("end", out);
	}
	
	public void displayDirs(File dir, String filename, DataOutputStream out) {
		try {
			File[] subfiles = dir.listFiles();
			for (File file : subfiles) {
				if (file.isDirectory()) {
					if (file.getCanonicalPath().endsWith(filename)) {
						sendOut("File exists within sub-directory: " + file.getParent(), out);
						sendOut("Path to file: " + file.getCanonicalPath(), out);
					}
//					System.out.println("directory: "+file.getCanonicalPath());
					// sendOut("directory: "+file.getCanonicalPath(), out);
					displayDirs(file, filename, out);
				} else {
//					if (file.getCanonicalPath().endsWith(filename)) {
//						// System.out.println("File exists within sub-directory:
//						// "+file.getCanonicalPath());
////						sendOut("File exists within sub-directory: " + file.getParent(), out);
////						sendOut("Path to file: " + file.getCanonicalPath(), out);
//					}
				}
			}

		} catch (IOException e) {
			System.out.println("Error in traversing sharedroot subdirectories");
		}
	}

	public void displayFiles(File dir, String filename, DataOutputStream out) {
		try {
			File[] subfiles = dir.listFiles();
			for (File file : subfiles) {
				if (file.isDirectory()) {
					if (file.getCanonicalPath().endsWith(filename)) {
						sendOut("File exists within sub-directory: " + file.getParent(), out);
						sendOut("Path to file: " + file.getCanonicalPath(), out);
					}
//					System.out.println("directory: "+file.getCanonicalPath());
					// sendOut("directory: "+file.getCanonicalPath(), out);
					displayFiles(file, filename, out);
				} else {
					if (file.getCanonicalPath().endsWith(filename)) {
						// System.out.println("File exists within sub-directory:
						// "+file.getCanonicalPath());
						sendOut("File exists within sub-directory: " + file.getParent(), out);
						sendOut("Path to file: " + file.getCanonicalPath(), out);
					}
				}
			}

		} catch (IOException e) {
			System.out.println("Error in traversing sharedroot subdirectories");
		}
	}
	
	public String getFileExtension(String filename) {
		if(filename.lastIndexOf(".") != -1 && filename.lastIndexOf(".") != 0) {
			return filename.substring(filename.lastIndexOf(".")+1);
		}
		return "";
	}

	public static void main(String[] args) {
	}

	public void run() {
		try {
			Server();
			// TCPServer server = new TCPServer(port, users);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in launching server");
		}
	}

}

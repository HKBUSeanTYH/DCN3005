import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class sharedRoot {
	String sharedroot;				//this can be public because it is a "shared" root
	String servernm;
	private File userData = new File("userData.txt");
	Users users = new Users(); //initiate the linked list
	private String userstring = "";
	
	public void start() throws IOException {

		//onstart check if userData file exist
		if (!userData.exists()) {
			//if not, create and note down shared root
			Scanner scanner = new Scanner(System.in);
			sharedroot = "sharedRoot: ";
			servernm = "servernm: ";
			FileOutputStream out = new FileOutputStream(userData);
			
			System.out.println("Please provide your server name");
			String svrName = scanner.nextLine();
			servernm = servernm + svrName+"\n";		
			//having separate line for servernm and sharedroot allows us to have whitespace inside the sharedroot
			//ie. some path has whitespace inside the path
			
			boolean isDir = false;

			while (!isDir) {
				System.out.println("Please provide the path to your shared directory");
				String spath = scanner.nextLine();
				String[] spathTokens = spath.trim().split(" ");
				File sfile = new File(spath);

				if (!sfile.isDirectory()) {		//only accept directory
					System.out.println("The path provided is not a directory!");
				}else {
					sharedroot = sharedroot + spath + "\n";
					out.write(sharedroot.getBytes());
					out.write(servernm.getBytes());
					System.out.println("Your shared directory path has been saved!");
					isDir = true;
				}
			}
			
			//some basic users that are initiated on setup
			out.write("admin 123456 full\n".getBytes());
			out.write("sean 654321 partial\n".getBytes());
			out.write("oshan 123321 partial\n".getBytes());
			out.write("client 111111 basic\n".getBytes());
			
			//implementation of simplified access control
			//owner --> full; group -->partial; universe --> basic
			//basic can only read, upload/download and list files; partial can create sub-directories, rename target and basic privileges;
			//admin can delete and partial privileges
			
			out.flush();
			out.close();
		}
		
		//scanning userdata to users string to copy to new userdata file if overwrite the shared root
		Scanner in = new Scanner(userData);
		
		while (in.hasNextLine()) {
			String line = in.nextLine();
			String[] lineElem = line.split(" ");
			
			if (lineElem[0].equals("sharedRoot:")) {
				for (int i=1; i<lineElem.length; i++) {
					if (i == 1) {
						sharedroot = lineElem[i];
					}else {
						sharedroot = sharedroot +" "+lineElem[i];
					}
				}
			}else if (lineElem[0].equals("servernm:")) {
				for (int i=1; i<lineElem.length; i++) {
					if (i == 1) {
						servernm = lineElem[i];
					}else {
						servernm = servernm +" "+lineElem[i];
					}
				}
			}else{
				userstring = userstring+line+"\n";
			}
		}
		//System.out.println(userstring);       //debugging
	}
	
	//experimental
	public void addUsers() throws IOException {
		FileOutputStream out = new FileOutputStream(userData, true);
		Scanner in = new Scanner(System.in);
		
		String name ="";
		
		while (true) {
			System.out.println("Please input username");
			name = in.nextLine();
			if (userstring.contains(name.trim())) {
				System.out.println("That username is already taken");
			}else {
				break;
			}
		}
		
		System.out.println("Please input password");
		String pw = in.nextLine();
		String acc = "";
		
		while (true) {
			System.out.println("Please input access level");
			acc = in.nextLine();
			if (acc.equalsIgnoreCase("full") || acc.equalsIgnoreCase("partial") ||acc.equalsIgnoreCase("basic")) {
				break;
			}
			System.out.println("Thats not a valid access level");
		}
		
		
		String result = name.trim() + " " +pw.trim() +" "+acc.trim();
		out.write((result+"\n").getBytes());
		users.add(name.trim(), pw.trim(), acc.trim());		//append to user linkedlist
		userstring = userstring + result+"\n";
		System.out.println("User successfully added!");
		out.close();
	}
	
	public void removeUsers(String username) throws IOException {
		if (!userstring.contains(username.trim())) {
			System.out.println("User does not exist");
			return;
		}
		
		FileOutputStream out = new FileOutputStream(userData);
		Scanner in = new Scanner(userstring);
		
		String s1 = "sharedRoot: "+sharedroot+"\n";
		String s2 = "servernm: "+servernm+"\n";
		
		
		
		out.write(s1.getBytes());
		out.write(s2.getBytes());
		
		String[] userTokens = userstring.split("\n");
		
		for(int i=0; i<userTokens.length; i++) {
			String line ="";
			String line2 = "";
			String[] tokens2 = userTokens[i].split(" ");         //users may have names with multiple words
			
			for (int j = 0; j<tokens2.length-2; j++) {
				if (j==0) {
					line2 = tokens2[j];
				}else {
					line2 = line2+" "+tokens2[j];
				}
			}
			if (line2.equals(username)) {
				userstring = userstring.replace((userTokens[i]+"\n"), "");		//remove from userstring so that future operations would not include removed user
			}else {
				line = userTokens[i]+"\n";
				out.write(line.getBytes());
			}
		}
//		users.print();       //debugging purposes
		users.remove(username);				//remove user from linked list
		//users.print();       //debuggin purposes
		
		out.flush();
		out.close();
	}
	
	public Users createUsers() throws IOException {		
		Scanner scanner = new Scanner(userData);
		
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] lineElem = line.split(" ");
			
			if (lineElem[0].equals("sharedRoot:")) {continue;}
			if (lineElem[0].equals("servernm:")) {continue;}
			
			String username = "";
			for (int i=0; i<lineElem.length-2; i++) {
				if (i==0) {
					username = lineElem[i];
				}else {
					username = username +" "+lineElem[i];
				}
			}
			
			users.add(username, lineElem[lineElem.length-2], lineElem[lineElem.length-1]); //add(user name, user password)
		}
		
		return users;
	}
	
	public String getRoot() {return this.sharedroot;}			//change this to an arraylist?
	public String getServer() {return this.servernm;}			//change this to an arraylist?

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		File filed = new File("userData.txt");
//		Scanner in = new Scanner(filed);
//		
//		while(in.hasNextLine()) {
//			System.out.print(in.nextLine());
//		}

	}

}

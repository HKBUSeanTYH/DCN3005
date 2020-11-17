import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class sharedRoot {
	String sharedroot;				//this can be public because it is a "shared" root
	String servernm;
	private File userData = new File("userData.txt");
	private String users = "";
	
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
				users = users + line+" ";
			}
		}
		//System.out.println(users);
	}
	
	public Users createUsers() throws IOException {
		Users users = new Users(); //initiate the linked list
		
		Scanner scanner = new Scanner(userData);
		
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] lineElem = line.split(" ");
			
			if (lineElem[0].equals("sharedRoot:")) {continue;}
			if (lineElem[0].equals("servernm:")) {continue;}
			users.add(lineElem[0], lineElem[1], lineElem[2]); //add(user name, user password)
		}
		
		return users;
	}
	
	public String getRoot() {return this.sharedroot;}
	public String getServer() {return this.servernm;}

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

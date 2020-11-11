import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class sharedRoot {
	private String sharedroot;
	private File userData = new File("userData.txt");
	
	public void start() throws IOException {

		//onstart check if userData file exist
		if (!userData.exists()) {
			//if not, create and note down shared root
			Scanner scanner = new Scanner(System.in);
			sharedroot = "sharedRoot: ";
			FileOutputStream out = new FileOutputStream(userData);

			System.out.println("Please provide the path to your shared directory");
			boolean isDir = false;

			while (!isDir) {
				String spath = scanner.nextLine();
				File sfile = new File(spath);

				if (!sfile.isDirectory()) {		//only accept directory
					System.out.println("The path provided is not a directory!");
				} else {
					sharedroot = sharedroot + spath + "\n";
					out.write(sharedroot.getBytes());
					System.out.println("Your shared directory path has been saved!");
					isDir = true;
				}
				
				//some basic users that are initiated on setup
				out.write("admin 123456".getBytes());
				out.write("sean 654321".getBytes());
				out.write("oshan 123321".getBytes());
				
			}

			out.close();
		}
		
		//i m planning on using this code elsewhere i got too much into coding and realised that i dont need to make User this early
//		else {
//			Scanner scanner = new Scanner(userData);
//			
//			while (scanner.hasNextLine()) {
//				String line = scanner.nextLine();
//				String[] lineElem = line.split(" ");
//				
//				if (lineElem[0] == "sharedRoot:") {continue;}
//			}
//			
//		}
	}
	
	public User createUsers() throws IOException {
		User users = new User(); //initiate the linked list
		
		Scanner scanner = new Scanner(userData);
		
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] lineElem = line.split(" ");
			
			if (lineElem[0] == "sharedRoot:") {continue;}
			users.add(lineElem[0], lineElem[1]); //add(user name, user password)
		}
		
		return users;
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		

	}

}

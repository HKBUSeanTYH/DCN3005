import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class StartUp {

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		
		sharedRoot sroot = new sharedRoot();
		sroot.start();
		User users = sroot.createUsers();			//on startup of the class we get the list of users to check for any logins
		
		
		
		Scanner in = new Scanner(System.in);
		
		System.out.println("Please input\nServer IP: ");
		String ip = in.nextLine().trim();
		
		System.out.println("Please input your password");
		String username = in.nextLine();
		
		System.out.println("Please input your password");
		String pw = in.nextLine();
		
		//use ipAddr to connect to tcp, send username and password to the TCP "server" to perform user.login()
		
		//need to put UDP and TCP on startup codes
		
		System.out.println(username+": "+pw);
		in.close();
	}

}

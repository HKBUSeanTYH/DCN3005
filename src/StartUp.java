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
		User users = sroot.createUsers();			
		//on startup of the class we get the list of users to check for any logins
		
		
		
		Scanner in = new Scanner(System.in);
		
		System.out.println("Please input\nServer IP: ");
		String ip = in.nextLine().trim();
		
		//do we set only one port for server? ie hard code server port as xxxxx?
		System.out.print("Port no: ");
		int port = Integer.parseInt(in.nextLine());
		
		TCPClient client = new TCPClient(ip, port);
		
		//use ipAddr to connect to tcp, send username and password to the TCP "server" to perform user.login()
		
		//need to put UDP and TCP on startup codes
		
		in.close();
	}

}

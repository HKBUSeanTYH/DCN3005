import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class StartUp {

	public static void main(String[] args) throws IOException, InterruptedException{
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
		
		System.out.println("Now running personal server in the background");
		ProcessBuilder server = new ProcessBuilder("java", "-cp", "D:\\seant\\eclipse-workspace\\DCN3005\\bin", "TCPServer", "12345", "users");
		
		//TCPServer server = new TCPServer(12345, users);	//this refers to this pc's self server action with its own list of accepted users
		
		ProcessBuilder client = new ProcessBuilder("java", "-cp", "D:\\seant\\eclipse-workspace\\DCN3005\\bin", "TCPClient", "192.168.50.245", "12345");
		
		server.inheritIO();
		client.inheritIO();
		Process s = server.start();
		Process c = client.start();
		s.waitFor();
		c.waitFor();
		
		System.out.println("System off");
		//TCPClient client = new TCPClient(ip, port);	//this is to act as a client and connect to other peoples server
		
		//use ipAddr to connect to tcp, send username and password to the TCP "server" to perform user.login()
		
		//need to put UDP and TCP on startup codes
		
		in.close();
	}

}

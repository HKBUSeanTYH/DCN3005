import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class StartUp {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

		sharedRoot sroot = new sharedRoot(); // D:\seant\2uniFiles
		sroot.start();
		Users users = sroot.createUsers();
		// on startup of the class we get the list of users to check for any logins

		Scanner in = new Scanner(System.in);
		
		TCPServer server = new TCPServer();
		server.port = 9999;
		server.users = users;
		server.sharedroot = sroot.getRoot();
		server.servernm = sroot.getServer();
		System.out.println("Now running personal server in the background");
		server.start();

		Thread.sleep(100);			//delays StartUp so that the server thread finish printing out messages
		
		//D:\seant\1. University Files\1. COMP Year 3
		
		while (true) {
			try {
				System.out.println("Please input Server IP: "); // 192.168.50.245
				String ip = in.nextLine().trim();

				// do we set only one port for server? ie hard code server port as xxxxx?
				System.out.print("Port no: ");
				int port = Integer.parseInt(in.nextLine());

				TCPClient client = new TCPClient();
				client.serverIP = ip;
				client.port = port;
				client.sroot = sroot;				//passing clients personal root to client so can add/remove users

				client.start();
				client.join();       
			}catch (Exception e) {
				System.err.println("Bad Input");
			}
		}

//		TCPServer server = new TCPServer(12345, users);	//this refers to this pc's self server action with its own list of accepted users
//		TCPClient client = new TCPClient(ip, port);	//this is to act as a client and connect to other peoples server

		// use ipAddr to connect to tcp, send username and password to the TCP "server"
		// to perform user.login()

		// need to put UDP and TCP on startup codes

		//in.close();
	}

}

//dont use multi-process because cant pass linkedlist as argument to processbuilder	
// ProcessBuilder server = new ProcessBuilder("java", "-cp",
// "D:\\seant\\eclipse-workspace\\DCN3005\\bin", "TCPServer", "12345", "users");
// ProcessBuilder client = new ProcessBuilder("java", "-cp",
// "D:\\seant\\eclipse-workspace\\DCN3005\\bin", "TCPClient", "192.168.50.245",
// "12345");

//		server.inheritIO();
//		client.inheritIO();
//		Process s = server.start();
//		Process c = client.start();
//		s.waitFor();
//		c.waitFor();

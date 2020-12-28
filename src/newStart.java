import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class newStart {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		sharedRoot sroot = new sharedRoot(); // D:\seant\2uniFiles
		sroot.start();
		Users users = sroot.createUsers();
		// on startup of the class we get the list of users to check for any logins

		Scanner in = new Scanner(System.in);
		
		TCPServer server = new TCPServer();
		server.port = 9999;
		server.users = users;
		//pass list of accepted users to personal server
		server.sharedroot = sroot.getRoot();
		server.servernm = sroot.getServer();
		System.out.println("Now running personal server in the background");
		server.start();

		Thread.sleep(500);			//delays StartUp so that the server thread finish printing out messages
		
	}
}

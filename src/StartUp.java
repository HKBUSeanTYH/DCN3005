import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
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

		Thread.sleep(500);			//delays StartUp so that the server thread finish printing out messages
		
		//call udp
		UDPDisc disc = new UDPDisc();
		disc.servernm = sroot.getServer();
		System.out.println("Initiating discovery process");
		disc.start();
		Thread.sleep(10000);
		//disc.join();
		
//		disc.sendMsg("initiate discovery");
//		while (true) {
//			try {
//				disc.receiveMsg();
//			}catch(SocketTimeoutException e) {
//				break;
//			}
//		}
//		
//		boolean initiate = true;
////		create a do-while loop with timer for 15s to receive for 15s 
////		then ask user whether to continue receiving or input a server to connect
//		do {
//			System.out.println("\nContinue with discovery process? Y/N ");
//			String response = in.nextLine();
//			if (response.toLowerCase().equals("y")) {
//				disc.sendMsg("initiate discovery");
//				while (true) {
//					try {
//						disc.receiveMsg();
//					}catch(SocketTimeoutException e) {
//						break;
//					}
//				}
//								
//			}else if (response.toLowerCase().equals("n")){
//				initiate = false;
//			}
//		}while(initiate);
		
		//D:\seant\1. University Files\1. COMP Year 3
		//D:\seant\2uniFiles\1. FINE2005 Year 3
		
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
	}
}

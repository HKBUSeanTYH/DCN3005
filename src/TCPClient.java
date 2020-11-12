import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class TCPClient {

	public TCPClient(String serverIP, int port) throws IOException{
		Socket socket = new Socket(serverIP, port);

		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		Scanner in = new Scanner(System.in); // take user input and send to server
		
		boolean login = false;
		
		while (!login) {
			System.out.println("Please input your username");
			String username = in.nextLine();
			System.out.println("Please input your password");
			String pw = in.nextLine();
			System.out.println("Connecting to server...");
			
			sendLogin(username, pw, out);

		}
	}
	
	public void sendLogin(String username, String pw, DataOutputStream out) {
		
	}

	public void sendCommand(DataOutputStream out) throws IOException {

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

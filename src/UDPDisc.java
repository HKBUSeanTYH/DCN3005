import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class UDPDisc extends Thread{		//extends Thread
	DatagramSocket socket;
	String servernm;
	ArrayList<String> serverpool = new ArrayList<String>();

	public UDPDisc() throws SocketException {
		socket = new DatagramSocket(9998);
		socket.setSoTimeout(15000);   //15s of receiving
	}
	
	public void sendMsg(String str) throws IOException {
		DatagramSocket socket = new DatagramSocket();			//randomly use a free port to send
		byte[] msg = str.getBytes();
		InetAddress dest = InetAddress.getByName("255.255.255.255");
		DatagramPacket packet = new DatagramPacket(msg, msg.length, dest, 9998);   //in this project udp only listens to 9998 
		socket.send(packet);
		socket.close();
	}
	
	public void receiveMsg() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
		socket.receive(packet);
		byte[] data = packet.getData();
		String str = new String(data, 0, packet.getLength());
		String srcAddr = packet.getAddress().toString();
		
		String[] strTokens = str.trim().split(" ");
		
		if (str.toLowerCase().equals("initiate discovery")) {
			sendMsg("responding: "+servernm);
		}
		
		if (strTokens[0].equalsIgnoreCase("responding:")) {
			String name = "";
			for (int i=1; i<strTokens.length; i++) {
				if (i==1) {
					name = strTokens[i];
				}else {
					name = name +" "+strTokens[i];
				}
			}
			if (!serverpool.contains(name)) {
				serverpool.add(name);
				System.out.println("responding:\t" + name);
				System.out.println("from IP: "+srcAddr);
			}
		}
		
	}
	
	public void disc() throws IOException {
		long start = System.currentTimeMillis();
		//System.out.println(start);
		do {
			while (true) {
				try {
					sendMsg("initiate discovery");
					//System.out.println("b4");
					receiveMsg();
					//System.out.println("after");
				} catch (SocketTimeoutException e /*Exception e*/) {
					// TODO Auto-generated catch block
					break;
				}
			}
			//System.out.println(System.currentTimeMillis() - start);
		}while(System.currentTimeMillis() - start <= 15000);				//non-stop discovery process in first 15s
		
		while (true) {														//while loop for response from other clients, timeout after 15s and then restart. prevents inactive thread
			try {
				receiveMsg();
			} catch (SocketTimeoutException e /*Exception e*/) {
				//System.out.println("Staying active");
			}
		}
		
	}
	
	public void run() {
		try {
			disc();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error in launching udp");
		}
	}
}

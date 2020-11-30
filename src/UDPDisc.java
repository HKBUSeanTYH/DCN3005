import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class UDPDisc{
	DatagramSocket socket;
	String servernm;
	

	public UDPDisc() throws SocketException {
		socket = new DatagramSocket(9998);
		socket.setSoTimeout(15000);   //10s of receiving
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
			System.out.println("responding:\t" + name);
			System.out.println("from IP: "+srcAddr);
		}
		
//		System.out.println("Received data:\t" + str);
//		System.out.println("data size:\t" + size);
//		System.out.println("sent by:\t" + srcAddr);
//		System.out.println("via port:\t" + srcPort);
	}
	
	public void disc() throws IOException {
		sendMsg("initiate discovery");
		while (true) {
			try {
				receiveMsg();
			} catch (SocketTimeoutException e) {
				// TODO Auto-generated catch block
				break;
			}
		}
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub	

	}
}

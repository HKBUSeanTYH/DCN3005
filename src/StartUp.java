import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class StartUp {

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		
		sharedRoot sroot = new sharedRoot();		//create separate class so that users cant tamper with the data
		sroot.start();
		
		Scanner in = new Scanner(System.in);
		
		System.out.println("Please input\nServer IP: ");
		String ip = in.nextLine().trim();
		
		System.out.println("Please input your password");
		String username = in.nextLine();
		
		System.out.println("Please input your password");
		String pw = in.nextLine();
		
		//need to put UDP and TCP on startup codes
		
		System.out.println(username+": "+pw);
		in.close();
	}

}

import java.util.Scanner;

public class StartUp {
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
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

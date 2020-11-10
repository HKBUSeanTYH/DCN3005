import java.util.Scanner;

public class StartUp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Scanner in = new Scanner(System.in);
		System.out.println("Please input your password");
		
		//need to put UDP and TCP on startup codes
		
		String pw = in.nextLine();
		System.out.println(pw);
		in.close();
	}

}

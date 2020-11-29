import java.util.ArrayList;

public class Users {
	private class Node{
		String username;
		String password;
		String access;
		
		Node next;
		
		Node(String x, String y, String z){this.username =x; this.password = y;this.access =z;}
		
		
	}
	
	ArrayList<String> loginName = new ArrayList<String>();
	ArrayList<String> loginAccess = new ArrayList<String>();
	
	private Node Head, Tail = null;
	private int count = 0;
	
	public void add(String username, String pw, String acc){     //enqueue

        Node n = new Node(username, pw, acc);

        if (find(username) == true){
            return;

        }else{
            if(this.Head == null && this.Tail == null){
                this.Head = this.Tail = n;
            }else{
                this.Tail.next = n;
                this.Tail = n;
            }
            count++;
        }

    }
	
	public void remove(String k) {
		Node prev = this.Head;

        for (int i=0; i<count; i++){
            if (prev.next.username.equals(k)){
                prev.next = prev.next.next;				//remove?
                count--;
            }else{
                prev= prev.next;
                continue;

            }
        }
	}

    public boolean find(String k){
        Node prev = Head;

        for (int i=0; i<count; i++){
            if (prev.username.equals(k)){
                return true;

            }else{
                prev= prev.next;
                continue;

            }
        }

        return false;
    }
    
    public boolean logIn(String username, String pw) {			
    	//used to check login we do not want to have getters or setters because one pc is logging into another persons pc. have to keep personal list of username and pw private and other user keeps their list private
    	Node prev = Head;

        for (int i=0; i<count; i++){
            if (prev.username.equals(username)){
            	if(prev.password.equals(pw)) {
            		loginName.add(prev.username);
            		loginAccess.add(prev.access);
            		return true;
            	}else {
            		System.out.println("Password not matching!");
            		return false;
            	}

            }else{
                prev= prev.next;
                continue;

            }
        }
        System.out.println("Username not found!");
    	
    	return false;
    }
    
    public int getCount(){return this.count;}
    public ArrayList<String> loggedIn() {return this.loginName;}
    public ArrayList<String> getAccess() {return this.loginAccess;}
    
    public void print() {
    	Node prev = Head;
    	
    	for (int i =0; i<count; i++) {
    		System.out.println(prev.username);
    		prev = prev.next;
    	}
    }
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
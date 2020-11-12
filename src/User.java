public class User {
	private class Node{
		String username;
		String password;
		
		Node next;
		
		Node(String x, String y){this.username =x; this.password = y;}
		
		
	}
	
	private Node Head, Tail = null;
	private int count = 0;
	
	public void add(String username, String pw){     //enqueue

        Node n = new Node(username, pw);

        if (find(username) == true){
            return;
            // if exists, do nothing

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
	
//	public int remove(int k){        //dequeue
//        int k = 0;
//
//        if (this.Head == null){
//            return k;
//        }
//
//        k = this.Head.value;
//        this.Head = this.Head.next;
//        count--;
//
//        if (this.Head == null){
//            this.Tail = null;
//        }
//
//        return k;
//    }

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

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

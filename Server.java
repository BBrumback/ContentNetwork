import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Server {
	public static void main(String args[]) throws RemoteException, UnknownHostException {
		Scanner input = new Scanner(System.in);
		String command;
		
		Node node = new NodeImpl();
		System.out.println("Server up and running");
		
		command = input.nextLine();
		while(!command.equals("quit")) {
			if(command.equals("insert")){
				System.out.println(node.insert(input.next()));
			}else if(command.equals("view")){
				System.out.println(node.view());
			}else if(command.equals("search")){
				System.out.println(node.search(input.next()));
			}else if(command.equals("join")){
				System.out.println(node.join(Math.random() * 10, Math.random() * 10));
			}else if(command.equals("help")) {
				System.out.println("quit      use to exit the program");
				System.out.println("insert    use to add a keyword to the network");
				System.out.println("serach    use to find a keyword in the network");
				System.out.println("view      use to view the current node including the data stored there");
				System.out.println("help      use to print this again");
			}else {
				System.out.println("unrecognized command, use help to see valid commands");
			}
			
			command = input.nextLine();
		}
		
		input.close();
	}
}

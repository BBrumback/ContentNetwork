import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Server {
	public static void main(String args[]) throws RemoteException, UnknownHostException, MalformedURLException, NotBoundException {
		Scanner input = new Scanner(System.in);
		String command;
		
		Node node = new NodeImpl();
		System.out.println("Server up and running");
		
		command = input.nextLine();
		while(!command.equals("quit")) {
			
			//Here are the basic commands
			if(command.equals("insert")){
				//node.insert(input.next());
				System.out.println(node.insert(input.nextLine()));
			}else if(command.equals("view")){
				System.out.println(node.view());
			}else if(command.equals("search")){
				//node.search(input.next());
				System.out.println(node.search(input.nextLine()));
			}else if(command.equals("join")){
				//node.join(Math.random() * 10, Math.random() * 10);
				System.out.println(node.join(Math.random() * 10, Math.random() * 10));
			}else if(command.equals("leave")) {
				System.out.println(node.leave());
			
			//Here is help to explain commands
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
		
		node.leave();
		node.unbindName();
		System.out.println("Server shutting down");
		input.close();
		System.exit(0);
	}
}

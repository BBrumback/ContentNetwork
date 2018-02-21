import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GradingServer {

	public static void main(String[] args) throws RemoteException, NotBoundException, UnknownHostException {
		Scanner input = new Scanner(System.in);
		String command;
		String nodeName;
		Registry reg;
		
		//You will need to change these if you are on a different server
		final String serverIP = "192.168.1.";
		final int serverPort = 1099;
		
		
		List<Node> allNodes = new ArrayList<Node>();
		Node node;
		
		
		//for(int j = 30; j <= 50; j++) {
			LocateRegistry.createRegistry(1099);
			reg = LocateRegistry.getRegistry(InetAddress.getLocalHost().getHostAddress(), serverPort);
			for(int i = 0; i <= 10; i++) {
				try {
					node = (Node) reg.lookup("node" + i);
					allNodes.add(node);
					break;
				} catch (Exception e) {
					//Keep going
				}
			}
		//}
		reg = LocateRegistry.getRegistry(InetAddress.getLocalHost().getHostAddress(), serverPort);
		
		
		System.out.println("Server up and running");
		
		command = input.nextLine();
		while(!command.equals("quit")) {
			
			try {
				//Here are the basic commands
				if(command.equals("insert")){
					System.out.println("Please input the node number you want to run this command on : ");
					nodeName = input.nextLine();
					node = findNode(nodeName, allNodes);
					System.out.println("input the keyword you would like to insert : ");
					System.out.println(node.insert(input.nextLine()));
					
				}else if(command.equals("view")){
					System.out.println("Please input the node number you want to run this command on\ninput blank to run on all nodes: ");
					nodeName = input.nextLine();
					if(!nodeName.equals("")) {
						node = findNode(nodeName, allNodes);
						System.out.println(node.view());
					}else {
						for(int i = 0; i < allNodes.size(); i++) {
							allNodes.get(i).view();
						}
					}
					
				}else if(command.equals("search")){
					
					System.out.println("Please input the node number you want to run this command on : ");
					nodeName = input.nextLine();
					node = findNode(nodeName, allNodes);
					System.out.println("input the keyword you are looking for : ");
					System.out.println(node.search(input.nextLine()));
					
				}else if(command.equals("join")){
					System.out.println("Please input the node number you want to run this command on : ");
					nodeName = input.nextLine();
					System.out.println(nodeName.length());
					if(!nodeName.equals("")) {
						node = findNode(nodeName, allNodes);
						System.out.println(node.join(Math.random() * 10, Math.random() * 10));
					}else {
						for(int i = 0; i < allNodes.size(); i++) {
							System.out.println(allNodes.get(i).join(Math.random() * 10, Math.random() * 10));
						}
					}
					
				}else if(command.equals("leave")) {
					System.out.println("Please input the node number you want to run this command on : ");
					nodeName = input.nextLine();
					if(!nodeName.equals("")) {
						node = findNode(nodeName, allNodes);
						System.out.println(node.leave());
					}else {
						for(int i = 0; i < allNodes.size(); i++) {
							System.out.println(allNodes.get(i).leave());
						}
					}
					
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
			}catch (Exception e) {
				System.out.println("Node not found");
			}
			
			command = input.nextLine();
		}
		
		System.out.println("Server shutting down");
		input.close();
		System.exit(0);
	}
		
	private static Node findNode(String name, List<Node> allNodes) throws RemoteException {
		for(int i = 0; i < allNodes.size(); i++) {
			if(allNodes.get(i).getName().equals(name)) {
				return allNodes.get(i);
			}
		}
		return null;
	}
}

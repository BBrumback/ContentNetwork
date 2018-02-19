import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
public class NodeImpl extends UnicastRemoteObject implements Node{

	//if I need to pass these it makes more sense to have them as an array
	final private String rmiHost = "rmi://localhost:1099/";
	
	private double xPos;
	private double yPos;
	private String ip;
	private String nodeName;
	
	private boolean isBootstrapper;
	
	private List<Zone> zones;
	private List<String> keywords;
	private Set<Node> neighbors;
	
	public NodeImpl() throws RemoteException, UnknownHostException {
		super();
		System.setSecurityManager(new SecurityManager());
		ip = InetAddress.getLocalHost().getHostAddress();
		zones = new ArrayList<Zone>();
		keywords = new ArrayList<String>();
		neighbors = new HashSet<Node>();
	}
	
	//add constructor back, might want to bind name in constructor
	
	//Simple helper to tell what nodes are used in routing
	public String viewLite() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Node identifier: " + nodeName + "\n");
		sb.append("Node IP: " + ip + "\n");
		
		return sb.toString();
	}
	
	//This returns all the data about the node
	public String view() throws RemoteException {
		
		StringBuilder sb = new StringBuilder();
		sb.append(viewLite());
		sb.append("Node location: " + xPos + ", " + yPos + "\n");
		sb.append("Neigbors: " + getNeighborNames() + "\n");
		sb.append("Keywords Stored " + keywords + "\n");
		for(int i = 0; i < zones.size(); i++)
			sb.append("Zone is: " + zones.get(i).toString());
		return sb.toString();
	}
	
	//This is a top level insert and should be the one that is used when calling insert.
	//TODO: might want to change to return a string
	
	public String insert(String keyword) throws RemoteException {
		double i_xPos = charAtOdd(keyword);
		double i_yPos = charAtEven(keyword);
		StringBuilder sb = new StringBuilder();
		
		System.out.println("Position of object being inserted " + i_xPos + " " + i_yPos);
		
		//finds out if data is stored here
		for(int i = 0; i < zones.size(); i++) {
			System.out.println("Zone " + zones.get(i).toString());
			if(zones.get(i).contains(i_xPos, i_yPos)) {
				keywords.add(keyword);
				return viewLite(); //Could change to view
			}
		}
		//if not find the correct node
		sb.append(viewLite());
		Node closest = findClosestNode(i_xPos, i_yPos);
		sb.append(closest.insert(keyword));
		return sb.toString();
	}
	
	public String search(String keyword) throws RemoteException {
		double i_xPos = charAtOdd(keyword);
		double i_yPos = charAtEven(keyword);
		StringBuilder sb = new StringBuilder();
		
		//finds out if data is stored here
		for(int i = 0; i < zones.size(); i++) {
			if(zones.get(i).contains(i_xPos, i_yPos)) {
				if(keywords.contains(keyword))
					return viewLite();
				else
					return "Failure";
			}
		}
		
		//if not find the correct node
		String temp = findClosestNode(i_xPos, i_yPos).search(keyword); 
		
		//when returning if there was a failure then just return failure
		//else return the path to that node
		if(temp.equals("Failure"))
			return temp;
		else {
			sb.append(temp);
			return sb.toString();
		}
	}
	
	public String join(double x, double y) throws RemoteException {
		xPos = x;
		yPos = y;
		BootStrapper bs = null;
		Node splitNode;
		//Could remove to put in constructor
		bindName(0);
		try {
			bs = (BootStrapper)Naming.lookup(rmiHost + "bootStrapper");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			//This is where I will create a bootstrapper
			//This returns so the bottom half of this code doesnt happen.
			bs = new BootStrapperImpl(this);
			try {
				Naming.bind("bootStrapper", bs);
			} catch (MalformedURLException | AlreadyBoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			isBootstrapper = true;
			zones.add(new Zone(0,0,10,10,true));
			return view();
		}
		
		splitNode = bs.splitNode(x, y);
		zones.add(splitNode.split());
		Set<Node> neighborsToCheck = splitNode.getNeighbors();
		this.findNeighbors(splitNode, neighborsToCheck);
		splitNode.findNeighbors(this, neighborsToCheck);
		splitNode.reInsert();
		return view();
	}
	
	
	//Gets the smallest distance to a point
	public double getDistance(double x, double y) {
		double dist = Double.MAX_VALUE;
		double calcDist;
		for(int i = 0; i < zones.size(); i++) {
			calcDist = zones.get(i).distanceTo(x, y);
			if(calcDist < dist)
				dist = calcDist;
		}
		return dist;
	}
	
	public String getName() {
		return nodeName;
	}
	
	/*
	 * These are basic getters and setter for some of the more basic
	 * variables that will need to be changed
	 */
	public void setxPos(double xPos) {
		this.xPos = xPos;
	}

	public void setyPos(double yPos) {
		this.yPos = yPos;
	}
	
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	
	public boolean isBootstrapper() {
		return isBootstrapper;
	}

	public void setBootstrapper(boolean isBootstrapper) {
		this.isBootstrapper = isBootstrapper;
	}

	public List<Zone> getZones() {
		return zones;
	}

	public Set<Node> getNeighbors() {
		return neighbors;
	}

	public void addNeighbor(Node node) {
		neighbors.add(node);
	}
	
	public void findNeighbors(Node node, Set<Node> neighborsToCheck) throws RemoteException {
		neighbors = new HashSet<Node>();
		neighbors.add(node);
		
		
		
		Iterator<Node> iter = neighborsToCheck.iterator();
		Node current;
		while(iter.hasNext()) {
			current = iter.next();
			for(int i = 0; i < zones.size(); i++) {
				List<Zone> neighborZones = current.getZones();
				for(int j = 0; j < neighborZones.size(); j++){
					if(zones.get(i).adjacent(neighborZones.get(j))) {
						neighbors.add(current);
						current.addNeighbor(this);
					}	
				}
			}
		}
	}

	public Node getClosestNode(double x, double y) throws RemoteException {
		for(int i = 0; i < zones.size(); i++) {
			if(zones.get(i).contains(x, y)) {
				return this;
			}
		}
		return findClosestNode(x,y).getClosestNode(x,y);
	}
	
	public Zone split() {
		Zone givenZone;
		
		if(zones.size() > 1) {
			givenZone = zones.get(zones.size()-1);
			zones.remove(zones.size()-1);
			return givenZone;
		} else {
			zones = zones.get(0).split();
			givenZone = zones.get(zones.size()-1);
			zones.remove(zones.size()-1);
			return givenZone;
		}
	}
	
	public void reInsert() throws RemoteException {
		List<String> rehashingKeywords = keywords;
		keywords = new ArrayList<String>();
		
		for(int i = 0; i < rehashingKeywords.size(); i++) {
			insert(rehashingKeywords.get(i));
		}
	}
	
	/*
	 * These are private helper methods
	 */
	
	//returns a list of all the neighbors names
	private String getNeighborNames() throws RemoteException {
		
		StringBuilder sb = new StringBuilder();
		
		Iterator<Node> iter = neighbors.iterator();
		while(iter.hasNext()) {
			sb.append(iter.next().getName());
			if(iter.hasNext())
				sb.append(", ");
		}
		return sb.toString();
	}
	
	//Finds the closest node in the set of neighbors to a point
	private Node findClosestNode(double x, double y) throws RemoteException {
		double dist = Double.MAX_VALUE;
		double calcDist;
		Node best = null;
		Node current;
		
		Iterator<Node> iter = neighbors.iterator();
		while(iter.hasNext()) {
			current = iter.next();
			calcDist = current.getDistance(x, y);
			System.out.println(calcDist);
			if(calcDist < dist) {
				dist = calcDist;
				best = current;
			}
		}
		return best;
	}
	
	//finds a name and binds it, if there is already a name bound try another
	private void bindName(int attempt) {
		try {
			Naming.bind("node" + attempt, this);
			nodeName = "node" + attempt;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			System.out.println("name taken, trying node" + (attempt + 1));
			bindName(attempt + 1);
		}
		
	}
	
	//These should be used over charAtMod
	//Used for hashing function
	private int charAtEven(String keyword) {
		return charAtMod(keyword, 0);
	}
	
	private int charAtOdd(String keyword) {
		return charAtMod(keyword, 1);
	}
	
	// mod should be 1 for odd 0 for even
	private int charAtMod(String keyword, int mod) {
		int hash = 0;
		for(int i = 0; i < keyword.length(); i++)
			if(i%2 == mod)
				hash += keyword.charAt(i);
		return hash%10;
	}
}

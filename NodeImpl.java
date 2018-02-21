import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
public class NodeImpl extends UnicastRemoteObject implements Node{

	//These should be changed if you are running on a different server
	final private String serverIP = "localhost";
	final private int serverPort = 1099;
	
	
	
	private double xPos;
	private double yPos;
	private String ip;
	private String nodeName;
	private boolean isInNetwork = false;
	
	private boolean isBootstrapper;
	
	Registry reg;
	
	BootStrapper bs = null;
	
	private List<Zone> zones;
	private List<String> keywords;
	private Set<Node> neighbors;
	
	public NodeImpl() throws RemoteException, UnknownHostException {
		super();
		reg = LocateRegistry.getRegistry(serverIP, serverPort);
		
		ip = InetAddress.getLocalHost().getHostAddress();
		zones = new ArrayList<Zone>();
		keywords = new ArrayList<String>();
		neighbors = new HashSet<Node>();
		bindName(0);
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
		//sb.append("Node location: " + xPos + ", " + yPos + "\n");
		sb.append("Neigbors: " + getNeighborNames() + "\n");
		sb.append("Keywords Stored " + keywords + "\n");
		for(int i = 0; i < zones.size(); i++)
			sb.append("Zone " + i + " is: " + zones.get(i).toString() + "\n");
		sb.append("This node is the Bootstrapper node: " + isBootstrapper + "\n");
		sb.append("This node is in the network: " + isInNetwork + "\n");
		sb.append("\n");
		return sb.toString();
	}
	
	public String insert(String keyword) throws RemoteException {
		double i_xPos = charAtOdd(keyword);
		double i_yPos = charAtEven(keyword);
		StringBuilder sb = new StringBuilder();
		
		//debugOutput("Position of object being inserted " + i_xPos + " " + i_yPos);
		
		if(isInNetwork) {
			//finds out if data is stored here
			for(int i = 0; i < zones.size(); i++) {
				if(zones.get(i).contains(i_xPos, i_yPos)) {
					keywords.add(keyword);
					return viewLite(); //Could change to view
				}
			}
			//if not find the correct node
			sb.append(viewLite());
			Node closest = findClosestNode(i_xPos, i_yPos);
			sb.append(closest.insert(keyword));
		}else {
			sb.append("Failure, not in network");
		}
		return sb.toString();
	}
	
	public String search(String keyword) throws RemoteException {
		double i_xPos = charAtOdd(keyword);
		double i_yPos = charAtEven(keyword);
		StringBuilder sb = new StringBuilder();
		String temp;
		
		if(isInNetwork) {
			//finds out if data is stored here
			for(int i = 0; i < zones.size(); i++) {
				if(zones.get(i).contains(i_xPos, i_yPos)) {
					if(keywords.contains(keyword))
						return viewLite();
					else
						return "Failure";
				}
			}
			temp = findClosestNode(i_xPos, i_yPos).search(keyword); 
			
		} else {
			return "Failure, not in network";
		}
		//if not find the correct node

		
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
		Node splitNode;
		//Make sure you arent in the network already
		if(!isInNetwork) {
			isInNetwork = true;
			try {
				bs = (BootStrapper)reg.lookup("bootStrapper");
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				//This is where I will create a bootstrapper
				//This returns so the bottom half of this code doesnt happen.
				bs = new BootStrapperImpl(this);
				try {
					reg.bind("bootStrapper", bs);
				} catch (AlreadyBoundException e1) {
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
		}
		return view();
	}
	
	public String leave() throws RemoteException, MalformedURLException, NotBoundException {
		//give all the nodes zones away
		StringBuilder sb = new StringBuilder();
		
		while(!zones.isEmpty() && !neighbors.isEmpty()) {
			giveZone(zones.get(0));
			zones.remove(0);
			//debugOutput(zones.toString());
		}
		//migrate all data
		reInsert();
		if(isBootstrapper) {
			if(!neighbors.isEmpty()) {
				Node newBootstrapper = neighbors.iterator().next();
				newBootstrapper.setBootstrapper(true);
				bs.setBootstrapNode(newBootstrapper);
			}else {
				reg.unbind("bootStrapper");
			}
		}
		Iterator<Node> iter = neighbors.iterator();
		Node current;
		sb.append("Nodes affected:\n");
		while(iter.hasNext()) {
			current = iter.next();
			current.addNeighbors(neighbors);
			current.removeNeighbor(this);
			sb.append(current.view());
		}
		sb.append("The node that left is:\n");
		sb.append(view());
		this.xPos = -1;
		this.yPos = -1;
		neighbors.clear();
		keywords.clear();
		zones.clear();
		isBootstrapper = false;
		isInNetwork = false;
		
		
		return sb.toString();
	}
	
	

	public String getName() {
		return nodeName;
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

	/*
	 * code that finds the closest node
	 * used in searching and inserting mainly
	 */
	
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
	
	public Node getClosestNode(double x, double y) throws RemoteException {
		for(int i = 0; i < zones.size(); i++) {
			if(zones.get(i).contains(x, y)) {
				return this;
			}
		}
		Node closest = findClosestNode(x,y);
		//debugOutput(this.getName() + " found " + closest.getName() + " as the closest");
		return findClosestNode(x,y).getClosestNode(x,y);
	}
	
	/*
	 * neighbor modifying code
	 */
	public Set<Node> getNeighbors() {
		return neighbors;
	}
	
	public void addNeighbors(Set<Node> neighborsToCheck) throws RemoteException {
		Iterator<Node> iter = neighborsToCheck.iterator();
		Node current;
		while(iter.hasNext()) {
			current = iter.next();
			if(adjacentNode(current)) {
				neighbors.add(current);
				current.addNeighbor(this);
			}else {
				current.removeNeighbor(this);
			}
		}
		neighbors.remove(this);
	}
	
	public void addNeighbor(Node node) throws RemoteException {
		neighbors.add(node);
	}
	
	public void findNeighbors(Node node, Set<Node> neighborsToCheck) throws RemoteException {
		neighbors = new HashSet<Node>();
		neighbors.add(node);
		
		addNeighbors(neighborsToCheck);
	}
	
	public void removeNeighbor(Node node) throws RemoteException {
		neighbors.remove(node);
	}



	public void reInsert() throws RemoteException {
		List<String> rehashingKeywords = keywords;
		keywords = new ArrayList<String>();
		
		for(int i = 0; i < rehashingKeywords.size(); i++) {
			debugOutput(rehashingKeywords.get(i));
			debugOutput(insert(rehashingKeywords.get(i)));
		}
	}
	

	/*
	 * Zone managing code 
	 */
	
	//adds a zone to the list of zones being managed
	public void addZone(Zone zone) throws RemoteException{
		zones.add(zone);
	}
	
	//splits the zone and returns half
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
	
	//Returns true if the zones merge
	public boolean merge(Zone zone) throws RemoteException{
		Zone mergeZone;
		for(int i = 0; i < zones.size(); i++) {
			mergeZone = zones.get(i);
			if(mergeZone.mergeable(zone)) {
				zones.remove(i);
				zones.add(mergeZone.merge(zone));
				return true;
			}
		}
		return false;
	}
	
	//Returns the area of a node
	public double getArea() throws RemoteException {
		double area = 0;
		
		for(int i = 0; i < zones.size(); i++) {
			area += zones.get(i).getArea();
		}
		return area;
	}

	public void unbindName() {
		try {
			reg.unbind(nodeName);
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * These are private helper methods
	 */
	
	//Gives a zone to a neigbor, this method tries to merge first if unable to merge
	// it gives the zone to the smallest neighbor
	private void giveZone(Zone zone) throws RemoteException{
		boolean isMerged = false;
		
		Iterator<Node> iter = neighbors.iterator();
		Node current;
		Node smallestNode = null;
		
		double area = Double.MAX_VALUE;
		double calcArea;
		
		while(iter.hasNext() && !isMerged) {
			current = iter.next();
			isMerged = current.merge(zone);
			calcArea = current.getArea();
			if(calcArea < area) {
				smallestNode = current;
				area = calcArea;
			}
		}
		
		if(!isMerged) {
			smallestNode.addZone(zone);
		}
	}
	
	//Returns true if the node is adjacent
	private boolean adjacentNode(Node node) throws RemoteException {
		boolean isAdjacent = false;
		List<Zone> neighborZones = node.getZones();
		for(int i = 0; i < neighborZones.size() && !isAdjacent; i++){
			isAdjacent = adjacentZone(neighborZones.get(i));
		}
		//debugOutput("This node " + node.getName() + " is " + isAdjacent + " for adjacency " + getName());
		return isAdjacent;
	}
	
	//returns true if the zone is adjacent to this node
	private boolean adjacentZone(Zone zone) {
		boolean isAdjacent = false;
		for(int i = 0; i < zones.size() && !isAdjacent; i++){
			isAdjacent = zones.get(i).adjacent(zone);	
		}
		//debugOutput("This zone " + zone.toString() + " is " + isAdjacent + " for adjacency with " + getName());
		return isAdjacent;
	}
	
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
			//debugOutput(calcDist + "");
			if(calcDist <= dist) {
				dist = calcDist;
				best = current;
			}
		}
		if(best == null) {
			//debugOutput("This got null on " + x + " " + y + " with node being " + this.getName());
			//debugOutput("The best distance was " + dist);
		}
		return best;
	}
	
	//finds a name and binds it, if there is already a name bound try another
	private void bindName(int attempt) {
		try {
			reg.bind("node" + attempt, this);
			nodeName = "node" + attempt;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			//debugOutput("name taken, trying node" + (attempt + 1));
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
	
	//Prints debugging output if the flag is true
	private void debugOutput(String print) {
		if(false)
			System.out.println(print);
	}
}

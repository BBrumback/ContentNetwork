import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

public interface Node extends Remote{
	
	//Basic commands
	public String insert(String keyword) throws RemoteException;
	
	public String search(String keyword) throws RemoteException;
	
	public String view() throws RemoteException;
	
	public String join(double x, double y) throws RemoteException;
	
	public String leave() throws RemoteException, MalformedURLException, NotBoundException;
	
	//Helper methods that are used when trying to complete one of the basic commands
	public double getDistance(double x, double y) throws RemoteException;
	
	public String getName() throws RemoteException;

	public Node getClosestNode(double x, double y) throws RemoteException;

	public Zone split() throws RemoteException;
	
	public void reInsert() throws RemoteException;
	
	public void addNeighbor(Node node) throws RemoteException;

	public List<Zone> getZones() throws RemoteException;

	public Set<Node> getNeighbors() throws RemoteException;

	public void findNeighbors(Node node, Set<Node> neighborsToCheck) throws RemoteException;
	
	public void removeNeighbor(Node node) throws RemoteException;
	
	public boolean merge(Zone zone) throws RemoteException;
	
	public double getArea() throws RemoteException;
	
	public void addZone(Zone zone) throws RemoteException;

	public void setBootstrapper(boolean b) throws RemoteException;

	public void setName(String name) throws RemoteException;	

	public void addNeighbors(Set<Node> neighbors) throws RemoteException;

	public void unbindName() throws RemoteException;
}

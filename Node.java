import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

public interface Node extends Remote{
	
	
	public String insert(String keyword) throws RemoteException;
	
	public String search(String keyword) throws RemoteException;
	
	public String view() throws RemoteException;
	
	public String join(double x, double y) throws RemoteException;
	
	public double getDistance(double x, double y) throws RemoteException;
	
	public String getName() throws RemoteException;

	public Node getClosestNode(double x, double y) throws RemoteException;

	public Zone split() throws RemoteException;
	
	public void reInsert() throws RemoteException;
	
	public void addNeighbor(Node node) throws RemoteException;

	public List<Zone> getZones() throws RemoteException;

	public Set<Node> getNeighbors() throws RemoteException;

	public void findNeighbors(Node node, Set<Node> neighborsToCheck) throws RemoteException;
}

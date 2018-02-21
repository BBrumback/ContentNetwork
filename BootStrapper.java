import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BootStrapper extends Remote {
	
	public Node splitNode(double x, double y) throws RemoteException;
	
	public void setBootstrapNode(Node node) throws RemoteException;

}

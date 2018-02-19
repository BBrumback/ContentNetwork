import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class BootStrapperImpl extends UnicastRemoteObject implements BootStrapper{

	Node bsNode;
	
	public BootStrapperImpl(Node node) throws RemoteException{
		bsNode = node;
	}
	
	protected BootStrapperImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public Node splitNode(double x, double y) throws RemoteException {
		// TODO Auto-generated method stub
		return bsNode.getClosestNode(x, y);
	}

}

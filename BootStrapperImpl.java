import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class BootStrapperImpl extends UnicastRemoteObject implements BootStrapper{

	private static final long serialVersionUID = -6881761651283960166L;
	Node bsNode;
	
	public BootStrapperImpl(Node node) throws RemoteException{
		bsNode = node;
	}

	public Node splitNode(double x, double y) throws RemoteException {
		// TODO Auto-generated method stub
		return bsNode.getClosestNode(x, y);
	}

	@Override
	public void setBootstrapNode(Node node) throws RemoteException {
		this.bsNode = node;
	}

}

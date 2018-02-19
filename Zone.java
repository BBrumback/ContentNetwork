import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Zone implements Serializable{
	/**
	 * 
	 */
	
	//Eclipse told me to do this, not sure what its used for
	private static final long serialVersionUID = 96252392889211435L;
	double x1;
	double x2;
	double y1;
	double y2;
	boolean isSquare;
	
	public Zone(double _x1, double _y1, double _x2, double _y2, boolean _isSquare) {
		this.x1 = _x1;
		this.x2 = _x2;
		this.y1 = _y1;
		this.y2 = _y2;
		this.isSquare = _isSquare;
	}
	
	//Given a location say if that point falls in the zone
	public boolean contains(double x, double y) {
		if(x1 <= x && x < x2) {
			if(y1 <= y && y < y2) {
				return true;
			}
		}
		return false;
	}
	
	//tells how far away the point is from each cornner. if its inside the area return 0
	public double distanceTo(double x, double y) {
		double distance = Double.MAX_VALUE;
		double calculatedDistance;
		
		if(contains(x,y))
			return 0.0;
		
		//Find the smallest distance to the points
		calculatedDistance = calculateDistance(this.x1, this.y1, x, y);
		if(distance < calculatedDistance)
			distance = calculatedDistance;
		calculatedDistance = calculateDistance(this.x1, this.y2, x, y);
		if(distance < calculatedDistance)
			distance = calculatedDistance;
		calculatedDistance = calculateDistance(this.x2, this.y1, x, y);
		if(distance < calculatedDistance)
			distance = calculatedDistance;
		calculatedDistance = calculateDistance(this.x2, this.y2, x, y);
		if(distance < calculatedDistance)
			distance = calculatedDistance;
		
		return distance;
	}
	
	public boolean isSquare() {
		return isSquare;
	}

	public void setSquare(boolean isSquare) {
		this.isSquare = isSquare;
	}

	//split function used when a node joins
	public List<Zone> split(){
		List<Zone> zones = new ArrayList<Zone>(2);
		double x3, y3;
		if(isSquare) {
			y3 = y2 - (y2 - y1)*0.5;
			zones.add(new Zone(x1,y1,x2,y3,!isSquare));
			zones.add(new Zone(x1,y3,x2,y2,!isSquare));
		}else {
			x3 = x2 - (x2 - x1)*0.5;
			zones.add(new Zone(x1,y1,x3,y2,!isSquare));
			zones.add(new Zone(x3,y1,x2,y2,!isSquare));
		}
		return zones;
	}
	
	//sees if the zoneis adjacent. meaning they share one edge
	
	
	
	
	
	public boolean adjacent(Zone zone) {
		return xAdjacent(zone) || yAdjacent(zone);
	}
	
	
	public List<Double> listPoints(){
		
		List<Double> list = new ArrayList<Double>();
		list.add(x1);
		list.add(y1);
		list.add(x2);
		list.add(y2);
		return list;
	}
	
	public String toString() {
		return x1 + " " + y1 + " " + x2 + " " + y2 + " " + isSquare;
	}
	
	private double calculateDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
	
	private boolean xAdjacent(Zone zone) {
		List<Double> points = zone.listPoints();
		if(x1 == points.get(2) || x2 == points.get(0)) {
			if(y1 <= points.get(1) && points.get(3) <= y2) {
				return true;
			}
		}
		return false;
	}
	
	private boolean yAdjacent(Zone zone) {
		List<Double> points = zone.listPoints();
		if(y1 == points.get(3) || y2 == points.get(1)) {
			if(x1 <= points.get(0) && points.get(2) <= x2) {
				return true;
			}
		}
		return false;
	}
	
	//TODO: add merge!! says if they can and did merge
	/*
	public boolean merge(Zone z) {
		
	}
	*/
	
	//TODO: also need isMergable but right now I think thats part of merge
	
	//TODO add getters for points
}

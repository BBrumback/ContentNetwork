import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Zone implements Serializable{
	
	private static final long serialVersionUID = 96252392889211435L;
	
	public double x1;
	public double x2;
	public double y1;
	public double y2;
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
		if(distance > calculatedDistance)
			distance = calculatedDistance;
		calculatedDistance = calculateDistance(this.x1, this.y2, x, y);
		if(distance > calculatedDistance)
			distance = calculatedDistance;
		calculatedDistance = calculateDistance(this.x2, this.y1, x, y);
		if(distance > calculatedDistance)
			distance = calculatedDistance;
		calculatedDistance = calculateDistance(this.x2, this.y2, x, y);
		if(distance > calculatedDistance)
			distance = calculatedDistance;
		
		return distance;
	}
	
	public String toString() {
		return x1 + " " + y1 + " " + x2 + " " + y2;
	}
	
	private double calculateDistance(double x1, double y1, double otherX, double otherY) {
		return Math.sqrt(Math.pow(otherX - x1, 2) + Math.pow(otherY - y1, 2));
	}
	
	//sees if the zone is adjacent. meaning they share one edge
	public boolean adjacent(Zone zone) {
		boolean test = xAdjacent(zone) || yAdjacent(zone);
		debugOutput("The test returned " + test); 
		return test;
	}
	
	private boolean xAdjacent(Zone zone) {
		if(x1 == zone.x2 || x2 == zone.x1) {
			debugOutput("They share an X edge");
			if (y1 >= zone.y1 && y1 < zone.y2) {
				return true;
			}
			if (y2 > zone.y1 && y2 <= zone.y2) {
				return true;
			}
			if (zone.y1 >= y1 && zone.y1 < y2) {
				return true;
			}
			if (zone.y2 > y1 && zone.y2 <= y2) {
				return true;
			}
		}
		return false;
	}
	
	private boolean yAdjacent(Zone zone) {
		if(y1 == zone.y2 || y2 == zone.y1) {
			debugOutput("They share an Y edge");
			if (x1 >= zone.x1 && x1 < zone.x2) {
				return true;
			}
			if (x2 > zone.x1 && x2 <= zone.x2) {
				return true;
			}
			if (zone.x1 >= x1 && zone.x1 < x2) {
				return true;
			}
			if (zone.x2 > x1 && zone.x2 <= x2) {
				return true;
			}
		}
		return false;
	}
	
	//split function used when a node joins
	public List<Zone> split(){
		List<Zone> zones = new ArrayList<Zone>(2);
		double x3, y3;
		if(!isSquare) {
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
	
	public boolean mergeable(Zone zone) {
		// TODO Auto-generated method stub
		if(this.isSquare && 
		   zone.isSquare &&
		   this.x1 == zone.x1 &&
		   this.x2 == zone.x2 &&
		   yAdjacent(zone)) 
		{
			return true;
		}else if(!this.isSquare &&
				!zone.isSquare &&
				this.y1 == zone.y1 &&
				this.y2 == zone.y2 &&
				xAdjacent(zone)) {
			return true;
		} else {
			return false;
		}
	}

	public Zone merge(Zone zone) {
		// TODO Auto-generated method stub
		if(this.isSquare) {
			if(this.y2 > zone.y2) {
				return new Zone(this.x1, zone.y1, this.x2, this.y2, !this.isSquare);
			}else {
				return new Zone(this.x1, this.y1, this.x2, zone.y2, !this.isSquare);
			}
		}else {
			if(this.x2 > zone.x2) {
				return new Zone(zone.x1, this.y1, this.x2, this.y2, !this.isSquare);
			}else {
				return new Zone(this.x1, this.y1, zone.x2, this.y2, !this.isSquare);
			}
		}
	}

	public double getArea() {
		// TODO Auto-generated method stub
		return (x2 - x1) * (y2 -y1);
	}
	
	//TODO: also need isMergable but right now I think thats part of merge
	
	//Prints debugging output if the flag is true
	private void debugOutput(String print) {
		if(false)
			System.out.println(print);
	}
}

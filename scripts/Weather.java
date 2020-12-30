package scripts;

import java.util.LinkedList;

public class Weather {

	public static final int TYPE_CLEAR = 0;
	public static final int TYPE_LIGHTLY_CLOUDLY = 0;
	public static final int TYPE_MODERATELY_CLOUDLY = 0;
	public static final int TYPE_CLOUDLY = 0;
	public static final int TYPE_NEARLY_OVERCAST = 0;
	public static final int TYPE_OVERCAST = 0;
	public static final int TYPE_PARTIAL_PRECIPITATION = 0;
	public static final int TYPE_PRECIPITATION = 0;
	public static final int TYPE_HEAVY_PRECIPITATION = 0;
	
	public static final int PRECIPITATION_RAIN = 0;
	public static final int PRECIPITATION_SNOW = 0;
	
	private Vector wind;
	private LinkedList<Vector> futureWinds;
	
	private int type;
	private int initTime;
	
	public static final int WIND_INTERVAL = 100000;

	public Weather(int type, int time) {
		futureWinds = new LinkedList<Vector>();
		initFutureWinds();
		wind = futureWinds.removeLast();
		
		this.type = type;
		initTime = time;
	}
	
	public void initFutureWinds() {
		futureWinds.push(new Vector(1, 0, -0.3));
		futureWinds.push(new Vector(1, 0, -0.3));
		futureWinds.push(new Vector(1, 0, -0.3));
		futureWinds.push(new Vector(1, 0, -0.3));
		futureWinds.push(new Vector(1, 0, -0.3));
		futureWinds.push(new Vector(1, 0, -0.3));
		futureWinds.push(new Vector(1, 0, -0.3));
		futureWinds.push(new Vector(1, 0, -0.3));
		futureWinds.push(new Vector(1, 0, -0.3));
		futureWinds.push(new Vector(1, 0, -0.3));
		futureWinds.push(new Vector(1, 0, -0.3));
		futureWinds.push(new Vector(1, 0, -0.3));
		futureWinds.push(new Vector(1, 0, -0.3));
	}
	
	public void update(int time) {
		
		if(time-initTime >= WIND_INTERVAL) {
			wind = futureWinds.removeLast();
			pushNextWind(time);
			initTime = time;
		}
	}
	
	private void pushNextWind(int time) {
		futureWinds.push(new Vector(1, 0, -0.3));
	}
	
	public void applyWind(Point p, double speed) {
		p.changeX(wind.getX()*speed);
		p.changeZ(wind.getZ()*speed);
	}
	
	public Point getClosestFuturePosition(Observer obs, Point p) {
				
		Point cp1 = new Point(p.x, p.y, p.z);
		Point cp2 = new Point(p.x, p.y, p.z);
		Point c, cm = new Point(p.x, p.y, p.z);
		Vector fovL = obs.getLeftBound();
		Vector fovR = obs.getRightBound();
		Point cpf1, cpf2;
		Vector cv;
		double minDist = Double.MAX_VALUE, currDist;
		Vector wind;
		boolean successful = false;
		
		for(int i = futureWinds.size()-1; i >= 0; i--) {
			
			wind = futureWinds.get(i);
			
			cp2.changeX(wind.getX()*WIND_INTERVAL);
			cp2.changeZ(wind.getZ()*WIND_INTERVAL);
			
			cv = new Vector(wind.getX()*WIND_INTERVAL, 0,
					wind.getZ()*WIND_INTERVAL);
			cpf1 = Utility.findXZIntersection(cp1, cv, obs.getPos(), fovL);
			cpf2 = Utility.findXZIntersection(cp1, cv, obs.getPos(), fovR);
			
			boolean cp1OutOfView = false;
			if(cpf1 == null) {
				if(!obs.isWithinView(cp1, 50))
					cp1OutOfView = true;
				cpf1 = cp1;
			}
			
			if(cpf2 == null) {
				cpf2 = cp2;
				if(cp1OutOfView && !obs.isWithinView(cp2, 50)) {
					cp1.changeX(wind.getX()*WIND_INTERVAL);
					cp1.changeZ(wind.getZ()*WIND_INTERVAL);
					continue;
				}
			}
			
			System.out.println(cpf1.toString() + " " + cpf2.toString());
			
			c = obs.closestPointToLineSegment(cpf1, cpf2);
			currDist = obs.getDistance(c);
			if(currDist < minDist) {
				minDist = currDist;
				cm = new Point(c.x, c.y, c.z);
				successful = true;
			}
			
			cp1.changeX(wind.getX()*WIND_INTERVAL);
			cp1.changeZ(wind.getZ()*WIND_INTERVAL);
		}
		if(!successful) return null;
		return cm;
	}
	
	public double getWindSpeed() {
		return wind.magnitude();
	}
	
	public void setWind(Vector wind) {
		this.wind = wind;
	}
	
	public Vector getWind() {
		return wind;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
}

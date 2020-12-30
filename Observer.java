package scripts;

import java.util.Random;

public class Observer extends PositionedObject {
	private Rotation rot;
	private final double xFOV, yFOV;
	private int screenWidth, screenHeight;
	
	public final int DISTANCE_MIN = 2000;
	public final int DISTANCE_MAX = 50000;
	
	public Observer(Point pos, Rotation rot, double FOV, int w, int h) {
		super(pos);
		this.rot = rot;
		
		if(w > h) {
			xFOV = FOV;
			yFOV = FOV*((double)h/w);
		} else {
			yFOV = FOV;
			xFOV = FOV*((double)w/h);
		}
		
		screenWidth = w;
		screenHeight = h;
	}
	
	/**
	 * Calculates where a 3D point must be displayed on a 2D screen.
	 * Objects under the PositionedObject superclass are generated
	 * in consideration of the observer, therefore this method should
	 * only be used to calculate their position, not their entire image.
	 * Returns null if the point is out of view.
	 * 
	 * @param p The 3D point.
	 * @return The 2D display position of the 3D point.
	 */	
	public Point getObservedPosition(Point p) {
		
		double[] u = {0, 0, 1};
		u = Utility.rotateXY(u, rot.getX(), rot.getY());
		double[] v = {pos.getX()-p.getX(), p.getY()-pos.getY(),
				p.getZ()-pos.getZ()};
		
		if(Utility.innerProduct(u, v) <= 0) return null;
		
		double[] proj = Utility.projection(u, v);
		double[] rel = Utility.rotateXY(Utility.subtract(v,proj),
				-rot.getX(), -rot.getY());
		
		int x = (int) (screenWidth/2
				- rel[0]/(Utility.magnitude(proj)*Math.tan(xFOV/2.0))
				* screenWidth/2);
		int y = (int) (screenHeight/2
				- rel[1]/(Utility.magnitude(proj)*Math.tan(yFOV/2.0))
				* screenHeight/2);
		
		return new Point(x, y);
	}
	
	public Point getRandomXZ(Random R, int y, int min, int max) {
		double mag = R.nextInt(max-min) + min;
		double[] u = {0, 0, 1};
		u = Utility.rotateX(u, (R.nextDouble()-0.5)*(xFOV));
				
		return new Point((int) (u[0]*mag), y, (int) (u[2]*mag));
	}
	
	public int getDistance(Point p) {
		double[] v = {pos.getX()-p.getX(), p.getY()-pos.getY(), p.getZ()-pos.getZ()};
		return (int) Utility.magnitude(v);
	}
	
	public int getObservedWidth(Point p, int width) {
		Point p1 = new Point(pos.getX()-p.getX(), p.getY()-pos.getY(),
				p.getZ()-pos.getZ());
		p1 = getObservedPosition(p1);
		if(p1 == null) return -1;
		
		Point p2 = new Point(pos.getX()-p.getX()+width, p.getY()-pos.getY(),
				p.getZ()-pos.getZ());
		p2 = getObservedPosition(p2);
		if(p2 == null) return -1;
		
		return (int) (p1.getX()-p2.getX());
	}
	
	public int getObservedHeight(Point p, int height) {
		Point p1 = new Point(pos.getX()-p.getX(), p.getY()-pos.getY(),
				p.getZ()-pos.getZ());
		p1 = getObservedPosition(p1);
		if(p1 == null) return -1;
		
		Point p2 = new Point(pos.getX()-p.getX(), p.getY()-pos.getY()+height,
				p.getZ()-pos.getZ());
		p2 = getObservedPosition(p2);
		if(p2 == null) return -1;
		
		return (int) Math.abs(p1.getY()-p2.getY());
	}
	
	public Point closestPointToLineSegment(Point lineStart, Point lineEnd) {
		double[] n = {lineEnd.x-lineStart.x, lineEnd.y-lineStart.y,
				lineEnd.z-lineStart.z};
		n = Utility.toUnitVector(n);
		double[] amp = {lineStart.x-pos.x, lineStart.y-pos.y, lineStart.z-pos.z};
		n = Utility.scale(n, Utility.innerProduct(amp, n));
		n = Utility.subtract(amp, n);
				
		Point orthMin = new Point(n[0], n[1], n[2]);
		
		if(n[0] > lineStart.x && n[0] > lineEnd.x
				|| n[0] < lineStart.x && n[0] < lineEnd.x
				|| n[1] > lineStart.y && n[1] > lineEnd.y
				|| n[1] < lineStart.y && n[1] < lineEnd.y
				|| n[2] > lineStart.z && n[2] > lineEnd.z
				|| n[2] < lineStart.z && n[2] < lineEnd.z) {
			return (orthMin.distanceTo(lineStart) < orthMin.distanceTo(lineEnd))
					? lineStart : lineEnd;
		}
		
		return orthMin;
	}
	
	public boolean isWithinView(Point p, int buffer) {
		Point obsPos = getObservedPosition(p);
		if(obsPos == null 
				|| obsPos.x < -buffer
				|| obsPos.x > screenWidth+buffer
				|| obsPos.y < -buffer
				|| obsPos.y > screenHeight+buffer)
			return false;
		return true;
	}
	
	public Vector getLeftBound() {
		return new Vector(Math.sin(-xFOV/2+rot.getX())*DISTANCE_MAX, 0,
				Math.cos(-xFOV/2+rot.getX())*DISTANCE_MAX);
	}
	
	public Vector getRightBound() {
		return new Vector(Math.sin(xFOV/2+rot.getX())*DISTANCE_MAX, 0,
				Math.cos(xFOV/2+rot.getX())*DISTANCE_MAX);
	}
	
	public void setRot(Rotation r) {
		rot = r;
	}
	
	public Rotation getRot() {
		return rot;
	}
	
	public void setScreenWidth(int w) {
		screenWidth = w;
	}
	
	public void setScreenHeight(int h) {
		screenHeight = h;
	}
	
	public int getScreenWidth() {
		return screenWidth;
	}
	
	public int getScreenHeight() {
		return screenHeight;
	}
}
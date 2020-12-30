package scripts;

public class Point {
	protected double x;
	protected double y;
	protected double z;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(double x, double y, double z) {
		this(x, y);
		this.z = z;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setZ(double z) {
		this.z = z;
	}
	
	public void changeX(double x) {
		this.x += x;
	}
	
	public void changeY(double y) {
		this.y += y;
	}
	
	public void changeZ(double z) {
		this.z += z;
	}
	
	public void flipX() {
		this.x*= -1;
	}
	
	public void flipY() {
		this.y*= -1;
	}
	
	public void flipZ() {
		this.z*= -1;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public double distanceTo(Point q) {
		return Math.sqrt(Math.pow(q.x-x,2) + Math.pow(q.y-y,2)
				+ Math.pow(q.z-z,2));
	}
	
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
}

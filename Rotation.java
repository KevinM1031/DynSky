package scripts;

public class Rotation {
	private double xr, yr;
	
	public Rotation(double xr, double yr) {
		this.xr = xr;
		this.yr = yr;
	}
	
	public void setX(double xr) {
		this.xr = xr;
	}
	
	public void setY(double yr) {
		this.yr = yr;
	}
	
	public void changeX(double xr) {
		this.xr += xr;
	}
	
	public void changeY(double yr) {
		this.yr += yr;
	}
	
	public double getX() {
		return xr;
	}
	
	public double getY() {
		return yr;
	}
}

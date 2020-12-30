package scripts;

public class Vector extends Point {

	public Vector(double x, double y, double z) {
		super(x, y, z);
	}
	
	public double magnitude() {
		return Math.sqrt(x*x + y*y + z*z);
	}
}

package scripts;

import java.util.Random;

public class Utility {
	
	public static int getARGB(int a, int r, int g, int b) {
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
	
	public static int[] mixARGB(
			int a1, int r1, int g1, int b1,
			int a2, int r2, int g2, int b2) {
				
		if(a2 == 255) return new int[] {a2, r2, g2, b2};
		if(a2 == 0) return new int[] {a1, r1, g1, b1};
				
		int aOut = a2 + (a1 * (255 - a2) / 255);
		
		return new int[] {aOut,
				(r2*a2 + r1*a1 * (255-a2)/255)/aOut,
				(g2*a2 + g1*a1 * (255-a2)/255)/aOut,
				(b2*a2 + b1*a1 * (255-a2)/255)/aOut};
	}
	
	/**
	 * A sinusoid noise control function that suppresses noise when there are
	 * less shifting present. This prevents coarse vertical perimeters. Returns
	 * a shortened maxNoise value for the suppression effect.
	 * 
	 * @param x The output of a shiftFunction.
	 * @param maxNoise Size of noise bound.
	 * @param amp The amplifier of the suppression effect.
	 * Accepts real numbers between 0 and 1 inclusive, where 1 maximizes the effect.
	 * Accepts nonnegative even integers.
	 * @return The suppressed maxNoise.
	 */
	public static double noiseControlSine(double x, double maxNoise, double amp) {
		return (amp*maxNoise)/2 * Math.sin(Math.PI*x + Math.PI/2) - (maxNoise*(amp-2))/2;
	}
	
	/**
	 * A polynomial noise control function that suppresses noise when there are
	 * less shifting present. This prevents coarse vertical perimeters. Returns
	 * a shortened maxNoise value for the suppression effect.
	 * 
	 * @param x The output of a shiftFunction.
	 * @param maxNoise Size of noise bound.
	 * @param amp The amplifier of the suppression effect.
	 * Accepts real numbers between 0 and 1 inclusive, where 1 maximizes the effect.
	 * @param pow the degree of the polynomial function.
	 * Accepts nonnegative even integers.
	 * @return The suppressed maxNoise.
	 */
	public static double noiseControlPoly(double x, double maxNoise, double amp, int pow) {
		return amp*maxNoise * Math.pow(x-1, pow) + maxNoise*(1-amp);
	}
	
	/**
	 * Performs an increasing insertion sort on an array of line objects.
	 * Each line is sorted by their left points.
	 * This method is efficient for short arrays (expected max size of 4) or
	 * arrays that are already sorted.
	 * 
	 * @param arr The array of line objects to be sorted.
	 */
	public static void sortLines(Line[] arr) {
		int s, c;
		Line curr;
		for(int i = 1; i < arr.length && arr[i] != null; i++) {
			curr = arr[i];
			s = i-1;
			
			c = curr.getMid()-curr.getLeft();
			while(s >= 0 && arr[s].getMid()-arr[s].getLeft() > c) {
				arr[s+1] = arr[s];
				s--;
			}
			
			arr[s+1] = curr;
		}
	}
	
	/**
	 * Shifts all null elements to the back of the array of line objects,
	 * while keeping the order between non-null elements.
	 * 
	 * @param arr The array of line objects to be organized in this manner.
	 */
	public static void organizeLines(Line[] arr) {
		boolean reiterate;
		
		do {
			reiterate = false;
			for(int i = 0; i < arr.length-1; i++) {
				if(arr[i] == null && arr[i+1] != null) {
					arr[i] = arr[i+1];
					arr[i+1] = null;
					reiterate = true;
				}
			}
		} while(reiterate);
	}
	
	/**
	 * Returns a biased random double between 0 (inclusive) and bound (exclusive).
	 * The probability of a certain value being selected is determined with
	 * a normal distribution with avg as its center. Standard deviation is set as
	 * bound/4 by default.
	 * 
	 * @param bound Upper bound (exclusive).
	 * @param avg Average value: that is, the most probable output.
	 * @return A biased random double.
	 */
	public static double biasedRandom(Random R, double bound, double avg) {
		double r = -1, stddev = bound/4, i;
		for(i = 0; i < 5 && (r >= bound || r < 0); i++)
			r = R.nextGaussian() * stddev + avg;
		if(i < 5) return r;
		return avg;
	}
	
	/**
	 * Returns a biased random integer between 0 (inclusive) and bound (exclusive).
	 * The probability of a certain value being selected is determined with
	 * a normal distribution with avg as its center. Standard deviation is set as
	 * bound/4 by default.
	 * 
	 * @param bound Upper bound (exclusive).
	 * @param avg Average value: that is, the most probable output.
	 * @return A biased random integer.
	 */
	public static int biasedRandom(Random R, int bound, int avg) {
		int r = -1, stddev = bound/4, i;
		for(i = 0; i < 3 && (r >= bound || r < 0); i++)
			r = (int) (R.nextGaussian() * stddev + avg+0.5);
		if(i < 3) return r;
		return avg;
	}
	
	public static Pixel[][] shrinkPixels(Pixel[][] pixels, int w, int h) {
		double wR = (double)w/pixels[0].length; // width shrink ratio
		double hR = (double)h/pixels.length; // height shrink ratio
		Pixel[][] shrunkPixels = new Pixel[h][w];
		
		for(int y = 0; y < h; y++) {
			for(int x = 0; x < w; x++) {
				shrunkPixels[y][x] = pixels[(int) (y/hR)][(int) (x/wR)];
			}
		}
		
		return shrunkPixels;
	}
	
	public static double magnitude(double[] v) {
		return Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
	}
	
	public static double[] subtract(double[] u, double[] v) {
		return new double[] {u[0]-v[0], u[1]-v[1], u[2]-v[2]};
	}
	
	public static double[] subtractXZ(double[] u, double[] v) {
		return new double[] {u[0]-v[0], u[1], u[2]-v[2]};
	}
	
	public static double[] scale(double[] u, double scalar) {
		return new double[] {scalar*u[0], scalar*u[1], scalar*u[2]};
	}
	
	public static double[] projection(double[] u, double[] v) {
		double scalar = innerProduct(v,u) / innerProduct(u,u);
		return scale(u, scalar);
	}
	
	public static double innerProduct(double[] u, double[] v) {
		return u[0]*v[0] + u[1]*v[1] + u[2]*v[2];
	}
	
	public static double crossProductXZ(double[] u, double[] v) {
		return u[0]*v[2] - u[2]*v[0];
	}
	
	public static double[] toUnitVector(double[] u) {
		double mag = magnitude(u);
		return new double[] {u[0]/mag, u[1]/mag, u[2]/mag};
	}
	
	public static double[] rotateXY(double[] v, double xr, double yr) {
		return rotateY(rotateX(v, xr), yr);
	}
	
	public static double[] rotateX(double[] v, double xr) {
		
		double[][] Tx = {
				{Math.cos(-xr), 0, Math.sin(-xr)},
				{0, 1, 0},
				{-Math.sin(-xr), 0, Math.cos(-xr)}};
		
		return new double[] {
				Tx[0][0]*v[0] + Tx[0][1]*v[1] + Tx[0][2]*v[2],
				Tx[1][0]*v[0] + Tx[1][1]*v[1] + Tx[1][2]*v[2],
				Tx[2][0]*v[0] + Tx[2][1]*v[1] + Tx[2][2]*v[2]};
	}
	
	public static double[] rotateY(double[] v, double yr) {
		
		double[][] Ty = {
				{1, 0, 0},
				{0, Math.cos(-yr), -Math.sin(-yr)},
				{0, Math.sin(-yr), Math.cos(-yr)}};
		
		return new double[] {
				Ty[0][0]*v[0] + Ty[0][1]*v[1] + Ty[0][2]*v[2],
				Ty[1][0]*v[0] + Ty[1][1]*v[1] + Ty[1][2]*v[2],
				Ty[2][0]*v[0] + Ty[2][1]*v[1] + Ty[2][2]*v[2]};
	}
	
	public static Point getRandomXZPoint(Random R, double[] u, boolean negativeOn) {
		Point p = new Point(R.nextDouble()*u[0], u[1], R.nextDouble()*u[2]);
		p.flipX();
		p.flipZ();
		return p;
	}
	
	public static Point findXZIntersection(Point p1, Vector v1, Point p2, Vector v2) {
		
		double[] p = {p1.x, p1.y, p1.z};
		double[] r = {v1.x, v1.y, v1.z};
		double[] q = {p2.x, p2.y, p2.z};
		double[] s = {v2.x, v2.y, v2.z};
		
		double b = crossProductXZ(r, s);
		if(b == 0) return null;
		
		double u = crossProductXZ(subtractXZ(q, p), r);
		double t = crossProductXZ(subtractXZ(q, p), s);
		
		if(u >= 0 && u <= 1 && t >= 0 && t <= 1)
			return new Point(p1.x+v1.x*t, p1.y, p1.z+v1.z*t);
		else return null;
	}
}

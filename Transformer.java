package scripts;

public class Transformer {
	private int type;
	private double[] params;
	private double y1, y2, offset;
	
	private static final int TYPE_POLYNOMIAL_PEAK = 0;
	private static final int TYPE_POLYNOMIAL_TRANSITION = 1;
	private static final int TYPE_SINUSOIDAL_PEAK = 2;
	private static final int TYPE_SINUSOIDAL_TRANSITION = 3;
	
	public static final int NUMBER_OF_TRANSFORMERS = 4;
	
	/**
	 * Default constructor.
	 * 
	 * @param transformerID The type of desired transformer.
	 * @param params An array of parameters for the desired input.
	 * @param y1 The lower bound of vertical span; inclusive.
	 * @param y2 The upper bound of vertical span; exclusive.
	 * Booleans are to be represented as 1 (true) or 0 (false).
	 */
	public Transformer(int type, double[] params, double y1, double y2, double offset) {
		this.type = type;
		this.params = params;
		this.y1 = y1;
		this.y2 = y2;
		this.offset = offset;
	}
	
	/**
	 * Runs a desired transformation and returns its output based on predefined parameters.
	 * If the y is out of predefined bound, -2 is returned, which indicates exactly this.
	 * 
	 * @param y The current y-value.
	 * @return The output of the function.
	 */
	public double calculate(double y) {
		
		if(y < y1 || y >= y2)
			return -2;
		
		y -= y1;
		
		switch(type) {
		
		case TYPE_POLYNOMIAL_PEAK:
			return PolynomialTransformer.polyPeak(y, params[0], params[1], params[2], 
					params[3]==1) + offset;
			
		case TYPE_POLYNOMIAL_TRANSITION:
			return PolynomialTransformer.polyTransition(y, params[0], params[1], params[2], 
					params[3]==1) + offset;
			
		case TYPE_SINUSOIDAL_PEAK:
			return SinusoidalTransformer.sinePeak(y, params[0], params[1]) + offset;
			
		case TYPE_SINUSOIDAL_TRANSITION:
			return SinusoidalTransformer.sineTransition(y, params[0], params[1],
					params[2]==1) + offset;
			
			default: 
				System.out.println("[ERROR:Transformer01] Code reached where it should "
						+ "never reach! (type: " + type + ")");
				return 0;
		}
	}
}

class PolynomialTransformer {
	
	/**
	 * A function of a single polynomial peak.
	 * Creates a more smooth double curve.
	 * The function begins from 0, peaks at cef, then returns to 0.
	 * 
	 * @param x Value for the function variable.
	 * @param bound x-axis span of the peak pattern.
	 * @param cef The coefficient of the polynomial function.
	 * @param pow Degree of the polynomial function. Negative values cause the function
	 * to reflect across the x-axis.
	 * @param decrease If true, the function returns a value that decreases from amp to 0.
	 * @return The output of the function.
	 */
	protected static double polyPeak(double x, double bound, double cef, 
			double pow, boolean decrease) {
		if(decrease) {
			if(x < bound/2) 
				return polyTransition(2*x, bound, cef, pow, !decrease);
			return polyTransition(2*x-bound, bound, cef, pow, decrease);
		} else {
			if(x < bound/2) 
				return -polyTransition(2*x, bound, cef, pow, !decrease)+cef;
			return -polyTransition(2*x-bound, bound, cef, pow, decrease)+cef;
		}
	}
	
	/**
	 * A smoothly transitioning function in a polynomial form.
	 * Creates a more smooth single curve.
	 * By default, the transition occurs from 0 to cef.
	 * 
	 * @param x Value for the function variable.
	 * @param bound x-axis span of the increasing pattern.
	 * @param cef The coefficient of the polynomial function.
	 * @param pow Degree of the polynomial function. Negative values cause the function
	 * to reflect across the x-axis.
	 * @param decrease If true, the function returns a value that decreases from amp to 0.
	 * @return The output of the function.
	 */
	protected static double polyTransition(double x, double bound, double cef,
			double pow, boolean decrease) {
		if(decrease) return cef * Math.abs(Math.pow((-x+bound)/bound, pow));
		return cef * Math.abs(Math.pow(x/bound, pow));
	}
}

class SinusoidalTransformer {
	
	/**
	 * A function of a single sinusoidal peak.
	 * Creates a less smooth double curve.
	 * The function begins from 0, peaks at amp, then returns to 0.
	 * 
	 * @param x Value for the function variable.
	 * @param bound x-axis span of the increasing pattern.
	 * @param amp Amplitude of the sine function. Negative values cause the function
	 * to reflect across the x-axis.
	 */
	protected static double sinePeak(double x, double bound, double amp) {
		return amp/2.0 * Math.sin((2.0*Math.PI)/bound * x - Math.PI/2.0) + amp/2.0;
	}
	
	/**
	 * A smoothly transitioning function in a sinusoidal form.
	 * Creates a less smooth single curve.
	 * By default, the transition occurs from 0 to amp.
	 * 
	 * @param x Value for the function variable.
	 * @param bound x-axis span of the peak pattern.
	 * @param amp Amplitude of the sine function. Negative values cause the function
	 * to reflect across the x-axis.
	 * @param decrease If true, the function returns a value that decreases from amp to 0.
	 * @return The output of the function.
	 */
	protected static double sineTransition(double x, double bound, double amp,
			boolean decrease) {
		if(decrease) return amp/2.0 * Math.cos((2.0*Math.PI)/(2.0*bound) * x) + amp/2.0;
		return -amp/2.0 * Math.cos((2.0*Math.PI)/(2.0*bound) * x) + amp/2.0;
	}
}
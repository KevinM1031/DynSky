package scripts;

public class CumulusCongestus extends Cumulus {
	
	public CumulusCongestus(Point pos, Observer obs, int seed, Weather weather,
			int screenWidth, int screenHeight) {
		super(pos, obs, seed, weather, screenWidth, screenHeight);
	}
	
	void initializeCloudSpecifics() {
		// Minimum value for maxSproutNoise
		MIN_SPROUT_NOISE = 6;
		// Maximum shift in x-direction relative to previous one
		MAX_NOISE = 25;
		// Cloud line shifting bounds (inclusive)
		MAX_SHIFT = 0.75;
		MIN_SHIFT = -0.05;
		// Number of lines per y-level
		MAX_LINES_PER_LEVEL = 6;
		// Noise suppressor for smoother sides
		NOISE_CONTROL_AMPLIFIER = 0.7;
		// Default altitude of this cloud
		DEFAULT_ALTITUDE = 2000;
		// Height of cloud (a.k.a. its size) in meters
		DEFAULT_HEIGHT = 3000;
	}
	
	void initializeShiftSpecifics() {
		// Further initialization based on maxHeight
		height = maxHeight - biasedRandom(maxHeight/2, maxHeight/8);
		originalHeight = height;
		totalDiv = maxHeight/3;
		minDiv = (int) (totalDiv*0.03);
		maxDiv = (int) (totalDiv*0.10);
		divBias = (int) (totalDiv*0.05);
		maxSproutNoise = maxHeight/10 < MIN_SPROUT_NOISE
				? MIN_SPROUT_NOISE : maxHeight/10;
		sproutChance = (int) (maxHeight*(1.0/4.0)) <= 20
				? 20 : (int) (maxHeight*(1.0/4.0));
		mergeChance = maxHeight/8 == 0
				? 1 : maxHeight/8;
	}
	
	double leftBiasFunction(double y) {
		if(y < 0.03) return 0.5;
		if(y < 0.3) return 0.3;
		if(y < 0.6) return 0.1;
		if(y < 0.8) return 0.15;
		return 0.25;
	}
	
	double rightBiasFunction(double y) {
		if(y < 0.02) return 0.5;
		if(y < 0.15) return 0.2;
		if(y < 0.6) return 0.05;
		return 0.1;
	}
	
	double sproutFunction(double y) {
		if(y < 0.2) return 0.15;
		if(y < 0.65) return 1;
		if(y < 0.8) return 0.3;
		return 0.4;
	}
	
	double leftSproutBiasFunction(double y) {
		if(y < 0.2) return 0.5;
		if(y < 0.65) return 0.25;
		if(y < 0.8) return 0.5;
		return 0.65;
	}
	
	double rightSproutBiasFunction(double y) {
		if(y < 0.2) return 0.15;
		if(y < 0.65) return 0.25;
		if(y < 0.8) return 0.4;
		return 0.2;
	}
}

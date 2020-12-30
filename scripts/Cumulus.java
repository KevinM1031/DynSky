package scripts;

import java.util.ArrayList;

public abstract class Cumulus extends Cloud {

	// Contains lines for each y-level of the cloud
	Line[][] shape;
	
	// Cloud transformer functions for left and right sides
	private ArrayList<Transformer> leftTransformers;
	private ArrayList<Transformer> rightTransformers;
	
	// Cloud generation
	int maxHeight, // Upper bound for randomized cloud height
		totalDiv, // Number of divisions the cloud will be divided into
		minDiv, maxDiv, // Number of divisions per transformation (inclusive)
		divBias, // Preferred number of divisions per transformation
		maxSproutNoise, // Maximum noise for sprout (both distance and width)
		sproutChance, // Chance of sprouting per level
		mergeChance; // Chance of sprout merging
	
	// Minimum value for maxSproutNoise
	int MIN_SPROUT_NOISE;
	// Maximum shift in x-direction relative to previous one
	int MAX_NOISE;
	// Cloud line shifting bounds (inclusive)
	double MAX_SHIFT, MIN_SHIFT;
	// Number of lines per y-level
	int MAX_LINES_PER_LEVEL;
	// Noise suppressor for smoother sides
	double NOISE_CONTROL_AMPLIFIER;
	// Extended pixels due to AA
	int DEFAULT_ANTIALIASING_BUFFER;
	// Default altitude of this cloud
	int DEFAULT_ALTITUDE;
	// Height of cloud (a.k.a. its size) in meters
	int DEFAULT_HEIGHT;
	
	double futureDist;
	
	
	public Cumulus(Point pos, Observer obs, int seed, Weather weather,
			int screenWidth, int screenHeight) {
		super(pos, seed, weather);
		
		// Initialize general cloud default specifics (all-caps variables)
		initializeCloudSpecifics();
				
		// Maximum height possible
		Point futureClosestPos = weather.getClosestFuturePosition(obs, pos);
		System.out.println(futureClosestPos.toString());
		futureDist = obs.getDistance(futureClosestPos);

		maxHeight = obs.getObservedHeight(futureClosestPos, DEFAULT_HEIGHT);
		// Initialize shift-related default specifics (non-caps variables)
		initializeShiftSpecifics();
		
		// Cloud randomized transformation generation
		leftTransformers = new ArrayList<Transformer>();
		generateCloudShape(leftTransformers, true);
		rightTransformers = new ArrayList<Transformer>();
		generateCloudShape(rightTransformers, false);
		
		// Cloud generation setup
		shape = new Line[height][MAX_LINES_PER_LEVEL];
		int left = maxSproutNoise/2, right = maxSproutNoise/2, mid = 0;
		int leftEnd = 0, rightEnd = 0, mark = 2;
		shape[0][0] = new Line(left, right, mid, mark);
		boolean merged;
		int timesNotSprouted = 0, currSproutChance;
		double yLevel;
		
		// Cloud shape calculations by generating relative coordinates to midpoint
		for(int y = 1; y < height; y++) {
			
			yLevel = (double)y/height;
			merged = false;
			
			for(int i = 0; i < MAX_LINES_PER_LEVEL; i++) {
				
				// Defining variables for the sake of readability
				left = shape[y-1][i].getLeft();
				right = shape[y-1][i].getRight();
				mid = shape[y-1][i].getMid();
				mark = shape[y-1][i].getMark();
				
				// Calculating shift
				if(left < maxSproutNoise) left = left + getTopShift(left, mark);
				else left = left + getLeftShift(y);
				if(right < maxSproutNoise) right = right + getTopShift(right, mark);
				else right = right + getRightShift(y);
				
				// Checking for new maximum values
				if(left-mid > leftEnd)
					leftEnd = left-mid;
				if(right+mid > rightEnd)
					rightEnd = right+mid;
				
				shape[y][i] = new Line(left, right, mid, mark);
				
				// Merge if this line overlaps with the one on its left
				if(i > 0 && shape[y][i-1].getRight()+shape[y][i-1].getMid() 
						>= mid-left && R.nextInt(mergeChance) == 0) {
										
					// Merge left side
					shape[y][i].setLeft(shape[y][i-1].getLeft()
							+mid-shape[y][i-1].getMid());
					
					// Also merge right side if necessary
					if(shape[y][i-1].getRight()+shape[y][i-1].getMid() > right+mid)
						shape[y][i].setRight(shape[y][i-1].getRight()
								-mid+shape[y][i-1].getMid());
					
					shape[y][i-1] = null;
					merged = true;
					
					if(i == MAX_LINES_PER_LEVEL-1 || shape[y-1][i+1] == null) break;
					continue;
				}
				
				// Checking to create new sprout
				currSproutChance = (int) (sproutFunction(yLevel)*sproutChance)+1;
				if(i < MAX_LINES_PER_LEVEL-1 && shape[y-1][i+1] == null && !merged) {
					if(y+1 == height || R.nextInt(currSproutChance) != 0
							&& timesNotSprouted < sproutChance*2) {
						timesNotSprouted++;
						break; // No sprout
					}
					timesNotSprouted = 0;
					
					if(R.nextInt(2) == 0) { // Sprout on left side
						mid = (int) -biasedRandom(maxSproutNoise, 
								maxSproutNoise*leftSproutBiasFunction(yLevel))
								- shape[y-1][i].getLeft()+mid;
						
					} else { // Sprout on right side
						mid = (int) biasedRandom(maxSproutNoise, 
								maxSproutNoise*rightSproutBiasFunction(yLevel))
								+ shape[y-1][i].getRight()+mid;
					}
					
					// Planting sprout
					mark = R.nextInt(3)+4;
					shape[y][i+1] = new Line(maxSproutNoise/mark,
							maxSproutNoise/mark+1, mid, mark);
					break;
					
				} else if(i == MAX_LINES_PER_LEVEL-1 || shape[y-1][i+1] == null) break;
			}
			
			Utility.organizeLines(shape[y]);
			Utility.sortLines(shape[y]);
		}
		
		// Width is calculated here
		width = leftEnd + rightEnd + DEFAULT_ANTIALIASING_BUFFER*2;
		originalWidth = width;
		
		// Final calculations to reflect absolute coordinates
		for(int y = 0; y < height; y++) {
			for(int i = 0; i < MAX_LINES_PER_LEVEL; i++) {
				left = leftEnd - shape[y][i].getLeft() + shape[y][i].getMid();
				shape[y][i].setLeft(left);

				right = shape[y][i].getRight() + leftEnd + shape[y][i].getMid();
				shape[y][i].setRight(right);
								
				if(i < MAX_LINES_PER_LEVEL-1 && shape[y][i+1] == null)
					break;
			}
		}
		
		// Initializing pixels with calculated width and height
		pixels = new Pixel[height][width];
		lightedPixels = new Pixel[height][width];
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				pixels[y][x] = new Pixel(0,0,0,0);
				lightedPixels[y][x] = new Pixel(0,0,0,0);
			}
		}
		
		// Generating pixel slots for cloud
		generatePixels();
	}
	
	abstract void initializeCloudSpecifics();
	abstract void initializeShiftSpecifics();
	
	/**
	 * Randomly generates the cloud transformation functions.
	 * As a result of running this method, T will be populated.
	 * 
	 * @param T An empty Transformer array list.
	 * @param isLeft Whether this transformation applies to the left side.
	 */
	private void generateCloudShape(ArrayList<Transformer> T, boolean isLeft) {
		
		// Pixel height of a single division
		final double divHeight = height/(double)totalDiv;
		
		int currDiv = 0;
		double currShift = 1, shiftBias;
		
		// Generates random transformations of cloud between predefined default settings.
		for(int i = currDiv; i < totalDiv; i++) {
			
			// Bringing bias value for current y level, then generating next shift
			if(isLeft) shiftBias = leftBiasFunction((double)i/totalDiv);
			else shiftBias = rightBiasFunction((double)i/totalDiv);
			double nextShift = biasedRandom(MAX_SHIFT-MIN_SHIFT, 
					(MAX_SHIFT-MIN_SHIFT)*shiftBias)+MIN_SHIFT;
			
			// Randomizing next transformer
			int nextTransformer = R.nextInt(Transformer.NUMBER_OF_TRANSFORMERS);
			
			// Calculating the size of current division
			int divSize = biasedRandom(maxDiv-minDiv, divBias)+minDiv;
							
			/*
			 * Select a random transformer, add random parameters, and add 
			 * to the transformers list.
			 */
			switch(nextTransformer) {
			
			case 0: // PolyPeak
				if(biasedRandom(2.0, shiftBias) > 1) { // Increasing
					T.add(new Transformer(nextTransformer, new double[] {
							divHeight*divSize, nextShift-currShift, 2, 0},
							divHeight*i, divHeight*(i+divSize), currShift));
				} else { // Decreasing
					T.add(new Transformer(nextTransformer, new double[] {
							divHeight*divSize, currShift-nextShift, 2, 1},
							divHeight*i, divHeight*(i+divSize), nextShift));
				} break;
				
			case 1: // PolyTransition
				if(biasedRandom(2.0, shiftBias) > 1) { // Increasing
					T.add(new Transformer(nextTransformer, new double[] {
							divHeight*divSize, nextShift-currShift, 2, 0},
							divHeight*i, divHeight*(i+divSize), currShift));
				} else { // Decreasing
					T.add(new Transformer(nextTransformer, new double[] {
							divHeight*divSize, currShift-nextShift, 2, 1},
							divHeight*i, divHeight*(i+divSize), nextShift));
				} break;
				
			case 2: // SinePeak
				T.add(new Transformer(nextTransformer, new double[] {
						divHeight*divSize, nextShift-currShift},
						divHeight*i, divHeight*(i+divSize), currShift));
				break;
				
			case 3: // SineTransition
				if(biasedRandom(2.0, shiftBias) > 1) { // Increasing
					T.add(new Transformer(nextTransformer, new double[] {
							divHeight*divSize, nextShift-currShift, 0},
							divHeight*i, divHeight*(i+divSize), currShift));
				} else { // Decreasing
					T.add(new Transformer(nextTransformer, new double[] {
							divHeight*divSize, currShift-nextShift, 1},
							divHeight*i, divHeight*(i+divSize), nextShift));
				} break;
				
				default: 
					System.out.println("[ERROR:Cloud01] Code reached where it "
							+ "should never reach!");
			}
			
			// Skip to the division that comes after this transformation 
			currShift = nextShift;
			i += divSize;
		}
	}
	
	public void updateCloud(Pixel[][] frame, Observer obs, double speed) {		
		
		Point p = getObservedPosition(obs);
		if(p == null) return;
		
		double currHeight = obs.getObservedHeight(pos, 1000);
		double shrinkRatio = currHeight/originalHeight;
		
		if(shrinkRatio > 1) {
			System.out.println(obs.getDistance(pos) + " < " + futureDist);
			shrinkRatio = 1;
		} else if(obs.getDistance(pos) < futureDist) {
			System.out.println(obs.getDistance(pos) + " << " + futureDist);
			System.out.println(pos.toString());
		}
		
		resizedPixels = Utility.shrinkPixels(pixels,
				(int) (originalWidth*shrinkRatio),
				(int) (originalHeight*shrinkRatio));
		width = (int) (originalWidth*shrinkRatio);
		height = (int) (originalHeight*shrinkRatio);
		
		// Drawing cloud
		for(int y = (int) (p.getY()-height); y < p.getY(); y++) {
			if(y >= obs.getScreenHeight() || y < 0) continue;
			
			for(int x = (int) (p.getX()-width/2); x < p.getX()+width/2; x++) {
				if(x >= obs.getScreenWidth() || x < 0) continue;
				
				// Lighting pixel
				int absY = (int) (y - p.getY() + height);
				int absX = (int) (x - p.getX() + width/2);
				applyLighting(absY, absX, obs);
				
				// Combining colors for transluscent pixels
				int[] frameCol = frame[y][x].getPixel();
				int[] cloudCol = lightedPixels[absY][absX].getPixel();
				int[] mixedCol = Utility.mixARGB(
					frameCol[0], frameCol[1], frameCol[2], frameCol[3],
					cloudCol[0], cloudCol[1], cloudCol[2], cloudCol[3]);
				
				// Plotting pixel
				frame[y][x].setPixel(
						mixedCol[0], mixedCol[1],
						mixedCol[2], mixedCol[3]);
			}
		}
		
		weather.applyWind(pos, speed);

	}
	
	public int getTrueWidth() {
		return originalWidth*(DEFAULT_HEIGHT/maxHeight);
	}
	
	public int getTrueHeight() {
		return originalHeight*(DEFAULT_HEIGHT/maxHeight);
	}
	
	private void generatePixels() {
		for(int y = 0; y < height; y++) {
			for(Line L : shape[y]) {
				
				if(L != null) {
					for(int x = L.getLeft(); x < L.getRight(); x++) {
						
						if(x == L.getLeft() || x+1 == L.getRight()) {
							pixels[y][x].setMark(1);
								
//						} else if(x+1 == L.getLeft() || x == L.getRight()) {
							
							
						} else
							pixels[y][x].setBlank();
					}
				}
			}
		}
	}
	
	private void applyLighting(int y, int x, Observer obs) {
		
		// Calculating hue depending on distance
		int hue = (int) (600000.0/(pos.getZ()+(120000.0/51.0)));
		hue = 255-hue;
		
		// Applying hue
		if(resizedPixels[y][x].getMark() == 1) {
			lightedPixels[y][x].setPixel(255, 255, 0, 0);
			
		} else if(resizedPixels[y][x].isBlank()) {
			lightedPixels[y][x].setPixel(255, hue, hue, hue);
			
		} else {
			try {
				lightedPixels[y][x] = resizedPixels[y][x];
			} catch(ArrayIndexOutOfBoundsException | NegativeArraySizeException e2) {
//				System.out.println(lightedPixels[y].length + " " + originalWidth);
//				System.out.println(height + " " + width);
//				System.out.println(y + " " + x);
			}
		}
	}
	
	private int getLeftShift(int y) {
		double mult = leftShiftFunction(y);
		int bound = (int) Utility.noiseControlPoly(mult, MAX_NOISE, 
				NOISE_CONTROL_AMPLIFIER, 6);
		return biasedRandom(bound, (int) (bound/2.0*mult)) - bound/2;
	}
	
	private int getRightShift(int y) {
		double mult = rightShiftFunction(y);
		int bound = (int) Utility.noiseControlPoly(mult, MAX_NOISE, 
				NOISE_CONTROL_AMPLIFIER, 6);
		return biasedRandom(bound, (int) (bound/2.0*mult)) - bound/2;
	}
	
	private int getTopShift(int y, double div) {
		double mult = (maxSproutNoise-y)/(double)(maxSproutNoise);
		return (int) biasedRandom(MAX_NOISE/div, (MAX_NOISE/div)*mult);
	}
	
	/**
	 * Returns a randomly generated leftward shift function.
	 * The returned value is between 0 and 2 inclusive,
	 * where values greater than 1 favors leftward shift, 
	 * and below 1 favors rightward shift.
	 * Returns bias if no transformation is defined for this y.
	 * 
	 * @param y Current y value.
	 * @return The output of the function.
	 */
	private double leftShiftFunction(int y) {
		double out = 0;
		
		for(Transformer T : leftTransformers) {
			out = T.calculate(y);
			if(out != -2) break;
		}
		
		if(out == -2)
			return leftBiasFunction((double)y/height)+MIN_SHIFT+1;
		
		return out+1;
	}
	
	/**
	 * Returns a randomly generated rightward shift function.
	 * The returned value is between 0 and 2 inclusive,
	 * where values greater than 1 favors rightward shift, 
	 * and below 1 favors leftward shift.
	 * Returns bias if no transformation is defined for this y.
	 * 
	 * @param y Current y value.
	 * @return The output of the function.
	 */
	private double rightShiftFunction(int y) {
		double out = 0;
		
		for(Transformer T : rightTransformers) {
			out = T.calculate(y);
			if(out != -2) break;
		}
		
		if(out == -2)
			return rightBiasFunction((double)y/height)+MIN_SHIFT+1;
			
		return out+1;
	}

	public int getDefaultAltitude() {
		return DEFAULT_ALTITUDE;
	}
	
	abstract double leftBiasFunction(double y);
	abstract double rightBiasFunction(double y);
	abstract double sproutFunction(double y);
	abstract double leftSproutBiasFunction(double y);
	abstract double rightSproutBiasFunction(double y);

}

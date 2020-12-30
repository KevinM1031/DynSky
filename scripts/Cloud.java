package scripts;

import java.util.Random;

public abstract class Cloud extends PositionedObject {

	final Random R; // Seeded randomizer object
	Pixel[][] pixels, resizedPixels, lightedPixels; // Cloud pixels
	
	int width, height; // Cloud width and height
	int originalWidth, originalHeight; // Cloud width and height before resizing
	
	private int dist; // Distance from observer
	private Point obsPos; // Observed position
	private boolean wasInView;
			
	Weather weather; // Wind
	
	public Cloud(Point pos, int seed, Weather weather) {
		super(pos);
		this.weather = weather;
		R = new Random(seed);
		wasInView = false;
	}
	
	public void update(Pixel[][] frame, Observer obs, double speed) {
		dist = getDistance(obs);
		obsPos = getObservedPosition(obs);
		
		updateCloud(frame, obs, speed);
	}
	
	public abstract void updateCloud(Pixel[][] frame, Observer obs, double speed);
	
	public abstract int getDefaultAltitude();
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getOriginalWidth() {
		return originalWidth;
	}
	
	public int getOriginalHeight() {
		return originalHeight;
	}
	
	public abstract int getTrueWidth();
	public abstract int getTrueHeight();
	
	public void setPos(Point pos) {
		this.pos = pos;
	}
	
	public Point getObservedPosition(Observer obs) {
		return obs.getObservedPosition(pos);
	}
	
	public Point getObservedPosition() {
		return obsPos;
	}
	
	public int getDistance(Observer obs) {
		return obs.getDistance(pos);
	}
	
	public int getDistance() {
		return dist;
	}
	
	public boolean wasInView() {
		return wasInView;
	}
	
	public boolean isWithinView(Observer obs) {
		final int FOV_2D_BUFFER = 50, buffer = width/2;
		final int lB = -FOV_2D_BUFFER - buffer, uB = FOV_2D_BUFFER + buffer;
		if(obsPos == null 
				|| obsPos.getX() < lB
				|| obsPos.getX() > obs.getScreenWidth()+uB
				|| obsPos.getY() < lB
				|| obsPos.getY() > obs.getScreenHeight()+uB)
			return false;
		return true;
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
	int biasedRandom(int bound, int avg) {
		return Utility.biasedRandom(R, bound, avg);
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
	double biasedRandom(double bound, double avg) {
		return Utility.biasedRandom(R, bound, avg);
	}

}

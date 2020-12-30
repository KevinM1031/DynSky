package scripts;

import java.util.ArrayList;
import java.util.Random;

public class CloudCluster {

	private ArrayList<Cloud> clouds;
	
	private int maxClouds;
	private final Random R;
	private final int screenWidth, screenHeight;
	private Weather weather;
	
	private static final double MIN_CLOUD_POPULATION = 0.5;
	private static final int CLOUD_ADD_CHANCE = 100;
	
	public CloudCluster(Observer obs, int maxClouds, int seed,
			int screenWidth, int screenHeight, Weather weather, double speed) {
		this.maxClouds = maxClouds;
		R = new Random(seed);
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.weather = weather;
		clouds = new ArrayList<Cloud>();
				
		for(int i = 0; i < maxClouds*MIN_CLOUD_POPULATION; i++) {
			addRandom(R, obs, speed);
		}
	}
	
	public void update(Pixel[][] frame, Observer obs, int speed) {
		
		// Update each cloud; remove any if necessary
		int n = clouds.size();
		for(int i = 0; i < n; i++) {
			clouds.get(i).update(frame, obs, speed);
			
			if(!clouds.get(i).isWithinView(obs)) {
				clouds.remove(i);
				n--;
				i--;
			}
		}
				
		// Add new clouds if there are empty slots
		if(n < maxClouds && R.nextInt(CLOUD_ADD_CHANCE) == 0
				|| n < maxClouds*MIN_CLOUD_POPULATION)
			addRandomHorizon(R, obs, speed);
	}
	
	public void add(Cloud c) {
		int n = clouds.size();
		for(int i = 0; i < n; i++) {
			if(clouds.get(i).pos.getZ() <= c.pos.getZ()) {
				clouds.add(i, c);
				return;
			}
		} clouds.add(c);
	}
	
	private void addRandomHorizon(Random R, Observer obs, double speed) {
		
		// Random cloud seed
		int cloudSeed = R.nextInt();
		
		// Vector perpendicular to wind and magnitude of DISTANCE_MAX
		double[] h = {weather.getWind().z, weather.getWind().y,
				-weather.getWind().x};
		// Finding a random point in this vector or its negative
		h = Utility.scale(Utility.toUnitVector(h), obs.DISTANCE_MAX);
		Point p = Utility.getRandomXZPoint(R, h, false);
		
		// Wind vector
		double[] w = {weather.getWind().x, weather.getWind().y,
				weather.getWind().z};
		// Scaling to reach horizon
		double s = Math.sqrt(Math.pow(obs.DISTANCE_MAX, 2)
				- Math.pow(Math.sqrt(p.x*p.x + p.y*p.y + p.z*p.z), 2));
		w = Utility.scale(Utility.toUnitVector(w), s);
		
		p.changeX(w[0]);
		p.setY(2000); //TODO algorithmize
		p.changeZ(w[2]);
		
		if(weather.getClosestFuturePosition(obs, p) == null) {
			System.out.println("fail " + p.toString());
			return;
		}
		System.out.println("success");
		
		
		Cloud c = new CumulusCongestus(p, obs, cloudSeed, weather,
				screenWidth, screenHeight);
		
//		c.setPos(new Point(c.getPos().getX()-c.getTrueWidth(),
//				c.getPos().getY(), c.getPos().getZ()));
		add(c);
	}
	
	private void addRandom(Random R, Observer obs, double speed) {
		int cloudSeed = R.nextInt();
		Point p = obs.getRandomXZ(R, 2000, obs.DISTANCE_MIN, obs.DISTANCE_MAX);
		
		add(new CumulusCongestus(p, obs, cloudSeed, weather,
				screenWidth, screenHeight));
	}
	
	
}

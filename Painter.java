package scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Painter extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public static final int MAX_TIME = 864000;
	
	private Pixel[][] framePixels;
	private BufferedImage frame;
	
	private Weather weather;
	private Sun sun;
	private ArrayList<CloudCluster> clouds;
	
	private Observer obs;
	private int time;
	
	private boolean firstFrame;
	private int trueWidth, trueHeight;
	
	private final int speed = 100;
	private final int seed = 3;
	
	public Painter(int width, int height) {
		time = 1; //TODO this is temporary
		weather = new Weather(Weather.TYPE_MODERATELY_CLOUDLY, time);
		
		obs = new Observer(
				new Point(0, 0, 0),
				new Rotation(0, Math.PI*(1.0/12.0)),
				Math.PI*(2.0/3.0), width, height);
		
		sun = new Sun();
		clouds = new ArrayList<CloudCluster>();
		clouds.add(new CloudCluster(obs, 20, seed, width, height, weather, time));
		
		setSize(width, height);
		
		firstFrame = true;
	}
	
	@Override
	public void paintComponent(Graphics G) {
		
		if(firstFrame) {
			trueWidth = getWidth();
			trueHeight = getHeight();
			
			obs.setScreenWidth(trueWidth);
			obs.setScreenHeight(trueHeight);
			
			framePixels = new Pixel[getHeight()][getWidth()];
			for(int y = 0; y < framePixels.length; y++) {
				for(int x = 0; x < framePixels[y].length; x++) {
					framePixels[y][x] = new Pixel(0, 0, 0, 0);
				}
			}
			
			frame = new BufferedImage(trueWidth, trueHeight,
					BufferedImage.TYPE_INT_ARGB);
			
			firstFrame = false;
		}
		
		weather.update(time);
		
		drawSky();
		Graphics2D frameG2 = frame.createGraphics();
		drawClouds();
		
		for(int y = 0; y < framePixels.length; y++) {
			for(int x = 0; x < framePixels[y].length; x++) {
				frame.setRGB(x, y, framePixels[y][x].getARGBValue());
			}
		}
		
		drawSun(frameG2);
		drawLand(frameG2);
				
		G.drawImage(frame, 0, 0, null);
		
		G.setColor(Color.black);
		G.drawLine(getWidth()/2, 0, getWidth()/2, getHeight());
		G.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
		G.drawString("seed: " + seed, 10, 20);
		
		time += speed;
	}
	
	private void drawSky() {
		for(int y = 0; y < framePixels.length; y++) {
			for(int x = 0; x < framePixels[y].length; x++) {
				framePixels[y][x].setPixel(255, 180, 255, 255);
			}
		}
	}
	
	// TODO fix this later; will break if horizon point is not in FOV
	private void drawLand(Graphics2D G2) {
		G2.setColor(new Color(40,150,0));
		int horizon = (int) obs.getObservedPosition(
				new Point(obs.getRot().getX(), obs.getRot().getY(), 10000000)).getY();
		G2.fillRect(0, horizon, getWidth(), horizon);
	}
	
	private void drawSun(Graphics2D G2) {
		sun.update(framePixels, obs, time);
		Point p = sun.getObservedPosition(obs);
		if(p == null) return;
		G2.setColor(Color.YELLOW); //TODO proceduralize
		G2.fillOval((int) (p.getX()-Sun.RADIUS), (int) (p.getY()-Sun.RADIUS),
				Sun.RADIUS*2, Sun.RADIUS*2);
	}
	
	private void drawClouds() {
		for(CloudCluster C : clouds) {
			C.update(framePixels, obs, speed);
		}
	}
}

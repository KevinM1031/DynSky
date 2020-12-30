package scripts;

public class Sun extends PositionedObject {
	public static final int DISTANCE = 10000000;
	public static final int RADIUS = 20;
	
	public Sun(int x, int y) {
		super(new Point(x, y, DISTANCE));
	}
	
	public Sun() {
		super(new Point(0, 0, DISTANCE));
	}

	public void update(Pixel[][] frame, Observer obs, int time) {
		double angle = (time*2.0*Math.PI)/Painter.MAX_TIME;
		pos.setY((int) (Math.sin(angle)*DISTANCE));
		pos.setZ((int) (Math.cos(angle)*DISTANCE));
	}
	
	public Point getObservedPosition(Observer obs) {
		return obs.getObservedPosition(pos);
	}
}

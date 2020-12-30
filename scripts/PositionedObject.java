package scripts;

public abstract class PositionedObject {
	Point pos;
	
	public PositionedObject(Point pos) {
		this.pos = pos;
	}
	
	public PositionedObject() {
		pos = new Point(0,0,0);
	}
	
	public void setPos(Point p) {
		pos = p;
	}
	
	public Point getPos() {
		return pos;
	}
}

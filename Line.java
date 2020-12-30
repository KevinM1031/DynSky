package scripts;

public class Line {
	private int left;
	private int right;
	private int mid;
	
	private int mark;
	
	public Line(int left, int right) {
		this.left = left;
		this.right = right;
		this.mid = 0;
		this.mark = 0;
	}
	
	public Line(int left, int right, int mid) {
		this(left, right);
		this.mid = mid;
	}
	
	public Line(int left, int right, int mid, int mark) {
		this(left, right, mid);
		this.mark = mark;
	}
	
	public void setLeft(int left) {
		this.left = left;
	}
	
	public void setRight(int right) {
		this.right = right;
	}
	
	public void setMid(int mid) {
		this.mid = mid;
	}
	
	public void setMark(int mark) {
		this.mark = mark;
	}
	
	public void setLine(int left, int right, int mid) {
		this.left = left;
		this.right = right;
		this.mid = mid;
	}
	
	public int getLeft() {
		return left;
	}
	
	public int getRight() {
		return right;
	}
	
	public int getMid() {
		return mid;
	}
	
	public int getMark() {
		return mark;
	}
}

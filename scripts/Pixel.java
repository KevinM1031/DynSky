package scripts;

public class Pixel {
	private int a, r, g, b;
	private int mark;
	
	public Pixel(int a, int r, int g, int b) {
		this.a = a;
		this.r = r;
		this.g = g;
		this.b = b;
		mark = 0;
	}
	
	public Pixel(int a, int r, int g, int b, int mark) {
		this(a, r, g, b);
		this.mark = mark;
	}
	
	public void setA(int a) {
		this.a = a;
		resetMark();
	}
	
	public void setR(int r) {
		this.r = r;
		resetMark();
	}
	
	public void setG(int g) {
		this.g = g;
		resetMark();
	}
	
	public void setB(int b) {
		this.b = b;
		resetMark();
	}
	
	public void setMark(int mark) {
		this.mark = mark;
	}
	
	public void setBlank() {
		mark = -1;
	}
	
	public void resetMark() {
		mark = 0;
	}
	
	public void setPixel(int a, int r, int g, int b) {
		this.a = a;
		this.r = r;
		this.g = g;
		this.b = b;
		resetMark();
	}
	
	public int getA() {
		return a;
	}
	
	public int getR() {
		return r;
	}
	
	public int getG() {
		return g;
	}
	
	public int getB() {
		return b;
	}
	
	public int[] getPixel() {
		return new int[] {a, r, g, b};
	}
	
	public int getARGBValue() {
		return Utility.getARGB(a, r, g, b);
	}
	
	public int getMark() {
		return mark;
	}
	
	public boolean isBlank() {
		return mark == -1;
	}
}

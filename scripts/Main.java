package scripts;

public class Main {

	private static final String TITLE = "SkyTime";
	private static final int WIDTH = 1500, HEIGHT = 1000;
	private final static boolean RUNNING = true;
	
	public static final long seed = 123456789;
	public static final long delayInMS = 1;
	
	public static void main(String[] args) {
		defaultDriver();
	}
	
	private static void defaultDriver() {
		Window W = new Window(TITLE, WIDTH, HEIGHT, new Painter(WIDTH, HEIGHT));
		
		long t1 = System.nanoTime(), t2, updateCount = 0;
		while(RUNNING) {
			
			W.update();
			
			try {
				
				Thread.sleep(delayInMS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			t2 = System.nanoTime();
			updateCount++;
			if(t2-t1 >= 1000000000l) {
				System.out.println("[SYSTEM] FPS: " + updateCount 
						+ " (optimal: " + 1000l/delayInMS + ")");
				updateCount = 0;
				t1 = System.nanoTime();
			}
		}
	}
}

package scripts;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Window extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	public Window(String title, int width, int height, JPanel content) {
		setName(title);
		setSize(width, height);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(true);
		setContentPane(content);
		setVisible(true);
	}
	
	public void update() {
		repaint();
		revalidate();
	}

	

}

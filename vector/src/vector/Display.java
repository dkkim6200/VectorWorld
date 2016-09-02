package vector;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class Display extends Canvas implements Runnable {
    private static final long serialVersionUID = -2561631169731351866L;
    
	public static final int WIDTH = 500;
	public static final int HEIGHT = 500;
	
	public static final int FPS = 30;
	
	private Renderer renderer;
	private World world;
	private boolean running;
	
	private Thread thread;
	
	
	public Display() {
		renderer = new Renderer(WIDTH, HEIGHT);
		world = new World();
		
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void run() {
		int frames = 0;
		double unprocessedTime = 0;
		long previousTime = System.nanoTime();
		double secondsPerFrame = 1.0 / FPS;
		
		while (running) {
			long currentTime = System.nanoTime();
			long passedTime = currentTime - previousTime;
			
			previousTime = currentTime;
			unprocessedTime += passedTime / 1000000000.0;
			
			while (unprocessedTime > secondsPerFrame) {
				renderer.render();
				world.update(renderer);
				
				repaint();
				
				unprocessedTime -= secondsPerFrame;
				frames++;
			}
		}
	}
	
	public void paint(Graphics g) {
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				image.setRGB(i, j, renderer.pixels[i][j]);
			}
		}
		
		g.drawImage(image, 0, 0, this);
	}
	
	public static void main(String [] args) {
		JFrame frame = new JFrame("Vector Space");
	    frame.getContentPane().add(new Display());

	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(WIDTH + 100, HEIGHT + 100);
	    frame.setVisible(true);
	}
}

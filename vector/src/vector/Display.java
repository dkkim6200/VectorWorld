package vector;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;
import javax.swing.JFrame;

public class Display extends Canvas implements Runnable {
    private static final long serialVersionUID = -2561631169731351866L;
    
	public static final int WIDTH = 500;
	public static final int HEIGHT = 500;
	public static final double GRID_SIZE = 50;
	
	public static final int FPS = 30;
	
	public static final int COLOR_WHITE = 0xFFFFFF;
	public static final int COLOR_BLACK = 0x000000;
	public static final int COLOR_GREEN = 0x27AE60;
	public static final int COLOR_RED = 0xE74C3C;
	
	public static final String TEAPOT_FILE_DIRECTORY = "/Users/DaekunKim/Documents/Programming Related/Eclipse Projects/VectorWorld/vector/assets/teapot";
	
	private Vector3[] vertices;
	private int[][] patches;
	
	private int numVertices;
	private int numPatches;
	
	private double scale;
	
	private int[][] pixels;
	private boolean running;
	
	private Thread thread;
	
	public Display() {
		pixels = new int[WIDTH][HEIGHT];
		clear();
		
		try {
			InputStream in = new FileInputStream(new File(TEAPOT_FILE_DIRECTORY));
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			
			String s;
			
			s = reader.readLine(); // Number of patches;
			numPatches = Integer.parseInt(s);
			patches = new int[numPatches][];
			
			for (int i = 0; i < numPatches; i++) {
				s = reader.readLine();
				
				String[] patchIndexStrings = s.split(",");
				
				patches[i] = new int[patchIndexStrings.length];
				for (int j = 0; j < patchIndexStrings.length; j++) {
					patches[i][j] = Integer.parseInt(patchIndexStrings[j]);
				}
			}
			
			s = reader.readLine(); // Number of vertices
			numVertices = Integer.parseInt(s);
			vertices = new Vector3[numVertices];
			
			for (int i = 0; i < numVertices; i++) {
				s = reader.readLine();
				
				String[] vertexIndexStrings = s.split(",");
				vertices[i] = new Vector3(Double.parseDouble(vertexIndexStrings[0]),
											Double.parseDouble(vertexIndexStrings[1]),
											Double.parseDouble(vertexIndexStrings[2]));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		scale = 50;
		
		for (int i = 0; i < vertices.length; i++) {
			vertices[i] = vertices[i].scalarMultiply(scale);
		}
		
		scale = 0.9;
		
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
				clear();
				update();
				repaint();
				
				unprocessedTime -= secondsPerFrame;
				frames++;
			}
		}
	}
	
	public void update() {
		if (scale == 0.9 && vertices[0].getMagnitude() < 20) {
			scale = 1.1;
		}
		else if (scale == 1.1 && vertices[0].getMagnitude() > 200) {
			scale = 0.9;
		}
		
		drawVertex(new Vector3(200, 200, 0), vertices, patches);
		
		for (int i = 0; i < vertices.length; i++) {
			vertices[i] = vertices[i].scalarMultiply(scale);
		}
		
		for (int i = 0; i < vertices.length - 1; i++) {
			vertices[i].rotate(new Vector3(1, 0, 0), 45.0 / FPS);
		}
	}
	
	public void drawVertex(Vector3 offset, Vector3[] vertices, int[][] patches) {
		for (int i = 0; i < patches.length; i++) {
			for (int j = 0; j < patches[i].length - 1; j++) {
				Vector3 start = vertices[patches[i][j] - 1];
				Vector3 end = vertices[patches[i][j+1] - 1];

				drawVector(offset.add(start), end.subtract(start), COLOR_BLACK);
			}
		}
	}
	
	public void drawVector(Vector3 offset, Vector3 vec, int color) {
		if (vec.getX() >= 0) {
			for (int x = (int) offset.getX(); x < (int) (offset.getX() + vec.getX()); x++) {
				int y = (int) (offset.getY() + vec.getY() * (x - offset.getX()) / vec.getX());
				
				draw(x, y, color);
			}
		} else {
			for (int x = (int) (offset.getX() + vec.getX()); x < (int) offset.getX(); x++) {
				int y = (int) (offset.getY() + vec.getY() * (x - offset.getX()) / vec.getX());
				
				draw(x, y, color);
			}
		}
		
		if (vec.getY() >= 0) {
			for (int y = (int) offset.getY(); y < (int) (offset.getY() + vec.getY()); y++) {
				int x = (int) (offset.getX() + vec.getX() * (y - offset.getY()) / vec.getY());
				
				draw(x, y, color);
			}
		} else {
			for (int y = (int) (offset.getY() + vec.getY()); y < (int) offset.getY(); y++) {
				int x = (int) (offset.getX() + vec.getX() * (y - offset.getY()) / vec.getY());
				
				draw(x, y, color);
			}
		}
	}
	
	public void draw(int x, int y, int color) {
		if (x < WIDTH && x > 0 &&
				y < HEIGHT && y > 0) {
				pixels[x][HEIGHT-y] = color;
			}
	}
	
	public void clear() {
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				draw(i, j, COLOR_WHITE);
			}
		}
		
		for (int i = 0; i < WIDTH / GRID_SIZE; i++) {
			drawVector(new Vector3(i * GRID_SIZE, 0, 0), new Vector3(0, HEIGHT, 0), COLOR_GREEN);
		}
		
		for (int i = 0; i < HEIGHT / GRID_SIZE; i++) {
			drawVector(new Vector3(0, i * GRID_SIZE, 0), new Vector3(WIDTH, 0, 0), COLOR_GREEN);
		}
	}
	
	public void paint(Graphics g) {
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				image.setRGB(i, j, pixels[i][j]);
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

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
	public static final String TEACUP_FILE_DIRECTORY = "/Users/DaekunKim/Documents/Programming Related/Eclipse Projects/VectorWorld/vector/assets/teacup";
	
	private Vector3[] teapotVertices;
	private int[][] teapotPatches;
	
	private int numTeapotVertices;
	private int numTeapotPatches;
	
	private double rotation;
	private double scale;
	private Vector3 translation;
	
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
			numTeapotPatches = Integer.parseInt(s);
			teapotPatches = new int[numTeapotPatches][];
			
			for (int i = 0; i < numTeapotPatches; i++) {
				s = reader.readLine();
				
				String[] patchIndexStrings = s.split(",");
				
				teapotPatches[i] = new int[patchIndexStrings.length];
				for (int j = 0; j < patchIndexStrings.length; j++) {
					teapotPatches[i][j] = Integer.parseInt(patchIndexStrings[j]);
				}
			}
			
			s = reader.readLine(); // Number of vertices
			numTeapotVertices = Integer.parseInt(s);
			teapotVertices = new Vector3[numTeapotVertices];
			
			for (int i = 0; i < numTeapotVertices; i++) {
				s = reader.readLine();
				
				String[] vertexIndexStrings = s.split(",");
				teapotVertices[i] = new Vector3(Double.parseDouble(vertexIndexStrings[0]),
											Double.parseDouble(vertexIndexStrings[1]),
											Double.parseDouble(vertexIndexStrings[2]));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		rotation = 0;
		scale = 100;
		translation = new Vector3(200, 200, 0);
		
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
		Vector3[] manipulatedVertices = new Vector3[teapotVertices.length];
		
		for (int i = 0; i < manipulatedVertices.length; i++) {
			manipulatedVertices[i] = teapotVertices[i].scalarMultiply(scale);
			manipulatedVertices[i].rotate(new Vector3(1, 1, 0), rotation);
			manipulatedVertices[i] = manipulatedVertices[i].add(translation);
		}
		
		drawObject(manipulatedVertices, teapotPatches);
		rotation += 90.0 / FPS;
	}
	
	/**
	 * Draws 3D object from its vertices and Bezier patches.
	 * 
	 * <p>
	 * Source
	 * 1. https://www.scratchapixel.com/lessons/advanced-rendering/bezier-curve-rendering-utah-teapot/bezier-surface
	 * </p>
	 * @param objVertices 3D object's vertices
	 * @param objPatches 3D object's bezier patches
	 */
	void drawObject(Vector3[] objVertices, int[][] objPatches) {
		int divs = 16; // Must be >= 16
		
		Vector3[] controlPoints = new Vector3[divs];
		Vector3[] resultVertices = new Vector3[(divs + 1) * (divs + 1)];
		int[][] vertexIndex = new int[divs * divs][4];
		
		for (int i = 0; i < objPatches.length; i++) { // numTeapotPatches
			for (int j = 0; j < objPatches[i].length; j++) {
				controlPoints[j] = objVertices[objPatches[i][j] - 1];
			}
			
			for (int j = 0, k = 0; j < divs + 1; j++) {
				for (int l = 0; l < divs + 1; l++, k++) {
					resultVertices[k] = evalBezierPatch(controlPoints, l / (double)divs, j / (double)divs); 
				}
			}
			
			for (int j = 0, k = 0; j < divs; j++) {
				for (int l = 0; l < divs; l++, k++) {
					vertexIndex[k][0] = (divs + 1) * j + l; 
					vertexIndex[k][1] = (divs + 1) * (j + 1) + l; 
	                vertexIndex[k][2] = (divs + 1) * (j + 1) + l + 1; 
	                vertexIndex[k][3] = (divs + 1) * j + l + 1; 
				}
			}
			
			for (int j = 0; j < vertexIndex.length; j++) {
				for (int k = 0; k < vertexIndex[j].length - 1; k++) {
					drawLine(resultVertices[vertexIndex[j][k]],
							resultVertices[vertexIndex[j][k + 1]], COLOR_BLACK);
				}
			}
		}
	}
	
	Vector3 evalBezierPatch(Vector3[] controlPoints, double u, double v) { 
	    Vector3[] uCurve = new Vector3[4];
	    
	    Vector3[] temp = new Vector3[4];
	    for (int i = 0; i < 4; i++){
	    	for (int j = 0; j < 4; j++) {
	    		temp[j] = controlPoints[i * 4 + j];
	    	}
	    	
	    	uCurve[i] = bezier(temp, u); 
	    }
	    
	    return bezier(uCurve, v); 
	}
	
	public Vector3 bezier(Vector3[] p, double t) {
		// compute coefficients
		double k1 = (1 - t) * (1 - t) * (1 - t); 
		double k2 = 3 * (1 - t) * (1 - t) * t; 
		double k3 = 3 * (1 - t) * t * t; 
		double k4 = t * t * t;
		
		// weight the four control points using coefficients
		return (p[0].scalarMultiply(k1))
				.add(p[1].scalarMultiply(k2))
				.add(p[2].scalarMultiply(k3))
				.add(p[3].scalarMultiply(k4)); 
	}
	
	public Vector3[] decasteljau(Vector3[] points, int numSegments) {
		Vector3[] result = new Vector3[numSegments + 1];
		
		for (int i = 0; i < numSegments + 1; i++) {
			result[i] = decasteljauRec(points, (double)i / numSegments);
		}
		
		return result;
	}
	
	public Vector3 decasteljauRec(Vector3[] points, double t) {
		if (points.length == 1) {
			return points[0];
		}
		else {
			Vector3[] pointsPrev = new Vector3[points.length - 1];
			for (int i = 0; i < points.length - 1; i++) {
				pointsPrev[i] = points[i];
			}
			
			Vector3[] pointsEnd = new Vector3[points.length - 1];
			for (int i = 1; i < points.length; i++) {
				pointsEnd[i-1] = points[i];
			} 
			
			Vector3 result1 = decasteljauRec(pointsPrev, t);
			Vector3 result2 = decasteljauRec(pointsEnd, t);
			
			return result1.scalarMultiply((1 - t)).add(result2.scalarMultiply(t));
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
	
	/**
	 * Draws vector on the screen.
	 * 
	 * <p>
	 * The formula is same as below:
	 * r = offset + (0 ~ 1)vec
	 * </p>
	 * 
	 * @param offset Position vector that the vector will be drawn from
	 * @param vec Direction vector(?) to be drawn
	 * @param color HTML color value
	 * @return No return value
	 */
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
	
	public void drawLine(Vector3 p1, Vector3 p2, int color) {
		drawVector(p1, p2.subtract(p1), color);
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

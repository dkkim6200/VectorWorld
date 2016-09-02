package vector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class World {
	public static final String TEAPOT_FILE_DIRECTORY = "/Users/DaekunKim/Documents/Programming Related/Eclipse Projects/VectorWorld/vector/assets/teapot";
	public static final String TEACUP_FILE_DIRECTORY = "/Users/DaekunKim/Documents/Programming Related/Eclipse Projects/VectorWorld/vector/assets/teacup";
	
	private Vector3[] teapotVertices;
	private int[][] teapotPatches;
	
	private int numTeapotVertices;
	private int numTeapotPatches;
	
	private double rotation;
	private Vector3 rotationAxis;
	private double scale;
	private Vector3 translation;
	
	public World() {
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
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		rotation = 0;
		rotationAxis = new Vector3(1, 1, 1);
		scale = 50;
		translation = new Vector3(200, 200, 0);
	}
	
	/**
	 * Updates the world and its objects, and renders it on renderer
	 * @param renderer Renderer object for the objects in the world to be rendered to
	 */
	public void update(Renderer renderer) {
		Vector3[] manipulatedVertices = new Vector3[teapotVertices.length];
		
		for (int i = 0; i < manipulatedVertices.length; i++) {
			manipulatedVertices[i] = teapotVertices[i].scalarMultiply(scale);
			manipulatedVertices[i].rotate(rotationAxis, rotation);
			manipulatedVertices[i] = manipulatedVertices[i].add(translation);
		}
		
		Mesh teapotMesh = new Mesh(manipulatedVertices, teapotPatches);
		
		renderer.drawMesh(teapotMesh);
		rotation += 90.0 / Display.FPS;
	}
}

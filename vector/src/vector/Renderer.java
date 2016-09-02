package vector;

public class Renderer {
	public static final int GRID_SIZE = 50;
	
	private final int width;
	private final int height;
	public int[][] pixels;
	
	public Renderer(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new int[width][height];
	}
	
	public void render() {
		clear();
		renderGrid(GRID_SIZE);
	}
	
	public void draw(int x, int y, int color) {
		if (x < width && x > 0 &&
				y < height && y > 0) {
				pixels[x][height - y] = color;
			}
	}
	
	public void clear() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				draw(i, j, Color.COLOR_WHITE);
			}
		}
	}
	
	public void renderGrid(int gridSize) {
		for (int i = 0; i < width / gridSize; i++) {
			renderVector(new Vector3(i * gridSize, 0, 0), new Vector3(0, height, 0), Color.COLOR_GREEN);
		}
		
		for (int i = 0; i < height / gridSize; i++) {
			renderVector(new Vector3(0, i * gridSize, 0), new Vector3(width, 0, 0), Color.COLOR_GREEN);
		}
	}
	
	/**
	 * Renders vector on the screen.
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
	public void renderVector(Vector3 offset, Vector3 vec, int color) {
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
	
	/**
	 * Renders line on the screen.
	 * 
	 * <p>
	 * This is a wrapper function of Renderer::renderVector(Vector3, Vector3, int).
	 * </p>
	 * 
	 * @param p1 Point where the line starts from
	 * @param p2 Point where the line ends
	 * @param color HTML color value
	 * @return No return value
	 */
	public void renderLine(Vector3 p1, Vector3 p2, int color) {
		renderVector(p1, p2.subtract(p1), color);
	}
	
	/**
	 * Evaluates the point on Bezier curve with parameter (t)
	 * 
	 * <p>
	 * Source
	 * 1. https://www.scratchapixel.com/lessons/advanced-rendering/bezier-curve-rendering-utah-teapot/bezier-curve
	 * </p>
	 * 
	 * @param p
	 * @param t
	 * @return Vector of point on Bezier curve with parameter (t)
	 */
	public Vector3 getBezierCurve(Vector3[] p, double t) {
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
	
	/**
	 * Evaluates the point on Bezier patch with parameter (u, v)
	 * 
	 * <p>
	 * Source
	 * 1. https://www.scratchapixel.com/lessons/advanced-rendering/bezier-curve-rendering-utah-teapot/bezier-surface
	 * </p>
	 * 
	 * @param controlPoints Bezier control points
	 * @param u u variable contained in range [0, 1]
	 * @param v v variable contained in range [0, 1]
	 * @return Vector of point on Bezier patch with parameter (u, v)
	 */
	Vector3 getBezierPatch(Vector3[] controlPoints, double u, double v) { 
	    Vector3[] uCurve = new Vector3[4];
	    
	    Vector3[] temp = new Vector3[4];
	    for (int i = 0; i < 4; i++){
	    	for (int j = 0; j < 4; j++) {
	    		temp[j] = controlPoints[i * 4 + j];
	    	}
	    	
	    	uCurve[i] = getBezierCurve(temp, u); 
	    }
	    
	    return getBezierCurve(uCurve, v); 
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
	void drawMesh(Mesh mesh) {
		int divs = 16; // Must be >= 16
		
		Vector3[] controlPoints = new Vector3[divs];
		Vector3[] resultVertices = new Vector3[(divs + 1) * (divs + 1)];
		int[][] vertexIndex = new int[divs * divs][4];
		
		for (int i = 0; i < mesh.patches.length; i++) { // numTeapotPatches
			for (int j = 0; j < mesh.patches[i].length; j++) {
				controlPoints[j] = mesh.vertices[mesh.patches[i][j] - 1];
			}
			
			for (int j = 0, k = 0; j < divs + 1; j++) {
				for (int l = 0; l < divs + 1; l++, k++) {
					resultVertices[k] = getBezierPatch(controlPoints, l / (double)divs, j / (double)divs); 
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
					renderLine(resultVertices[vertexIndex[j][k]],
							resultVertices[vertexIndex[j][k + 1]], Color.COLOR_BLACK);
				}
			}
		}
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
}

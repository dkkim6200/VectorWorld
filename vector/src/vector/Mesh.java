package vector;

public class Mesh {
	public Vector3[] vertices;
	public int[][] patches;
	
	public Mesh() {
		this.vertices = null;
		this.patches = null;
	}
	
	public Mesh(Vector3[] vertices, int[][] patches) {
		this.vertices = vertices;
		this.patches = patches;
	}
}

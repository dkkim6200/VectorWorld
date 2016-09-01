package vector;

public class Matrix {
	protected int numRow;
	protected int numCol;
	
	protected double[][] matrix;
	
	public Matrix(int row, int col) {
		this.numRow = row;
		this.numCol = col;
		
		matrix = new double[row][col];
		
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				this.matrix[i][j] = 0.0;
			}
		}
	}
	
	public Matrix(int row, int col, double... args) {
		if (args.length == row * col) {
			this.numRow = row;
			this.numCol = col;
			
			matrix = new double[row][col];
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < col; j++) {
					matrix[i][j] = args[i * col + j];
				}
			}
		} else {
			System.out.println("===== ERROR: Matrix cannot be initialized =====");
			return;
		}
	}
	
	public String toString() {
		String s = "";
		
		for (int i = 0; i < numRow; i++) {
			s += "[";
			for (int j = 0; j < numCol; j++) {
				s += " " + matrix[i][j] + " ";
			}
			s += "]\n";
		}
		
		return s;
	}
	
	public double get(int row, int col) {
		return matrix[row][col];
	}
	
	public void set(int row, int col, double value) {
		matrix[row][col] = value;
	}
	
	public int getNumRow() {
		return numRow;
	}
	
	public int getNumCol() {
		return numCol;
	}
	
	public boolean isSquare() {
		return (numRow == numCol);
	}
	
	public Matrix add(Matrix mat) {
		if (numRow != mat.numRow || numCol != mat.numCol) {
			return null;
		}
		
		Matrix result = new Matrix(numRow, numCol);
		
		for (int i = 0; i < numRow; i++) {
			for (int j = 0; j < numCol; j++) {
				result.matrix[i][j] = this.matrix[i][j] + mat.matrix[i][j];
			}
		}
		
		return result;
	}
	
	public Matrix subtract(Matrix mat) {
		if (numRow != mat.numRow || numCol != mat.numCol) {
			return null;
		}
		
		Matrix result = new Matrix(numRow, numCol);
		
		for (int i = 0; i < numRow; i++) {
			for (int j = 0; j < numCol; j++) {
				result.matrix[i][j] = this.matrix[i][j] - mat.matrix[i][j];
			}
		}
		
		return result;
	}
	
	public Matrix multiply(Matrix mat) {
		if (numCol != mat.numRow) {
			return null;
		}
		
		int m = numRow;
		int n = mat.numCol;
		
		Matrix result = new Matrix(m, n);
		
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < numCol; k++) {
					result.matrix[i][j] += this.matrix[i][k] * mat.matrix[k][j];
				}
			}
		}
		
		return result;
	}
	
	public Matrix multiply(Vector3 vec) {
		return multiply(new Matrix(3, 1,
								   vec.getX(),
								   vec.getY(),
								   vec.getZ()));
	}
}

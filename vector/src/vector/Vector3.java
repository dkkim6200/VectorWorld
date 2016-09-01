package vector;

public class Vector3 {
	protected double x;
	protected double y;
	protected double z;
	
	/**
	 * Initializes new Vector3 object as a zero vector;
	 * 
	 * @return Returns new Vector3 object.
	 */
	public Vector3() {
		x = 0;
	    y = 0;
	    z = 0;
	}
	/**
	 * Initializes new Vector3 object as a zero vector;
	 * 
	 * @param x X-value of vector
	 * @param y Y-value of vector
	 * @param z Z-value of vector
	 * @return Returns new Vector3 object.
	 */
	public Vector3(double x, double y, double z) {
		this.x = x;
	    this.y = y;
	    this.z = z;
	}
	
	public String toString() {
		String str = "";
		
		str += "<" + x + ", " + y + ", " + z + ">";
		
		return str;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public Vector3 add(Vector3 vec) {
		return new Vector3(x + vec.getX(), y + vec.getY(), z + vec.getZ());
	}
	
	public Vector3 subtract(Vector3 vec) {
		return new Vector3(x - vec.getX(), y - vec.getY(), z - vec.getZ());
	}
	
	public Vector3 scalarMultiply(double s) {
		return new Vector3(x * s, y * s, z * s);
	}
	
	public double getMagnitude() {
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	public Vector3 getNormalized() {
		return new Vector3(x / getMagnitude(), y / getMagnitude(), z / getMagnitude());
	}
	
	/**
	 * Returns the dot product of this and 'vec';
	 * 
	 * <p>
	 * I could have used Matrix, but that is not necessary
	 * since this is quite simple algorithm.
	 * </p>
	 * @param  vec The vector to be dotted with this vector.
	 * @return Dot product of this and 'vec'.
	 */
	public double dot(Vector3 vec) {
		return x * vec.getX() + y * vec.getY() + z * vec.getZ();
	}
	
	/**
	 * Returns the cross product of this and 'vec';
	 * 
	 * @param  vec The vector to be crossed with this vector.
	 * @return Cross product of this and 'vec'.
	 */
	public Vector3 cross(Vector3 vec) {
		return new Vector3(y * vec.z - z * vec.y,
							z * vec.x - x * vec.z,
							x * vec.y - y * vec.x);
	}
	
	public double scalarProject(Vector3 vec) {
		return dot(vec) / vec.getMagnitude();
	}
	
	public Vector3 vectorProject(Vector3 vec) {
		return vec.scalarMultiply(this.dot(vec) / vec.dot(vec));
	}
	
	public double angle(Vector3 vec) {
		double cosine = this.dot(vec) / (this.getMagnitude() * vec.getMagnitude());
	    return Math.acos(cosine);
	}
	
	/**
	 * Rotates this vector.
	 * 
	 * <p>
	 * The rotation matrix used (from [2]):<br>
	 * <br>
	 * [ cosA  -sinA  0  ][ cosB  0 sinB ][ 1   0      0  ][ x ]<br>
	 * [ sinA  cosA   0  ][  0    1  0   ][ 0  cosY -sinY ][ y ]<br>
	 * [ 0     0      1  ][ -sinB 0 cosB ][ 0  sinY  cosY ][ z ]<br>
	 * <br>
	 * whose yaw(around Z-axis), pitch(around Y-axis) and roll(around X-axis) angles are A(alpha), B(beta) and Y(gamma).<br>
	 * WHY IS THE ROTATION MATRIX ABOVE NOT WORKING?????<br>
	 * </p>
	 * 
	 * <p>
	 * For now, I'm rotating the vector around Z-axis, then Y-axis, then X-axis.
	 * </p>
	 * 
	 * <p>
	 * Sources<br>
	 * 1. http://stackoverflow.com/questions/5207708/rotation-in-a-vector2d-class-in-java<br>
	 * 2. https://en.wikipedia.org/wiki/Rotation_matrix<br>
	 * 3. http://mathworld.wolfram.com/RotationMatrix.html<br>
	 * 4. http://www.iue.tuwien.ac.at/phd/wessner/node42.html<br>
	 * 5. http://www.nh.cas.cz/people/lazar/celler/online_tools.php?start_vec=100,100,100&rot_ax=60,30,20&rot_ang=150
	 * 6. https://docs.unity3d.com/ScriptReference/Transform.Rotate.html
	 * <b>CAUTION: It alters the current vector.</b>
	 * </p>
	 * 
	 * @param  xAngle Amount of x angle to rotate this vector in degrees.
	 * @param  yAngle Amount of y angle to rotate this vector in degrees.
	 * @param  zAngle Amount of z angle to rotate this vector in degrees.
	 * @return No return value.
	 */
	public void rotate(double xAngle, double yAngle, double zAngle) {
		rotate(new Vector3(0, 0, 1), xAngle);
		rotate(new Vector3(0, 1, 0), yAngle);
		rotate(new Vector3(1, 0, 0), zAngle);
	}
	
	public void rotate(Vector3 axis, double angle) {
		axis = axis.getNormalized();
		angle = angle * Math.PI / 180.0;
		
		Matrix rotationMat = new Matrix (3, 3,
				Math.cos(angle) + axis.x*axis.x * (1 - Math.cos(angle)),			 	axis.x * axis.y * (1 - Math.cos(angle)) - axis.z * Math.sin(angle), 	axis.x * axis.z * (1 - Math.cos(angle)) + axis.y * Math.sin(angle),
				axis.y * axis.x * (1 - Math.cos(angle)) + axis.z * Math.sin(angle),	 	Math.cos(angle) + axis.y*axis.y * (1 - Math.cos(angle)),			 	axis.y * axis.z * (1 - Math.cos(angle)) - axis.x * Math.sin(angle),
				axis.z * axis.x * (1 - Math.cos(angle)) - axis.y * Math.sin(angle),	 	axis.z * axis.y * (1 - Math.cos(angle)) + axis.x * Math.sin(angle), 	Math.cos(angle) + axis.z*axis.z * (1 - Math.cos(angle)));
		
		
		Matrix rotatedMat = rotationMat.multiply(this);
		
		x = rotatedMat.get(0, 0);
		y = rotatedMat.get(1, 0);
		z = rotatedMat.get(2, 0);
	}
}

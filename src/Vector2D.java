
public class Vector2D {
	public static Vector polar(double r, double theta) {
		return new Vector(r * Math.cos(theta), r * Math.sin(theta));
	}
	
	public static Vector rotate(Vector vector, double theta) {
		double angle = angle(vector);
		double magnitude = vector.magnitude();
		
		double newAngle = angle + theta;
		return polar(magnitude, newAngle);
	}
	
	public static double angle(Vector vector) {
		return Math.atan2(vector.getComponent(1), vector.getComponent(0));
	}
}

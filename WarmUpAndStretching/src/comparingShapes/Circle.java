package comparingShapes;

/**
 * A simple circle.
 *
 * @author Matt Boutell. Created Dec 1, 2013.
 */
public class Circle implements Comparable<Circle> {
	// DONE: Make this Circle comparable to other Circles so it can be sorted.
	// That is,
	// implement the Comparable<Circle> interface.

	private double radius;

	/**
	 * Creates a circle with the given radius
	 * 
	 * @param radius
	 */
	public Circle(double radius) {
		this.radius = radius;
	}

	/**
	 * @return The area of this circle.
	 */
	public double area() {
		return Math.PI * this.radius * this.radius;
	}

	/**
	 * @return The circumference of this circle.
	 */
	public double perimeter() {
		return 2 * Math.PI * this.radius;
	}

	@Override
	public String toString() {
		return String.format("Circle with r=%.2f", this.radius);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Circle)) {
			return false;
		}
		Circle other = (Circle) obj;
		return this.radius == other.radius;
	}

	// compares circles based on their area. returns 1 if given Circle's
	// area is larger than this.area(). returns 0 if they are equal.
	// Returns -1 if it is less than this.area().
	@Override
	public int compareTo(Circle o) {
		if (o.area() > this.area()) {
			return -1;
		} else if (o.area() == this.area()) {
			return 0;
		}
		return 1;
	}

}
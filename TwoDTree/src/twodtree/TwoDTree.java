package twodtree;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * A 2D Tree implementation.
 * 
 * @author Matt Boutell and TODO: You!
 */
public class TwoDTree {
	/*
	 * TODO: Directions: Implement the methods below. See the specifications for
	 * details.
	 */
	private Node root;
	private Point2 nearestFound = null;
	public final Node NULL_NODE = new Node();

	/** For drawing the plane. */
	public static final double DOT_RADIUS = 5;
	private int planePanelWidth;
	private int planePanelHeight;

	// For drawing the tree
	private static final int MARGIN = 30;
	private static final double RADIUS_SCALE_FACTOR = 0.75;
	private static final double FONT_SCALE_FACTOR = 1.5;
	private static final double DIRECTION_TYPE_SCALE_FACTOR = 1.1;
	private int treePanelWidth;
	private int treePanelHeight;
	// The number of pixels horizontally and vertically between nodes.
	private int xStep, yStep;
	private double radius;
	// font to use for labeling nodes
	private Font font;
	private int fontSize;

	/**
	 * Constructs an empty tree.
	 * 
	 */
	public TwoDTree() {
		this(0, 0, 0, 0); // When called within params, it won't be visualized
	}

	/**
	 * Constructs an empty tree.
	 * 
	 */
	public TwoDTree(int planePanelWidth, int planePanelHeight, int treePanelWidth, int treePanelHeight) {
		root = NULL_NODE;
		this.planePanelWidth = planePanelWidth;
		this.planePanelHeight = planePanelHeight;
		this.treePanelWidth = treePanelWidth;
		this.treePanelHeight = treePanelHeight;
	}

	/**
	 * Inserts the given point into the tree
	 * 
	 * @param p
	 *            A point to insert.
	 */
	public void insert(Point2 p, String label) {
		// TODO: write this.
		if (this.root == NULL_NODE) {
			RectHV rootBounds = new RectHV(0, 0, 1, 1); // change???
			this.root = new Node(p.x, p.y, label, Direction.X, rootBounds, 0);
			this.root.bottomRight = NULL_NODE;
			this.root.topLeft = NULL_NODE;
			return;
		} // else

		// with root, compare the X
		if (this.root.p.x < p.x) {
			this.root.bottomRight = this.root.bottomRight.insert(p, label, 1, this.root.bounds, this.root.p);
		} else {// else insert on the left
			this.root.topLeft = this.root.topLeft.insert(p, label, 1, this.root.bounds, this.root.p);
		}

	}

	/**
	 * Checks to see if the given query point is in this tree.
	 * 
	 * @param q
	 *            Query point.
	 * @return True if and only if the query point is in this tree.
	 * 
	 */
	public boolean contains(Point2 q) {
		return this.root.contains(q, 1);
	}

	/**
	 * Finds the closest point in the tree to the query point.
	 * 
	 * @param q
	 *            The query point
	 * @throws IllegalStateException.
	 *             If the tree is empty.
	 * @return The closest point
	 */
	public Point2 nearestNeighbor(Point2 q) throws IllegalStateException {
		if (this.root == NULL_NODE) {
			throw new IllegalStateException();
		}

		this.nearestFound = this.root.nearestNeighbor(q, this.root.p, this.root.p.distanceTo(q));
		return this.nearestFound;
	}

	public void drawTree(Graphics2D g) {
		int nodeCountPlusOne = root.setInOrderNumbers(1);
		this.xStep = (this.treePanelWidth - 2 * MARGIN) / nodeCountPlusOne;
		this.yStep = (this.treePanelHeight - 2 * MARGIN) / (height() + 2);
		this.radius = ((xStep < yStep) ? xStep : yStep) * RADIUS_SCALE_FACTOR;
		this.fontSize = (int) (radius * FONT_SCALE_FACTOR * 96 / 72);
		this.font = new Font("Monospaced", Font.BOLD, fontSize);
		root.drawTree(g, -1, -1);
	}

	public void clear() {
		root = NULL_NODE;
		nearestFound = null;
	}

	@Override
	public String toString() {
		if (root == NULL_NODE) {
			return "()";
		}
		StringBuilder sb = new StringBuilder();
		root.buildString(sb);
		return sb.toString();
	}

	public void draw(Graphics2D g2, double minX, double maxX, double minY, double maxY) {
		root.drawInPlane(g2, minX, maxY, minY, maxY);

		if (nearestFound != null) {
			Ellipse2D.Double nodeDot = new Ellipse2D.Double(screenX(nearestFound.x) - DOT_RADIUS,
					screenY(nearestFound.y) - DOT_RADIUS, DOT_RADIUS * 2, DOT_RADIUS * 2);
			g2.setColor(Color.RED);
			g2.fill(nodeDot);
		}
	}

	private int screenX(double x) {
		return (int) (x * planePanelWidth);
	}

	private int screenY(double y) {
		return (int) (y * planePanelHeight);
	}

	private int height() {
		return root.height();
	}

	enum Direction {
		X, Y
	}

	public class Node {
		// The two data fields: a label and a point
		public String label;
		public Point2 p;

		// Children
		Node topLeft;
		Node bottomRight;
		public Direction dir;
		// Each node knows the bounds it is part of. Helpful when searching.
		public RectHV bounds;

		// For tree drawing
		private int depth;
		private int inOrderNumber;

		// This one is used by the NULL_NODE.
		public Node() {
			// do nothing
		}

		public Node(Point2 p) {
			if (p == null) {
				return;
			}
			this.p = new Point2(p);
			this.topLeft = NULL_NODE;
			this.bottomRight = NULL_NODE;
			this.bounds = null;
		}

		// You will probably use this when writing insert()
		public Node(double x, double y, String label, Direction dir, RectHV bounds, int depth) {
			this.p = new Point2(x, y);
			this.label = label;
			this.dir = dir;
			this.topLeft = NULL_NODE;
			this.bottomRight = NULL_NODE;
			this.bounds = bounds;
			this.depth = depth;
		}

		/**
		 * returns the nearestNeighbor of the chian of nodes to the given point
		 *
		 * @param q
		 *            -- point your comparing to
		 * @param level
		 *            -- the level which the node is on
		 * @return
		 */
		public Point2 nearestNeighbor(Point2 q, Point2 closest, double smallDistance) {
			if (this.topLeft == NULL_NODE && this.bottomRight == NULL_NODE) {
				return closest;
			}
			double smallestDistance = smallDistance;
			Point2 smallestPoint = closest;
			if (this.topLeft != NULL_NODE) {
				double leftDistance = this.topLeft.bounds.distanceTo(q);
				if (leftDistance < smallDistance) {
					double leftD = this.topLeft.p.distanceTo(q);
					if(leftD < smallDistance) {
						smallestDistance = leftD;
						smallestPoint = this.topLeft.p;
					}
					smallestPoint = this.topLeft.nearestNeighbor(q, smallestPoint, smallestDistance);
					
				}
			}
			if(this.bottomRight != NULL_NODE) {
				double rightDistance = this.bottomRight.bounds.distanceTo(q);
				if(rightDistance < smallestDistance) {
					double rightD = this.bottomRight.p.distanceTo(q);
					if(rightD < smallestDistance) {
						smallestDistance = rightD;
						smallestPoint = this.bottomRight.p;
					}
					smallestPoint = this.bottomRight.nearestNeighbor(q, smallestPoint, smallestDistance);
				}
			}
			//else 
			return smallestPoint;
			
		}

		/**
		 * returns true if point q is contain in the chain of nodes else it
		 * returns falase
		 *
		 * @param q
		 * @return
		 */
		public boolean contains(Point2 q, int level) {
			if (this == NULL_NODE) {
				return false;
			}
			if (q.equals(this.p)) {
				return true;
			}

			if (level % 2 == 0) {// compare to Y values
				if (q.y < this.p.y) {
					return this.topLeft.contains(q, level + 1);
				} // else
				return this.bottomRight.contains(q, level + 1);
			} // else compare to X values
			if (q.x < this.p.x) {
				return this.topLeft.contains(q, level + 1);
			} // else
			return this.bottomRight.contains(q, level + 1);
		}

		/**
		 * inserts a node into the tree at the correct spot
		 *
		 * @param p2
		 *            -- point to insert
		 * @param label2
		 *            -- label to insert
		 * @param level
		 *            -- level of the tree you are at
		 * @param bounds
		 *            -- bounds of the parent
		 * @param parent
		 *            -- the parent point
		 * @return
		 */
		public Node insert(Point2 p2, String label2, int level, RectHV bounds, Point2 parent) {
			if (this == NULL_NODE) {
				// this becomes a new Node
				if (level % 2 == 0) { // Direction is Y
					if (p2.y < parent.y) {// p2 is up/left of parent
						RectHV newBounds = new RectHV(bounds.xmin(), bounds.ymin(), bounds.xmax(), parent.y);
						return new Node(p2.x, p2.y, label2, Direction.X, newBounds, level);
					} // else p2 is down/right of parent

					RectHV newBounds = new RectHV(bounds.xmin(), parent.y, bounds.xmax(), bounds.ymax());
					return new Node(p2.x, p2.y, label2, Direction.X, newBounds, level);
				} // else level is on a X directon

				if (p2.x < parent.x) { // p2 is up/left of parent
					RectHV newBounds = new RectHV(bounds.xmin(), bounds.ymin(), parent.x, bounds.ymax());
					return new Node(p2.x, p2.y, label2, Direction.Y, newBounds, level);
				} // else p2 is down/right of parent

				RectHV newBounds = new RectHV(parent.x, bounds.ymin(), bounds.xmax(), bounds.ymax());
				return new Node(p2.x, p2.y, label2, Direction.Y, newBounds, level);
			} // else find a place to insert

			if (level % 2 == 0) {// compare X
				if (p2.x > this.p.x) {// go right
					this.bottomRight = this.bottomRight.insert(p2, label2, level + 1, this.bounds, this.p);
				} else if (p2.x < this.p.x) {
					this.topLeft = this.topLeft.insert(p2, label2, level + 1, this.bounds, this.p);
				} else {
					// they are equal
					// deal with this case later
				}
			} else {// else compare Y
				if (p2.y > this.p.y) {
					this.bottomRight = this.bottomRight.insert(p2, label2, level + 1, this.bounds, this.p);
				} else if (p2.y < this.p.y) {
					this.topLeft = this.topLeft.insert(p2, label2, level + 1, this.bounds, this.p);
				} else {
					// they are equal
					// take care of this case later
				}
			}
			return this;

		}

		private void buildString(StringBuilder sb) {
			if (this == NULL_NODE) {
				return;
			}
			if (topLeft != NULL_NODE) {
				sb.append("(");
				topLeft.buildString(sb);
				sb.append(")");
			}
			sb.append(String.format("%s(%4.2f,%4.2f)", label, p.x, p.y));
			if (bottomRight != NULL_NODE) {
				sb.append("(");
				bottomRight.buildString(sb);
				sb.append(")");
			}
		}

		private void drawInPlane(Graphics2D g2, double minX, double maxX, double minY, double maxY) {
			if (this == NULL_NODE) {
				return;
			}

			// Dot
			Ellipse2D.Double nodeDot = new Ellipse2D.Double(screenX(p.x) - DOT_RADIUS, screenY(p.y) - DOT_RADIUS,
					DOT_RADIUS * 2, DOT_RADIUS * 2);
			g2.fill(nodeDot);

			// Label
			int xOffset = this.dir == Direction.X ? 10 : 0;
			int yOffset = this.dir == Direction.X ? 0 : 20;
			g2.drawString(label, (int) screenX(p.x) + xOffset, (int) screenY(p.y) + yOffset);

			if (dir == Direction.X) {
				// Draw vertical line from (x, minY) to (x, maxY)
				Line2D.Double line = new Line2D.Double(new Point2D.Double(screenX(p.x), screenY(minY)),
						new Point2D.Double(screenX(p.x), screenY(maxY)));
				g2.draw(line);
				topLeft.drawInPlane(g2, minX, p.x, minY, maxY);
				bottomRight.drawInPlane(g2, p.x, maxX, minY, maxY);
			} else {
				// VERTICAL separation, so draw horizontal line
				Line2D.Double line = new Line2D.Double(new Point2D.Double(screenX(minX), screenY(p.y)),
						new Point2D.Double(screenX(maxX), screenY(p.y)));
				g2.draw(line);
				topLeft.drawInPlane(g2, minX, maxX, minY, p.y);
				bottomRight.drawInPlane(g2, minX, maxX, p.y, maxY);
			}
		}

		private void drawTree(Graphics2D g, double parentX, double parentY) {
			if (this == NULL_NODE) {
				return;
			}

			double centerX = this.inOrderNumber * xStep + MARGIN;
			double centerY = (this.depth + 1) * yStep + MARGIN;

			if (parentX > 0 && parentY > 0) {
				// Draws line
				g.setColor(Color.BLACK);
				double deltaX = parentX - centerX;
				double deltaY = parentY - centerY;
				double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
				double xOffset = deltaX * radius / distance;
				double yOffset = deltaY * radius / distance;
				Point2D.Double selfEdge = new Point2D.Double(centerX + xOffset, centerY + yOffset);
				Point2D.Double parentEdge = new Point2D.Double(parentX - xOffset, parentY - yOffset);
				g.draw(new Line2D.Double(selfEdge, parentEdge));
			}

			// Draws the circle
			Ellipse2D.Double circ = new Ellipse2D.Double(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
			g.setColor(Color.WHITE);
			g.fill(circ);
			g.setColor(Color.BLACK);
			g.draw(circ);

			// Labels the circle
			g.setFont(font);
			// coefficients on radius determined experimentally
			g.drawString(label.toString(), (int) (centerX - 0.5 * radius), (int) (centerY + 0.6 * radius));

			// Direction
			Point2D.Double first = new Point2D.Double(centerX, centerY);
			Point2D.Double second = new Point2D.Double(centerX, centerY);
			if (dir == Direction.X) {
				first.y -= radius * DIRECTION_TYPE_SCALE_FACTOR;
				second.y += radius * DIRECTION_TYPE_SCALE_FACTOR;
			} else {
				first.x -= radius * DIRECTION_TYPE_SCALE_FACTOR;
				second.x += radius * DIRECTION_TYPE_SCALE_FACTOR;
			}
			g.setStroke(new BasicStroke(2));
			g.draw(new Line2D.Double(first, second));

			// Draw children
			topLeft.drawTree(g, centerX, centerY);
			bottomRight.drawTree(g, centerX, centerY);
		}

		// The next two are helpers for the drawTree.
		private int height() {
			if (this == NULL_NODE) {
				return -1;
			}
			return Math.max(topLeft.height(), bottomRight.height()) + 1;
		}

		private int setInOrderNumbers(int nextNumber) {
			if (this == NULL_NODE) {
				return nextNumber;
			}
			nextNumber = topLeft.setInOrderNumbers(nextNumber);
			this.inOrderNumber = nextNumber++;
			return bottomRight.setInOrderNumbers(nextNumber);
		}
	}
}
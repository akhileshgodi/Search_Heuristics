
package alviz2.graph;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import javafx.scene.paint.Color;
import javafx.geometry.Point2D;

public class Node {

	private int id;
	private Point2D pos;
	Color fillColor;
	double cost;
	boolean visible;
	public int deg;
	public ArrayList<Edge> orderedEdges;
	public Set<Node> tourNodes;
	public static class PropChanger {
		private Set<Node> changedNodes;

		private PropChanger() {
			changedNodes = new HashSet<Node>();
		}

		public static PropChanger create() {
			return new PropChanger();
		}

		public void setFillColor(Node n, Color c) {
			changedNodes.add(n);
			n.setFillColor(c);
		}

		public void setVisible(Node n, boolean v) {
			changedNodes.add(n);
			n.setVisible(v);
		}

		public void setPosition(Node n, Point2D pt) {
			n.setPosition(pt);
		}

		public Set<Node> getChangedNodes() {
			return changedNodes;
		}

		public void clearChangedNodes() {
			changedNodes.clear();
		}
	}

	public Node(int id) {
		this.id = id;
		pos = new Point2D(0,0);
		fillColor = Color.DIMGRAY;
		cost = 0;
		visible = false;
		deg = 0;
		tourNodes = new HashSet<Node>();
	}

	public int getId() {
		return id;
	}

	public boolean isVisible() {
		return visible;
	}

	private void setVisible(boolean v) {
		visible = v;
	}

	public final double getCost() {
		return cost;
	}

	public final void setCost(double c) {
		cost = c;
	}

	public final Color getFillColor() {
		return fillColor;
	}

	private void setFillColor(Color c) {
		fillColor = c;
	}

	public final Point2D getPosition() {
		return pos;
	}

	private void setPosition(Point2D pt) {
		pos = pt;
	}
	
}
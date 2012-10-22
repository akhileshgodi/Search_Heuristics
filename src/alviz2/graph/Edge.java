
package alviz2.graph;

import java.util.Set;
import java.util.HashSet;

import javafx.scene.paint.Color;
import javafx.geometry.Point2D;

import alviz2.graph.Node;

public class Edge implements Comparable<Edge>{

	private int id;
	private Node ns, nd;
	Color strokeColor;
	double cost;
	boolean visible;

	public static class PropChanger {
		private Set<Edge> changedEdges;

		private PropChanger() {
			changedEdges = new HashSet<>();
		}

		public static PropChanger create() {
			return new PropChanger();
		}

		public void setStrokeColor(Edge n, Color c) {
			changedEdges.add(n);
			n.setStrokeColor(c);
		}

		public void setVisible(Edge e, boolean v) {
			changedEdges.add(e);
			e.setVisible(v);
		}

		public Set<Edge> getChangedEdges() {
			return changedEdges;
		}

		public void clearChangedEdges() {
			changedEdges.clear();
		}
	}

	public Edge(int id, Node s, Node d) {
		this.id = id;
		ns = s;
		nd = d;
		strokeColor = Color.BLACK;
		cost = 0;
		visible = false;
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

	public final Color getStrokeColor() {
		return strokeColor;
	}

	private void setStrokeColor(Color c) {
		strokeColor = c;
	}

	public final Point2D getPositionS() {
		return ns.getPosition();
	}

	public final Point2D getPositionD() {
		return nd.getPosition();
	}

	@Override
	public int compareTo(Edge o) {
		if(cost > o.cost) return 1;
		if(cost < o.cost) return -1;
		return 0;
	}
	
}
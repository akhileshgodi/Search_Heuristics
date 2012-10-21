
package alviz2.app;

import org.jgrapht.Graph;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

import alviz2.graph.Node;
import alviz2.graph.Edge;

class Visualizer {

	private Graph<? extends Node, ? extends Edge> graph;
	private Node.PropChanger npr;
	private Edge.PropChanger epr;
	private Canvas c;
	private double maxX, maxY;

	public Visualizer(Graph<? extends Node, ? extends Edge> g, Node.PropChanger npr, Edge.PropChanger epr) {
		graph = g;
		this.npr = npr;
		this.epr = epr;

		maxX = Double.MIN_VALUE;
		maxY = Double.MIN_VALUE;

		for (Node n: graph.vertexSet()) {
			Point2D p = n.getPosition();
			maxX = maxX > p.getX() ? maxX : p.getX();
			maxY = maxY > p.getY() ? maxY : p.getY();
		}
	}

	public Canvas render(double width, double height) {
		boolean imgNull = c == null;
		boolean heightDiff = c!= null && (c.getHeight() != height || c.getWidth() != width);
		boolean stateChange = (!npr.getChangedNodes().isEmpty() || !epr.getChangedEdges().isEmpty());

		if(imgNull || heightDiff || stateChange) {
			if(imgNull || heightDiff) {
				c = new Canvas(width, height);
			}

			GraphicsContext gc = c.getGraphicsContext2D();
			gc.clearRect(0, 0, width, height);

			gc.save();
			gc.scale(width/(maxX+10), height/(maxY+10));

			for (Edge e: graph.edgeSet()) {
				if(!e.isVisible())
					continue;
				Point2D ps = e.getPositionS();
				Point2D pd = e.getPositionD();
				Point2D nps = new Point2D(ps.getX(), ps.getY());
				Point2D npd = new Point2D(pd.getX(), pd.getY());
				Color c = e.getStrokeColor();
				gc.setStroke(c);
				gc.strokeLine(ps.getX(), ps.getY(), npd.getX(), npd.getY());
			}

			for (Node n: graph.vertexSet()) {
				if(!n.isVisible())
					continue;
				Point2D p = n.getPosition();
				Color c = n.getFillColor();
				gc.setFill(c);
				gc.fillOval(p.getX(), p.getY(), 5, 5);
			}

			gc.restore();
		}

		npr.clearChangedNodes();
		epr.clearChangedEdges();

		return c;
	}
	
}
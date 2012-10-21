
package alviz2.algo;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.VertexFactory;
import org.jgrapht.EdgeFactory;

import alviz2.app.ColorPalette;
import alviz2.graph.Node;
import alviz2.graph.Edge;
import alviz2.util.AlgorithmRequirements;
import alviz2.util.GraphType;
import alviz2.util.GraphInit;

@AlgorithmRequirements (
	graphType = GraphType.ANY_GRAPH,
	graphInitOptions = {GraphInit.START_NODE, GraphInit.MANY_GOAL_NODES}
)

public class DFS implements Algorithm<Node, Edge> {

	private Graph<Node, Edge> graph;
	private XYChart<Number, Number> chart;
	private Node.PropChanger npr;
	private Edge.PropChanger epr;
	private Node start;
	private Set<Node> goals;
	private LinkedList<Node> open;
	private Set<Node> closed;
	private Map<Node, Node> parents;
	private XYChart.Series<Number, Number> openListSeries;
	private XYChart.Series<Number, Number> closedListSeries;
	private int iterCnt;
	private ColorPalette palette;

	public static class VFac implements VertexFactory<Node> {
		int id;
		
		private VFac() {
			id = 0;
		}

		@Override public Node createVertex() {
			return new Node(id++);
		}
	}

	public static class EFac implements EdgeFactory<Node, Edge> {
		int id;

		private EFac() {
			id = 0;
		}

		@Override public Edge createEdge(Node s, Node d) {
			return new Edge(id++, s, d);
		}
	}

	public DFS() {
		graph = null;
		chart = null;
		start = null;
		goals = null;
		open = new LinkedList<Node>();
		closed = new HashSet<Node>();
		parents = new HashMap<Node, Node>();
		openListSeries = new XYChart.Series<Number, Number>();
		closedListSeries = new XYChart.Series<Number, Number>();
		iterCnt = 0;
		palette = ColorPalette.getInstance();
	}

	@Override
	public void setGraph(Graph<Node,Edge> graph, Node.PropChanger npr, Edge.PropChanger epr, Set<Node> start, Set<Node> goals) {
		this.graph = graph;
		this.epr = epr;
		this.npr = npr;
		for (Node n : start) {
			this.start = n;
		}

		for (Node n : graph.vertexSet()) {
			npr.setVisible(n, true);
		}

		for (Edge e : graph.edgeSet()) {
			epr.setVisible(e, true);
		}

		this.goals = goals;
		open.push(this.start);
		parents.put(this.start, null);
		npr.setFillColor(this.start, palette.getColor("node.open"));
	}

	@Override
	public void setChart(XYChart<Number, Number> chart) {
		this.chart = chart;
		this.chart.setTitle("Depth First Search");
		openListSeries.setName("Size of open list");
		closedListSeries.setName("Size of closed list");
		this.chart.getData().add(openListSeries);
		this.chart.getData().add(closedListSeries);
	}

	@Override public VertexFactory<Node> getVertexFactory()
	{
		return new VFac();
	}

	@Override public EdgeFactory<Node,Edge> getEdgeFactory()
	{
		return new EFac();
	}

	@Override
	public boolean executeSingleStep() {

		if (open.isEmpty()) {
			return false;			
		}

		Node n = null;
		while(!open.isEmpty() && (n == null || closed.contains(n))) {
			n = open.pop();
		}

		if (n == null) {
			return false;
		}

		closed.add(n);
		npr.setFillColor(n, Color.BLUE);
		if (parents.containsKey(n) && parents.get(n) != null) {
			epr.setStrokeColor(graph.getEdge(n, parents.get(n)), palette.getColor("edge.closed"));
		}

		if (goals.contains(n)) {
			Node curNode = n;
			Node parent = parents.get(curNode);
			Color edgeColor = palette.getColor("edge.path");
			while (parent != null) {
				epr.setStrokeColor(graph.getEdge(curNode, parent), edgeColor);
				curNode = parent;
				parent = parents.get(curNode);
			}

			return false;
		}

		for (Node nn : Graphs.neighborListOf(graph, n)) {
			if (!closed.contains(nn)) {
				open.push(nn);
				npr.setFillColor(nn, palette.getColor("node.open"));
				epr.setStrokeColor(graph.getEdge(n, nn), palette.getColor("edge.open"));
				parents.put(nn, n);
			}
		}

		openListSeries.getData().add(new XYChart.Data<Number, Number>(iterCnt, open.size()));
		closedListSeries.getData().add(new XYChart.Data<Number, Number>(iterCnt, closed.size()));
		iterCnt++;

		return true;
	}
}
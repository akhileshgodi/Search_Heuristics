
package alviz2.algo;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.VertexFactory;
import org.jgrapht.EdgeFactory;

import javafx.scene.paint.Color;
import javafx.scene.chart.XYChart;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import alviz2.graph.Node;
import alviz2.graph.Edge;
import alviz2.util.*;

@AlgorithmRequirements (
	graphType = GraphType.COMPLETE_GRAPH,
	graphInitOptions = {GraphInit.EDGE_COST}
)
public class GreedyTSP implements Algorithm<Node, Edge> {

	Graph<Node, Edge> gph;
	Color c;
	Node.PropChanger npr;
	Edge.PropChanger epr;
	Node curNode;
	Set<Node> closedSet;

	public GreedyTSP() {
		gph = null;
		npr = null;
		c = Color.RED;
		closedSet = new HashSet<Node>();
	}

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

	@Override public void setGraph(Graph<Node,Edge> graph, Node.PropChanger npr, Edge.PropChanger epr, Set<Node> Start, Set<Node> Goal)
	{
		gph = graph;
		this.npr = npr;
		this.epr = epr;

		for (Node n: gph.vertexSet()) {
			npr.setVisible(n, true);
			curNode = n;
		}
		npr.setFillColor(curNode, Color.BLUE);
		closedSet.add(curNode);
	}

	@Override public void setChart(XYChart<Number,Number> chart)
	{
		return;
	}

	@Override public VertexFactory<Node> getVertexFactory()
	{
		return new VFac();
	}

	@Override public EdgeFactory<Node,Edge> getEdgeFactory()
	{
		return new EFac();
	}

	@Override public boolean executeSingleStep()
	{
		Set<Edge> es = gph.edgesOf(curNode);
		double cost = Double.MAX_VALUE;
		Node nextNode = null;
		Edge minE = null;

		for (Edge e: es) {
			Node tn = gph.getEdgeSource(e) == curNode ? gph.getEdgeTarget(e) : gph.getEdgeSource(e);
			if(cost > e.getCost() && !closedSet.contains(tn)) {
				cost = e.getCost();
				nextNode = tn;
				minE = e;
			}
		}

		if(nextNode == null) {
			return false;
		}
		else {
			epr.setVisible(minE, true);
			epr.setStrokeColor(minE, Color.RED);
			npr.setFillColor(nextNode, Color.BLUE);
			closedSet.add(nextNode);
			curNode = nextNode;
			return true;
		}
	}
}
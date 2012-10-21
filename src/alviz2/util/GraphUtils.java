
package alviz2.util;

import java.util.Random;
import java.util.Map;
import javafx.geometry.Point2D;

import org.jgrapht.Graphs;
import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.SimpleGraph;

import alviz2.graph.Node;
import alviz2.graph.Edge;

public class GraphUtils {

	public static <N extends Node,E extends Edge> Graph<N,E> cloneGraph(Graph<Node,Edge> srcGraph, VertexFactory<N> vfac, EdgeFactory<N,E> efac, Map<Node, N> nodeMap) {
		Graph<N,E> g = new SimpleGraph<N,E>(efac);
		Node.PropChanger npr = Node.PropChanger.create();

		for (Node n: srcGraph.vertexSet()) {
			N nn = vfac.createVertex();
			nn.setCost(n.getCost());
			npr.setPosition(nn, n.getPosition());
			nodeMap.put(n, nn);
		}

		Graphs.addAllVertices(g, nodeMap.values());

		for(Edge e: srcGraph.edgeSet()) {
			N ns = nodeMap.get(srcGraph.getEdgeSource(e));
			N nd = nodeMap.get(srcGraph.getEdgeTarget(e));
			E newE = g.addEdge(ns, nd);
			newE.setCost(e.getCost());
		}

		return g;		
	}
}
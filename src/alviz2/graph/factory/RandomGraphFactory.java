
package alviz2.graph.factory;

import java.util.Random;
import javafx.geometry.Point2D;

import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.generate.RandomGraphGenerator;

import alviz2.graph.Node;
import alviz2.graph.Edge;
import alviz2.util.InputDialog;

public class RandomGraphFactory implements GraphFactory {
	public String getName() {
		return "Random Graph";
	}

	@Override
	public <N extends Node, E extends Edge> Graph<N,E> createGraph(VertexFactory<N> vfac, EdgeFactory<N,E> efac) {
		Integer nodes = InputDialog.getIntegerInput("Random Graph Factory", "No. of Nodes?", 2, Integer.MAX_VALUE);
		if (nodes == null) {
			return null;
		}

		Integer edges = InputDialog.getIntegerInput("Random Graph Factory", "No. of edges?", 1, Integer.MAX_VALUE);
		if (edges == null) {
			return null;
		}

		Graph<N,E> g = new SimpleGraph<N,E>(efac);
		RandomGraphGenerator<N,E> gen = new RandomGraphGenerator<N,E>(nodes, edges);
		gen.generateGraph(g, vfac, null);
		Node.PropChanger npr = Node.PropChanger.create();

		Random r = new Random();
		for (Node n: g.vertexSet()) {
			npr.setPosition(n, new Point2D(r.nextInt(600), r.nextInt(600)));
		}

		return g;
	}
	
}
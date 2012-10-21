
package alviz2.graph.factory;

import java.util.Random;
import javafx.geometry.Point2D;

import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.generate.CompleteGraphGenerator;

import alviz2.graph.factory.GraphFactory;
import alviz2.graph.Node;
import alviz2.graph.Edge;
import alviz2.util.InputDialog;

public class CompleteGraphFactory implements GraphFactory {

	public String getName() {
		return "Complete Graph";
	}

	@Override
	public <N extends Node, E extends Edge> Graph<N,E> createGraph(VertexFactory<N> vfac, EdgeFactory<N,E> efac) {
		Integer inp = InputDialog.getIntegerInput("Complete Graph Factory", "No. of Nodes?", 2, Integer.MAX_VALUE);
		if (inp == null) {
			return null;
		}

		Graph<N,E> g = new SimpleGraph<N,E>(efac);
		CompleteGraphGenerator<N,E> gen = new CompleteGraphGenerator<N,E>(inp);
		gen.generateGraph(g, vfac, null);
		Node.PropChanger npr = Node.PropChanger.create();

		Random r = new Random();
		for (Node n: g.vertexSet()) {
			npr.setPosition(n, new Point2D(r.nextInt(600), r.nextInt(600)));
		}

		return g;
	}
}
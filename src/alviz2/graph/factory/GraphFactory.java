
package alviz2.graph.factory;

import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;
import org.jgrapht.EdgeFactory;

import alviz2.graph.Node;
import alviz2.graph.Edge;

public interface GraphFactory {
	String getName();
	<N extends Node, E extends Edge> Graph<N,E> createGraph(VertexFactory<N> vfac, EdgeFactory<N,E> efac);
}
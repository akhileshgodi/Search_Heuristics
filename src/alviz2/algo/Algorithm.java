
package alviz2.algo;

import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;
import org.jgrapht.EdgeFactory;

import java.util.Set;
import javafx.scene.chart.XYChart;

import alviz2.graph.Node;
import alviz2.graph.Edge;

public interface Algorithm<N extends Node, E extends Edge> {
	void setGraph(Graph<N,E> graph, Node.PropChanger npr, Edge.PropChanger epr, Set<N> Start, Set<N> Goal);
	void setChart(XYChart<Number, Number> chart);
	VertexFactory<N> getVertexFactory();
	EdgeFactory<N,E> getEdgeFactory();
	boolean executeSingleStep();
}
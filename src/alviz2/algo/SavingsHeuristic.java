/**
 * 
 */
package alviz2.algo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;

import alviz2.graph.Edge;
import alviz2.graph.Node;
import alviz2.graph.Node.PropChanger;
import alviz2.util.AlgorithmRequirements;
import alviz2.util.GraphInit;
import alviz2.util.GraphType;

/**
 * @author akhilesh
 *
 */
@AlgorithmRequirements (
		graphType = GraphType.COMPLETE_GRAPH,
		graphInitOptions = {GraphInit.EDGE_COST}
	)

public class SavingsHeuristic implements Algorithm<Node, Edge>{
	
	Graph<Node, Edge> gph;
	Color c;
	Node.PropChanger npr;
	Edge.PropChanger epr;
	Node curNode;
	Node centralNode;
	Set<Node> closedSet;
	private double[][] savings;
	Object[] nodeArray;
	int noOfTours;
	public SavingsHeuristic() {
		// TODO Auto-generated constructor stub
		gph = null;
		npr = null;
		c = Color.RED;
		curNode = null;
		centralNode = null;
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

	@Override
	public void setGraph(Graph<Node, Edge> graph, PropChanger npr,
			Edge.PropChanger epr, Set<Node> Start, Set<Node> Goal) {
		// TODO Auto-generated method stub
		gph = graph;
		this.npr = npr;
		this.epr = epr;
		boolean first = false;
		Node zeroth = null;
		noOfTours = gph.vertexSet().size()-1;
		for (Node n: gph.vertexSet()) {
			System.out.println(n.getId());
			npr.setVisible(n, true);
			curNode = n;
			if(first == false){
				first = true;
				zeroth = curNode;
			}
		}
		curNode = zeroth;
		centralNode = zeroth;
		npr.setFillColor(curNode, Color.BLUE);
		
		nodeArray = gph.vertexSet().toArray();
		for(Edge e : gph.edgesOf(curNode)){
			epr.setVisible(e, true);
			epr.setStrokeColor(e, Color.BLACK);
			Node src = gph.getEdgeSource(e);
			Node dest = gph.getEdgeTarget(e);
			src.tourNodes.add(dest);
			dest.tourNodes.add(src);
		}
		
		savings = new double [gph.vertexSet().size()][gph.vertexSet().size()];
		//Setting up the savings cost matrix
		for(Node i : gph.vertexSet()){
			for(Node j : gph.vertexSet()){
				c = i.getFillColor();
				if(j.equals(i) || (j.equals(centralNode) || i.equals(centralNode))){
					savings[i.getId()][j.getId()] = Double.NEGATIVE_INFINITY;
				}
				else{
					Edge i0 = gph.getEdge(i, centralNode);
					Edge j0 = gph.getEdge(j, centralNode);
					Edge ij = gph.getEdge(i, j);
					savings[i.getId()][j.getId()] = i0.getCost() + j0.getCost() - ij.getCost();
				}
			}
		}
		
		for(int i = 0 ; i < gph.vertexSet().size(); i++){
			for(int j = 0 ; j < gph.vertexSet().size(); j++){
				System.out.printf( savings[i][j]+" ");
			}
			System.out.println();
		}
	}

	@Override
	public void setChart(XYChart<Number, Number> chart) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public VertexFactory<Node> getVertexFactory() {
		// TODO Auto-generated method stub
		return new VFac();
	}

	@Override
	public EdgeFactory<Node, Edge> getEdgeFactory() {
		// TODO Auto-generated method stub
		return new EFac();
	}

	@Override
	public boolean executeSingleStep() {
		// TODO Auto-generated method stub
		
		//	If boiled down to one tour then we are done 
		if(noOfTours == 1)
			return false;
		
		//	Else find the present edge that will give the maximum saving
		int maxI = 0;
		int maxJ = 0;
		double maxSavings = 0;
		for(int i = 0 ; i < gph.vertexSet().size(); i++){
			for(int j = 0 ; j < i; j++){
				if(savings[i][j] > maxSavings){
					maxSavings = savings[i][j];
					maxI = i;
					maxJ = j;
				}
			}
		}
		Node i = (Node)nodeArray[maxI];
		Node j = (Node)nodeArray[maxJ];
		System.out.println(" Before : " + i.tourNodes.size() + " " + j.tourNodes.size());
		Edge i0 = gph.getEdge(i, centralNode);
		Edge j0 = gph.getEdge(j, centralNode);
		if(i0.getStrokeColor() == Color.BLACK && j0.getStrokeColor() == Color.BLACK){
			epr.setStrokeColor(i0, Color.RED);
			epr.setStrokeColor(j0, Color.RED);
			Edge ij = gph.getEdge(i, j);
			epr.setVisible(i0, true);
			epr.setVisible(j0, true);
			epr.setStrokeColor(ij, Color.RED);
			epr.setVisible(ij, true);
			i.tourNodes.addAll(j.tourNodes);
			j.tourNodes.addAll(i.tourNodes);
			i.tourNodes.add(j);
			j.tourNodes.add(i);
			for(Node n : i.tourNodes){
				n.tourNodes.addAll(j.tourNodes);
			}
			for(Node n : j.tourNodes){
				n.tourNodes.addAll(i.tourNodes);
			}
			noOfTours--;
		}
		else if(i0.getStrokeColor() != Color.BLUE && j0.getStrokeColor() != Color.BLUE){
			if(!i.tourNodes.contains(j) && !j.tourNodes.contains(i)){
				Edge ij = gph.getEdge(i, j);
				epr.setStrokeColor(ij, Color.RED);
				epr.setVisible(ij, true);
				i.tourNodes.addAll(j.tourNodes);
				j.tourNodes.addAll(i.tourNodes);
				i.tourNodes.add(j);
				j.tourNodes.add(i);
				for(Node n : i.tourNodes){
					n.tourNodes.addAll(j.tourNodes);
				}
				for(Node n : j.tourNodes){
					n.tourNodes.addAll(i.tourNodes);
				}
				noOfTours--;
				if(i0.getStrokeColor() == Color.RED && j0.getStrokeColor() == Color.BLACK){
					epr.setStrokeColor(i0, Color.BLUE);
					epr.setStrokeColor(j0, Color.RED);
					//epr.setVisible(i0, false);
					//epr.setVisible(j0, true);
					centralNode.tourNodes.remove(i);
				}
				else if(i0.getStrokeColor() == Color.BLACK && j0.getStrokeColor() == Color.RED){
					epr.setStrokeColor(i0, Color.RED);
					epr.setStrokeColor(j0, Color.BLUE);
					//epr.setVisible(i0, true);
					//epr.setVisible(j0, false);
				}
				else {
					epr.setStrokeColor(i0, Color.BLUE);
					epr.setStrokeColor(j0, Color.BLUE);
					//epr.setVisible(i0, false);
					//epr.setVisible(j0, false);
					centralNode.tourNodes.remove(i);
					centralNode.tourNodes.remove(j);
				}
			}
		}
		savings[maxI][maxJ] = 0;
		savings[maxJ][maxI] = 0;
		System.out.println(" After : " + i.tourNodes.size() + " " + j.tourNodes.size());
		
		return true;
		
	}

}

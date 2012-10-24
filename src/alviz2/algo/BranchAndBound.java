package alviz2.algo;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.VertexFactory;
import org.jgrapht.EdgeFactory;

import javafx.scene.paint.Color;
import javafx.scene.chart.XYChart;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;
import java.util.Random;
import java.util.Stack;

import alviz2.graph.Node;
import alviz2.graph.Edge;
import alviz2.util.*;

@AlgorithmRequirements (
	graphType = GraphType.COMPLETE_GRAPH,
	graphInitOptions = {GraphInit.EDGE_COST}
)
public class BranchAndBound implements Algorithm<Node, Edge> {

	Graph<Node, Edge> gph;
	Color sc,rc;
	Node.PropChanger npr;
	Edge.PropChanger epr;
	Node curNode;
	int lowestYet;
	Stack<Node> rem;
	

	public BranchAndBound() {
		gph = null;
		npr = null;
		rc = Color.RED;
		sc = Color.BLACK;
		lowestYet = Integer.MAX_VALUE;
		rem = new Stack<>();
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
		Set<Node> nodes = gph.vertexSet();
		for(Node n:nodes){
			double min = Double.MAX_VALUE, smin = Double.MAX_VALUE;
			Set<Edge> te = gph.edgesOf(n);
			for(Edge e:te){
				if(e.getCost() < min){
					smin = min;
					min = e.getCost();
				}
				else if(e.getCost() < smin) smin = e.getCost();
			}
			n.leastCost = min;
			n.secLeastCost = smin;
		}
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
	
	void bab(Set<Edge> sel, Set<Edge> rej){
		Node curr;
		double lb;
		double currcost = 0;
		while(true){
			curr = rem.pop();
			for (Edge e : gph.edgesOf(curr)) {
				Node other = otherEnd(e, curr);
				if (other.deg < 2) {
					lb = lowerbound(e, sel, rej, currcost);
					if (lb < lowestYet)
						rem.push(other);
				}
			}
			boolean isCycle;
			isCycle = bfs(gph.getEdgeSource(curr), gph.getEdgeTarget(curr), sel);
		}
	}
	
	double lowerbound(Edge e, Set<Edge> sel, Set<Edge> rej,double  currcost){
		double lb = currcost;
		Set<Node> nodes = gph.vertexSet();
		for(Node n:nodes){
			if(n.deg == 0) lb += n.leastCost + n.secLeastCost;
			else if(n.deg == 1) lb += n.leastCost;
		}
		return lb;
	}
	Node otherEnd(Edge e, Node s) {
		Node temps, tempt, other;
		temps = gph.getEdgeSource(e);
		tempt = gph.getEdgeTarget(e);
		other = temps.equals(s) ? tempt : temps;
		return other;
	}	

	boolean bfs(Node start, Node end, Set<Edge> edges){
		Node curr = start;
		//System.out.println("BFS: start " + start.id + " end " + end.id);
		Set<Node> closed = new HashSet<Node>();
		Queue<Node> nq = new LinkedList<Node>();
		nq.add(start);
		closed.add(start);
		while(!nq.isEmpty() && curr != end){
			curr = nq.poll();
			for(Edge e:gph.edgesOf(curr)){
				Node other = otherEnd(e,curr);
				if(edges.contains(e) && !closed.contains(other)) {
					nq.add(other);
					closed.add(other);
				}
			}
		}
		boolean ret = curr.getId() == end.getId();
		System.out.println(curr.getId()+ " " + end.getId() + " " + ret);
		return ret;
	}	
	@Override public boolean executeSingleStep()
	{	
		
		return true;
	}
	
}
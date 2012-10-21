
package alviz2.app;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.effect.BlendMode;
import javafx.scene.canvas.Canvas;
import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

import org.jgrapht.Graph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.VertexFactory;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.graph.SimpleGraph;

import alviz2.algo.Algorithm;
import alviz2.graph.Node;
import alviz2.graph.Edge;
import alviz2.util.*;
import alviz2.graph.factory.*;

public class AlvizController implements Initializable {

	@FXML private BorderPane rootPane;
	@FXML private Button startButton;
	@FXML private Button stopButton;
	@FXML private Button stepButton;
	@FXML private Menu menuAlgo;
	@FXML private Menu menuGraph;
	@FXML private Slider algoUpdCycle;
	@FXML private FlowPane chartPane;
	@FXML private Group canvasPane;

	private List<String> algoList;
	private Class<? extends Algorithm> curAlgo;
	private List<Algorithm<Node,Edge>> activeAlgos;
	private List<Visualizer> activeVizs;
	private Timeline animTimer;
	private Graph<Node,Edge> curGraph;
	private Set<Node> curStartNodes;
	private Set<Node> curGoalNodes;

	private class AlgoMenuHandler implements EventHandler<ActionEvent> {
		@SuppressWarnings("unchecked")
		@Override
		public void handle(ActionEvent e) {
			MenuItem itm = (MenuItem) e.getSource();
			curAlgo = (Class<? extends Algorithm>) itm.getUserData();

			if(curGraph != null) {
				Algorithm<Node, Edge> algo = null;
				try {
					algo = curAlgo.newInstance();
				}
				catch(Exception ex) {
					System.out.println(ex);
				}

				VertexFactory<Node> vfac = algo.getVertexFactory();
				EdgeFactory<Node, Edge> efac = algo.getEdgeFactory();
				HashMap<Node, Node> cloneMap = new HashMap<Node, Node>();
				curGraph = GraphUtils.cloneGraph(curGraph, vfac, efac, cloneMap);
				curAlgo = null;

				Node.PropChanger npr = Node.PropChanger.create();
				Edge.PropChanger epr = Edge.PropChanger.create();
				NumberAxis xAxis = new NumberAxis();
				NumberAxis yAxis = new NumberAxis();
				LineChart<Number, Number> chart = new LineChart<Number, Number>(xAxis, yAxis);
				chart.setCreateSymbols(false);
				chartPane.getChildren().add(chart);

				HashSet<Node> newStartNodes = new HashSet<Node>();
				for (Node n : curStartNodes) {
					newStartNodes.add(cloneMap.get(n));
				}
				HashSet<Node> newGoalNodes = new HashSet<Node>();
				for (Node n : curGoalNodes) {
					newGoalNodes.add(cloneMap.get(n));
				}
				curStartNodes = newStartNodes;
				curGoalNodes = newGoalNodes;

				algo.setGraph(curGraph, npr, epr, curStartNodes, curGoalNodes);
				algo.setChart(chart);
				Visualizer v = new Visualizer(curGraph, npr, epr);
				activeAlgos.add(algo);
				activeVizs.add(v);
				AlvizController.this.updateCanvas();

				menuAlgo.setDisable(true);
				startButton.setDisable(false);
				stepButton.setDisable(false);
				algoUpdCycle.setDisable(false);
			}
			else {
				AlgorithmRequirements ar = curAlgo.getAnnotation(AlgorithmRequirements.class);
				GraphMenuHandler gmh = new GraphMenuHandler();

				menuGraph.getItems().clear();
				Map<String, GraphFactory> gt = ar.graphType().getFactoryMap();
				for (Map.Entry<String, GraphFactory> me: gt.entrySet()) {
					MenuItem gitm = new MenuItem(me.getKey());
					gitm.setUserData(me.getValue());
					gitm.setOnAction(gmh);
					menuGraph.getItems().add(gitm);
				}
				menuGraph.setDisable(false);
			}
		}
	}

	private class GraphMenuHandler implements EventHandler<ActionEvent> {
		@SuppressWarnings("unchecked")
		@Override
		public void	handle(ActionEvent e) {
			menuAlgo.setDisable(true);

			Algorithm<Node,Edge> algo = null;
			try {
				algo = curAlgo.newInstance();
			}
			catch(Exception ex) {
				System.out.println(ex);
			}

			MenuItem itm = (MenuItem) e.getSource();
			GraphFactory gfac = (GraphFactory) itm.getUserData();
			VertexFactory<Node> vfac = algo.getVertexFactory();
			EdgeFactory<Node,Edge> efac = algo.getEdgeFactory();
			curGraph = gfac.createGraph(vfac, efac);
			if (curGraph == null) {
				return;
			}

			PointIndex2D idx = new PointIndex2D();
			for(Node n: curGraph.vertexSet())
				idx.insert(n);
			GraphInputDialog grphInpDlg = new GraphInputDialog(curGraph, idx);

			Node.PropChanger npr = Node.PropChanger.create();
			Edge.PropChanger epr = Edge.PropChanger.create();

			AlgorithmRequirements ar = curAlgo.getAnnotation(AlgorithmRequirements.class);
			for (GraphInit gi: ar.graphInitOptions()) {
				switch(gi) {
					case NODE_COST:
					{
						Random r = new Random();
						for (Node n: curGraph.vertexSet()) {
							n.setCost(r.nextInt(100));
						}
					}
					break;

					case EDGE_COST:
					{
						Random r = new Random();
						for (Edge ed: curGraph.edgeSet()) {
							Point2D sp = ed.getPositionS();
							Point2D dp = ed.getPositionD();
							ed.setCost(sp.distance(dp) + r.nextInt(10));
						}
					}
					break;

					case START_NODE:
					{
						curStartNodes = grphInpDlg.getNodes("Select a Node", 
							"Select a single node for a start", Collections.EMPTY_SET, false);
					}
					break;

					case GOAL_NODE:
					{
						curGoalNodes = grphInpDlg.getNodes("Select a Node",
							"Select a single node for a goal", curStartNodes, false);
					}
					break;

					case MANY_START_NODES:
					{
						curStartNodes = grphInpDlg.getNodes("Select a Node", 
							"Select a single node for a start", Collections.<Node> emptySet(), true);
					}
					break;

					case MANY_GOAL_NODES:
					{
						curGoalNodes = grphInpDlg.getNodes("Select a Node",
							"Select a single node for a goal", curStartNodes, true);
					}
					break; 
				}
			}

			curAlgo = null;

			NumberAxis xAxis = new NumberAxis();
			NumberAxis yAxis = new NumberAxis();
			LineChart<Number, Number> chart = new LineChart<Number, Number>(xAxis, yAxis);
			chart.setCreateSymbols(false);
			//chart.setMaxHeight(200);
			chartPane.getChildren().add(chart);
			algo.setGraph(curGraph, npr, epr, curStartNodes, curGoalNodes);
			algo.setChart(chart);
			Visualizer v = new Visualizer(curGraph, npr, epr);
			activeAlgos.add(algo);
			activeVizs.add(v);
			AlvizController.this.updateCanvas();

			menuGraph.setDisable(true);
			startButton.setDisable(false);
			stepButton.setDisable(false);
			algoUpdCycle.setDisable(false);
		}
	}

	private void updateCanvas() {
		canvasPane.getChildren().clear();
		for (Visualizer v: activeVizs) {
			Canvas c = v.render(1000, 1000);
			c.setBlendMode(BlendMode.DARKEN);
			canvasPane.getChildren().add(c);
		}
	}

	private List<String> getAlgoList() {
		List<String> list = new ArrayList<String>();
		try {
			Path configPath = Paths.get(System.getProperty("alviz2ConfigFile", "./dist/alviz2.config"));
			Scanner sc = new Scanner(configPath);
			while (sc.hasNextLine()) {
				list.add(sc.nextLine());
			}
		}
		catch(Exception ex) {
			System.out.println(ex);
		}
		return list;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		algoList = getAlgoList();

		activeAlgos = new ArrayList<Algorithm<Node, Edge>>();
		activeVizs = new ArrayList<Visualizer>();
		curStartNodes = new HashSet<Node>();
		curGoalNodes = new HashSet<Node>();

		AlgoMenuHandler algoMH = new AlgoMenuHandler();

		menuAlgo.setDisable(true);
		menuGraph.setDisable(true);
		startButton.setDisable(true);
		stopButton.setDisable(true);
		stepButton.setDisable(true);
		algoUpdCycle.setDisable(true);

		for (String clName: algoList) {
			try {
				Class<? extends Algorithm> algo = Class.forName(clName).asSubclass(Algorithm.class);
				MenuItem itm = new MenuItem(algo.getSimpleName());
				itm.setUserData(algo);
				itm.setOnAction(algoMH);
				menuAlgo.getItems().add(itm);
			}
			catch(Exception e) { 
				System.out.println(e);
			}
		}
	}

	private void stepAlgos() {
		ArrayList<Algorithm<Node,Edge>> finAlgo = new ArrayList<Algorithm<Node,Edge>>();

		for (Algorithm<Node,Edge> alg: activeAlgos) {
			if(!alg.executeSingleStep()) {
				finAlgo.add(alg);
			}
		}

		activeAlgos.removeAll(finAlgo);
	}

	@FXML
	private void handleAlgoStart(ActionEvent event) {
		EventHandler<ActionEvent> timerHandler = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				stepAlgos();
				updateCanvas();

				if(activeAlgos.isEmpty()) {
					animTimer.stop();
					animTimer = null;

					startButton.setDisable(true);
					stopButton.setDisable(true);
				}
			}
		};
		animTimer = new Timeline(new KeyFrame(Duration.millis(algoUpdCycle.getValue()), timerHandler));
		animTimer.setCycleCount(Timeline.INDEFINITE);
		animTimer.playFromStart();

		algoUpdCycle.setDisable(true);
		startButton.setDisable(true);
		stopButton.setDisable(false);
		stepButton.setDisable(true);
	}

	@FXML
	private void handleAlgoStop(ActionEvent event) {
		animTimer.stop();
		animTimer = null;

		stepButton.setDisable(false);
		startButton.setDisable(false);
		stopButton.setDisable(true);
		algoUpdCycle.setDisable(false);
	}

	@FXML
	private void handleAlgoStep(ActionEvent event) {
		stepAlgos();
		updateCanvas();

		if(activeAlgos.isEmpty()) {
			startButton.setDisable(true);
			stepButton.setDisable(true);
			stopButton.setDisable(true);
		}
	}

	@FXML
	private void handleFileClose(ActionEvent event) throws Exception {
		Platform.exit();
	}

	@FXML void handleFileNew(ActionEvent event) {
		activeAlgos.clear();
		activeVizs.clear();
		chartPane.getChildren().clear();
		curGraph = null;
		curStartNodes = new HashSet<Node>();
		curGoalNodes = new HashSet<Node>();

		menuAlgo.setDisable(false);
	}

	@FXML void handleFilePipe(ActionEvent event) {
		menuAlgo.setDisable(false);
	}
}

package alviz2.util;

import java.util.Map;
import java.util.HashMap;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import alviz2.graph.factory.*;

public enum GraphType {
	COMPLETE_GRAPH(CompleteGraphFactory.class), ANY_GRAPH(CompleteGraphFactory.class, RandomGraphFactory.class, GameTree.class), 
	GAME_TREE(GameTree.class), TREE(GameTree.class);

	private Map<String, GraphFactory> facMap;

	@SafeVarargs
	GraphType(Class<? extends GraphFactory>... fs) {
		facMap = new HashMap<String, GraphFactory>();

		try {
			for (Class<? extends GraphFactory> f: fs) {
				GraphFactory i = f.newInstance();
				facMap.put(i.getName(), i);
			}
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}

	public Map<String, GraphFactory> getFactoryMap() {
		return facMap;
	}
}
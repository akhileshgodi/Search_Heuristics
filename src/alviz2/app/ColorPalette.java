
package alviz2.app;

import java.util.HashMap;
import javafx.scene.paint.Color;

public class ColorPalette {

	private static ColorPalette palette = null;
	private HashMap<String, Color> colorStateMap;

	private ColorPalette() {
		colorStateMap = new HashMap<String, Color>();
		colorStateMap.put("node.open", Color.RED);
		colorStateMap.put("node.closed", Color.BLUE);
		colorStateMap.put("node.path", Color.GREEN);
		colorStateMap.put("node.relay", Color.DEEPPINK);
		colorStateMap.put("edge.open", Color.RED);
		colorStateMap.put("edge.closed", Color.BLUE);
		colorStateMap.put("edge.path", Color.GREEN);
		palette = this;
	}

	public static ColorPalette getInstance() {
		if (palette == null) {
			palette = new ColorPalette();
			
		}
		return palette;
	}

	public Color getColor(String state) {
		return colorStateMap.get(state);
	}

	public Color getColor(double p) {
		if(p < 0.0 || p > 1.0)
			return null;
		return Color.GREEN.deriveColor(0, p, 1, 1);
	}
	
}
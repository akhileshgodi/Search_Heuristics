
package alviz2.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AlgorithmRequirements {
	GraphType graphType();
	GraphInit[] graphInitOptions();
}
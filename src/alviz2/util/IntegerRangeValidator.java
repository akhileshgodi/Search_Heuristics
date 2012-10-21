
package alviz2.util;

import alviz2.util.InputValidator;

public class IntegerRangeValidator implements InputValidator<Integer> {

	private int min, max;

	public IntegerRangeValidator(int low, int high) {
		min = low;
		max = high;
	}

	@Override
	public boolean validate(Integer o) {
		int t = o;
		if(t >= min && t <= max)
			return true;
		return false;
	}
	
}
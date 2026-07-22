package swing.util;

public class Tool {

	public static boolean checkNumber(String value) {
		boolean isNum = false;
		try {
			Integer.parseInt(value);
			isNum = true;
		} catch (Exception ignored) {
		}
		return isNum;
	}
}

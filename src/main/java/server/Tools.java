package server;

import java.util.regex.Pattern;

/**
 * Tools
 */
public class Tools {
	private static final String REG_NUM = "\\d*";
	private static final Pattern PTN_NUM = Pattern.compile(REG_NUM);

	public static boolean isPureNumber(final String s) {
		return PTN_NUM.matcher(s).matches();
	}
}
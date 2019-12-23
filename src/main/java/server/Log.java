package server;

import java.io.PrintStream;

/**
 * Log
 */
class Log {
	private final static String SEP = " ";
	private final static int COLUMNS = 80;
	private final static String BLOCK_LINE = "=";
	private final static PrintStream out = System.out;
	private final static PrintStream err = System.err;
	public static boolean LOG_INFO = true;
	public static boolean LOG_WARNING = true;
	public static boolean LOG_ERROR = true;
	public static boolean LOG_BLOCK = true;

	public static void info(final String s) {
		if (!LOG_INFO)
			return;
		out.println("INFO: " + getDataTime() + ": " + s);
	}

	public static void warning(final String s) {
		if (!LOG_WARNING)
			return;
		out.println("WARN: " + getDataTime() + SEP + getCallName() + SEP + s);
	}

	public static <T extends Exception> void warning(final T e) {
		warning(e, "");
	}

	public static <T extends Exception> void warning(final T e, final String s) {
		if (!LOG_WARNING)
			return;
		out.println("WARN : " + getDataTime() + SEP + e.getClass() + "@" + getCallName(e) + SEP + s);
	}

	public static void error(final String s) {
		if (!LOG_ERROR)
			return;
		err.println("ERR : " + getDataTime() + SEP + getCallName() + SEP + s);
	}

	public static <T extends Exception> void error(final T e) {
		error(e, "");
	}

	public static <T extends Exception> void error(final T e, final String s) {
		if (!LOG_ERROR)
			return;
		err.println("ERR : " + getDataTime() + SEP + e.getClass() + "@" + getCallName(e) + SEP + s);
	}

	public static void block(final String s) {
		if (!LOG_BLOCK)
			return;
		if (s.length() >= COLUMNS - 4) {
			out.println(repeatString(BLOCK_LINE, COLUMNS));
			out.println(s);
			out.println(repeatString(BLOCK_LINE, COLUMNS));
		} else {
			out.println(repeatString(BLOCK_LINE, (COLUMNS - s.length() - 1) / 2) + " " + s + " "
					+ repeatString(BLOCK_LINE, (COLUMNS - s.length() - 2) / 2));
		}
	}

	private static String repeatString(final String str, final int n) {
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < n; i++) {
			sb.append(str);
		}
		return sb.toString();
	}

	private static String getDataTime() {
		return java.time.Clock.systemUTC().instant().toString();
	}

	private static String getCallName() {
		final StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		final StackTraceElement s = stacktrace[3];
		final String className = s.getClassName();
		final String methodName = s.getMethodName();
		final int lineNumber = s.getLineNumber();
		return "@" + className + "::" + methodName + "[" + lineNumber + "]";
	}

	private static <T extends Exception> String getCallName(T e) {
		final StackTraceElement s = e.getStackTrace()[0];
		final String className = s.getClassName();
		final String methodName = s.getMethodName();
		final int lineNumber = s.getLineNumber();
		return "@" + className + "::" + methodName + "[" + lineNumber + "]";
	}
}
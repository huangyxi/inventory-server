package server;

/**
 * Main
 */
public class Main {
	public static void main(String[] args) {
		var s = new Server(1234);
		s.start();
		// Runtime.getRuntime().addShutdownHook(new Thread(() -> {
		// 	s.close();
		// }));
	}
}

// var console = System.console();
// String idUsername = "sta1ff", password = "staff";
// try {
// System.out.print("ID or USERNAME:");
// idUsername = console.readLine();
// System.out.print("PASSWORD:");
// password = new String(console.readPassword());
// } catch (Exception e) {
// Log.error(e.getMessage());
// }
// LoginType r = LoginType.OTHER_ERROR;
// Login a = null;
// try {
// a = new Login();
// r = a.checkLogin(idUsername, password);
// } catch (Exception e) {
// Log.error(e.getMessage());
// }
// System.out.println(r);
package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Duplex TCP multi-connection multi-thread server
 */
public class Server {
	private static int id = 0;
	private HashMap<Integer, ServerThread> socketList = new HashMap<>();
	private ServerSocket server;
	private static final String EXIT = "exit";

	public static void main(String[] args) {
		int port = 1314;// Integer.parseInt(args[0]);
		new Server(port).start();
	}

	public Server(int port) {
		try {
			this.server = new ServerSocket(port);
			Log.block("TCP SERVER STARTED @ port:" + port);
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
	}

	public void start() {
		new Writer().start();
		try {
			while (true) {
				Socket socket = server.accept();
				Log.info(++id + ": Connected:" + socket.getInetAddress() + ":" + socket.getPort());
				ServerThread thread = new ServerThread(id, socket);
				socketList.put(id, thread);
				thread.run();
			}
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
	}

	public void close() {
		sendAll(EXIT);
		try {
			if (server != null) {
				server.close();
			}
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
		Log.block("SERVER EXITED");
		System.exit(0);
	}

	// Traversing `Map` when updating it (when disconnecting to all) will throw
	// exception, get `id`s before.
	public void sendAll(String data) {
		LinkedList<Integer> list = new LinkedList<>();
		Set<Map.Entry<Integer, ServerThread>> set = socketList.entrySet();
		for (Map.Entry<Integer, ServerThread> entry : set) {
			list.add(entry.getKey());
		}
		for (Integer id : list) {
			send(id, data);
		}
	}

	public void send(int id, String data) {
		ServerThread thread = socketList.get(id);
		thread.send(data);
		if (EXIT.equals(data)) {
			thread.close();
		}
	}

	private class ServerThread implements Runnable {
		private int id;
		private Socket socket;
		private InputStream in;
		private OutputStream out;
		private PrintWriter writer;
		private InputStreamReader streamReader;
		private BufferedReader reader;
		private Parser p = new Parser();

		ServerThread(int id, Socket socket) {
			try {
				this.id = id;
				this.socket = socket;
				this.in = socket.getInputStream();
				this.out = socket.getOutputStream();
				this.writer = new PrintWriter(out);
				streamReader = new InputStreamReader(in);
				reader = new BufferedReader(streamReader);
			} catch (IOException e) {
				// close();
				Log.error(e.getMessage());
			}
		}

		// Open new thread to I/O because of duplex communication
		@Override
		public void run() {
			try {
				String line = "";
				try {
					Thread.sleep(200);
				} catch (Exception e) {
				}
				send(p.getKey());
				Log.info("key: " + p.getKey());
				var login = reader.readLine();
				var r = p.checkLogin(login);
				if (r == OperateType.ALL_RIGHT) {
					send("Login successful!");
				} else {
					send(r.toString() + " (" + r.toInt() + ")");
					close();
					return;
				}
				while (!socket.isClosed() && line != null && !EXIT.equals(line)) {
					line = reader.readLine();
					if (line != null) {
						Log.info(id + ": " + line);
						send(p.parse(line));
					}
				}
				close();
				Log.info(id + ": disconnected normally by itself");
			} catch (IOException e) {
				Log.warning(id + ": disconnected abnormally");
			} finally {
				try {
					if (streamReader != null) {
						streamReader.close();
					}
					if (reader != null) {
						reader.close();
					}
					close();
				} catch (IOException e) {
					Log.error(e.getMessage());
				}
			}
		}

		public void send(String data) {
			if (!socket.isClosed() && data != null && !EXIT.equals(data)) {
				writer.println(data);
				writer.flush();
			}
		}

		public void close() {
			try {
				if (writer != null) {
					writer.close();
				}
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
				if (socket != null) {
					socket.close();
				}
				socketList.remove(id);
			} catch (IOException e) {
				Log.error(e.getMessage());
			}
		}
	}

	private class Writer extends Thread {
		private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		@Override
		public void run() {
			String line = "";
			while (true) {
				try {
					line = reader.readLine();
					if (line == null || EXIT.equals(line)) {
						break;
					}
				} catch (IOException e) {
					Log.error(e.getMessage());
				}
				try {
					String[] data = line.split(":");
					if ("*".equals(data[0])) {
						sendAll(data[1]);
					} else {
						send(Integer.parseInt(data[0]), data[1]);
					}
				} catch (NumberFormatException e) {
					Log.info("input number `id` or '*' first");
				} catch (ArrayIndexOutOfBoundsException e) {
					Log.info("Message couldn't be empty and use `:` to spilt `id` with message");
				} catch (NullPointerException e) {
					Log.info("Connection doesn't exsit or has been disconnected");
				}
			}
			close();
		}
	}

}
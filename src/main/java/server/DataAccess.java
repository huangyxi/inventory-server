package server;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

/**
 * DataAccess
 */
abstract class DataAccess {
	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String DATABASE = "store";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/" + DATABASE
			+ "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
	private static final String USER = "store_server";
	private static final String PASSWORD = "qoK1M*Gk^Rop2e3ExKm@ba5Nl@0&JZeR";
	private static Connection conn = null;

	protected DataAccess() throws Exception {
		try {
			if (null == conn) {
				Class.forName(JDBC_DRIVER);
				Log.info("Class loaded");
				conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
				Log.info("Database connected");
			}
			connectIfNeed();
		} catch (final ClassNotFoundException e) {
			Log.error("Class " + e.getMessage() + " not found!");
			throw new Exception(e);
		} catch (final SQLException se) {
			Log.error(se.getMessage());
			throw new Exception(se);
		}
	}

	protected final int update(final String sql, final List<Object> args) throws SQLException {
		PreparedStatement ps = null;
		int rows = -1;
		try {
			ps = conn.prepareStatement(sql);
			Log.info(ColorTB.uline() + ColorTB.whiteT() + sql + ColorTB.reset());
			for (int i = 0; i < args.size(); i++) {
				ps.setObject(i + 1, args.get(i));
			}
			Log.info(ColorTB.uline() + ColorTB.whiteT() + ps.toString() + ColorTB.reset());
			rows = ps.executeUpdate();
		} catch (final SQLException se) {
			Log.error(se.getMessage());
			throw se;
		} finally {
			try {
				closeX(ps);
			} catch (final SQLException se) {
				Log.error(se.getMessage());
			}
		}
		return rows;
	}

	protected final int update(final String sql, final Object... args) throws SQLException {
		return update(sql, List.of(args));
	}

	protected final ArrayList<ArrayList<Object>> query(final String sql, int columns, final List<Object> args)
			throws SQLException {
		var result = new ArrayList<ArrayList<Object>>();
		PreparedStatement ps = null;
		ResultSet rset = null;
		try {
			ps = conn.prepareStatement(sql);
			Log.info(ColorTB.uline() + ColorTB.whiteT() + sql + ColorTB.reset());
			for (int i = 0; i < args.size(); i++) {
				ps.setObject(i + 1, args.get(i));
			}
			Log.info(ColorTB.uline() + ColorTB.whiteT() + ps.toString() + ColorTB.reset());
			rset = ps.executeQuery();
			while (rset.next()) {
				var thisRow = new ArrayList<Object>();
				for (int column = 1; column <= columns; column++) {
					thisRow.add(rset.getObject(column));
				}
				result.add(thisRow);
			}
		} catch (final SQLException se) {
			Log.error(se.getMessage());
			throw se;
		} finally {
			try {
				closeX(rset);
				closeX(ps);
			} catch (final SQLException se) {
				Log.error(se.getMessage());
			}
		}
		return result;
	}

	protected final ArrayList<ArrayList<Object>> query(final String sql, int columns, final Object... args)
			throws SQLException {
		return query(sql, columns, List.of(args));
	}

	protected static final ArrayList<ArrayList<Object>> queryNull() {
		return (ArrayList<ArrayList<Object>>) null;
	}

	@Deprecated
	protected final ResultSet execute(final String sql) throws SQLException {
		Statement stmt = null;
		ResultSet result = null;
		try {
			stmt = conn.createStatement();
			result = stmt.executeQuery(sql);
		} catch (final SQLException se) {
			Log.error(se.getMessage());
			throw se;
		}
		return result;
	}

	private final void connectIfNeed() throws SQLException {
		try {
			if (conn.isClosed()) {
				conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
				Log.info("Database connected");
			}
		} catch (SQLException se) {
			Log.error(se.getMessage());
			throw se;
		}
	}

	public static final void close() {
		try {
			if (!conn.isClosed()) {
				conn.close();
				Log.info("Database disconnected");
			} else {
				Log.warning("Database has been disconnected previously");
			}
		} catch (final SQLException se) {
			Log.error(se.getMessage());
		}
	}

	protected String getTable() {
		return DATABASE + ".";
	}

	private <T extends Statement> void closeX(T x) throws SQLException {
		if (x != null) {
			if (!x.isClosed())
				x.close();
			x = null;
		}
	}

	private <T extends ResultSet> void closeX(T x) throws SQLException {
		if (x != null) {
			if (!x.isClosed())
				x.close();
			x = null;
		}
	}
}
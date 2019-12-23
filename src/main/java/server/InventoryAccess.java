package server;

import java.util.*;
import java.math.BigDecimal;

/**
 * InventoryAccess
 */
final class InventoryAccess extends DataAccess {
	private static final String TABLE = "inventory";
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String PRICE = "price";
	private static final String STORAGE = "storage";

	public InventoryAccess() throws Exception {
		super();
	}

	public String show(String idName) {
		String s = null;
		if (null == idName | 0 == idName.length()) {
			return s;
		}
		var ra = queryNull();
		try {
			String sql;
			if (Tools.isPureNumber(idName)) {
				sql = String.format("SELECT %s, %s, %s, %s FROM %s WHERE %s = ?", ID, NAME, PRICE, STORAGE, getTable(),
						ID);
				ra = query(sql, 4, Integer.parseInt(idName));
				if (0 == ra.size()) {
					return OperateType.ID_NOT_FOUND.toString();
				}
			} else {
				sql = String.format("SELECT %s, %s, %s, %s FROM %s WHERE %s = ?", ID, NAME, PRICE, STORAGE, getTable(),
						NAME);
				ra = query(sql, 4, idName);
				if (0 == ra.size()) {
					return OperateType.NAME_NOT_FOUND.toString();
				}
			}
			s = multiResult(ra);
		} catch (Exception e) {
			Log.error(e);
		}
		Log.info(s);
		return s;
	}

	public String between(String sb1, String sb2) {
		String s = null;
		if (null == sb1 || null == sb2) {
			return s;
		}
		var ra = queryNull();
		try {
			s = "";
			String sql;
			sql = String.format("SELECT %s, %s, %s, %s FROM %s WHERE %s >= ? AND %s <= ?", ID, NAME, PRICE, STORAGE,
					getTable(), PRICE, PRICE);
			var b1 = new BigDecimal(sb1);
			var b2 = new BigDecimal(sb2);
			switch (b1.compareTo(b2)) {
			case 1:
				ra = query(sql, 4, b2, b1);
				break;
			case -1:
				ra = query(sql, 4, b1, b2);
				break;
			default:
				return "";
			}
			if (0 == ra.size()) {
				return s;
			}
			s = multiResult(ra);
		} catch (Exception e) {
			Log.error(e);
		}
		Log.info(s);
		return s;
	}

	public String rm(String idName) {
		String s = null;
		if (null == idName | 0 == idName.length()) {
			return s;
		}
		try {
			int row;
			String sql;
			if (Tools.isPureNumber(idName)) {
				sql = String.format("DELETEs FROM %s WHERE %s = ?", getTable(), ID);
				row = update(sql, Integer.parseInt(idName));
				if (row == 0) {
					return OperateType.ID_NOT_FOUND.toString();
				}
			} else {
				sql = String.format("DELETEs FROM %s WHERE %s = ?", getTable(), NAME);
				row = update(sql, idName);
				if (row == 0) {
					return OperateType.NAME_NOT_FOUND.toString();
				}
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return OperateType.ALL_RIGHT.toString();
	}

	public String rename(String idName, String name) {
		if (null == idName | 0 == idName.length()) {
			return null;
		}
		String sql;
		var ra = queryNull();
		Long id = null;
		try {
			if (Tools.isPureNumber(idName)) {
				sql = String.format("SELECT %s FROM %s WHERE %s = ?", ID, getTable(), ID);
				ra = query(sql, 1, Integer.parseInt(idName));
				if (0 == ra.size()) {
					return OperateType.ID_NOT_FOUND.toString();
				}
			} else {
				sql = String.format("SELECT %s FROM %s WHERE %s = ?", ID, getTable(), NAME);
				ra = query(sql, 1, idName);
				if (0 == ra.size()) {
					OperateType.NAME_NOT_FOUND.toString();
				}
			}
			id = (Long) ra.get(0).get(0);
		} catch (Exception e) {
			Log.error(e);
		}
		try {
			sql = String.format("UPDATE %s SET %s = ? WHERE %s = ?", getTable(), NAME, ID);
			update(sql, name, id);
			return OperateType.ALL_RIGHT.toString();
		} catch (Exception e) {
			if (e instanceof java.sql.SQLIntegrityConstraintViolationException) {
				return OperateType.NAME_DUPLICATE.toString();
			}
			Log.error(e);
		}
		return OperateType.OTHER_ERROR.toString();
	}

	public String buy(String idName, String snum) {
		if (null == idName | 0 == idName.length()) {
			return null;
		}
		String sql;
		int row;
		var num = Long.valueOf(snum);
		try {
			if (Tools.isPureNumber(idName)) {
				sql = String.format("UPDATE %s SET %s = %s - ? WHERE %s = ?", getTable(), STORAGE, STORAGE, ID);
				row = update(sql, num, Integer.parseInt(idName));
				if (0 == row) {
					return OperateType.ID_NOT_FOUND.toString() + "or " + OperateType.STORAGE_EMPTY.toString();
				}
			} else {
				sql = String.format("UPDATE %s SET %s = %s - ? WHERE %s = ?", getTable(), STORAGE, STORAGE, NAME);
				row = update(sql, num, NAME);
				if (0 == row) {
					return OperateType.NAME_NOT_FOUND.toString() + "or " + OperateType.STORAGE_EMPTY.toString();
				}
			}
			return OperateType.ALL_RIGHT.toString();
		} catch (Exception e) {
			if (e instanceof java.sql.DataTruncation) {
				return OperateType.STORAGE_EMPTY.toString();
			}
			Log.error(e);
		}
		return OperateType.OTHER_ERROR.toString();
	}

	public String ls() {
		String s = null;
		var ra = queryNull();
		try {
			s = new String();
			String sql;
			sql = String.format("SELECT %s, %s, %s, %s FROM %s", ID, NAME, PRICE, STORAGE, getTable(), PRICE, PRICE);
			ra = query(sql, 4);
			s = multiResult(ra);
		} catch (Exception e) {
			Log.error(e);
		}
		Log.info(s);
		return s;
	}

	private String oneResult(ArrayList<Object> result) {
		return ID + ": " + (Long) result.get(0) + ",\t" + NAME + ": " + (String) result.get(1) + ",\t" + PRICE + ": "
				+ ((BigDecimal) result.get(2)).toString() + ",\t" + STORAGE + ": " + (Long) result.get(3);
	}

	private String multiResult(ArrayList<ArrayList<Object>> result) {
		if (result == null)
			return null;
		if (result.size() == 0)
			return OperateType.QUERY_EMPTY.toString();
		var s = new StringBuilder();
		for (int i = 0; i < result.size(); i++) {
			s.append(oneResult(result.get(i))).append("\n");
		}
		return s.substring(0, s.length() - 1);
	}

	@Deprecated
	public void naive() {
		try {
			String sql;
			sql = String.format("SELECT id,name FROM %s", getTable());
			Log.block("Query");
			final var a = query(sql, 2);
			for (int i = 0; i < a.size(); i++) {
				var id = (Long) a.get(i).get(0);
				var name = (String) a.get(i).get(1);
				System.out.print("id: " + id);
				System.out.println(", name: " + name);
			}
		} catch (Exception se) {
			Log.error(se.getMessage());
		}
		System.out.println("Goodbye!");
	}

	@Override
	protected String getTable() {
		return super.getTable() + TABLE;
	}
}
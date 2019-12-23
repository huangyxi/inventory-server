package server;

/**
 * Login
 */
final class Login extends DataAccess {
	private static final String TABLE = "staff";
	private static final String ID = "staff_id";
	private static final String NAME = "username";
	private static final String PASSWORD = "password";

	public Login() throws Exception {
		super();
	}

	@Deprecated
	public OperateType checkLogin(final String idUsername, final String password) {
		String sql;
		if (null == idUsername | 0 == idUsername.length() | password == null | 0 == password.length()) {
			return OperateType.INPUT_EMPTY;
		}
		var ra = queryNull();
		try {
			if (Tools.isPureNumber(idUsername)) {
				sql = String.format("SELECT %s FROM %s WHERE %s = ?", PASSWORD, getTable(), ID);
				ra = query(sql, 1, Integer.parseInt(idUsername));
				if (0 == ra.size()) {
					return OperateType.ID_NOT_FOUND;
				}
			} else {
				sql = String.format("SELECT %s FROM %s WHERE %s = ?", PASSWORD, getTable(), NAME);
				ra = query(sql, 1, idUsername);
				if (0 == ra.size()) {
					return OperateType.NAME_NOT_FOUND;
				}
			}
			final var sPassword = (String) ra.get(0).get(0);
			if (sPassword.equals(password)) {
				return OperateType.ALL_RIGHT;
			} else {
				return OperateType.PASSWORD_WRONG;
			}
		} catch (final Exception e) {
			Log.error(e.getMessage());
		}
		return OperateType.ALL_RIGHT;
	}

	public String getPassword(final int id) {
		try {
			var sql = String.format("SELECT %s FROM %s WHERE %s = ?", PASSWORD, getTable(), ID);
			var ra = query(sql, 1, id);
			if (0 == ra.size()) {
				return null;
			}
			return (String) ra.get(0).get(0);
		} catch (final Exception e) {
			Log.error(e.getMessage());
		}
		return null;
	}

	public String getPassword(final String username) {
		if (null == username) {
			return null;
		}
		try {
			var sql = String.format("SELECT %s FROM %s WHERE %s = ?", PASSWORD, getTable(), NAME);
			var ra = query(sql, 1, username);
			if (0 == ra.size()) {
				return null;
			}
			return (String) ra.get(0).get(0);
		} catch (final Exception e) {
			Log.error(e.getMessage());
		}
		return null;
	}

	@Override
	protected String getTable() {
		return super.getTable() + TABLE;
	}
}
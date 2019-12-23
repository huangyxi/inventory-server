package server;

/**
 * Parser
 */
class Parser {
	InventoryAccess dd = null;
	SPassword sPassword = new SPassword();
	private String HELP = "\nAvailable commands: ls, between, rm, rename, buy, exit, help";

	public String parse(String sqript) {
		String[] args = sqript.split("\\s+");
		String s = null;
		if (args.length == 0) {
			return OperateType.INPUT_EMPTY.toString();
		}
		switch (args[0]) {
		case "ls":
			if (args.length == 1) {
				return dd.ls();
			}
			if (args.length < 2) {
				return OperateType.ARGUMENT_MISSING.toString();
			}
			s = dd.show(args[1]);
			return stringCheck(s);
		case "between":
			if (args.length < 3) {
				return OperateType.ARGUMENT_MISSING.toString();
			}
			s = dd.between(args[1], args[2]);
			return stringCheck(s);
		case "rm":
			if (args.length < 2) {
				return OperateType.ARGUMENT_MISSING.toString();
			}
			s = dd.rm(args[1]);
			return stringCheck(s);
		case "rename":
			if (args.length < 3) {
				return OperateType.ARGUMENT_MISSING.toString();
			}
			s = dd.rename(args[1], args[2]);
			return stringCheck(s);
		case "buy":
			if (args.length < 3) {
				return OperateType.ARGUMENT_MISSING.toString();
			}
			s = dd.buy(args[1], args[2]);
			return stringCheck(s);
		case "all":
			s = dd.ls();
			return stringCheck(s);
		case "help":
			return HELP;
		default:
			return OperateType.COMMAND_NOT_EXISTS.toString() + HELP;
		}
	}

	private String stringCheck(String s) {
		if (null == s) {
			return OperateType.OTHER_ERROR.toString();
		}
		if (0 == s.length()) {
			return OperateType.QUERY_EMPTY.toString();
		}
		return s;
	}
	// @Deprecated
	// OperateType checkLogin0(final String idUsername, final String password) {
	// var res = OperateType.OTHER_ERROR;
	// try {
	// res = (new Login()).checkLogin(idUsername, password);
	// if (OperateType.ALL_RIGHT == res)
	// dd = new InventoryAccess();
	// } catch (Exception e) {
	// Log.error(e.getMessage());
	// }
	// return res;
	// }

	public String getKey() {
		return sPassword.getKey();
	}

	public OperateType checkLogin(final String login) {
		String[] args = login.split("\\s+");
		if (null == args || args.length < 2) {
			return OperateType.INPUT_EMPTY;
		}
		var idUsername = args[0];
		Log.info("idUsername: " + idUsername);
		var encoded = args[1];
		Log.info("encoded: " + encoded);
		String password = null;
		try {
			password = getPassword(idUsername);
		} catch (Exception e) {
			return OperateType.OTHER_ERROR;
		}
		if (null == password) {
			return Tools.isPureNumber(idUsername) == true ? OperateType.ID_NOT_FOUND : OperateType.NAME_NOT_FOUND;
		}
		if (!sPassword.isSame(password, encoded)) {
			return OperateType.PASSWORD_WRONG;
		}
		try {
			dd = new InventoryAccess();
		} catch (Exception e) {
			Log.warning(e);
			return OperateType.OTHER_ERROR;
		}
		return OperateType.ALL_RIGHT;
	}

	private String getPassword(final String idUsername) throws Exception {
		String password = null;
		try {
			if (Tools.isPureNumber(idUsername)) {
				password = (new Login()).getPassword(Integer.parseInt(idUsername));
			} else {
				password = (new Login()).getPassword(idUsername);
			}

		} catch (Exception e) {
			Log.error(e);
			throw e;
		}
		return password;
	}
}
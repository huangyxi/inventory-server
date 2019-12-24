package server;

/**
 * Parser
 */
class Parser {
	InventoryAccess dd = null;
	KeyGen sPassword = new KeyGen();
	private String secret = null;
	private String HELP = "\nAvailable commands: ls, between, rm, rename, buy, touch, exit, help";
	private int nEncrypt = 0;
	private int nDecrypt = 0;

	public String parse(String encryptedScript) {
		try {
			var script = decrypt(encryptedScript);
			String[] args = script.split("\\s+");
			String s = null;
			if (args.length == 0) {
				return encrypt(OperateType.INPUT_EMPTY.toString());
			}
			switch (args[0]) {
			case "ls":
				if (args.length == 1) {
					return encrypt(dd.ls());
				}
				if (args.length < 2) {
					return encrypt(OperateType.ARGUMENT_MISSING.toString());
				}
				s = dd.show(args[1]);
				return encrypt(stringCheck(s));
			case "between":
				if (args.length < 3) {
					return encrypt(OperateType.ARGUMENT_MISSING.toString());
				}
				s = dd.between(args[1], args[2]);
				return encrypt(stringCheck(s));
			case "rm":
				if (args.length < 2) {
					return encrypt(OperateType.ARGUMENT_MISSING.toString());
				}
				s = dd.rm(args[1]);
				return encrypt(stringCheck(s));
			case "rename":
				if (args.length < 3) {
					return encrypt(OperateType.ARGUMENT_MISSING.toString());
				}
				s = dd.rename(args[1], args[2]);
				return encrypt(stringCheck(s));
			case "buy":
				if (args.length < 3) {
					return encrypt(OperateType.ARGUMENT_MISSING.toString());
				}
				s = dd.buy(args[1], args[2]);
				return encrypt(stringCheck(s));
			case "touch":
				if (args.length < 4) {
					return encrypt(OperateType.ARGUMENT_MISSING.toString());
				}
				if (args.length == 4) {
					return encrypt(dd.touch(args[1], args[2], args[3]));
				}
				return encrypt(dd.touch(args[1], args[2], args[3], args[4]));
			case "all":
				s = dd.ls();
				return encrypt(stringCheck(s));
			case "help":
				return encrypt(HELP);
			default:
				return encrypt(OperateType.COMMAND_NOT_EXISTS.toString() + HELP);
			}
		} catch (Exception e) {
			Log.error(e);
		}
		return encrypt(OperateType.OTHER_ERROR.toString() + HELP);
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

	private String encrypt(String strToEncrypt) {
		return Secure.encrypt(strToEncrypt, secret + nEncrypt++);
	}

	private String decrypt(String strToDecrypt) {
		return Secure.decrypt(strToDecrypt, secret + nDecrypt++);
	}

	public String getKey() {
		return sPassword.getKey();
	}

	public OperateType checkLogin(final String login) {
		if (null == login) {
			return OperateType.INPUT_EMPTY;
		}
		String[] args = login.split("\\s+");
		if (args.length < 2) {
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
		secret = Secure.encode(sPassword.getKey(), password);
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
package server;

/**
 * OperateType
 */
enum OperateType implements EnumType {
	ALL_RIGHT(0), INPUT_EMPTY(1), ID_NOT_FOUND(2), NAME_NOT_FOUND(3), PASSWORD_WRONG(4), STORAGE_EMPTY(5),
	COMMAND_NOT_EXISTS(6), ARGUMENT_MISSING(7), QUERY_EMPTY(8), ID_DUPLICATE(9), NAME_DUPLICATE(10), OTHER_ERROR(-1);

	private final int num;

	OperateType(int num) {
		this.num = num;
	}

	@Override
	public int toInt() {
		return num;
	}
}

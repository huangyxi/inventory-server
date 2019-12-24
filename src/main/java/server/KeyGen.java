package server;

import java.security.SecureRandom;

/**
 * SPassword
 */
class KeyGen {
	private static final String RANDOM_LOWER = "abcdefghijklmnopqrstuvwxyz";
	private static final String RANDOM_UPPER = RANDOM_LOWER.toUpperCase();
	private static final String RANDOM_NUMBER = "0123456789";
	private static final String RANDOM_SYMBOL = "~!@#$%^&*()-=[]\\;',./`_+{}|:\"<>?";
	private static final String RANDOM_CHAR_POOL = RANDOM_LOWER + RANDOM_UPPER + RANDOM_NUMBER + RANDOM_SYMBOL;
	private static final int RANDOM_CHAR_POOL_LENGTH = RANDOM_CHAR_POOL.length();
	private static final SecureRandom random = new SecureRandom();
	private int length = 64;
	private String key = null;

	public String getKey() {
		if (null == key) {
			key = generateKey();
		}
		return key;
	}

	public String newKey() {
		key = generateKey();
		return key;
	}

	public boolean isSame(String password, String encoded) {
		var rightEncoded = Secure.encode(password, key);
		Log.info("Right encoded: " + rightEncoded);
		return encoded.equals(rightEncoded);
	}

	private String generateKey() {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			// 0-62 (exclusive), random returns 0-61
			int rndCharAt = random.nextInt(RANDOM_CHAR_POOL_LENGTH);
			char rndChar = RANDOM_CHAR_POOL.charAt(rndCharAt);
			sb.append(rndChar);
		}
		return sb.toString();
	}
}
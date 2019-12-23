package server;

/**
 * PasswordEncode
 */
public class PasswordEncode {
	private static java.security.MessageDigest digest = null;
	static {
		try {
			digest = java.security.MessageDigest.getInstance("SHA-256");
		} catch (Exception e) {
		}
	}

	public static String encode(String password, String key) {
		String catPassword = password + key;
		if (null == digest) {
			return catPassword;
		}
		byte[] encodedHash = digest.digest(catPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8));
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < encodedHash.length; i++) {
			String hex = Integer.toHexString(0xff & encodedHash[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		String encoded = hexString.toString();
		Log.info("Right encoded: " + encoded);
		return encoded;
	}
}
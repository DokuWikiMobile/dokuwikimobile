package org.dokuwikimobile.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The HashUtil offers simple methods to hash a string.
 * It supports different hash algorithms that can be passed as a parameter to the
 * hash function.
 * 
 * @author Tim Roes <mail@timroes.de>
 */
public class HashUtil {

	public final static String MD5 = "MD5";
	public final static String SHA256 = "SHA-256";

	public final static String DEFAULT_ALGORITHM = SHA256;

	private static byte[] salt = new byte[]{0x2A, 0x19, 0x05, 0x12, 0x07};

	/**
	 * Return the MessageDigest for a given algorithm.
	 * 
	 * @param The algorithm that should be used for hashing. If the algorithm
	 * 		does not exist, a NoSuchHashAlgorithmException will be thrown.
	 * @return The MessageDigest for the given algorithm.
	 */
	private static MessageDigest digest(String algorithm) {

		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			return digest;
		} catch (NoSuchAlgorithmException ex) {
			throw new NoSuchHashAlgorithmException("No algorithm '" + algorithm + "' could be found.");
		}
		
	}

	/**
	 * Hash a string with the default algorithm and return the hexadecimal representation
	 * as a string.
	 * 
	 * @param input The input string, that should be hashed.
	 * @return The hash of the input string in hexadecimal representation.
	 */
	public static String hash(String input) {
		return hash(input, DEFAULT_ALGORITHM);
	}

	/**
	 * Hash a string with a given algorithm and return the hexadecimal representation
	 * as a string.
	 * 
	 * @param input The input string, that should be hashed.
	 * @param algorithm The algorithm, that should be used for hashing.
	 * @return The hash of the input string in hexadecimal representation.
	 */
	public static String hash(String input, String algorithm) {
		
		byte[] hash = hashBytes(input, algorithm);
		StringBuilder builder = new StringBuilder();

		for(byte b : hash) {
			builder.append(String.format("%02x", b));
		}

		return builder.toString();
		
	}

	/**
	 * Hash a string with the default algorithm and return the hash as bytes.
	 * 
	 * @param input The input string, that should be hashed.
	 * @return The hash of the input string as bytes.
	 */
	public static byte[] hashBytes(String input) {
		return hashBytes(input, DEFAULT_ALGORITHM);
	}
	
	/**
	 * Hash a string with a given algorithm and return the hash as bytes.
	 * 
	 * @param input The input string, that should be hashed.
	 * @param algorithm The algorithm, that should be used for hashing.
	 * @return The hash of the input string as bytes.
	 */
	public static byte[] hashBytes(String input, String algorithm) {

		MessageDigest digest = digest(algorithm);
		
		digest.reset();
		digest.update(salt);

		return digest.digest(input.getBytes());
		
	}

	/**
	 * A NoSuchHashAlgorithmException will be thrown, if a hash algorithm has been requested,
	 * that doesn't exist on this device.
	 */
	public static class NoSuchHashAlgorithmException extends RuntimeException {

		public NoSuchHashAlgorithmException(String detailMessage) {
			super(detailMessage);
		}
		
	}
	
}

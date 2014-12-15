package com.kduborenko.mypassword;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordGenerator {

	public static final int PASSWORD_BINARY_LENGTH = 12;

	public String generatePassword(String siteName, String masterPassword) {
		try {
			byte[] sha1 = getSha1(masterPassword + "|" + siteName + "\n");
			return Base64.encodeToString(
				cutBytesArray(sha1, PASSWORD_BINARY_LENGTH),
				Base64.NO_WRAP
			);
		} catch (UnsupportedEncodingException e) {
			return null;
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	private byte[] cutBytesArray(byte[] bytes, int size) {
		byte[] passwordBinary = new byte[size];
		System.arraycopy(bytes, 0, passwordBinary, 0, size);
		return passwordBinary;
	}

	private byte[] getSha1(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(str.getBytes(), 0, str.length());
		return md.digest();
	}

}

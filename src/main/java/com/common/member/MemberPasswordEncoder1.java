package com.common.member;

import java.math.BigInteger;
import java.security.MessageDigest;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class MemberPasswordEncoder1 implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
			messageDigest.update(rawPassword.toString().getBytes());
			String hex = String.format("%0128x", new BigInteger(1, messageDigest.digest()));

			return hex;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return this.encode(rawPassword).equals(encodedPassword);
	}
}
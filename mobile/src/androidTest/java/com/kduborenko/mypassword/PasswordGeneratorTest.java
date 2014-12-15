package com.kduborenko.mypassword;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

public class PasswordGeneratorTest extends TestCase {

	@SmallTest
	public void testGeneratePassword() {
		PasswordGenerator pg = new PasswordGenerator();
		assertEquals("/KIqb37icn/lFMRl", pg.generatePassword("test", "test"));
		assertEquals("AEmqLTHdUJItsfFO", pg.generatePassword("test.com", "qwertyasdfgh"));
	}

}

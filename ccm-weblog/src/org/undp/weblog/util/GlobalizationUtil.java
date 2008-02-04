package org.undp.weblog.util;

import com.arsdigita.globalization.Globalized;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * @author Peter Kopunec
 */
public class GlobalizationUtil implements Globalized {

	private static final String BUNDLE_NAME = "org.undp.weblog.ui.WebLog";

	public static GlobalizedMessage globalize(String key) {
		return new GlobalizedMessage(key, BUNDLE_NAME);
	}

	public static GlobalizedMessage globalize(String key, Object[] args) {
		return new GlobalizedMessage(key, BUNDLE_NAME, args);

	}

	public static String localize(String key) {
		return (String) globalize(key).localize();
	}

	public static String localize(String key, Object[] args) {
		return (String) globalize(key, args).localize();
	}
}

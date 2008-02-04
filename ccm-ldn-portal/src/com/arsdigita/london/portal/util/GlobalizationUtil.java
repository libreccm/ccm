package com.arsdigita.london.portal.util;

import com.arsdigita.globalization.Globalized;
import com.arsdigita.globalization.GlobalizedMessage;

public class GlobalizationUtil implements Globalized {
	private static final String BUNDLE_NAME = "com.arsdigita.london.portal.ui.PortalResources";

	public static GlobalizedMessage globalize(String key) {
		return new GlobalizedMessage(key, BUNDLE_NAME);
	}

	public static GlobalizedMessage globalize(String key, Object[] args) {
		return new GlobalizedMessage(key, BUNDLE_NAME, args);

	}
}

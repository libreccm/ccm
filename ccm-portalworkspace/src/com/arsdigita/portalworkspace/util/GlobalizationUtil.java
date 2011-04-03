package com.arsdigita.portalworkspace.util;

import com.arsdigita.globalization.Globalized;
import com.arsdigita.globalization.GlobalizedMessage;

public class GlobalizationUtil implements Globalized {
	private static final String BUNDLE_NAME = 
            "com.arsdigita.portalworkspace.WorkspaceResources";

	public static GlobalizedMessage globalize(String key) {
		return new GlobalizedMessage(key, BUNDLE_NAME);
	}

	public static GlobalizedMessage globalize(String key, Object[] args) {
		return new GlobalizedMessage(key, BUNDLE_NAME, args);

	}
}

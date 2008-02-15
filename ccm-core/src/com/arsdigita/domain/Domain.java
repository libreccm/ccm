package com.arsdigita.domain;


public class Domain {

	
	private static DomainConfig s_config;

	/**
	 * Gets the <code>DomainConfig</code> object.
	 */
	public static final DomainConfig getConfig() {
		if (s_config == null) {
			s_config = new DomainConfig();
			s_config.load("ccm-core/domain.properties");
	        	}
		return s_config;
	    
	}	

}

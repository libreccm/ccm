package com.arsdigita.domain;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;

public final class DomainConfig extends AbstractConfig {

	private final BooleanParameter m_queryBlobContent;

	public DomainConfig() {

		m_queryBlobContent = new BooleanParameter(
				"waf.domain.query_file_attachment_blob", Parameter.OPTIONAL,
				Boolean.TRUE);

		register(m_queryBlobContent);
		loadInfo();
	}

	public boolean queryBlobContentForFileAttachments() {
		return ((Boolean) get(m_queryBlobContent)).booleanValue();
	}

}

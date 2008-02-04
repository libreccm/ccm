/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.portal.ui;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.london.portal.WorkspacePage;
import com.arsdigita.persistence.OID;

public class PortalListViewer extends PortalList {

	private static final Logger s_log = Logger
			.getLogger(PortalListViewer.class);

	public PortalListViewer(PortalSelectionModel portal) {
		this(null, portal);
	}

	public PortalListViewer(WorkspaceSelectionModel workspace,
			PortalSelectionModel portal) {
		super(workspace, portal);

		addPortalAction("select", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PageState state = e.getPageState();
				String value = state.getControlEventValue();

				if (s_log.isDebugEnabled()) {
					s_log.debug("Selecting portal " + value);
				}

				WorkspacePage portal = (WorkspacePage) DomainObjectFactory
						.newInstance(new OID(
								WorkspacePage.BASE_DATA_OBJECT_TYPE,
								new BigDecimal(value)));

				setSelectedPortal(state, portal);
			}
		});
	}

}

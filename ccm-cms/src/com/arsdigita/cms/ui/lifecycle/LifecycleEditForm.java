/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import org.apache.log4j.Logger;

/**
 * This class contains a form component to edit a lifecycle
 * definition.
 *
 * @author Jack Chung
 * @author Xixi D'Moon &lt;xdmoon@redhat.com&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: LifecycleEditForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
class LifecycleEditForm extends BaseLifecycleForm {
    public static final String versionId =
        "$Id: LifecycleEditForm.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private static final Logger s_log = Logger.getLogger
        (LifecycleEditForm.class);

    private LifecycleDefinitionRequestLocal m_definition;

    LifecycleEditForm(final LifecycleDefinitionRequestLocal definition) {
        super("LifecycleEdit", gz("cms.ui.lifecycle.edit"));

        m_definition = definition;

        m_name.addValidationListener(new NameUniqueListener(m_definition));

        addInitListener(new InitListener());
        addProcessListener(new ProcessListener());
    }

    private class InitListener implements FormInitListener {
        public final void init(final FormSectionEvent e) {
            final PageState state = e.getPageState();
            final LifecycleDefinition cycle = m_definition.getLifecycleDefinition
                (state);

            m_name.setValue(state, cycle.getLabel());
            m_description.setValue(state, cycle.getDescription());
        }
    }

    private class ProcessListener implements FormProcessListener {
        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();
            final LifecycleDefinition definition =
                m_definition.getLifecycleDefinition(state);

            definition.setLabel((String) m_name.getValue(state));
            definition.setDescription((String) m_description.getValue(state));
            definition.save();
        }
    }
}

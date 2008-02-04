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
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.LifecycleDefinitionCollection;
import com.arsdigita.cms.ui.BaseForm;
import com.arsdigita.cms.ui.FormSecurityListener;
import com.arsdigita.globalization.GlobalizedMessage;
import org.apache.log4j.Logger;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: BaseLifecycleForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
class BaseLifecycleForm extends BaseForm {
    public static final String versionId =
        "$Id: BaseLifecycleForm.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private static final Logger s_log = Logger.getLogger
        (BaseLifecycleForm.class);

    final TextField m_name;
    final TextArea m_description;

    BaseLifecycleForm(final String key, final GlobalizedMessage message) {
        super(key, message);

        m_name = new TextField(new TrimmedStringParameter("label"));
        addField(gz("cms.ui.name"), m_name);

        m_name.addValidationListener(new NotEmptyValidationListener());
        m_name.setSize(40);
        m_name.setMaxLength(1000);

        m_description = new TextArea
            (new TrimmedStringParameter("description"));
        addField(gz("cms.ui.description"), m_description);

        m_description.addValidationListener
            (new StringLengthValidationListener(4000));
        m_description.setCols(40);
        m_description.setRows(5);
        m_description.setWrap(TextArea.SOFT);

        addAction(new Finish());
        addAction(new Cancel());

        addSubmissionListener
            (new FormSecurityListener(SecurityManager.LIFECYCLE_ADMIN));
    }

    class NameUniqueListener implements ParameterListener {
        private final LifecycleDefinitionRequestLocal m_definition;

        NameUniqueListener(final LifecycleDefinitionRequestLocal definition) {
            m_definition = definition;
        }

        public final void validate(final ParameterEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();
            final String label = (String) m_name.getValue(state);
            final LifecycleDefinitionCollection definitions =
                CMS.getContext().getContentSection().getLifecycleDefinitions();

            while (definitions.next()) {
                final LifecycleDefinition definition =
                    definitions.getLifecycleDefinition();

                if (definition.getLabel().equalsIgnoreCase(label)
                        && (m_definition == null
                            || !m_definition.getLifecycleDefinition
                                (state).equals(definition))) {
                    definitions.close();

                    throw new FormProcessException
                        (lz("cms.ui.lifecycle.name_not_unique"));
                }
            }
        }
    }
}

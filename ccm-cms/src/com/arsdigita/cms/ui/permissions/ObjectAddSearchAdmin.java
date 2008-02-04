/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.permissions;


import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.cms.ui.UserSearchForm;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.xml.Element;

/**
 * <p>This panel allows a staff administrator to search for users and add
 * them to a staff role for the content section.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #9 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class ObjectAddSearchAdmin extends SimpleContainer {

    public static final String versionId = "$Id: ObjectAddSearchAdmin.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    private ACSObjectSelectionModel m_object;

    private UserSearchForm m_searchForm;
    private ObjectAddAdmin m_addPanel;
    private ActionLink m_return;

    public ObjectAddSearchAdmin(ACSObjectSelectionModel model) {
        super();

        m_object = model;

        m_searchForm = new UserSearchForm("ObjectAdminSearch");
        add(m_searchForm);

        m_addPanel = getObjectAddAdmin(model, m_searchForm);
        add(m_addPanel);

        m_addPanel.addCompletionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireCompletionEvent(e.getPageState());
                }
            });

        m_return = new ActionLink( (String) GlobalizationUtil.globalize("cms.ui.permissions.return_to_object_info").localize());
        m_return.setClassAttr("actionLink");
        m_return.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireCompletionEvent(e.getPageState());
                }
            });
        add(m_return);
    }


    /**
     * Displays the appropriate form(s).
     */
    public void generateXML(PageState state, Element parent) {
        FormData data = m_searchForm.getFormData(state);
        FormData data2 = m_addPanel.getForm().getFormData(state);

        if ( data != null && (data.isSubmission() || data2.isSubmission()) ) {
            m_addPanel.setVisible(state, true);
        } else {
            m_addPanel.setVisible(state, false);
        }
        super.generateXML(state, parent);
    }


    /**
     * This returns the form for adding object administrators
     */
    protected ObjectAddAdmin getObjectAddAdmin(ACSObjectSelectionModel model,
                                               UserSearchForm searchForm) {
        return new ObjectAddAdmin(model, searchForm.getSearchWidget());
    }
}

/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.ui.ItemSearch;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

import java.math.BigDecimal;

/**
 * A form which displays a select box of all content types available under the
 * given content section, and forwards to the item creation UI when the user
 * selects a content type to instantiate.
 *
 * @author Stanislav Freidin (sfreidin@arsdigtia.com)
 * @version $Revision: #12 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: NewItemForm.java 2161 2011-02-02 00:16:13Z pboy $ 
 */
public abstract class NewItemForm extends Form {

    public static String DP_TYPE_PREFIX = "com.arsdigita.dp.";
    private SingleSelect m_typeWidget;
    private Submit m_submit;
    private Label m_emptyLabel;
    private Label m_createLabel;
    public static final String TYPE_ID = "tid";

    /**
     * Construct a new NewItemForm
     *
     */
    public NewItemForm(String name) {
        //super(name, new BoxPanel(BoxPanel.HORIZONTAL));
        super(name, new BoxPanel(BoxPanel.VERTICAL));
        setIdAttr("new_item_form");

        BoxPanel panel = new BoxPanel(BoxPanel.HORIZONTAL);//(BoxPanel) getPanel();
        panel.setWidth("2%");
        panel.setBorder(0);

        m_emptyLabel = new Label(GlobalizationUtil
                                 .globalize("cms.ui.authoring.no_types_registered"), 
                                 false);
        m_emptyLabel.setIdAttr("empty_label");
        panel.add(m_emptyLabel);

        m_createLabel = new Label(GlobalizationUtil
                                      .globalize("cms.ui.authoring.create_new"), 
                                  false);
        m_createLabel.setIdAttr("create_label");
        panel.add(m_createLabel);

        m_typeWidget = new SingleSelect(new BigDecimalParameter(TYPE_ID));
        try {
            m_typeWidget.addPrintListener(new PrintListener() {

                // Read the content section's content types and add them as options
                public void prepare(PrintEvent e) {
                    OptionGroup o = (OptionGroup) e.getTarget();
                    PageState state = e.getPageState();

                    ContentSection section = getContentSection(state);
                    ContentType parentType = null;
                    ContentTypeCollection typesCollection = null;
                    BigDecimal singleTypeID = (BigDecimal) 
                                              state.getValue(new 
                                                BigDecimalParameter(
                                                  ItemSearch.SINGLE_TYPE_PARAM));

                    if (singleTypeID != null) {
                        try {
                            parentType = new ContentType(singleTypeID);
                        } catch (DataObjectNotFoundException ex) {
                            parentType = null;
                        }
                    }
                    
                    if (parentType == null) {
                        typesCollection = section.getCreatableContentTypes();
                    } else {
                        typesCollection = section.getDescendantsOfContentType(parentType);
                    }

                    typesCollection.addOrder(ContentType.LABEL);

                    if (!typesCollection.isEmpty()) {
                        // Add content types
                        while (typesCollection.next()) {
                            boolean list = true;
                            ContentType type = typesCollection.getContentType();
                            if (PermissionService.getDirectGrantedPermissions(type.getOID()).size() > 0) {
                                // chris gilbert - allow restriction of some types to certain
                                // users/groups. No interface to do this, but group could be
                                // created and permission granted in a content type loader
                                //
                                // can't permission filter the collection because most types
                                // will have no permissions granted. This approach involves
                                // a small overhead getting the count of granted permissions for
                                // each type (mitigated by only checking DIRECT permissions)

                                Party party = Kernel.getContext().getParty();
                                if (party == null) {
                                    party = Kernel.getPublicUser();
                                }
                                PermissionDescriptor create = new PermissionDescriptor(PrivilegeDescriptor.get(SecurityManager.CMS_NEW_ITEM), type, party);
                                list = PermissionService.checkPermission(create);

                            }
                            if (list) {
                                //for dp content type label localization
                                //String t = type.getAssociatedObjectType();
                                String cn = type.getClassName();
                                String l = type.getLabel();
                                if (cn.startsWith(DP_TYPE_PREFIX, 0)) {
                                    o.addOption(new Option(type.getID().toString(),
                                            new Label(GlobalizationUtil.globalize(l.replace(' ', '_')))));
                                } else {
                                    o.addOption(new Option(type.getID().toString(), type.getLabel()));
                                }
                            }

                        }
                        typesCollection.reset();
                    }
                }
            });
        } catch (java.util.TooManyListenersException e) {
            throw new UncheckedWrapperException("Too  many listeners: " + e.getMessage(), e);
        }

        panel.add(m_typeWidget);

        m_submit = new Submit("new", GlobalizationUtil.globalize(
                                                       "cms.ui.authoring.go"));
        panel.add(m_submit);
        
        add(panel);
    }

    public abstract ContentSection getContentSection(PageState state);

    public BigDecimal getTypeID(PageState state) {
        return (BigDecimal) m_typeWidget.getValue(state);
    }

    public final SingleSelect getTypeSelect() {
        return m_typeWidget;
    }

    // Generate XML - show/hide labels/widgets
    @Override
    public void generateXML(PageState state, Element parent) {

        if (isVisible(state)) {
            ContentSection section = getContentSection(state);

            ContentTypeCollection c = section.getCreatableContentTypes();
            boolean isEmpty = c.isEmpty();
            c.close();

            m_createLabel.setVisible(state, !isEmpty);
            m_typeWidget.setVisible(state, !isEmpty);
            m_submit.setVisible(state, !isEmpty);
            m_emptyLabel.setVisible(state, isEmpty);

            super.generateXML(state, parent);
        }
    }
}

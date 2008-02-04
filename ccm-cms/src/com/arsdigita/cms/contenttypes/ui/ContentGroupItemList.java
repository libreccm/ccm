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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.ExternalLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contenttypes.ContentGroup;
import com.arsdigita.cms.contenttypes.ContentGroupContainer;
import com.arsdigita.cms.ui.ItemListModel;
import com.arsdigita.cms.ui.SortableList;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;

import javax.servlet.ServletException;
import java.math.BigDecimal;


/**
 *  This displays a sortable list of items within a given content group
 */
class ContentGroupItemList extends SortableList {

    public static final String versionId = "$Id: ContentGroupItemList.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    private static final org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(ContentGroupItemList.class);

    public final static String SUB_ITEM = "sub";
    protected String m_attributeName;
    private final ACSObjectSelectionModel m_model;

    /**
     *  The passed in attribute name is the name of the attribute to
     *  retrieve from the index.
     */
    public ContentGroupItemList(final ACSObjectSelectionModel model,
                                final String attributeName) {
        super(new ParameterSingleSelectionModel
              (new BigDecimalParameter(SUB_ITEM)));

        m_model = model;
        m_attributeName = attributeName;

        setIdAttr("group_list");

        setModelBuilder(new ItemListModelBuilder(model));

        // Select the category in the main tree when the
        // user selects it here
        addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    PageState state = event.getPageState();
                    String id = (String) getSelectedKey(state);

                    if (id != null) {
                        model.setSelectedKey(state, id);
                    }
                }
            });

        Label l = new Label(GlobalizationUtil.globalize("no_items"));
        l.setFontWeight(Label.ITALIC);
        setEmptyView(l);
    }

    /**
     *  This actually performs the sorting
     */
    public void respond(PageState ps) throws ServletException {
        String event = ps.getControlEventName();

        if (NEXT_EVENT.equals(event) || PREV_EVENT.equals(event)) {
            try {
                ContentItem contentItem =
                    (ContentItem)DomainObjectFactory.newInstance
                    (new OID(ContentItem.BASE_DATA_OBJECT_TYPE,
                             new BigDecimal(ps.getControlEventValue())));
                ContentGroupContainer item = 
                    (ContentGroupContainer) m_model.getSelectedObject(ps);
                ContentGroup group = item.getContentGroup(m_attributeName);
                if (NEXT_EVENT.equals(event)) {
                    group.swapWithNext(contentItem);
                } else {
                    group.swapWithPrevious(contentItem);
                }
            } catch (DataObjectNotFoundException exception) {
                s_log.error("Trying to create groups with state = " + ps,
                            exception);
                throw new ServletException(exception);
            }
        }
    }

    protected void generateLabelXML(PageState state, Element parent,
                                    Label label, String key) {
        ExternalLink link =
            new ExternalLink
            (label, URL.there(state.getRequest(),
                              "/content-center/searchredirect",
                              ParameterMap.fromString
                              ("item_id=" + key + "&context=draft")).toString());
        link.generateXML(state, parent);

    }

    private class ItemListModelBuilder extends LockableImpl
        implements ListModelBuilder {

        ACSObjectSelectionModel m_model;

        public ItemListModelBuilder(ACSObjectSelectionModel model) {
            super();
            m_model = model;
        }

        public ListModel makeModel(List l, PageState state) {
            ContentGroupContainer item =
                (ContentGroupContainer) m_model.getSelectedObject(state);
            ContentGroup group = item.getContentGroup(m_attributeName);
            if (group == null) {
                return List.EMPTY_MODEL;
            } else {
                return new ItemListModel(group.getContentItems());
            }
        }
    }
}

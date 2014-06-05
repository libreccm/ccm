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

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.ContentGroup;
import com.arsdigita.cms.contenttypes.ContentGroupContainer;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.UncheckedWrapperException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Form to edit the basic properties of a Content Group. This form can be
 * extended to create forms for Brand subclasses.
 */
public class ContentGroupPropertyForm extends BasicItemForm {

    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties int hte runtime environment
     *  and set com.arsdigita.cms.contenttypes.ui.ContentGroupPropertyForm=DEBUG 
     *  by uncommenting or adding the line.                                   */
    private final static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(ContentGroupPropertyForm.class);


    public final static String ASSOCIATED_ITEMS = "associatedItems";
    public final static String RELATED_ITEM = "relatedItem";

    private String m_attributeName;
    private CheckboxGroup m_checkboxGroup;

    /**
     * Creates a new form to edit the Brand object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    ContentGroupContainer to work on
     * @param attributeName The name of the content group to use during
     *        the process stage of this form.
     */
    public ContentGroupPropertyForm(ItemSelectionModel itemModel, 
                                    String attributeName) {
        super("contentGroupProperty", itemModel);
        m_attributeName = attributeName;
    }

    /**
     * Adds widgets to the form.
     */
    @Override
    protected void addWidgets() {
        add(new Label(GlobalizationUtil
                      .globalize("cms.contenttypes.ui.content_group_name")));
        TextField nameWidget = 
            new TextField(new TrimmedStringParameter(ContentGroup.NAME));
        nameWidget.addValidationListener(new NotNullValidationListener());
        add(nameWidget);

        add(new Label(GlobalizationUtil
                      .globalize("cms.contenttypes.ui.content_group_current_items")) {
                @Override
                public boolean isVisible(PageState state) {
                    ContentGroupContainer item = 
                        (ContentGroupContainer) getItemSelectionModel()
                        .getSelectedObject(state);
                    ContentGroup group = 
                        getCurrentGroup(item, m_attributeName);
                    return group != null && group.getContentItems().size() > 0;
                }
            });
        m_checkboxGroup = new CheckboxGroup(ASSOCIATED_ITEMS) {
                @Override
                public boolean isVisible(PageState state) {
                    ContentGroupContainer item = 
                        (ContentGroupContainer) getItemSelectionModel()
                        .getSelectedObject(state);
                    ContentGroup group = 
                        getCurrentGroup(item, m_attributeName);
                    return group != null && group.getContentItems().size() > 0;
                }
            };
        add(m_checkboxGroup);

        add(new Label(GlobalizationUtil
                      .globalize("cms.contenttypes.ui.content_group_new_item")));
        try {
            add(new ItemSearchWidget(RELATED_ITEM, getSearchContentType()));
        } catch (DataObjectNotFoundException de) {
            throw new UncheckedWrapperException(de);
        }
    }


    /**
     * Perform form initialization. Children should override this
     * this method to pre-fill the widgets with data, instantiate
     * the content item, etc.
     * 
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void init(FormSectionEvent e) throws FormProcessException {
        s_log.debug("here in init");
        FormData data = e.getFormData();
        PageState state = e.getPageState();
        ContentGroupContainer item = 
            (ContentGroupContainer) getItemSelectionModel().getSelectedObject(state);
        ContentGroup group = getCurrentGroup(item, m_attributeName);
        if (group != null) {
            data.put(ContentGroup.NAME, group.getName());
        }

        if (group != null) {
            ItemCollection collection = group.getContentItems();
            Collection selected = new ArrayList();
            while (collection.next()) {
                s_log.debug("NAME = " + 
                                   collection.getContentItem().getName());
                s_log.debug("ID = " + collection.getID());
                m_checkboxGroup.addOption
                    (new Option(collection.getID().toString(), 
                                collection.getContentItem().getName()), state);
                selected.add(collection.getID().toString());
            }
            data.put(ASSOCIATED_ITEMS, selected.toArray());
        }
    }


    /**
     * Process the form. Children should override this method to save
     * the user's changes to the database.
     * 
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void process(FormSectionEvent e) throws FormProcessException {
        s_log.debug("here in process");
        ContentGroupContainer item = 
            (ContentGroupContainer) getItemSelectionModel()
            .getSelectedObject(e.getPageState());

        FormData data = e.getFormData();

        if ( item != null && !getSaveCancelSection().getCancelButton()
             .isSelected( e.getPageState() ) ) {
            
            ContentGroup group = getCurrentGroup(item, m_attributeName);
            if (group == null) {
                group = new ContentGroup();
                group.setMaster((ContentItem) item);
                setContentGroup(item, group, m_attributeName);
            }
	    group.setName((String)data.get(ContentGroup.NAME));
	    group.save ();
            
            // iterate through the values so that we only remove items
            // that have been unchecked.
            ItemCollection collection = group.getContentItems();
            Collection ids = new ArrayList();
            HashMap itemMap = new HashMap();
            while (collection.next()) {
                String id = collection.getID().toString();
                itemMap.put(id, collection.getContentItem());
                ids.add(id);
                s_log.debug("XXXX adding " + id);
                s_log.debug("XXXX isDeleted? " + collection.getContentItem().isDeleted());
            }
            String[] values =  
                (String[])m_checkboxGroup.getValue(e.getPageState());
            if (values != null) {
                for (String value : values) {
                    ids.remove(value);
                    s_log.debug("marking " + value + " for keeping");
                }
            }
            // now, we remove the itmes that were unselected
            Iterator iterator = ids.iterator();
            while (iterator.hasNext()) {
                s_log.debug("Removing item");
                group.removeContentItem
                    ((ContentItem)itemMap.get((String)iterator.next()));
            }
            
            ContentItem newItem = (ContentItem)data.get(RELATED_ITEM);
            if (newItem != null) {
                group.addContentItem(newItem);
            }

            group.save();
            ((ContentItem) item).save();
        }
    }


    /**
     *  This returns the correct group for the attribute name
     */
    private ContentGroup getCurrentGroup(ContentGroupContainer item, String attributeName) {
        return item.getContentGroup(attributeName);
    }


    /**
     *  This sets the group for the given attribute name
     */
    private void setContentGroup(ContentGroupContainer item, ContentGroup group, 
                                 String attributeName) {
        item.setContentGroup(attributeName, group);
    }

    /**
     * The name of the Content Type to restrict the ItemSearchWidget to.
     * To allow the user to search for any content type, this should
     * return null.
     * 
     * @return 
     */
    protected String getSearchContentType() {
        return null;
    }
}

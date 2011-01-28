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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A form for editing subclasses of ContentItem. This is just a convenience
 * class.
 *
 * @author Stanislav Freidin (stas@arsdigita.com)
 * @version $Revision: #13 $ $DateTime: 2004/08/17 23:15:09 $
 **/
public abstract class BasicItemForm extends FormSection
        implements FormInitListener, FormProcessListener,
                   FormValidationListener {

    private static final Logger s_log = Logger.getLogger(BasicItemForm.class);
    private final ItemSelectionModel m_itemModel;
    private SaveCancelSection m_saveCancelSection;
    private FormSection m_widgetSection;
    public static final String NAME = ContentItem.NAME;
    public static final String TITLE = ContentPage.TITLE;
    public static final String LANGUAGE = ContentItem.LANGUAGE;

    /**
     * Construct a new BasicItemForm
     *
     * @param formName the name of this form
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current item
     */
    public BasicItemForm(String formName, ItemSelectionModel itemModel) {
        super(new ColumnPanel(2));
        //super(formName, new ColumnPanel(2));
        m_widgetSection = new FormSection(new ColumnPanel(2, true));
        super.add(m_widgetSection, ColumnPanel.INSERT);
        m_itemModel = itemModel;

        ColumnPanel panel = (ColumnPanel) getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("100%");

        addWidgets();

        m_saveCancelSection = new SaveCancelSection();
        super.add(m_saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        addInitListener(this);
        addProcessListener(this);
        addValidationListener(this);
    }

    /**
     * Construct a new BasicItemForm with nothing on it
     *
     * @param formName the name of this form
     * @param columnPanel the columnpanel of the form
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current item
     */
    public BasicItemForm(String formName,
                         ColumnPanel columnPanel,
                         ItemSelectionModel itemModel) {
        super(columnPanel);
        //super(formName, columnPanel);
        m_widgetSection =
        new FormSection(new ColumnPanel(columnPanel.getNumCols(), true));
        super.add(m_widgetSection, ColumnPanel.INSERT);
        m_itemModel = itemModel;
    }

    /**
     * instanciate and add the save/cancel section for this form
     */
    public void addSaveCancelSection() {
        m_saveCancelSection = new SaveCancelSection();
        super.add(m_saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
    }
    private Label m_script = new Label(
            "<script language=\"javascript\" src=\"/javascript/manipulate-input.js\"></script>",
            false);

    /**
     * Add various widgets to the form. Child classes should override
     * this method to perform all their widget-adding needs
     */
    protected void addWidgets() {
        //add(new FormErrorDisplay(this), ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        // JavaScript auto-name generation is off by default.
        // It is turned on under the following circumstances
        //
        // * If the name is null, upon starting edit of the title
        // * If the name is null, upon finishing edit of name
        //
        // The rationale is that, auto-name generation is useful
        // if the name is currently null, but once a name has been
        // created you don't want to subsequently change it since
        // it breaks URLs & potentially overwrites the user's
        // customizations.

        //jensp 2011-01-28: For some content types it is maybe useful to change
        //the label of this field to something other than 'title'. This can now
        //be done by overwriting the getTitleLabel() method.
        //add(new Label(GlobalizationUtil.globalize("cms.ui.authoring.page_title")));
        add(new Label(getTitleLabel()));
        TextField titleWidget = new TextField(new TrimmedStringParameter(TITLE));
        titleWidget.addValidationListener(new NotNullValidationListener());
        titleWidget.setOnFocus("if (this.form." + NAME + ".value == '') { "
                               + " defaulting = true; this.form." + NAME
                               + ".value = urlize(this.value); }");
        titleWidget.setOnKeyUp(
                "if (defaulting) { this.form." + NAME
                + ".value = urlize(this.value) }");
        add(titleWidget);

        //jensp 2011-01-28: For some content types it is maybe useful to change
        //the label of this field to something other than 'title'. This can now
        //be done by overwriting the getNameLabel() method.
        //add(new Label(GlobalizationUtil.globalize("cms.ui.authoring.name_url")));
        add(new Label(getNameLabel()));
        TextField nameWidget = new TextField(new TrimmedStringParameter(NAME));
        nameWidget.addValidationListener(new NameValidationListener());
        nameWidget.setOnFocus("defaulting = false");
        nameWidget.setOnBlur(
                "if (this.value == '') "
                + "{ defaulting = true; this.value = urlize(this.form." + TITLE
                + ".value) } " + " else { this.value = urlize(this.value); }");
        add(nameWidget);

    }

    public void generateXML(PageState ps, Element parent) {
        m_script.generateXML(ps, parent);
        super.generateXML(ps, parent);
    }

    /**
     * @return the item selection model used in this form
     */
    public ItemSelectionModel getItemSelectionModel() {
        return m_itemModel;
    }

    /**
     * @return the save/cancel section for this form
     */
    public SaveCancelSection getSaveCancelSection() {
        return m_saveCancelSection;
    }

    /**
     * Perform form initialization. Children should override this
     * this method to pre-fill the widgets with data, instantiate
     * the content item, etc.
     */
    public abstract void init(FormSectionEvent e) throws FormProcessException;

    /**
     * Process the form. Children should override this method to save
     * the user's changes to the database.
     */
    public abstract void process(FormSectionEvent e) throws FormProcessException;

    /**
     * Validate the form. Children should override this method to provide
     * custom form validation.
     */
    public void validate(FormSectionEvent e) throws FormProcessException {
        // do nothing
    }

    /**
     * Ensure that the name of an item is unique within a folder. A "New
     * item" form should call this method in the validation listener.
     *
     * @param parent the folder in which to check
     * @param event the {@link FormSectionEvent} which was passed to the
     *    validation listener
     * @throws FormProcessException if the folder already contains an item
     * with the name the user provided on the input form.
     */
    public void validateNameUniqueness(Folder parent, FormSectionEvent event)
            throws FormProcessException {

        FormData data = event.getFormData();
        String newName = (String) data.get(NAME);

        validateNameUniqueness(parent, event, newName);
    }

    public void validateNameUniqueness(Folder parent, FormSectionEvent event,
                                       String newName)
            throws FormProcessException {
        if (newName != null) {
            final String query = "com.arsdigita.cms.validateUniqueItemName";
            DataQuery dq = SessionManager.getSession().retrieveQuery(query);
            dq.setParameter("parentId", parent.getID());
            dq.setParameter("name", newName.toUpperCase());

            if (dq.size() > 0) {
                // we need to add all of the items that are
                // different versions of this item to the list
                // so that we do not throw an error if those
                // are the only problems
                BigDecimal itemID = null;

                ContentItem item = null;
                if (getItemSelectionModel() != null) {
                    item = (ContentItem) getItemSelectionModel().
                            getSelectedObject(event.getPageState());
                }
                if (item == null) {
                    // this means it is a creation form
                    throw new FormProcessException(
                            "An item with this name already exists");
                }
                Collection list = getAllVersionIDs(item);
                while (dq.next()) {
                    itemID = (BigDecimal) dq.get("itemID");
                    if (!list.contains(itemID)) {
                        dq.close();
                        throw new FormProcessException(
                                "An item with this name already exists");
                    }
                }
            }
        }
    }

    /**
     * Ensure that the name of an item is unique within a category. This 
     * should only be called from the validation listener of an "edit" form.
     *
     * @param event the {@link FormSectionEvent} which was passed to the
     *    validation listener
     * @param id The id of the item that is being checked.  This must no
     *           be null.
     * @throws FormProcessException if the folder already contains an item
     * with the name the user provided on the input form.
     */
    public void validateNameUniquenessWithinCategory(FormSectionEvent event,
                                                     BigDecimal id)
            throws FormProcessException {
        if (id == null) {
            s_log.warn("Trying to validation the name uniqueness without "
                       + " a valid item is invalid.  This method should only "
                       + " be called in \"edit\" forms.  The passed in id "
                       + " was null.");
            return;
        }
        // now we check to make sure that the new name is valid
        // within every category that the item is mapped to
        // this is only necessary for category browsing
        FormData data = event.getFormData();
        String url = (String) data.get(NAME);
        if (url == null) {
            return;
        }
        DataQuery query =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.categorization.getAllItemURLsForCategoryFromItem");
        query.setParameter("itemID", id);
        query.addEqualsFilter("lower(url)", url.toLowerCase());
        if (query.size() > 0) {
            // we need to make sure that the conflicting item is not a
            // pending or live version of the same item
            BigDecimal itemID = null;

            ContentItem item =
                        (ContentItem) getItemSelectionModel().getSelectedObject(event.
                    getPageState());
            Collection list = getAllVersionIDs(item);
            try {
                while (query.next()) {
                    itemID = (BigDecimal) query.get("itemID");
                    if (!list.contains(itemID)) {
                        StringBuffer buffer =
                                     new StringBuffer("There are conflicts with this URL.  "
                                                      + "Specifically, there is at least one item "
                                                      + "in the same category as this item with "
                                                      + "the name (url) of ");
                        buffer.append(url);
                        throw new FormProcessException(buffer.toString());
                    }
                }

            } finally {
                query.close();
            }
        }
    }

    public static Collection getAllVersionIDs(ContentItem item) {
        // we need to add all of the items that are different versions
        // of this item to the list so that we do not throw an error
        // if those are the only problems
        ArrayList list = new ArrayList();
        list.add(item.getID());
        ContentItem live = item.getLiveVersion();
        if (live != null) {
            list.add(live.getID());
        }
        ItemCollection collection = item.getPendingVersions();
        while (collection.next()) {
            list.add(collection.getID());
        }
        return list;
    }

    /**
     * Adds a component to this container.
     *
     * @param pc the component to add to this BasicPageForm
     * */
    public void add(Component pc) {
        m_widgetSection.add(pc);
    }

    /**
     * Adds a component with the specified layout constraints to this
     * container. Layout constraints are defined in each layout container as
     * static ints. Use a bitwise OR to specify multiple constraints.
     *
     * @param pc the component to add to this container
     *
     * @param constraints layout constraints (a
     * bitwise OR of static ints in the particular layout)
     * */
    public void add(Component pc, int constraints) {
        m_widgetSection.add(pc, constraints);
    }

    /**
     * jensp, 2011-01-28
     * This method can be overridden to change the label of the title field.
     * To change to label of the title field can be useful for some
     * content types. For example, for an organization the label "Title" for
     * the field is may confusing for the normal user. For such a content type,
     * the label would be changed to something like "Name of the organization".
     *
     * @return (Content for the) Label for the title field as string
     */
    protected String getTitleLabel() {
        return (String) GlobalizationUtil.globalize(
                "cms.ui.authoring.page_title").
                localize();
    }

    /**
     * jensp, 2011-01-28
     * This method does the same as {@link #getTitleLabel() } for the label of
     * the name (URL) field.
     *
     * @return (Content for the) Label for the name field as string
     */
    protected String getNameLabel() {
        return (String) GlobalizationUtil.globalize("cms.ui.authoring.name_url").
                localize();
    }
}

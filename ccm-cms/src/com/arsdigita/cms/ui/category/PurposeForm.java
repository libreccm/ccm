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
package com.arsdigita.cms.ui.category;


import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.util.SequentialMap;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryPurpose;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.OID;
import com.arsdigita.toolbox.ui.Cancellable;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.TooManyListenersException;

/**
 * Form which assigns purposes to a category
 *
 *  Displays two listboxes for assigning purposes to categories, with two
 * submit buttons to move purposes back and forth. The left
 * listbox displays all available purposes which have not been
 * assigned to the current category. The right listbox displays all purposes
 * assigned to the current category.
 * <p>
 *
 *
 * @author Scott Seago (scott@arsdigita.com)
 * @version $Id: PurposeForm.java 754 2005-09-02 13:26:17Z sskracic $
 */
public class PurposeForm extends Form
    implements FormProcessListener, FormValidationListener,
               Cancellable {

    private RequestLocal m_assigned;
    private Submit m_assign, m_remove;
    Label m_freeLabel, m_assignedLabel;


    public static final String FREE = "free";
    public static final String ASSIGNED = "assigned";
    public static final String ASSIGN = "assign";
    public static final String REMOVE = "remove";
    public static final int SELECT_WIDTH = 30;
    public static final int SELECT_HEIGHT = 10;
    public static final String FILLER_OPTION = StringUtils.repeat("_", SELECT_WIDTH);

    private static final Logger s_log = Logger.getLogger(PurposeForm.class);
    private final CategoryRequestLocal m_category;

    private Submit m_cancelButton;

    /**
     * Construct a new PurposeForm component
     *
     * @param model the name of the form
     */
    public PurposeForm(final CategoryRequestLocal category) {
        super("PurposeForm", new ColumnPanel(3));

        m_category = category;

        ColumnPanel panel = (ColumnPanel)getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "0%");
        panel.setColumnWidth(2, "0%");
        panel.setColumnWidth(3, "0");
        panel.setWidth("0%");
        panel.setClassAttr("CMS Admin");

        // Create the request local
        m_assigned = new RequestLocal() {
                public Object initialValue(PageState state) {
                    PurposeMap m = new PurposeMap();
                    initAssignedPurposes(state, m);
                    return m;
                }
            };

        // Top row
        m_freeLabel = new Label(GlobalizationUtil
                          .globalize("cms.ui.category.purpose_available"),  false);
        m_freeLabel.setFontWeight(Label.BOLD);
        add(m_freeLabel, ColumnPanel.LEFT);

        add(new Label("&nbsp;", false));

        m_assignedLabel = new Label(GlobalizationUtil
                              .globalize("cms.ui.category.purpose_assigned"),  false);
        m_assignedLabel.setFontWeight(Label.BOLD);
        add(m_assignedLabel, ColumnPanel.LEFT);

        // Middle Row
        SingleSelect freeWidget =
            new SingleSelect(new BigDecimalParameter(FREE));
        try {
            freeWidget.addPrintListener(new FreePrintListener());
        } catch (TooManyListenersException e) {
            throw new UncheckedWrapperException("TooManyListeners" + e.getMessage(), e);
        }
        freeWidget.setSize(SELECT_HEIGHT);
        add(freeWidget);

        BoxPanel box = new BoxPanel(BoxPanel.VERTICAL, true);
        box.setWidth("2%");
        addSubmitButtons(box);
        add(box, ColumnPanel.CENTER | ColumnPanel.MIDDLE);

        SingleSelect assignedWidget =
            new SingleSelect(new BigDecimalParameter(ASSIGNED));
        try {
            assignedWidget.addPrintListener(new AssignedPrintListener());
        } catch (TooManyListenersException e) {
            throw new UncheckedWrapperException("TooManyListeners" + e.getMessage(), e);
        }
        assignedWidget.setSize(SELECT_HEIGHT);
        add(assignedWidget);

        // Add listeners
        addProcessListener(this);
        addValidationListener(this);

        setClassAttr("PurposeForm");

        m_cancelButton = new Submit("Finish");
        add(m_cancelButton, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
    }

    protected void addSubmitButtons(Container c) {
        addAssignButton(c);
        addRemoveButton(c);
    }

    protected void addAssignButton(Container c) {
        m_assign = new Submit(ASSIGN, ">>");
        m_assign.setSize(10);
        c.add(m_assign);
    }
    protected void addRemoveButton(Container c) {
        m_remove = new Submit(REMOVE, "<<");
        m_remove.setSize(10);
        c.add(m_remove);
    }
    /**
     * Set the caption of the unassigned categories label
     *
     * @param caption the new caption
     */
    public void setUnassignedCaption(String caption) {
        m_freeLabel.setLabel(caption);
    }

    /**
     * Set the caption of the assigned categories label
     *
     * @param caption the new caption
     */
    public void setAssignedCaption(String caption) {
        m_assignedLabel.setLabel(caption);
    }

    /**
     * @param s the page state
     * @return a {@link PurposeMap} of all assigned categories
     */
    public PurposeMap getAssignedPurposes(PageState s) {
        return (PurposeMap)m_assigned.get(s);
    }

    // A print listener which populates the listbox with all
    // unassigned purposes
    //
    // WARNING: This method is currently slow. It should be
    // optimized to do a connect by query that excludes all
    // categories which are already assigned.
    private class FreePrintListener implements PrintListener {

        public void prepare(PrintEvent e) {
            OptionGroup o = (OptionGroup)e.getTarget();
            PageState state = e.getPageState();

            PurposeMap assigned = getAssignedPurposes(state);
            Iterator allPurposes = CategoryPurpose.getAllPurposes().iterator();

            while(allPurposes.hasNext()) {
                CategoryPurpose purpose = (CategoryPurpose) allPurposes.next();
                String id = purpose.getID().toString();
                String name = purpose.getName();

                // Process the node unless:
                // The purpose is assigned
                // The purpose's name is empty
                if(!assigned.containsKey(id) && name.length() > 0) {
                    o.addOption(new Option(id, name));
                }
            }

            addFillerOption(o);
        }
    }

    /**
     * Populate a {@link PurposeMap} with all purposes which are assigned to
     * the catgegory.
     *
     * @param map The sequential map of all purposes which are assigned to
     *   the current category.
     * @param state The page state
     */
    protected void initAssignedPurposes(PageState state, PurposeMap map) {
        Category category = m_category.getCategory(state);
        Collection purposes = category.getPurposes();

        for ( Iterator i = purposes.iterator(); i.hasNext(); ) {
            CategoryPurpose p = (CategoryPurpose) i.next();
            map.add(p);
        }
        return;
    }

    /**
     * Assign a purpose, moving it from the list on the left
     * to the list on the right
     *
     * @param state the page state
     * @param purpose The purpsoe to add
     */
    public void assignPurpose(PageState state, CategoryPurpose purpose) {
        Category category = m_category.getCategory(state);
        if (category.canEdit()) {
            category.addPurpose(purpose);
            category.save();
        }
    }

    /**
     * Unassign a purpose, moving it from the list on the right
     * to the list on the left
     *
     * @param state the page state
     * @param purpose The purpsoe to add
     */
    public void unassignPurpose(PageState state, CategoryPurpose purpose) {
        Category category = m_category.getCategory(state);
        if (category.canEdit()) {
            category.removePurpose(purpose);
            category.save();
        }
    }

    // Populates the "assigned purposes" widget
    private class AssignedPrintListener implements PrintListener {

        public void prepare(PrintEvent e) {
            OptionGroup o = (OptionGroup)e.getTarget();
            PageState state = e.getPageState();
            PurposeMap m = getAssignedPurposes(state);

            if(!m.isEmpty()) {
                for (Iterator i = m.values().iterator(); i.hasNext(); ) {
                    CategoryPurpose p = (CategoryPurpose)i.next();
                    o.addOption(new Option(p.getID().toString(), p.getName()));
                }
            } else {
                o.addOption(new Option("", "-- none --"));
            }

            addFillerOption(o);
        }
    }

    // Process the form: assign/unassign categories
    public void process(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();
        FormData data = e.getFormData();
        BigDecimal id;

        if(m_assign.isSelected(state)) {
            id = (BigDecimal) data.get(FREE);

            // Assign a new category
            try {
                CategoryPurpose purpose =
                        new CategoryPurpose(
                                new OID(CategoryPurpose.BASE_DATA_OBJECT_TYPE, id));
                assignPurpose(state, purpose);
                // Highlight the item
                data.put(ASSIGNED, id);
            } catch (DataObjectNotFoundException ex) {
                s_log.error("Can't find CategoryPurpose", ex);
                throw new FormProcessException(ex);
            } catch (com.arsdigita.persistence.PersistenceException pe) {
                s_log.error("Ignored persistence exception", pe);
            }


        } else if(m_remove.isSelected(state)) {
            id = (BigDecimal) data.get(ASSIGNED);

            // Unassign a category
            try {
                CategoryPurpose purpose =
                        new CategoryPurpose(
                                new OID(CategoryPurpose.BASE_DATA_OBJECT_TYPE, id));
                unassignPurpose(state, purpose);
                // Highlight the item
                data.put(FREE, id);
            } catch (DataObjectNotFoundException ex) {
                s_log.error("Can't find CategoryPurpose", ex);
                throw new FormProcessException(ex);
            }
        }
    }

    // Validate the form: make sure that a category is selected
    // for the remove/assign buttons
    public void validate(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();
        FormData data = e.getFormData();

        if(m_assign.isSelected(state)) {
            if(data.get(FREE) == null) {
                data.addError("Please select a purpose to assign");
            }
        } else if(m_remove.isSelected(state)) {
            if(data.get(ASSIGNED) == null) {
                data.addError("Please select a purpose to remove");
            }
        }
    }

    // Add a "filler" option to the option group in order to ensure
    // the correct horizontal width
    private static void addFillerOption(OptionGroup o) {
        o.addOption(new Option("", FILLER_OPTION));
    }

    /**
     * Fetch the cancel button.
     *
     * @return The cancel button
     */
    public Submit getCancelButton() {
        return m_cancelButton;
    }

    public boolean isCancelled(final PageState state) {
        return m_cancelButton.isSelected(state);
    }

    /**
     * A convenience method that abstracts SequentialMap
     * to deal with categories
     */
    protected static class PurposeMap extends SequentialMap {

        public PurposeMap() {
            super();
        }

        public void add(CategoryPurpose p) {
            super.put(p.getID().toString(), p);
        }
    }



}

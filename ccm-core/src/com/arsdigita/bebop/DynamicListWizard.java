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
package com.arsdigita.bebop;

import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.util.Assert;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;


/**
 * A {@link SplitWizard} that can be used to maintain a list of items.<p>
 *
 * The selector for the wizard consists of a list of items and an "add"
 * link. The list is backed by an arbitrary ListModel that, presumably,
 * will load the list of items from the database.<p>
 *
 * The right side of the wizard will choose between two panes.
 * The "edit" pane will edit an item from the list, and the
 * "add" pane will add a new item to the list. The "add" pane
 * will be visible only when the user clicks on the "add" link.
 *
 */
public class DynamicListWizard extends SplitWizard implements Resettable {

    public static final String versionId = "$Id: DynamicListWizard.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Label m_listLabel;
    private ToggleLink m_addLink;
    private Component m_editPane;
    private Component m_addPane;
    private Component m_list;
    private SingleSelectionModel m_sel;

    /**
     * The name of the state parameter that stores the current selection.
     */
    public static final String CURRENT_PANE = "cp";

    /**
     * Creates a new <code>DynamicListWizard</code>.
     *
     * @param listLabel the label that will appear above the selector list.
     * @param selector  the component that will be responsible for displaying
     *   the list of items. Typically, this will be a {@link List} or a {@link Tree}
     * @param selectionModel the <code>SingleSelectionModel</code> that belongs
     *   to the selector. For example, the selection model for a {@link List}
     *   may be obtained by calling {@link List#getSelectionModel()}
     * @param addLinkLabel the label for the "add" link.
     * @param defaultPane  the component that will be shown if nothing is
     *   selected.
     */
    public DynamicListWizard (
                              String listLabel, Component selector,
                              SingleSelectionModel selectionModel,
                              String addLinkLabel,
                              Component defaultPane
                              ) {
        super(defaultPane);

        // Create the list label
        m_listLabel = new Label(listLabel);
        m_listLabel.setFontWeight(Label.BOLD);

        // Create the selection model
        m_sel = selectionModel;
        ComponentSelectionModel csel = new AddEditSelectionModel(m_sel);

        // Deselect the link whenever the list is clicked
        m_sel.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    PageState s = e.getPageState();
                    if ( ((AddEditSelectionModel)getSelectionModel()).isListSelected(s) ) {
                        m_addLink.setSelected(s, false);
                    }
                }
            });

        setSelectionModel(csel);
        m_list = selector;

        // Create the "add" link
        m_addLink = new ToggleLink(addLinkLabel);
        m_addLink.setClassAttr("actionLink");
        Label l = new Label(addLinkLabel);
        l.setFontWeight(Label.BOLD);
        m_addLink.setSelectedComponent(l);

        // Deselect the list when the link is clicked
        m_addLink.addActionListener(new ActionListener() {
                // Toggle link has been clicked
                public void actionPerformed(ActionEvent e) {
                    PageState s = e.getPageState();
                    if ( ((AddEditSelectionModel)getSelectionModel()).isLinkSelected(s) ) {
                        getSelectionModel().clearSelection(s);
                    }
                }
            });

        // Lay out components
        BoxPanel box = new BoxPanel(BoxPanel.VERTICAL);
        box.setBorder(0);
        layoutComponents(box);
        setSelector(box);

        // Set the XML class
        setClassAttr("dynamicListWizard");
    };

    /**
     * Creates a new <code>DynamicListWizard</code> that uses a plain
     * {@link List} in order to show the list of items in the left pane
     *
     * @param listLabel the label that will appear above the selector list.
     * @param modelBuilder the list model builder used to construct the list.
     *   Typically, the list model builder will load some items from the
     *   database.
     * @param addLinkLabel the label for the "add" link.
     * @param defaultPane  the component that will be shown if nothing is
     *   selected.
     */
    public DynamicListWizard (
                              String listLabel, ListModelBuilder modelBuilder, String addLinkLabel,
                              Component defaultPane
                              ) {
        this(listLabel, new List(modelBuilder), addLinkLabel, defaultPane);
    }

    /**
     * Creates a new <code>DynamicListWizard</code> that uses a plain
     * {@link List} in order to show the list of items in the left pane
     *
     * @param modelBuilder the list model builder used to construct the list.
     *   Typically, the list model builder will load some items from the
     *   database.
     * @param addLinkLabel the label for the "add" link.
     * @param defaultPane  the component that will be shown if nothing is
     *   selected.
     */
    public DynamicListWizard (
                              ListModelBuilder modelBuilder, String addLinkLabel,
                              Component defaultPane
                              ) {
        this("", new List(modelBuilder), addLinkLabel, defaultPane);
    }

    /**
     * Creates a new <code>DynamicListWizard</code> that uses the
     * specified {@link List} in order to show the list of items in the left pane
     *
     * @param listLabel the label that will appear above the selector list.
     * @param list use this <code>List</code> to display the items in
     *   the left pane
     * @param addLinkLabel the label for the "add" link.
     * @param defaultPane  the component that will be shown if nothing is
     *   selected.
     */
    public DynamicListWizard (
                              String listLabel, List list, String addLinkLabel,
                              Component defaultPane
                              ) {
        this(listLabel, list, list.getSelectionModel(), addLinkLabel, defaultPane);
    }

    /**
     * Adds components to a container. This is useful when overriding this class.
     *
     * @param c the container to which the components are added
     * */
    protected void layoutComponents(Container c) {
        c.add(getListLabel());
        c.add(getListingComponent());
        c.add(getAddLink());
    }

    /**
     * Gets the label that appears above the listing component.
     *
     * @return the list label.
     */
    public final Label getListLabel() {
        return m_listLabel;
    }

    /**
     * Gets the component that contains the list of all the items
     * that this wizard manages. Typically, the component will be a {@link List}
     *
     * @return the listing component
     */
    public final Component getListingComponent() {
        return m_list;
    }

    /**
     * Return the <code>List</code> of items in the left pane
     * @deprecated use getListingComponent instead
     */
    public List getList() {
        Component c = getListingComponent();
        Assert.assertTrue(c instanceof List,
                          "The listing component is not a List, but " + c.getClass().getName());
        return (List)c;
    }

    /**
     * Gets the "add" link.
     * @return the "add" link.
     */
    public final ToggleLink getAddLink() {
        return m_addLink;
    }

    /**
     * Clears the selection on the dynamic list and the toggle link.
     *
     * @param state the current page state
     */
    public void reset(PageState state) {
        getSelectionModel().clearSelection(state);
        m_addLink.setSelected(state, false);
    }

    /**
     * Sets the "add" pane. Throws an exception if the "add" pane has
     * already been specified.
     *
     * @param c the component to show if the "add" link is
     *   selected
     */
    public void setAddPane(Component c) {
        Assert.assertTrue(m_addPane == null, "Add pane has already been set");

        if(!super.contains(c)) {
            super.add(c);
        }

        m_addPane = c;
    }

    /**
     * Gets the "add" pane that will be used to add items
     *         to the list.
     * @return the "add" pane that will be used to add items
     *         to the list.
     */
    public final Component getAddPane() {
        return m_addPane;
    }

    /**
     * Sets the "edit" pane. Throws an exception if the "edit"
     * pane has already been specified.
     *
     * @param c the component to be shown if one of the list items
     *   is selected
     */
    public void setEditPane(Component c) {
        Assert.assertTrue(m_editPane == null, "Edit pane has already been set");

        if(!super.contains(c)) {
            super.add(c);
        }

        m_editPane = c;
    }

    /**
     * Gets the "edit" pane that will be used to edit the
     *         items in the list.
     * @return the "edit" pane that will be used to edit the
     *         items in the list.
     */
    public final Component getEditPane() {
        return m_editPane;
    }

    /**
     * A special ComponentSelectionModel. If the list is selected,
     * returns the Edit component. Otherwise, returns the Add component.
     * Uses a state parameter to store the selection on the list
     */
    private class AddEditSelectionModel implements ComponentSelectionModel {

        private SingleSelectionModel m_model;

        /**
         * Construct a new <code>AddEditSelectionModel</code>
         */
        public AddEditSelectionModel(SingleSelectionModel model) {
            m_model = model;
        }

        /**
         * @return <code>true</code> if either the link or the internal
         * model is selected; <code>false</code> otherwise.
         */
        public boolean isSelected(PageState state) {
            return (this.isLinkSelected(state) || this.isListSelected(state));
        }

        /**
         * @return <code>true</code> if the toggle link is selected;
         * <code>false</code> otherwise.
         */
        public boolean isLinkSelected(PageState state) {
            return getAddLink().isSelected(state);
        }

        /**
         * @return true if the list is selected;
         * <code>false</code> otherwise.
         */
        public boolean isListSelected(PageState state) {
            return m_model.isSelected(state);
        }

        // Return either the Add or the Edit component, depending
        // on whether the list or the link is selected
        public Component getComponent(PageState state) {
            if(this.isLinkSelected(state)) {
                return getAddPane();
            }

            if(this.isListSelected(state)) {
                return getEditPane();
            }

            // Otherwise, nothing is selected
            return getDefaultPane();
        }

        // Passthrough

        public Object getSelectedKey(PageState s) {
            return m_model.getSelectedKey(s);
        }

        public void setSelectedKey(PageState s, Object key) {
            m_model.setSelectedKey(s, key);
        }

        public void clearSelection(PageState s) {
            m_model.clearSelection(s);
        }

        public void addChangeListener(ChangeListener l) {
            m_model.addChangeListener(l);
        }

        public void removeChangeListener(ChangeListener l) {
            m_model.removeChangeListener(l);
        }

        public ParameterModel getStateParameter() {
            return m_model.getStateParameter();
        }

    }

}

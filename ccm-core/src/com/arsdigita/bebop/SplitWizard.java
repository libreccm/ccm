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

import com.arsdigita.bebop.util.GlobalizationUtil ; 
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

/**
 * A wizard that associates links with components.
 * The wizard consists of two parts: the <b>model</b> and the
 * <b>selector</b>. <p>
 *
 * The <b>selector</b> is a component that displays a list of
 * choices in the form of links, radio buttons, and so on. Child classes
 * should call the <code>setSelector(Component selector)</code>
 * method in order to provide the selector. The {@link List} component can
 * be used as a selector, as shown here:
 *
 * <blockquote><pre><code>   ...
 *  setSelector(new List(getSelectionModel));
 *   ...</code></pre></blockquote>
 *
 * The actual appearance of the selector is completely irrelevant, as long as
 * it somehow incorporates the model in order to decide which component is
 * currently selected.
 * <p>
 * The <b>model</b> is a {@link ComponentSelectionModel}, which is a
 * subclass of {@link SingleSelectionModel}. The model
 * returns a component that should be shown in the right pane of the
 * wizard. The model decides which component to return based on the value
 * of the current selection. A {@link MapComponentSelectionModel} could
 * be used for this purpose. The model may be supplied in the constructor,
 * or set via the
 * <code>setSelectionModel(ComponentSelectionModel model)</code>
 * method.
 *
 * <p>
 * Both the selector and the model <b>must</b> be supplied
 * before the wizard can be displayed.
 *
 * @version $Id: SplitWizard.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class SplitWizard extends SplitPanel {

    private ComponentSelectionModel m_sel;
    private Component m_defaultPane;
    private Component m_selector;

    /**
     * Constructs a new, empty SplitWizard without a selector.
     *
     * @param header the header that will be shown across the top
     *  of the wizard
     *
     * @param model       the ComponentSelectionModel that is used to
     *   determine which component should be shown in the  right pane
     *
     * @param defaultPane the component that will be shown if there is no
     *   selected component. Usually, this parameter is a simple label.
     */
    public SplitWizard (Component header, ComponentSelectionModel model, 
                        Component defaultPane ) {
        this(model, defaultPane);
        setHeader(header);
    }

    /**
     * Construct a new, empty SplitWizard without a selector.
     *
     * @param model   the ComponentSelectionModel that is used to
     *   determine which component should be shown in the  right pane
     *
     * @param defaultPane the component that will be shown if there is no
     *   selected component. Usually, this parameter is a simple label.
     */
    public SplitWizard (ComponentSelectionModel model, Component defaultPane) {
        super();
        m_defaultPane = defaultPane;
        if(m_defaultPane != null)
            super.add(m_defaultPane);
        m_sel = model;
        setRightComponent(new WizardPaneSelector());

        // Set the XML class attribute
        setClassAttr("wizard");
    }

    /**
     * Constructs a new, empty SplitWizard.
     *
     * @param model    the ComponentSelectionModel that is used by the
     *                 selector
     */
    public SplitWizard(ComponentSelectionModel model) {
        this(model, new Label(GlobalizationUtil.globalize(
                        "bebop.please_select_choice_from_the_list_on_the_left")));
    }

    /**
     * Constructs a new, empty SplitWizard without a selector or a model
     *
     * @param defaultPane the component that will be shown if there is no
     *                    selected component. Usually, this parameter is
     *                    a simple label.
     */
    public SplitWizard (Component defaultPane) {
        this(null, defaultPane);
    }

    /**
     * Constructs a new, empty SplitWizard without a selector or a model.
     *
     * @param model    the ComponentSelectionModel that is used by the
     *                 selector
     */
    public SplitWizard() {
        this(null, new Label(GlobalizationUtil.globalize(
                       "bebop.please_select_choice_from_the_list_on_the_left")));
    }

    /**
     * Sets the selector component. The selector <b>must</b> incorporate
     * the model returned by <code>getSelectionModel()</code>
     * in order to decide which component is currently selected.
     *
     * @param selector the selector
     */
    public void setSelector(Component selector) {
        setLeftComponent(selector);
        m_selector = selector;
    }

    /**
     * Returns the current selector.
     *
     * @return the component that will be shown in the left
     *         pane of the wizard.
     */
    public final Component getSelector() {
        return m_selector;
    }

    /**
     * Sets the selection model for this wizard. Note that all the
     * listeners on the old selection model will be discarded.
     *
     * @param model the selection model for the wizard that
     *   is responsible for returning the currently selected
     *   component
     */
    public void setSelectionModel(ComponentSelectionModel model) {
        Assert.isUnlocked(this);
        m_sel = model;
    }

    /**
     * Gets the selection model for this wizard.
     * @return the selection model for this wizard.
     *
     */
    public final ComponentSelectionModel getSelectionModel() {
        return m_sel;
    }

    /**
     * Sets the default pane.
     * @param the component to use as the default pane
     */
    public void setDefaultPane(Component c) {
        Assert.isUnlocked(this);

        if(m_defaultPane != null) {
            throw new IllegalStateException("Default pane has already been set");
        }

        if(!super.contains(c)) {
            super.add(c);
        }

        m_defaultPane = c;
    }

    /**
     * Gets the default pane.
     * @return the default pane.
     */
    public final Component getDefaultPane() {
        return m_defaultPane;
    }

    /**
     * A dummy class which uses the selection to print the
     * appropriate component
     */
    private class WizardPaneSelector extends SimpleComponent {

        public WizardPaneSelector() {
            super();
        }

        public Component getVisibleComponent(PageState state) {
            if(!m_sel.isSelected(state)) {
                return m_defaultPane;
            } else {
                return m_sel.getComponent(state);
            }
        }

        // Wrap the component in a cell so that stylesheet class
        // will apply to it
        public void generateXML(PageState state, Element parent) {
            if(!isVisible(state)) return;

            Element cell = parent.newChildElement ("bebop:cell", BEBOP_XML_NS);
            exportAttributes(cell);
            getVisibleComponent(state).generateXML(state, cell);
        }

    }

    /**
     * A simple class which displays a label above some component.
     * Could be used on the right side of the wizard.
     */
    public static class HeaderPanel extends BoxPanel {

        private Label m_label;
        private Component m_child;

        /**
         * Construct a new HeaderPanel. The panel will display
         * a label at the top, and some component at the bottom.
         * The label may later be supplied with PrintListeners.
         *
         * @param label The label to display at the top
         * @param c The component to display at the bottom
         */
        public HeaderPanel(String label, Component c) {
            super(VERTICAL);
            setBorder(0);

            m_label = new Label(label);
            m_label.setFontWeight(Label.BOLD);
            add(m_label);

            m_child = c;
            add(m_child);
        }

        /**
         * Retrieve the label. This method may be used to add
         * print listeners to the label.
         *
         * @return The label
         */
        public final Label getLabel() {
            return m_label;
        }

        /**
         * @return the component at the bottom
         */
        public final Component getChild() {
            return m_child;
        }
    }
}

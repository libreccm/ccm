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


import com.arsdigita.bebop.list.DefaultListCellRenderer;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.util.SequentialMap;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;


/**
 * A {@link SplitWizard} that can be used to implement the
 * classic, static wizard.<p>
 *
 * The wizard is little more than a Map from labels to components.
 * The selector for the wizard shows all the available steps, and
 * when the user clicks a step, the corresponding component appears
 * on the right.<p>
 *
 * In addition, the wizard maintains a "progress step" state parameter.
 * All steps above the progress step will be disabled, until the
 * setProgress(PageState state, int progress) method is called.
 * Thus, the users are forced to proceed through the wizard in a
 * linear fashion.<p>
 *
 * The wizard does not provide the "Next" and "Previous" buttons.
 * However, it does provide the stepForward(PageState state) and
 * stepBack(PageState state) methods.<p>
 *
 * In addition, the wizard overrides the default pane behavior.
 * If no step is selected, the wizard automatically selects the default
 * step. The default step may be changed with
 */
public class MapWizard extends SplitWizard {

    public static final String versionId = "$Id: MapWizard.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * The name of the state parameter which stores the current selection
     */
    public static final String CURRENT_STEP = "cs";

    /**
     * The name of the state parameter which stores the progress
     */
    public static final String PROGRESS = "p";

    private SequentialMap m_labels;
    private SequentialMap m_panels;
    private Label m_listLabel;
    private List m_list;
    private StringParameter m_stepParam;
    private IntegerParameter m_progressParam;

    /**
     * Construct a new, empty MapWizard
     *
     * @param header      The header which will be shown across the top
     *  of the wizard
     *
     * @param listLabel   The label which will appear above the list of
     *  steps
     *
     */
    public MapWizard(String header, String listLabel) {
        super(new Label(header), null, null);

        // Create parameters
        m_stepParam = new StringParameter(CURRENT_STEP);
        m_progressParam = new IntegerParameter(PROGRESS);
        setDefaultProgress(0);

        // Create the selection model
        m_labels = new SequentialMap();
        m_panels = new SequentialMap();

        ParameterSingleSelectionModel s =
            new ParameterSingleSelectionModel(m_stepParam);

        // Ensure consistency
        s.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    PageState state = e.getPageState();
                    Object key = getCurrentStepKey(state);

                    if (key == null) {
                        return;
                    }

                    if (!m_panels.containsKey(key)) {
                        throw new IllegalArgumentException (
                                                            "Key " + key + " is not in the wizard"
                                                            );
                    }

                    int i = m_panels.findKey(key);
                    int prog = getProgress(state);
                    if (i > prog) {
                        throw new IllegalArgumentException (
                                                            "Key " + key + " identifies the component at index " + i +
                                                            ", but the highest enabled step is " + prog
                                                            );
                    }
                }
            });

        MapComponentSelectionModel sel =
            new MapComponentSelectionModel(s, m_panels);
        setSelectionModel(sel);

        // Create the selector
        m_list = new List(sel);
        m_list.setCellRenderer(new ProgressListCellRenderer());
        m_list.setListData(m_labels);

        BoxPanel box = new BoxPanel(BoxPanel.VERTICAL);
        box.setBorder(0);

        if (listLabel != null) {
            m_listLabel = new Label(listLabel);
            m_listLabel.setFontWeight(Label.BOLD);
            box.add(m_listLabel);
        }

        box.add(m_list);
        setSelector(box);

        // Set the XSL class attribute
        super.setClassAttr("mapWizard");
    }

    /**
     * Construct a new, empty MapWizard
     *
     * @param header      The header which will be shown across the top
     *  of the wizard
     *
     */
    public MapWizard(String header) {
        this(header, null);
    }

    /**
     * Register the "progress" parameter
     */
    public void register(Page p) {
        super.register(p);
        p.addComponent(this);
        p.addComponentStateParam(this, m_progressParam);
    }

    /**
     * Add a panel to the wizard
     *
     * @param key The unique key of the panel
     * @param label The label of the panel as it appears in the list on the left
     * @param c The component which will appear on the right
     */
    public void add(String key, String label, Component c) {
        if (m_labels.containsKey(key)) {
            throw new IllegalArgumentException(
                                               "Wizard already contains the key '" + key + "'"
                                               );
        }

        if (getDefaultStepKey() == null) {
            setDefaultStepKey(key);
        }

        m_labels.put(key, label);
        m_panels.put(key, c);
        super.add(c);
    }

    /**
     * Add a panel to the wizard
     *
     * @param key The unique key of the panel
     * @param label The label of the panel as it appears in the list on the left
     * @param caption The caption which will appear above the component
     * @param c The component which will appear on the right
     */
    public void add(String key, String label, String caption, Component c) {
        add(key, label, new HeaderPanel(caption, c));
    }

    /**
     * @return The label which appears above the list
     */
    public final Label getListLabel() {
        return m_listLabel;
    }

    /**
     * @return The number of steps in the wizard
     */
    public int getStepCount() {
        return m_panels.size();
    }

    /**
     * @return The List component which is responsible for displaying
     *     the steps
     */
    public final List getList() {
        return m_list;
    }

    // Overridden add methods
    public void add(Component c) {
        throw new UnsupportedOperationException(
                                                "Use add(String key, String label, Component c)"
                                                );
    }

    // Overridden add methods
    public void add(Component c, int constraints) {
        throw new UnsupportedOperationException(
                                                "Use add(String key, String label, Component c)"
                                                );
    }

    /**
     * Get the current progress
     *
     * @param state The current page state
     * @return The index of the highest enabled step
     */
    public int getProgress(PageState state) {
        return ((Integer)state.getValue(m_progressParam)).intValue();
    }

    /**
     * Set the current progress
     *
     * @param state The current page state
     * @param p The index of the highest step to be enabled. All steps above
     *   this index will be disabled. The index must be between -1 and
     *   getStepCount() - 1, inclusive. -1 means that ALL steps are disabled.
     */
    public void setProgress(PageState state, int p) {
        if (p < -1 || p >= getStepCount()) {
            throw new IllegalArgumentException(
                                               "Expecting an integer between -1 and " +
                                               (getStepCount() - 1) + ", but got " + p
                                               );
        }

        state.setValue(m_progressParam, new Integer(p));
    }

    /**
     * Set the default progress. All steps above the default progress
     * will initially be disabled.
     *
     * @param index The default progress
     */
    public void setDefaultProgress(int index) {
        m_progressParam.setDefaultValue(new Integer(index));
    }

    /**
     * Get the default progress.
     *
     * @return  The default progress
     */
    public int getDefaultProgress() {
        return ((Integer)m_progressParam.getDefaultValue()).intValue();
    }

    /**
     * Set the key of the default step.
     *
     * @param index The default step key
     */
    public void setDefaultStepKey(String key) {
        m_stepParam.setDefaultValue(key);
    }

    /**
     * @return  The key of the default step
     */
    public String getDefaultStepKey() {
        return (String)m_stepParam.getDefaultValue();
    }

    /**
     * Get the key which identifies the current step.
     * If there is no current step, returns the key of the
     * first step.
     *
     * @param state The current page state
     * @return the key of the current step
     */
    public String getCurrentStepKey(PageState state) {
        return (String)getSelectionModel().getSelectedKey(state);
    }

    /**
     * Select the step with the specified key. The step may not
     * be higher than the current progress.
     *
     * @param state The current page state
     * @return the key of the current step
     */
    public void setCurrentStepKey(PageState state, String key) {
        getSelectionModel().setSelectedKey(state, key);
    }

    /**
     * Get the index which identifies the current step.
     * If there is no current step, returns 0
     *
     * @param state The current page state
     * @return the index of the current step
     */
    public int getCurrentStep(PageState state) {
        String key = (String)getSelectionModel().getSelectedKey(state);

        if (key == null) {
            return 0;
        }

        return m_panels.findKey(key);
    }

    /**
     * Select the step with the specified index. The step may not
     * be higher than the current progress.
     *
     * @param state The current page state
     * @return the index of the current step
     */
    public void setCurrentStep(PageState state, int step) {
        setCurrentStepKey(state, getStepKey(step));
    }

    /**
     * Move to the next step. If there is nowhere to go,
     * do nothing.
     *
     * @param state The current page state
     */
    public void stepForward(PageState state) {
        stepForward(state, false);
    }

    /**
     * <p>
     * Move to the next step and possibly force the progress so that we can do
     * it.
     * </p>
     *
     * @param state The current PageState
     * @param force boolean that determines whether or not we force the
     *              progress to increment if need be.
     */
    public void stepForward(PageState state, boolean force) {
        int s = getCurrentStep(state);
        int p = getProgress(state);

        if (s < p) {
            setCurrentStep(state, s + 1);
        } else if (s == p) {
            if (force) {
                setProgress(state, p + 1);
                setCurrentStep(state, s + 1);
            }
        }
    }

    /**
     * Move to the previous step. If there is nowhere to go,
     * do nothing.
     *
     * @param state The current page state
     */
    public void stepBack(PageState state) {
        int i = getCurrentStep(state);

        if (i > 0) {
            setCurrentStep(state, i - 1);
        }
    }

    /**
     * @param i The numeric index of a step
     * @return the string key of the specified step, or null if no such
     *   key exists
     */
    public String getStepKey(int i) {
        return (String)m_panels.getKey(i);
    }

    /**
     * @return The {@link SequentialMap} of labels for this wizard
     */
    protected final SequentialMap getLabelsMap() {
        return m_labels;
    }

    /**
     * @return The {@link SequentialMap} of components for this wizard
     */
    protected final SequentialMap getPanelsMap() {
        return m_panels;
    }

    /**
     * A special ListCellRenderer which will return a "disabled" Label
     * if the progress has not advanced far enough
     */
    private class ProgressListCellRenderer extends DefaultListCellRenderer {

        private static final String DIS_OPEN = "<font color=\"#bbbbbb\"><b>";
        private static final String DIS_CLOSE = "</b></font>";

        public Component getComponent(
                                      List list,
                                      PageState state,
                                      Object value,
                                      String key,
                                      int index,
                                      boolean isSelected
                                      ) {
            Component c = null;
            int p = getProgress(state);

            if (p == -1 || p < index) {
                // Return a disabled label
                StringBuffer s = new StringBuffer(DIS_OPEN);
                s.append(m_labels.get(key));
                s.append(DIS_CLOSE);

                c = new Label(s.toString(), false);
            } else {
                // Business as usual
                c = super.getComponent(
                                       list, state, value, key, index, isSelected
                                       );
            }

            return c;
        }
    }
}

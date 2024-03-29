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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.cms.AuthoringKit;
import com.arsdigita.cms.AuthoringStep;
import com.arsdigita.cms.AuthoringStepCollection;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.ui.item.ItemWorkflowRequestLocal;
import com.arsdigita.cms.ui.workflow.AssignedTaskSection;
import com.arsdigita.cms.ui.workflow.AssignedTaskTable;
import com.arsdigita.cms.ui.workflow.TaskFinishForm;
import com.arsdigita.cms.ui.workflow.TaskRequestLocal;
import com.arsdigita.cms.ui.workflow.WorkflowRequestLocal;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.cms.workflow.CMSTask;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.toolbox.ui.ModalPanel;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.util.Assert;
import com.arsdigita.util.SequentialMap;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class represents a single authoring kit.  The wizard accepts a
 * {@link ContentType} in the constructor; it then extracts
 * the {@link AuthoringKit} for the content type, and creates the
 * components for all the steps in the kit.</p>
 *
 * Note that the individual authoring kit steps must provide the following
 * constructor:
 *
 * <blockquote><pre><code>
 * public TheClass(ItemSelectionModel model, AuthoringKitWizard parent) { ... }
 * </code></pre></blockquote>
 *
 * This constructor will be called when the component is automatically
 * instantiated by the <code>AuthoringKitWizard</code>.</p>
 *
 * @version $Id: AuthoringKitWizard.java 2140 2011-01-16 12:04:20Z pboy $
 */
public class AuthoringKitWizard extends LayoutPanel implements Resettable {

    /** Private Logger instance for this class */
    private static final Logger s_log = Logger.getLogger(
            AuthoringKitWizard.class);
    private static Class[] s_args = new Class[]{
        ItemSelectionModel.class,
        AuthoringKitWizard.class
    };
    private static Class[] s_userDefinedArgs = new Class[]{
        ItemSelectionModel.class,
        AuthoringKitWizard.class,
        ContentType.class
    };
    //private static final ArrayList s_assets = new ArrayList();
    private static final java.util.List<AssetStepEntry> s_assets = new 
                         ArrayList<AssetStepEntry>();
    private final Object[] m_vals;
    private final ContentType m_type;
    private final AuthoringKit m_kit;
    private final ItemSelectionModel m_sel;
    private final WorkflowRequestLocal m_workflow;
    private final AssignedTaskTable m_tasks;
    private final SequentialMap m_labels;
    private final List m_list;
    private String m_defaultKey;
    private final GridPanel m_left;
    private final ModalPanel m_body;
    private final SimpleContainer m_steps;
    private final TaskFinishForm m_taskFinishForm;
    /**
     * The name of the state parameter that determines whether the
     * wizard is in item creation mode or item editing mode.
     */
    public static final String IS_EDITING = "is_edit";
    /**
     * The key for the item creation step.
     */
    public static final String CREATION = "_creation_";

    /**
     * Construct a new AuthoringKitWizard. Add all the steps in the
     * authoring kit to the wizard.
     *
     * @param type The content type of the items that this wizard will
     * handle
     *
     * @param itemModel The item selection model which will supply
     * this wizard with the content item object
     */
    public AuthoringKitWizard(final ContentType type,
                              final ItemSelectionModel model) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Authoring kit wizard for type " + type + " "
                        + "undergoing creation");
        }

        m_type = type;
        m_kit = type.getAuthoringKit();
        m_sel = model;
        m_vals = new Object[]{m_sel, this};
        m_workflow = new ItemWorkflowRequestLocal();
        m_labels = new SequentialMap();

        m_left = new GridPanel(1);
        setLeft(m_left);

        m_tasks = new AssignedTaskTable(m_workflow);

        m_left.add(new AssignedTaskSection(m_workflow, m_tasks));

        final Section stepSection = new Section(gz("cms.ui.authoring.steps"));
        m_left.add(stepSection);

        m_list = new List();
        stepSection.setBody(m_list);

        m_list.setListData(m_labels);
        m_list.setCellRenderer(new ListCellRenderer() {

            public Component getComponent(
                    List list,
                    PageState state,
                    Object value,
                    String key,
                    int index,
                    boolean isSelected) {
                Label l = null;
                if (value instanceof GlobalizedMessage) {
                    l = new Label((GlobalizedMessage) value);
                } else {
                    l = new Label((String) value);
                }
                if (isSelected) {
                    l.setFontWeight(Label.BOLD);
                    return l;
                }
                return new ControlLink(l);
            }
        });

        m_body = new ModalPanel();
        setBody(m_body);

        m_steps = new SimpleContainer();
        m_body.add(m_steps);
        m_body.setDefault(m_steps);

        final AuthoringStepCollection steps = m_kit.getSteps();

        if (Assert.isEnabled()) {
            Assert.isTrue(!steps.isEmpty(),
                          "The authoring kit for " + type.getID() + " "
                          + "(java class " + type.getClassName() + ") "
                          + "has no steps.");
        }

        StepComponent panel = null;
        while (steps.next()) {
            final AuthoringStep step = steps.getAuthoringStep();
            final String key = step.getID().toString();

            if (m_defaultKey == null) {
                m_defaultKey = key;
            }

            /**
             *  The "label" and "description" are only here for backwards
             *  compatibility
             */
            final String label = step.getLabel();
            final String labelKey = step.getLabelKey();
            final String labelBundle = step.getLabelBundle();
            final String description = step.getDescription();
            final String descriptionKey = step.getDescription();
            final String descriptionBundle = step.getDescription();
            final String str = step.getComponent();

            if (panel != null) {
                panel.setNextStepKey(step.getID());
            }
            panel = new StepComponent(step.getID());
            m_steps.add(panel);
            final Component comp;

            if (str.equals("com.arsdigita.cms.ui.authoring."
                           + "SecondaryPageEditDynamic")
                || str.equals("com.arsdigita.cms.ui.authoring."
                              + "PageEditDynamic")) {
                comp = instantiateUserDefinedStep(str, m_type);
            } else {
                comp = instantiateStep(str);
            }
            panel.add(comp);
            // XXX should be optional
            if (comp instanceof AuthoringStepComponent) {
                ((AuthoringStepComponent) comp).addCompletionListener(
                        new StepCompletionListener());
            }

            GlobalizedMessage gzLabel = null;
            if (labelKey != null) {
                if (labelBundle == null) {
                    gzLabel = gz(labelKey);
                } else {
                    gzLabel = new GlobalizedMessage(labelKey, labelBundle);
                }
            }
            m_labels.put(key,
                         gzLabel == null ? (Object) label : (Object) gzLabel);
        }

        ObjectType thisType = MetadataRoot.getMetadataRoot().getObjectType(type.
                getAssociatedObjectType());
        Collection skipSteps = ContentSection.getConfig().getAssetStepsToSkip(
                type);
        Iterator it = skipSteps.iterator();
        if (s_log.isDebugEnabled()) {
            while (it.hasNext()) {
                s_log.debug("skip step " + it.next());
            }
        }
        //Iterator assets = s_assets.iterator();
        Iterator<AssetStepEntry> assets = s_assets.iterator();
        while (assets.hasNext()) {
            //Object[] data = (Object[]) assets.next();
            final AssetStepEntry data = assets.next();
            //String baseObjectType = (String) data[0];
            final String baseObjectType = data.getBaseDataObjectType();
            //Class step = (Class) data[1];
            Class step = data.getStep();
            s_log.debug("possibly adding asset step " + step.getName());
            if (!skipSteps.contains(step.getName())) {
                //GlobalizedMessage label = (GlobalizedMessage) data[2];
                GlobalizedMessage label = data.getLabel();

                if (!thisType.isSubtypeOf(baseObjectType)) {
                    continue;
                }

                if (panel != null) {
                    panel.setNextStepKey(step);
                }
                panel = new StepComponent(step);
                m_steps.add(panel);

                Component comp = instantiateStep(step.getName());
                if (comp instanceof AuthoringStepComponent) {
                    ((AuthoringStepComponent) comp).addCompletionListener(
                            new StepCompletionListener());
                }
                panel.add(comp);

                m_labels.put(step, label);
            }
        }

        m_list.addChangeListener(new StepListener());

        m_taskFinishForm = new TaskFinishForm(new TaskSelectionRequestLocal());
        m_body.add(m_taskFinishForm);

        m_body.connect(m_tasks, 2, m_taskFinishForm);
        m_body.connect(m_taskFinishForm);

        m_taskFinishForm.addProcessListener(new FormProcessListener() {

            public final void process(final FormSectionEvent e)
                    throws FormProcessException {
                final PageState state = e.getPageState();

                m_tasks.getRowSelectionModel().clearSelection(state);
            }
        });
    }

    /** 
     * 
     */
    private final class StepListener implements ChangeListener {

        public final void stateChanged(final ChangeEvent e) {
            final PageState state = e.getPageState();
            final String key = m_list.getSelectedKey(state).toString();

            final Iterator iter = m_steps.children();

            while (iter.hasNext()) {
                final StepComponent step = (StepComponent) iter.next();

                if (step.getStepKey().toString().equals(key)) {
                    step.setVisible(state, true);
                } else {
                    step.setVisible(state, false);
                }
            }
        }
    }

    /** 
     * 
     */
    private final class StepCompletionListener implements ActionListener {

        public final void actionPerformed(final ActionEvent e) {
            final PageState state = e.getPageState();
            if (ContentItemPage.isStreamlinedCreationActive(state)) {
                final String key = m_list.getSelectedKey(state).toString();

                final Iterator iter = m_steps.children();

                while (iter.hasNext()) {
                    final StepComponent step = (StepComponent) iter.next();
                    if (step.getStepKey().toString().equals(key)) {
                        Object nextStep = step.getNextStepKey();
                        if (nextStep != null) {
                            m_list.getSelectionModel().setSelectedKey(state,
                                                                      nextStep.
                                    toString());
                        }
                    }
                }
            }
        }
    }

    /** 
     * 
     * @param page
     */
    @Override
    public final void register(final Page page) {
        super.register(page);

        final Iterator iter = m_steps.children();

        while (iter.hasNext()) {
            final StepComponent child = (StepComponent) iter.next();

            page.setVisibleDefault(child, false);
        }

        page.addActionListener(new ActionListener() {

            public final void actionPerformed(final ActionEvent e) {
                final PageState state = e.getPageState();

                if (state.isVisibleOnPage(AuthoringKitWizard.this)) {
                    final SingleSelectionModel model =
                                               m_list.getSelectionModel();

                    if (!model.isSelected(state)) {
                        model.setSelectedKey(state, m_defaultKey);
                    }
                }
            }
        });
    }

    /**
     *
     * @param baseObjectType
     * @param step
     * @param label
     * @param description
     * @param sortKey
     */
    public static void registerAssetStep(String baseObjectType,
                                         Class step,
                                         GlobalizedMessage label,
                                         GlobalizedMessage description,
                                         int sortKey) {
        // cg - allow registered steps to be overridden by registering a step with the same label
        // this is a bit of a hack used specifically for creating a specialised version of image
        // step. There is no straightforward way of preventing the original image step from being
        // registered, but I needed the image step to use a different step class if the specialised
        // image step application was loaded. Solution is to ensure initialiser in new project
        // runs after original ccm-ldn-image-step initializer and override the registered step here
        s_log.debug(
                "registering asset step - label: "
                + label.localize()
                + " step class: "
                + step.getName());

        //Iterator assets = s_assets.iterator();
        Iterator<AssetStepEntry> assets = s_assets.iterator();
        while (assets.hasNext()) {
            //Object[] data = (Object[]) assets.next();
            //String thisObjectType = (String) data[0];
            //GlobalizedMessage thisLabel = (GlobalizedMessage) data[2];
            
            final AssetStepEntry data = assets.next();
            String thisObjectType = data.getBaseDataObjectType();
            GlobalizedMessage thisLabel = data.getLabel();
            
            /**
             * jensp 2011-11-14: The code above was only testing for the same
             * label, but not for the same object type. I don't think that
             * this was indented since this made it impossible to attach the
             * same step to different object types. 
             * The orginal line was
             * if (thisLabel.localize().equals(label.localize())) {
             * 
             */
            if ((thisObjectType.equals(baseObjectType))
                && (thisLabel.localize().equals(label.localize()))) {
                s_log.debug(
                        "registering authoring step with same label as previously registered step");
                s_assets.remove(data);
                break;
            }
        }
        s_assets.add(new AssetStepEntry(baseObjectType, step, label, description, sortKey));
        Collections.sort(s_assets);
        //s_assets.add(new Object[]{baseObjectType, step, label, description});
    }

    private static class AssetStepEntry implements Comparable<AssetStepEntry> {
        private String baseDataObjectType;
        private Class step;
        private GlobalizedMessage label;
        private GlobalizedMessage description;
        private Integer sortKey;
        
        public AssetStepEntry() {
            super();
        }
        
        public AssetStepEntry(final String baseDataObjectType,
                              final Class step,
                              final GlobalizedMessage label,
                              final GlobalizedMessage description,
                              final Integer sortKey) {
            this.baseDataObjectType = baseDataObjectType;
            this.step = step;
            this.label = label;
            this.description = description;
            this.sortKey = sortKey;
        }

        public String getBaseDataObjectType() {
            return baseDataObjectType;
        }

        public void setBaseDataObjectType(final String baseDataObjectType) {
            this.baseDataObjectType = baseDataObjectType;
        }

        public Class getStep() {
            return step;
        }

        public void setStep(final Class step) {
            this.step = step;
        }

        public GlobalizedMessage getLabel() {
            return label;
        }

        public void setLabel(final GlobalizedMessage label) {
            this.label = label;
        }

        public GlobalizedMessage getDescription() {
            return description;
        }

        public void setDescription(final GlobalizedMessage description) {
            this.description = description;
        }

        public Integer getSortKey() {
            return sortKey;
        }

        public void setSortKey(final Integer sortKey) {
            this.sortKey = sortKey;
        }
        
        public int compareTo(final AssetStepEntry other) {
            if (sortKey == other.getSortKey()) {
                return step.getName().compareTo(other.getStep().getName());
            } else {
                return sortKey.compareTo(other.getSortKey());
            }
        }
    }
    
    /**
     * @return The content type handled by this wizard
     */
    public ContentType getContentType() {
        return m_type;
    }

    /**
     * 
     * @return
     */
    public List getList() {
        return m_list;
    }

    /**
     * @return The authoring kit which is represented by this wizard
     */
    public AuthoringKit getAuthoringKit() {
        return m_kit;
    }

    /**
     * @return The ItemSelectionModel used by the steps in this wizard
     */
    public ItemSelectionModel getItemSelectionModel() {
        return m_sel;
    }

    /**
     * Instantiate the specified authoring kit step. Will throw a
     * RuntimeException on failure.
     *
     * @param className The Java class name of the step
     */
    protected Component instantiateStep(String name) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Instantiating kit wizard '" + name + "' with "
                        + "arguments " + s_args);
        }

        Object[] vals;
        try {
            // Get the creation component
            Class createClass = Class.forName(name);
            Constructor constr = createClass.getConstructor(s_args);
            Component c = (Component) constr.newInstance(m_vals);
            return c;
        } catch (Exception e) {
            Throwable cause = e.getCause(); // JDK1.4
            if (cause == null) {
                cause = e;
            }
            throw new UncheckedWrapperException(
                    "Failed to instantiate authoring kit component " + m_kit.
                    getCreateComponent() + ": " + e.getMessage(), cause);
        }
    }

    /**
     * Instantiate the specified authoring kit step for a user defined content type.
     * Will throw a
     * RuntimeException on failure.
     *
     * @param className The Java class name of the step
     * @param description The step description, which for dynamically generated
     *        steps will be the object type which originally defined the step.
     */
    protected Component instantiateUserDefinedStep(String name,
                                                   ContentType originatingType) {
        Object[] vals;
        try {
            // Get the creation component
            Class createClass = Class.forName(name);
            Constructor constr = createClass.getConstructor(s_userDefinedArgs);
            Object[] userDefinedVals =
                     new Object[]{m_sel, this, originatingType};
            Component c = (Component) constr.newInstance(userDefinedVals);
            return c;
        } catch (ClassNotFoundException cnfe) {
            throw new UncheckedWrapperException(cnfe);
        } catch (NoSuchMethodException nsme) {
            throw new UncheckedWrapperException(nsme);
        } catch (InstantiationException ie) {
            throw new UncheckedWrapperException(ie);
        } catch (IllegalAccessException iae) {
            throw new UncheckedWrapperException(iae);
        } catch (InvocationTargetException ite) {
            throw new UncheckedWrapperException(ite);
        }
    }

//     /**
//      * Tell the parent page to redirect back to its return URL
//      *
//      * @param state The page state
//      */
//     public static void redirectBack(PageState state) {
//         ((ContentItemPage)state.getPage()).redirectBack(state);
//     }
    /**
     * Reset the state of this wizard
     */
    public final void reset(PageState state) {
        m_list.setSelectedKey(state, m_defaultKey);
    }

    private final class StepComponent extends SimpleContainer {

        private final Object m_key;
        private Object m_nextKey;
        

        public StepComponent(Object key) {
            m_key = key;
        }

        public Object getStepKey() {
            return m_key;
        }

        public Object getNextStepKey() {
            return m_nextKey;
        }

        public void setNextStepKey(Object nextKey) {
            m_nextKey = nextKey;
        }
    }

    private final class TaskSelectionRequestLocal extends TaskRequestLocal {

        protected final Object initialValue(final PageState state) {
            final String id = m_tasks.getRowSelectionModel().getSelectedKey(
                    state).toString();

            return new CMSTask(new BigDecimal(id));
        }
    }

    protected final static GlobalizedMessage gz(final String key) {
        return GlobalizationUtil.globalize(key);
    }

    protected final static String lz(final String key) {
        return (String) gz(key).localize();
    }
}

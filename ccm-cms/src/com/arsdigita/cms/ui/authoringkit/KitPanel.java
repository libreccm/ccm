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
package com.arsdigita.cms.ui.authoringkit;


import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.ToggleLink;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.AuthoringKit;
import com.arsdigita.cms.AuthoringStep;
import com.arsdigita.cms.AuthoringStepCollection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ui.type.ContentTypeRequestLocal;
import com.arsdigita.cms.ui.type.TypeSecurityContainer;
import com.arsdigita.cms.ui.util.ToggleLinkFormListener;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

import org.apache.log4j.Logger;


/**
 * This class contains the component which displays the information
 * for a particular authoring kit.
 *
 * @author Jack Chung
 * @version $Revision: #15 $ $Date: 2004/08/17 $
 * @version $Id: KitPanel.java 2090 2010-04-17 08:04:14Z pboy $
 */
public class KitPanel extends BoxPanel
    implements ActionListener, TableActionListener {

    private static final Logger s_log = Logger.getLogger( KitPanel.class );

    public final static Integer EDIT_COLUMN = new Integer(4);
    public final static Integer DELETE_COLUMN = new Integer(5);

    private final ContentTypeRequestLocal m_type;
    //private final ToggleLink m_toggleLink;
    private final Table m_stepTable;

    private ToggleLink m_addStep;
    private ToggleLink m_editKit;
    //private ToggleLink m_viewContentType;

    private SimpleContainer m_kitInfo;
    private EditKit m_editKitPanel;
    private AddStep m_addStepPanel;
    private EditStep m_editStepPanel;
    private DeleteStep m_deleteStepPanel;

    //public KitPanel(SingleSelectionModel m, ToggleLink l) {
    public KitPanel(ContentTypeRequestLocal type) {

        super();

        m_type = type;
        //m_toggleLink = l;

        //display heading
        SimpleContainer heading = new SimpleContainer();
        heading.add(new Label(GlobalizationUtil.globalize(
                        "cms.ui.authoringkit.authoring_kit_for")));
        heading.add(makeContentType());

        //edit kit
        m_editKit = new ToggleLink("edit");
        m_editKit.setClassAttr("actionLink");
        m_editKit.addActionListener(this);
        m_editKitPanel =  new EditKit(m_type);
        m_editKitPanel.addProcessListener(new ToggleLinkFormListener(m_editKit));
        m_editKitPanel.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent event)
                    throws FormProcessException {
                    PageState state = event.getPageState();
                    if ( m_editKitPanel.isCancelled(state) ) {
			m_addStep.setSelected(state, false);
			m_editKit.setSelected(state, false);
			m_stepTable.clearSelection(state);
                    }
                }
            });

        //display table of steps
        m_stepTable = makeStepTable();

        //add step
        m_addStep = new ToggleLink("add step");
        m_addStep.setClassAttr("actionLink");
        m_addStep.addActionListener(this);
        m_addStepPanel = new AddStep(m_type);
        m_addStepPanel.addProcessListener(new ToggleLinkFormListener(m_addStep));
        m_addStepPanel.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent event)
                    throws FormProcessException {
                    PageState state = event.getPageState();
                    if ( m_addStepPanel.isCancelled(state) ) {
			m_addStep.setSelected(state, false);
			m_editKit.setSelected(state, false);
			m_stepTable.clearSelection(state);
                    }
                }
            });


        //listeners to clear the stepTable
        FormProcessListener clearStepsProcessListener =
            new FormProcessListener() {
                public void process(FormSectionEvent e) throws FormProcessException {
                    PageState s = e.getPageState();

                    m_stepTable.clearSelection(s);
                }
            };

        FormSubmissionListener clearStepsSubmissionListener =
            new FormSubmissionListener() {
                public void submitted(FormSectionEvent e) throws FormProcessException {
                    PageState s = e.getPageState();

                    if (m_deleteStepPanel.isCancelled(s)) {
                        m_stepTable.clearSelection(s);
                    }
                }
            };


        //edit step
        m_editStepPanel = new EditStep(m_type, m_stepTable.getRowSelectionModel());
        m_editStepPanel.addProcessListener(clearStepsProcessListener);
        m_editStepPanel.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent event)
                    throws FormProcessException {
                    PageState state = event.getPageState();
                    if ( m_editStepPanel.isCancelled(state) ) {
			m_addStep.setSelected(state, false);
			m_editKit.setSelected(state, false);
			m_stepTable.clearSelection(state);
                    }
                }
            });


        //delete step
        m_deleteStepPanel = new DeleteStep(m_stepTable.getRowSelectionModel(), 
                                           m_type);
        m_deleteStepPanel.addProcessListener(clearStepsProcessListener);
        m_deleteStepPanel.addSubmissionListener(clearStepsSubmissionListener);

        //view content type
        //m_viewContentType = new ToggleLink("view content type");
        //m_viewContentType.addActionListener(this);

        //create component info
        SimpleContainer createComponent = new SimpleContainer();
        createComponent.add(new Label(GlobalizationUtil.globalize(
                                      "cms.ui.authoringkit.create_component")));
        createComponent.add(makeCreateComponent());
        createComponent.add(new TypeSecurityContainer(m_editKit));

        //info for this kit
        m_kitInfo = new BoxPanel();
        m_kitInfo.add(heading);
        m_kitInfo.add(createComponent);
        //m_kitInfo.add(m_viewContentType);
        m_kitInfo.add(m_stepTable);
        m_kitInfo.add(new TypeSecurityContainer(m_addStep));

        add(m_kitInfo);

        //register the different components
        add(m_editKitPanel);
        add(m_addStepPanel);
        add(m_editStepPanel);
        add(m_deleteStepPanel);
    }

    public void reset(PageState s) {
	m_addStep.setSelected(s, false);
	m_editKit.setSelected(s, false);
	m_stepTable.clearSelection(s);
    }

    /**
     * Displays the appropriate label for the content type
     */
    private Label makeContentType() {
        PrintListener l = new PrintListener() {
                public void prepare(PrintEvent e) {
                    Label t = (Label) e.getTarget();
                    PageState s = e.getPageState();
                    //Assert.assertTrue(m_types.isSelected(s));

                    ContentType type = m_type.getContentType(s);
                    t.setFontWeight(Label.BOLD);
                    t.setLabel(type.getLabel());
                }
            };

        return new Label(l);
    }

    /**
     * Displays the appropriate label for the content type
     */
    private Label makeCreateComponent() {
        PrintListener l = new PrintListener() {
                public void prepare(PrintEvent e) {
                    Label t = (Label) e.getTarget();
                    PageState s = e.getPageState();
                    //Assert.assertTrue(m_types.isSelected(s));

                    ContentType type = m_type.getContentType(s);
                    String createComponent;
                    if (type.getAuthoringKit() != null){
                        createComponent = type.getAuthoringKit()
                                              .getCreateComponent();
                        if (createComponent == null) {
                            createComponent = "n/a";
                        }
                    } else {
                        createComponent = 
                                "n/a - This is not a creatable Content Type";
                    }

                    t.setLabel(createComponent);

                }
            };

        return new Label(l);
    }


    /**
     * Listeners for edit kit and add step
     */
    public void actionPerformed(ActionEvent e) {
        PageState s = e.getPageState();

        if ( m_editKit.isSelected(s) ) {             // edit kit is clicked
            m_addStep.setSelected(s, false);
            m_stepTable.clearSelection(s);
            //m_viewContentType.setSelected(s, false);
        } else if ( m_addStep.isSelected(s) ) {      // add step is clicked
            m_editKit.setSelected(s, false);
            m_stepTable.clearSelection(s);
            //m_viewContentType.setSelected(s, false);
        }
        
//        else if ( m_viewContentType.isSelected(s) ) {  //view content type
//            m_addStep.setSelected(s, false);
//            m_editKit.setSelected(s, false);
//            m_stepTable.clearSelection(s);
//            //m_toggleLink.setSelected(s, false);
//        }
    }


    /**
     * Listener for the step table
     */
    public void cellSelected(TableActionEvent e) {
        PageState s = e.getPageState();

        if ( e.getSource() == this ) {
            m_editKit.setSelected(s, false);
            m_addStep.setSelected(s, false);
            //m_viewContentType.setSelected(s, false);
        }
    }

    public void headSelected(TableActionEvent e) {
        return;
    }


    private Table makeStepTable() {
        final String[] headers = { "Ordering", "Label", "Description",
                                   "Component", "Action", ""};

        TableModelBuilder b = new TableModelBuilder () {
                private boolean m_locked;

                public TableModel makeModel(final Table t, final PageState s) {
                    //Assert.assertTrue(m_types.isSelected(s));

                    return new TableModel() {

                            AuthoringKit kit = getKit();
                            AuthoringStepCollection steps =
                                (kit == null) ? null : kit.getSteps();
                            AuthoringStep currentStep = null;

                            private AuthoringKit getKit() {
                                ContentType type = m_type.getContentType(s);
                                try {
                                    AuthoringKit k = type.getAuthoringKit();
                                    // k may be null if it's a non-creatable 
                                    // content type;
                                    return k;

                                } catch (DataObjectNotFoundException e) {
                                    throw new UncheckedWrapperException
                                        ("Authoring kit not found", e);
                                }
                            }

                            public int getColumnCount() {
                                return headers.length;
                            }

                            public boolean nextRow() {
                                boolean next;
                                //steps = kit.getSteps();
                                if (steps == null) {
                                    next = false;
                                } else {
                                    next = steps.next();
                                    if ( next ) {
                                        currentStep = steps.getAuthoringStep();
                                    }
                                }
                                return next;
                            }

                            public Object getElementAt(int columnIndex) {
                                if (currentStep == null) {
                                    throw new IllegalArgumentException( (String) 
                                         GlobalizationUtil.globalize(
                                         "cms.ui.authoringkit.current_row_dont_exists")
                                         .localize());
                                }

                                switch (columnIndex) {
                                case 0:
                                    if (kit != null) {
                                        return kit.getOrdering(currentStep);
                                    } else {
                                        return "";
                                    }
                                case 1:
                                    if (currentStep.getLabelKey() == null) {
                                        return currentStep.getLabel();
                                    } else if (currentStep.getLabelBundle() == null) {
                                        return new Label
                                            (GlobalizationUtil
                                             .globalize(currentStep.getLabelKey()));
                                    } else {
                                        return new Label
                                            (new GlobalizedMessage(currentStep.getLabelKey(),
                                                                   currentStep.getLabelBundle()));
                                    }
                                case 2:
                                    if (currentStep.getDescriptionKey() == null) {
                                        return currentStep.getDescription();
                                    } else if (currentStep.getDescriptionBundle() == null) {
                                        return new Label
                                            (GlobalizationUtil.globalize(currentStep.getDescriptionKey()));
                                    } else {
                                        return new Label
                                            (new GlobalizedMessage(currentStep.getDescriptionKey(),
                                                                   currentStep.getDescriptionBundle()));
                                    }
                                case 3:
                                    return currentStep.getComponent();
                                case 4:
                                    return "edit";
                                case 5:
                                    return "delete";
                                default:
                                    throw new IllegalArgumentException(
                                            "columnIndex exceeds " +
                                            "number of columns available");
                                }
                            }

                            public Object getKeyAt(int columnIndex) {
                                if (currentStep == null) {
                                    throw new IllegalArgumentException( (String) 
                                        GlobalizationUtil.globalize(
                                        "cms.ui.authoringkit.current_row_dont_exists")
                                        .localize());
                                } else {
                                    return currentStep.getID();
                                }
                            }
                        };
                }

                public void lock() {
                    m_locked = true;
                }

                public boolean isLocked() {
                    return m_locked;
                }

            };

        Table result = new Table(b, headers);
        //set the Edit and Delete Actions to be links
        result.getColumn(4).setCellRenderer(new KitTableCellRenderer(true));
        result.getColumn(5).setCellRenderer(new KitTableCellRenderer(true));
        return result;
    }


    public Table getStepTable() {
        return m_stepTable;
    }

    public void register(Page p) {
        p.addComponent(this);
    }

    public void generateXML(PageState state, Element parent) {
        if (m_editKit.isSelected(state)) {
            s_log.debug( "Displaying edit kit panel" );

            m_editKitPanel.generateXML(state,parent);

        } else if (m_addStep.isSelected(state)) {
            s_log.debug( "Displaying add step panel" );

            m_addStepPanel.generateXML(state,parent);

        } else if ( m_stepTable.isSelectedColumn(state, EDIT_COLUMN) ) {
            s_log.debug( "Displaying edit step panel" );

            m_editStepPanel.generateXML(state,parent);

        } else if ( m_stepTable.isSelectedColumn(state, DELETE_COLUMN) ) {
            s_log.debug( "Displaying delete step panel" );

            m_deleteStepPanel.generateXML(state, parent);

        } else {
            s_log.debug( "Displaying kit info" );

            m_kitInfo.generateXML(state, parent);
        }

    }

    private class KitTableCellRenderer extends DefaultTableCellRenderer {

        public KitTableCellRenderer(boolean active) {
            super(active);
        }
        public Component getComponent(Table table, PageState state, Object value, 
                                      boolean isSelected, Object key, int row, int column) {
            return new TypeSecurityContainer(super.getComponent(table, state, value,
                                                                isSelected, key, row, column));
        }
    }
}

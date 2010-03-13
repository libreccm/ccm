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
package com.arsdigita.cms.ui.templates;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.categorization.Category;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.CategoryTemplateMapping;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.cms.SectionTemplateMapping;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.Template;
import com.arsdigita.cms.TemplateCollection;
import com.arsdigita.cms.TemplateManager;
import com.arsdigita.cms.TemplateManagerFactory;
import com.arsdigita.cms.dispatcher.ContentSectionDispatcher;
import com.arsdigita.cms.ui.CMSContainer;
import com.arsdigita.cms.ui.FormSecurityListener;
import com.arsdigita.cms.ui.category.CategoryComponentAccess;
import com.arsdigita.cms.ui.category.CategoryRequestLocal;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

import java.util.TooManyListenersException;

/**
 * This component will eventually contain the full templates UI
 * for content items. It is just a placeholder for now.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: CategoryTemplates.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class CategoryTemplates extends CMSContainer {
    private CategoryRequestLocal m_category;
    private ACSObjectSelectionModel m_types;
    private SingleSelectionModel m_contexts;

    private CMSContainer m_display;
    private CMSContainer m_assign;

    public static final String ASSIGN_TEMPLATE = "assignTemplate";

    /**
     * Construct a new CategoryTemplates component
     *
     * @param category the <code>CategoryRequestLocal</code> that will supply the
     *   current content item
     */
    public CategoryTemplates(CategoryRequestLocal category) {
        m_category = category;

        m_types = new ACSObjectSelectionModel(new BigDecimalParameter("t"));
        m_contexts = new ParameterSingleSelectionModel(new StringParameter("c"));

        AssignForm form = new AssignForm("assign",
                                         m_types,
                                         m_contexts);
        form.addSubmissionListener
            (new FormSecurityListener(SecurityManager.CATEGORY_ADMIN));

        form.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent e)
                    throws FormProcessException {

                    PageState state = e.getPageState();

                    m_display.setVisible(state, false);
                    m_assign.setVisible(state, true);
                }
            });

        CategoryTemplatesListingImpl l = new CategoryTemplatesListingImpl(category);
        SegmentedPanel st = new SegmentedPanel();
        st.addSegment(new Label("Assigned Templates"), l);

        m_display = new CMSContainer();
        m_display.add(form);
        m_display.add(st);
        add(m_display);

        SegmentedPanel sa = new SegmentedPanel();
        Label assignLabel = new Label("dummy");
        assignLabel.addPrintListener(new PrintListener() {
                public void prepare(PrintEvent e) {
                    PageState s = e.getPageState();
                    Label targetLabel = (Label)e.getTarget();
                    Category category = m_category.getCategory(s);
                    Assert.exists(category, "category");
                    targetLabel.setLabel("Assign a template to " + category.getName());
                }
            });
        sa.addSegment(assignLabel,
                      new AvailableTemplatesListing(m_category,
                                                    m_types,
                                                    m_contexts));

        ActionLink returnLink = new ActionLink("Return to template listing");
        returnLink.setClassAttr("actionLink");
        returnLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState s = e.getPageState();
                    m_display.setVisible(s, true);
                    m_assign.setVisible(s, false);
                }
            });

        m_assign = new CMSContainer();
        m_assign.add(sa);
        m_assign.add(returnLink);
        add(m_assign);
    }

    public void register(Page p) {
        super.register(p);

        p.setVisibleDefault(m_assign, false);
        p.addComponentStateParam(this, m_types.getStateParameter());
        p.addComponentStateParam(this, m_contexts.getStateParameter());
    }

    /**
     * Displays a list of templates which are currently assigned to
     * the current item
     */
    protected class CategoryTemplatesListingImpl extends CategoryTemplatesListing {

        private CategoryComponentAccess m_access;

        public CategoryTemplatesListingImpl(CategoryRequestLocal category) {
            super(category);
            m_access = new CategoryComponentAccess(null, category);
        }

        public void assignLinkClicked(PageState s,
                                      Category category,
                                      String useContext) {
        }

        public void register(Page p) {
            super.register(p);

            // Hide action columns if user has no access

            p.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        final PageState state = e.getPageState();

                        if (state.isVisibleOnPage(CategoryTemplates.this)
                            && !m_access.canAccess
                            (state,
                             CMS.getContext().getSecurityManager())) {
                            getRemoveColumn().setVisible(state, false);
                        }
                    }
                });
        }
    }

    /**
     * Displays a list of templates for the given content item in the
     * given context, along with a link to select a template
     */
    protected class AvailableTemplatesListing extends TemplatesListing {
        TableColumn m_assignCol;
        ACSObjectSelectionModel m_type;
        CategoryRequestLocal m_category;
        SingleSelectionModel m_context;

        /**
         * Construct a new AvailableTemplatesListing
         *
         * @param contextModel the SingleSelectionModel that will define the
         *   current use context
         */
        public AvailableTemplatesListing(CategoryRequestLocal category,
                                         ACSObjectSelectionModel type,
                                         SingleSelectionModel context) {
            m_type = type;
            m_category = category;
            m_context = context;
            
            // Add the "assign" column and corresponding action listener
            m_assignCol = addColumn("Assign",
                                    TemplateCollection.TEMPLATE, false,
                                    new AssignCellRenderer());
            
            addTableActionListener(new TableActionAdapter() {
                    public void cellSelected(TableActionEvent e) {
                        PageState s = e.getPageState();
                        TemplatesListing l = (TemplatesListing)e.getSource();
                        int i = e.getColumn().intValue();
                        TableColumn c = l.getColumnModel().get(i);
                        
                        // Safe to check pointer equality since the column is
                        // created statically
                        if(c == m_assignCol) {
                            SectionTemplateMapping m =
                                (SectionTemplateMapping)getMappingModel().getSelectedObject(s);
                            assignTemplate(s, m.getTemplate());
                        }
                    }
                });
        }

        /**
         * Get all the templates for the given type in the current section
         */
        protected TemplateCollection getTemplateCollection(PageState s) {
            ContentSection sec = ContentSectionDispatcher.getContentSection(s.getRequest());
            Assert.exists(sec, "content section");

            /*
              ContentItem item = m_category.getSelectedItem(s);
              Assert.exists(item, "item");
            */

            ContentType type = getContentType(s);

            TemplateCollection c = TemplateManagerFactory
                .getInstance().getTemplates(sec, type);
            /*
              c.addEqualsFilter(TemplateCollection.USE_CONTEXT,
              TemplateManager.PUBLIC_CONTEXT);
            */
            return c;
        }

        /**
         * Get the currently selected use context
         */
        protected ContentType getContentType(PageState s) {
            ContentType type = (ContentType)m_type.getSelectedObject(s);
            Assert.exists(type, "content type");
            return type;
        }

        /**
         * Assign a template to the current item
         */
        public void assignTemplate(PageState s, Template t) {
            Category category = m_category.getCategory(s);
            ContentType type = (ContentType)m_type.getSelectedObject(s);
            String useContext = (String)m_context.getSelectedKey(s);
            CategoryTemplateMapping map = 
                CategoryTemplateMapping.getMapping(category, type, t, 
                                                   useContext);
            if(map == null) {
                map = new CategoryTemplateMapping();
                map.setCategory(category);
                map.setContentType(type);
                map.setUseContext(useContext);
                map.setTemplate(t);
            }
            map.setContentSection(ContentSectionDispatcher.getContentSection(s.getRequest()));
            map.save();

            m_display.setVisible(s, true);
            m_assign.setVisible(s, false);
        }

        /**
         * Render the "assign" link
         */
        protected class AssignCellRenderer implements TableCellRenderer {

            private ControlLink m_link;

            public AssignCellRenderer() {
                m_link = new ControlLink("Assign this template");
                m_link.setClassAttr("assignTemplateLink");
            }

            public Component getComponent(Table table, PageState state, Object value,
                                          boolean isSelected, Object key,
                                          int row, int column) {
                return m_link;
            }
        }

    }

    private class AssignForm extends Form {

        SingleSelect m_type;
        //SingleSelect m_context;
        Hidden m_context;

        public AssignForm(String name,
                          final ACSObjectSelectionModel type,
                          final SingleSelectionModel context) {

            super(name, new GridPanel(3));

            add(new Label("Content type:"));
            //add(new Label("Use context:"));
            //add(new Label(""));

            m_type = new SingleSelect(type.getStateParameter());
            try {
                m_type.addPrintListener(new PrintListener() {
                        public void prepare(PrintEvent e) {
                            PageState state = e.getPageState();
                            ContentSection section = ContentSectionDispatcher.getContentSection(state.getRequest());

                            SingleSelect target = (SingleSelect)e.getTarget();

                            ContentTypeCollection types = section.getContentTypes();
                            types.addOrder(ContentType.LABEL);

                            while (types.next()) {
                                ContentType type = types.getContentType();
                                target.addOption(new Option(type.getID().toString(),
                                                            type.getLabel()));
                            }
                        }
                    });
            } catch (TooManyListenersException ex) {
                throw new UncheckedWrapperException("This can never happen", ex);
            }
            add(m_type);

            // XXX no need for selecting template contexts currently
            m_context = new Hidden(context.getStateParameter());
            m_context.setDefaultValue(TemplateManager.PUBLIC_CONTEXT);
            /*
              m_context = new SingleSelect(context.getStateParameter());
              try {
              m_context.addPrintListener(new PrintListener() {
              public void prepare(PrintEvent e) {
              PageState state = e.getPageState();

              SingleSelect target = (SingleSelect)e.getTarget();

              TemplateContextCollection contexts = TemplateContext.retrieveAll();
              contexts.addOrder(TemplateContext.LABEL);

              while (contexts.next()) {
              TemplateContext type = contexts.getTemplateContext();
              target.addOption(new Option(type.getContext(),
              type.getLabel()));
              }
              context.setSelectedKey(state, TemplateManager.PUBLIC_CONTEXT);
              }
              });
              } catch (TooManyListenersException ex) {
              throw new UncheckedWrapperException("This can never happen", ex);
              }
            */
            add(m_context);

            add(new Submit("Assign template"));
        }
    }
}

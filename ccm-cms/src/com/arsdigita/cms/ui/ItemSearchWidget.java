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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeLifecycleDefinition;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ContentCenter;
import com.arsdigita.cms.ContentCenterServlet;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.workflow.simple.Workflow;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * A class representing a content item search field in an HTML form.
 *
 * @author Scott Seago (sseago@redhat.com)
 * @version $Id: ItemSearchWidget.java 1166 2006-06-14 11:45:15Z fabrice $
 */
public class ItemSearchWidget extends FormSection
                                      implements BebopConstants, 
                                                 FormSubmissionListener, 
                                                 FormInitListener {

    private static final Logger s_log = Logger.getLogger(ItemSearchWidget.class);
    //private Hidden m_selected;    
    private TextField m_selected;
    private TextField m_item;
    private Hidden m_publish;
    private Submit m_search;
    private Submit m_clear;
    private Label m_jsLabel;
    private Label m_topHR;
    private Label m_bottomHR;
    private ContentType m_contentType;
    private ItemSearchSectionInline m_searchComponent;
    private String m_name;
    private String m_searchName;
    private String m_clearName;
    private Folder m_defaultCreationFolder;
    private String m_queryField;
    private ParameterModel m_model;
    private ParameterModel m_searchModel;
    private boolean disableCreatePane = false;
    private boolean editAfterCreate = true;
    private GlobalizedMessage searchLabelText = GlobalizationUtil.globalize(
                                                "cms.ui.item_search.search");
    private GlobalizedMessage selectedLabelText = GlobalizationUtil.globalize(
                                                  "cms.ui.item_search.selected");
    public static final String BEBOP_ITEM_SEARCH = "bebop:itemSearch";
    public static final String SEARCH = "search";
    public static final boolean LIMIT_TO_CONTENT_SECTION = false;
    public static final String PUBLISH = "publish";

    private class ItemFragment extends TextField {

        private ItemSearchWidget parent;

        public ItemFragment(ParameterModel parameter, ItemSearchWidget parent) {
            super(parameter);
            this.parent = parent;
            this.setReadOnly();
            this.setSize(35);
        }

    }

    private class SearchFragment extends Submit {

        private ItemSearchWidget parent;

        public SearchFragment(String name, ItemSearchWidget parent) {
            super(name, GlobalizationUtil.globalize("cms.ui.search"));    
            this.parent = parent;
            this.setAttribute("onClick", "return " + parent.m_selected.getName(). //+ parent.m_item.getName().
                    replace('.', '_') + "Popup(this.form)");
            this.setAttribute("value", "Search");
        }

        @Override
        public boolean isVisible(PageState ps) {
            return (!(parent.m_search.isSelected(ps)
                      || parent.m_searchComponent.hasQuery(ps))
                    && super.isVisible(ps));
        }

    }

    private class ClearFragment extends Submit {

        private ItemSearchWidget parent;

        public ClearFragment(String name, ItemSearchWidget parent) {
            super(name, GlobalizationUtil.globalize("cms.ui.clear"));    
            this.parent = parent;
            this.setAttribute("onClick", "this.form." + parent.m_selected.getName() 
                                         + ".value = \"\";" + "this.form." 
                                         + parent.m_item.getName() + ".value = \"\";"
                                         + "return false;");
            this.setAttribute("value", "Clear");
        }

    }

    private class LabelFragment extends Label {

        private ItemSearchWidget parent;

        public LabelFragment(String name, boolean escaping,
                             ItemSearchWidget parent) {
            super(name, escaping);
            this.parent = parent;
        }

    }

    private class ItemSearchFragment extends ItemSearchSectionInline {

        private ItemSearchWidget parent;

        public ItemSearchFragment(String name, String context,
                                  ItemSearchWidget parent,
                                  boolean limitToContentSection) {
            super(name, context, limitToContentSection);
            this.parent = parent;
        }

        public ItemSearchFragment(String name, String context,
                                  ItemSearchWidget parent,
                                  boolean limitToContentSection,
                                  ContentType type) {
            super(name, context, limitToContentSection, type);
            this.parent = parent;
        }

        @Override
        public boolean isVisible(PageState ps) {
            return ((m_search.isSelected(ps)
                     || hasQuery(ps))
                    && super.isVisible(ps));
        }

    }

    private class HRLabel extends Label {

        public HRLabel() {
            super("<hr/>", false);
        }

        @Override
        public boolean isVisible(PageState ps) {
            return ((m_search.isSelected(ps)
                     || m_searchComponent.hasQuery(ps))
                    && super.isVisible(ps));
        }

    }

    /**
     * Construct a new ItemSearchWidget. The model must be an ItemSearchParameter
     */
    public ItemSearchWidget(ParameterModel model) {
        this(model, null);
    }

    /**
     * Construct a new ItemSearchWidget. The model must be an ItemSearchParameter
     *
     * @param model                                                                                                                         contentType
     * @param contentType  
     */
    public ItemSearchWidget(final ParameterModel model, 
                            final ContentType contentType) {
        super(new BoxPanel(BoxPanel.VERTICAL));

        if (!(model instanceof ItemSearchParameter)) {
            throw new IllegalArgumentException(
                    "The ItemSearch widget " + model.getName()
                    + " must be backed by a ItemSearchParameter parameter model");
        }

        m_name = model.getName();
        m_searchName = m_name + "_search";
        m_clearName = m_name + "_clear";
        m_model = model;

        final String typeURLFrag;
        if (contentType != null) {
            typeURLFrag = contentType.getID().toString();
        } else {
            typeURLFrag = null;
        }

        m_searchModel = new StringParameter(SEARCH);

        m_contentType = contentType;
        //m_selected = new Hidden(model);
        m_selected = new ItemFragment(model, this);
        final Label selectedItemLabel = new Label(GlobalizationUtil.globalize(""
                + "                               cms.ui.item_search.selected"));
        selectedItemLabel.addPrintListener(new PrintListener() {

            public void prepare(final PrintEvent event) {
                final Label target = (Label) event.getTarget();
                target.setLabel(GlobalizationUtil.globalize(
                                "cms.ui.item_search.selected"));
            }

        });
        final Label searchLabel = new Label(searchLabelText);
        searchLabel.addPrintListener(new PrintListener() {

            public void prepare(final PrintEvent event) {
                final Label target = (Label) event.getTarget();
                target.setLabel(searchLabelText);
            }

        });
        //m_item = new ItemFragment(model, this);

        m_publish = new Hidden(PUBLISH);
        add(m_publish);

        m_item = new TextField(m_searchModel);
        m_search = new SearchFragment(m_searchName, this);
        m_clear = new ClearFragment(m_clearName, this);
        m_jsLabel = new LabelFragment("", false, this);
        m_jsLabel.addPrintListener(new PrintListener() {

            public void prepare(PrintEvent event) {
                PageState state = event.getPageState();
                Label t = (Label) event.getTarget();
                String formName = ((LabelFragment) t).parent.getSearchButton().
                        getForm().getName();
                ParameterMap params = new ParameterMap();
                params.setParameter("section_id",
                                    CMS.getContext().getContentSection().getID());
                params.setParameter("widget", formName + ".elements['" + m_selected. //m_item.
                        getName() + "']");
                params.setParameter("searchWidget", formName + ".elements['" + m_item.getName() + "']");
                if (typeURLFrag != null) {
                    params.setParameter("single_type", typeURLFrag);
                }
                params.setParameter("publishWidget", formName + ".elements['" + m_publish.getName() + "']");
                params.setParameter("disableCreatePane", Boolean.toString(disableCreatePane));
                params.setParameter("editAfterCreate", Boolean.toString(editAfterCreate));

                if (m_defaultCreationFolder != null) {
                    params.setParameter("defaultCreationFolder", m_defaultCreationFolder.getOID().toString());
                }
                
                if (m_queryField != null) {
                    params.setParameter("queryField", m_queryField);
                }

                String searchURL = ContentCenterServlet.getURLStubForClass(
                        ItemSearchPage.class.getName());
                s_log.debug("Search URL stub is: " + searchURL);

                searchURL = ContentCenter.getURL() + searchURL;

                // TODO Not sure what to do when you get a null here

                URL url = URL.there(state.getRequest(), searchURL, params);

                t.setLabel(
                        " <script language=javascript> "
                        + " <!-- \n"
                        + " function "
                        + m_selected.getName().replace('.', '_')
                        + "Popup(theForm) { \n"
                        + "var width = screen.width * 0.5;\n"
                        + "var height = screen.height * 0.5;\n"
                        + "if ((width < 800) && (screen.width >= 800)) {\n"
                        + "width = 800;\n"
                        + "}\n"
                        + "if ((height < 600) && (screen.height >= 600)) {\n"
                        + "height = 600;\n"
                        + "}\n"
                        + " aWindow = window.open(\"" + url + "&query=\" + document.getElementById('"
                        + m_item.getName() + "').value , "
                        // The following line worked not for nested ItemSearchWidgets. The line with m_name seems to work.
                        //+ "\"search\", \"toolbar=no,width=\" + width + \",height=\" + height + \",status=no,scrollbars=yes,resize=yes\");\n"
                        + "\"" + m_name + "\", \"toolbar=no,width=\" + width + \",height=\" + height + \",status=no,scrollbars=yes,resize=yes\");\n"
                        + "document." + formName + "." + m_publish.getName() + ".value = \"false\";\n "
                        + "return false;\n"
                        + " } \n"
                        + " --> \n"
                        + " </script> ");
            }

        });
        m_topHR = new HRLabel();
        add(m_topHR);
        final FormSection searchSection = new FormSection(new BoxPanel(BoxPanel.VERTICAL));
        final BoxPanel searchRow = new BoxPanel(BoxPanel.HORIZONTAL);
        searchRow.add(searchLabel);
        searchRow.add(m_item);
        searchRow.add(m_search);
        searchRow.add(m_clear);
        final BoxPanel itemRow = new BoxPanel(BoxPanel.HORIZONTAL);
        itemRow.add(selectedItemLabel);
        itemRow.add(m_selected);
        searchSection.add(searchRow);
        searchSection.add(itemRow);
        searchSection.add(m_jsLabel);
        add(searchSection);
        if (m_contentType == null) {
            m_searchComponent = new ItemSearchFragment(m_name, ContentItem.DRAFT,
                                                       this,
                                                       LIMIT_TO_CONTENT_SECTION);
        } else {
            m_searchComponent = new ItemSearchFragment(m_name, ContentItem.DRAFT,
                                                       this,
                                                       LIMIT_TO_CONTENT_SECTION,
                                                       m_contentType);
        }
        add(m_searchComponent);
        addSubmissionListener(this);
        addInitListener(this);
        m_bottomHR = new HRLabel();
        add(m_bottomHR);

    }

    @Override
    public void register(Page p) {
        super.register(p);
        p.setVisibleDefault(m_topHR, false);
        p.setVisibleDefault(m_searchComponent, false);
        p.setVisibleDefault(m_bottomHR, false);
    }

    public ItemSearchWidget(String name) {
        this(new ItemSearchParameter(name));
    }

    public ItemSearchWidget(String name,
                            String objectType)
            throws DataObjectNotFoundException {
        this(name, (objectType == null || objectType.length() == 0
                    ? null
                    : ContentType.findByAssociatedObjectType(objectType)));
    }

    public ItemSearchWidget(String name, ContentType contentType) {
        this(new ItemSearchParameter(name, contentType), contentType);
    }

    public Submit getSearchButton() {
        return m_search;
    }

    public Submit getClearButton() {
        return m_clear;
    }

    public TextField getItemField() {
        return m_item;
    }

    public void init(FormSectionEvent e) throws FormProcessException {
        PageState s = e.getPageState();
        try {
            m_searchComponent.setVisible(s, false);
            m_topHR.setVisible(s, false);
            m_bottomHR.setVisible(s, false);
            m_search.setVisible(s, true);
            e.getFormData().put(PUBLISH, Boolean.FALSE.toString());
        } catch (IllegalStateException ex) {
            // component is in metaform. nothing to do here. Custom generateXML must hide for us
        }
    }

    public void submitted(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();
        final FormData data = event.getFormData();
        s_log.debug("Doing submission");
        if (m_searchComponent.isItemSelected(state)) {
            s_log.debug("Item selected");
            ContentItem item = m_searchComponent.getSelectedItem(state);
            if (item != null) {
                m_item.setValue(state, item);
            }
            try {
                m_searchComponent.setVisible(state, false);
                m_topHR.setVisible(state, false);
                m_bottomHR.setVisible(state, false);
                m_search.setVisible(state, true);
            } catch (IllegalStateException ex) {
                // component is in metaform. nothing to do here. Custom generateXML must hide for us
            }

            throw new FormProcessException("item search FormSection submit");

        } else if (m_searchComponent.hasQuery(state)) {
            s_log.debug("Has query");
            try {
                m_searchComponent.setVisible(state, true);
                m_searchComponent.processQuery(state);
                m_topHR.setVisible(state, true);
                m_bottomHR.setVisible(state, true);
                m_search.setVisible(state, false);
            } catch (IllegalStateException ex) {
                // component is in metaform. nothing to do here. Custom generateXML must hide for us
            }

            if (m_contentType != null) {
                state.setValue(new BigDecimalParameter(ItemSearch.SINGLE_TYPE_PARAM),
                               m_contentType.getID());
            } else {
                state.setValue(new BigDecimalParameter(ItemSearch.SINGLE_TYPE_PARAM),
                               null);
            }
            throw new FormProcessException("item search FormSection submit");
        } else if (m_search.isSelected(state)) {
            s_log.debug("Search selected");
            try {
                m_searchComponent.setVisible(state, true);
                m_searchComponent.processQuery(state);
                m_topHR.setVisible(state, true);
                m_bottomHR.setVisible(state, true);
                m_search.setVisible(state, false);
            } catch (IllegalStateException ex) {
                // component is in metaform. nothing to do here. Custom generateXML must hide for us
            }

            if (m_contentType != null) {
                state.setValue(new BigDecimalParameter(ItemSearch.SINGLE_TYPE_PARAM),
                               m_contentType.getID());
            } else {
                state.setValue(new BigDecimalParameter(ItemSearch.SINGLE_TYPE_PARAM),
                               null);
            }
            throw new FormProcessException("item search FormSection submit");
        } else if (m_clear.isSelected(state)) {
            s_log.debug("Clear selected");
            m_item.setValue(state, null);
            try {
                m_searchComponent.setVisible(state, false);
                m_topHR.setVisible(state, false);
                m_bottomHR.setVisible(state, false);
                m_search.setVisible(state, true);
            } catch (IllegalStateException ex) {
                // component is in metaform. nothing to do here. Custom generateXML must hide for us
            }
            throw new FormProcessException("item search FormSection submit");
        } else {
            s_log.debug("Something else");
            try {
                m_searchComponent.setVisible(state, false);
                m_topHR.setVisible(state, false);
                m_bottomHR.setVisible(state, false);
                m_search.setVisible(state, true);
            } catch (IllegalStateException ex) {
                // component is in metaform. nothing to do here. Custom generateXML must hide for us
            }
        }
    }

    public GlobalizedMessage getSearchLabelText() {
        return searchLabelText;
    }

    public void setSearchLabelText(final GlobalizedMessage searchLabelText) {
        this.searchLabelText = searchLabelText;
    }

    public GlobalizedMessage getSelectedLabelText() {
        return selectedLabelText;
    }

    public void setSelectedLabelText(final GlobalizedMessage selectedLabelText) {
        this.selectedLabelText = selectedLabelText;
    }

    public void setDefaultCreationFolder(final Folder folder) {
        m_defaultCreationFolder = folder;
    }

    public void setQueryField(final String queryField) {
        m_queryField = queryField;
    }
    
    public void publishCreatedItem(final FormData data, final ContentItem item) {
        final String publishStr = data.getString(ItemSearchWidget.PUBLISH);
        final Boolean publish = Boolean.valueOf(publishStr);
        if (publish) {
            final LifecycleDefinition lifecycleDef = ContentTypeLifecycleDefinition.getLifecycleDefinition(
                    item.getContentSection(), item.getContentType());

            if (lifecycleDef == null) {
                s_log.warn(String.format("Cannot publish item %s because it has no default lifecycle",
                                         item.getOID().toString()));
            } else {
                item.publish(lifecycleDef, new Date());
                item.getLifecycle().start();
                final Workflow workflow = Workflow.getObjectWorkflow(item);
                if (workflow != null) {
                    workflow.delete();
                }
            }
        }
    }
    
    public boolean getDisableCreatePane() {
        return disableCreatePane;
    }
    
    public void setDisableCreatePane(final boolean disableCreatePane) {
        this.disableCreatePane = disableCreatePane;
    }
    
    public void setEditAfterCreate(final boolean editAfterCreate) {
        this.editAfterCreate = editAfterCreate;
    }
}
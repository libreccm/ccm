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
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Workspace;
import com.arsdigita.cms.dispatcher.ContentCenterDispatcher;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import org.apache.log4j.Logger;

/**
 * A class representing a content item search field in an HTML form.
 *
 * @author Scott Seago (sseago@redhat.com)
 * @version $Id: ItemSearchWidget.java 1166 2006-06-14 11:45:15Z fabrice $
 */
public class ItemSearchWidget extends FormSection
        implements BebopConstants, FormSubmissionListener, FormInitListener {

    private static final Logger s_log = Logger.getLogger(ItemSearchWidget.class);
    private TextField m_item;
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
    private ParameterModel m_model;
    public static final String BEBOP_ITEM_SEARCH = "bebop:itemSearch";
    public static final boolean LIMIT_TO_CONTENT_SECTION = false;

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
            super(name, "Search");
            this.parent = parent;
            this.setAttribute("onClick", "return " + parent.m_item.getName().
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
            super(name, "Clear");
            this.parent = parent;
            this.setAttribute("onClick", "this.form." + parent.m_item.getName()
                                         + ".value = \"\"; return false;");
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
     * Construct a new ItemSearchWidget. The model must be an
     * ItemSearchParameter
     */
    public ItemSearchWidget(ParameterModel model) {
        this(model, null);
    }

    /**
     * Construct a new ItemSearchWidget. The model must be an
     * ItemSearchParameter
     */
    public ItemSearchWidget(ParameterModel model, ContentType contentType) {
        super(new BoxPanel(BoxPanel.VERTICAL));

        if (!(model instanceof ItemSearchParameter)) {
            throw new IllegalArgumentException(
                    "The ItemSearch widget " + model.getName()
                    + " must be backed by a ItemSearchParameter parmeter model");
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

        m_contentType = contentType;
        m_item = new ItemFragment(model, this);
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
                params.setParameter("widget", formName + ".elements['" + m_item.
                        getName() + "']");
                if (typeURLFrag != null) {
                    params.setParameter("single_type", typeURLFrag);
                }


                String searchURL = ContentCenterDispatcher.getURLStubForClass(
                        ItemSearchPage.class.getName());
                s_log.debug("Search URL stub is: " + searchURL);

                searchURL = Workspace.getURL() + searchURL;

                // TODO Not sure what to do when you get a null here

                URL url = URL.there(state.getRequest(), searchURL, params);

                t.setLabel(" <script language=javascript> "
                           + " <!-- \n"
                           + " function "
                           + m_item.getName().replace('.', '_')
                           + "Popup(theForm) { \n"
                           + " aWindow = window.open(\"" + url
                           + "\", \"search\", \"toolbar=no,width=800,height=600,status=no,scrollbars=yes,resize=yes,menubar=no\");\n return false;\n"
                           + " } \n"
                           + " --> \n"
                           + " </script> ");
            }
        });
        m_topHR = new HRLabel();
        add(m_topHR);
        FormSection searchSection = new FormSection(new BoxPanel(
                BoxPanel.HORIZONTAL));
        searchSection.add(m_item);
        searchSection.add(m_search);
        searchSection.add(m_clear);
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
        } catch (IllegalStateException ex) {
            // component is in metaform. nothing to do here. Custom generateXML must hide for us
        }
    }

    public void submitted(FormSectionEvent e) throws FormProcessException {
        PageState s = e.getPageState();
        FormData data = e.getFormData();
        s_log.debug("Doing submission");
        if (m_searchComponent.isItemSelected(s)) {
            s_log.debug("Item selected");
            ContentItem item = m_searchComponent.getSelectedItem(s);
            if (item != null) {
                m_item.setValue(s, item);
            }
            try {
                m_searchComponent.setVisible(s, false);
                m_topHR.setVisible(s, false);
                m_bottomHR.setVisible(s, false);
                m_search.setVisible(s, true);
            } catch (IllegalStateException ex) {
                // component is in metaform. nothing to do here. Custom generateXML must hide for us
            }

            throw new FormProcessException("item search FormSection submit");

        } else if (m_searchComponent.hasQuery(s)) {
            s_log.debug("Has query");
            try {
                m_searchComponent.setVisible(s, true);
                m_searchComponent.processQuery(s);
                m_topHR.setVisible(s, true);
                m_bottomHR.setVisible(s, true);
                m_search.setVisible(s, false);
            } catch (IllegalStateException ex) {
                // component is in metaform. nothing to do here. Custom generateXML must hide for us
            }

            if (m_contentType != null) {
                s.setValue(new BigDecimalParameter(ItemSearch.SINGLE_TYPE_PARAM),
                           m_contentType.getID());
            } else {
                s.setValue(new BigDecimalParameter(ItemSearch.SINGLE_TYPE_PARAM),
                           null);
            }
            throw new FormProcessException("item search FormSection submit");
        } else if (m_search.isSelected(s)) {
            s_log.debug("Search selected");
            try {
                m_searchComponent.setVisible(s, true);
                m_searchComponent.processQuery(s);
                m_topHR.setVisible(s, true);
                m_bottomHR.setVisible(s, true);
                m_search.setVisible(s, false);
            } catch (IllegalStateException ex) {
                // component is in metaform. nothing to do here. Custom generateXML must hide for us
            }

            if (m_contentType != null) {
                s.setValue(new BigDecimalParameter(ItemSearch.SINGLE_TYPE_PARAM),
                           m_contentType.getID());
            } else {
                s.setValue(new BigDecimalParameter(ItemSearch.SINGLE_TYPE_PARAM),
                           null);
            }
            throw new FormProcessException("item search FormSection submit");
        } else if (m_clear.isSelected(s)) {
            s_log.debug("Clear selected");
            m_item.setValue(s, null);
            try {
                m_searchComponent.setVisible(s, false);
                m_topHR.setVisible(s, false);
                m_bottomHR.setVisible(s, false);
                m_search.setVisible(s, true);
            } catch (IllegalStateException ex) {
                // component is in metaform. nothing to do here. Custom generateXML must hide for us
            }
            throw new FormProcessException("item search FormSection submit");
        } else {
            s_log.debug("Something else");
            try {
                m_searchComponent.setVisible(s, false);
                m_topHR.setVisible(s, false);
                m_bottomHR.setVisible(s, false);
                m_search.setVisible(s, true);
            } catch (IllegalStateException ex) {
                // component is in metaform. nothing to do here. Custom generateXML must hide for us
            }
        }
    }
}

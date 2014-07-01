/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.cms.contentassets.DublinCoreES;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.category.CategoryIteratorListModel;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.xml.Element;

import java.util.Date;
import java.text.DateFormat;

import org.apache.log4j.Category;

/**
 * 
 * 
 */
public class DublinCoreSummary extends ColumnPanel {

    private static final Category s_log =
        Category.getInstance( DublinCoreSummary.class );

    private Label m_title;

    private Label m_contributor;
    private Label m_coverage;
    private Label m_creator;

    private Label m_date_created;
    private Label m_date_issued;
    private Label m_date_modified;
    private Label m_dateValid;

    private Label m_description;

    private Label m_identifier;
    private Label m_language;

    private Label m_publisher;
    private Label m_relation;
    private Label m_rights;
    private Label m_source;
    private Label m_subject;
    private Label m_documentType;
    private List m_categories;

    private ItemSelectionModel m_itemModel;

    /**
     * Constructor, creates the panel.
     * 
     * @param itemModel 
     */
    public DublinCoreSummary(ItemSelectionModel itemModel) {
        super(2);

        setColumnWidth( 1, "40%" );

        m_itemModel = itemModel;

        m_title = new Label();


        m_date_created = new Label();
        m_date_issued = new Label();
        m_date_modified = new Label();

        m_dateValid = new Label();

        m_documentType = new Label();

        m_creator = new Label();

        m_description = new Label();
        m_publisher = new Label();
        m_rights = new Label();

        m_language = new Label();

        m_subject = new Label();

        ListCellRenderer simpleCellRenderer = new SimpleCellRenderer();
        m_categories = new List(new CategoryListModelBuilder(itemModel,
                                                             "subject"));
        m_categories.setCellRenderer(simpleCellRenderer);


        add(new Label("Title:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_title);

        add(new Label("Coverage:", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_coverage);

        add(new Label("Creator (owner):", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_creator);

        add(new Label("Date (created):", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_date_created);

        add(new Label("Date (issued):", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_date_issued);

        add(new Label("Date (modified):", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_date_modified);

        add(new Label("Date (valid):", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_dateValid);

        add(new Label("Description:", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_description);

        add(new Label("Publisher:", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_publisher);

        add(new Label("Rights:", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_rights);

        add(new Label("Type (document type):", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_documentType);

     // add(new Label("Disposal (review):", Label.BOLD),
     //     ColumnPanel.RIGHT);
     // add(m_disposalReview);

        add(new Label("Language:", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_language);

        add(new Label("Keywords:", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_subject);

        add(new Label("Subject Categories:", Label.BOLD),
            ColumnPanel.RIGHT | ColumnPanel.TOP);
        add(m_categories);

    }

    /**
     * 
     * @param state
     * @param parent 
     */
    @Override
    public void generateXML(PageState state,
                            Element parent) {

        ContentPage item = (ContentPage)m_itemModel.getSelectedObject(state);
        DublinCoreES dces = DublinCoreES.findByOwner(item);

        /* Retrieve dc title from associated content items title. Not persisted
         * in dces database table.                                            */
        m_title.setLabel(item.getDisplayName(), state);

        if (dces != null) {
            m_coverage.setLabel(dces.getCoverage(), state);
        }

        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, 
                                                               DateFormat.MEDIUM);

        ContentPage latest;
        String issueDate;
        if (item.isLive()) {
            latest = (ContentPage)item.getLiveVersion();

            Date issued = item.getLifecycle().getStartDate();
            if (issued != null) {
                issueDate = dateFormat.format(issued);
            } else {
                issueDate = "";
            }
        } else {
            latest = item;
            issueDate = "";
        }

        String creationDate = (item.getCreationDate() != null ? 
                               dateFormat.format( item.getCreationDate()) : "" );
        String modifiedDate = (latest.getLastModifiedDate() != null ? 
                               dateFormat.format( latest.getLastModifiedDate()) : "");

        m_date_created.setLabel( creationDate, state );
        m_date_issued.setLabel( issueDate, state );
        m_date_modified.setLabel( modifiedDate, state );

        if (dces != null) {
            m_creator.setLabel(dces.getCreator(), state);
        }

        m_description.setLabel(item.getSearchSummary(), state);

        if (dces != null) {
            m_publisher.setLabel(dces.getPublisher(), state);
            m_rights.setLabel(dces.getRights(), state);
        }
            
        m_documentType.setLabel(item.getContentType().getName(), state);

        m_language.setLabel(item.getLanguage(), state);
            
        if (dces != null) {
            m_dateValid.setLabel(dces.getDate(), state);
            
         // m_keywords.setName(dces.getKeywords(), state); will be Subject!
        }

        super.generateXML(state, parent);
    }

    /**
     * 
     */
    protected class CategoryListModelBuilder extends LockableImpl
                                             implements ListModelBuilder {

        private ItemSelectionModel m_itemModel;
        private String m_context;

        /**
         * 
         * @param itemModel
         * @param context 
         */
        public CategoryListModelBuilder(ItemSelectionModel itemModel,
                                        String context) {
            m_itemModel = itemModel;
            m_context = context;
        }

        /**
         * 
         * @param l
         * @param state
         * @return 
         */
        public ListModel makeModel( List l, PageState state ) {
            ContentPage item = (ContentPage) m_itemModel.getSelectedObject(state);
            return new CategoryIteratorListModel(item.getCategories(m_context));
        }
    }

    /**
     * 
     */
    protected class SimpleCellRenderer implements ListCellRenderer {

        /**
         * 
         * @param list
         * @param state
         * @param value
         * @param key
         * @param index
         * @param isSelected
         * @return 
         */
        public Component getComponent(List list, 
                                      PageState state, 
                                      Object value,
                                      String key, 
                                      int index,
                                      boolean isSelected) {
            return new Label(value.toString());
        }
    };
}

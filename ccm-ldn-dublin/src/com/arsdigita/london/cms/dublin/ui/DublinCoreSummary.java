/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.cms.dublin.ui;

import com.arsdigita.london.cms.dublin.DublinCoreItem;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.cms.ui.category.CategoryIteratorListModel;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.xml.Element;

import java.util.Date;
import java.text.DateFormat;

import org.apache.log4j.Category;

public class DublinCoreSummary extends ColumnPanel {

    private static final Category s_log =
        Category.getInstance( DublinCoreSummary.class );

    private Label m_title;
    private Label m_audience;
    //private Label m_contributor;
    private Label m_coverage_spatial;
    private Label m_coverage_postcode;
    private Label m_coverage_unit;
    private Label m_date_created;
    private Label m_date_issued;
    private Label m_date_modified;
    private Label m_dateValid;
    private Label m_temporal_begin;
    private Label m_temporal_end;
    private Label m_creator_owner;
    private Label m_creator_contact;
    private Label m_description;
    //private Label m_identifier;
    //private Label m_location;
    //private Label m_preservation;
    private Label m_publisher;
    //private Label m_relation;
    private Label m_rights;
    //private Label m_source;
    private Label m_documentType;
    private Label m_disposalReview;
    private Label m_language;
    private Label m_coverageSpatialRef;
    private List m_categories;
    private List m_interaction;
    private Label m_keywords;
    private Label m_ccn_portal_instance;

    private ItemSelectionModel m_itemModel;

    public DublinCoreSummary(ItemSelectionModel itemModel) {
        super(2);

        setColumnWidth( 1, "40%" );

        m_itemModel = itemModel;

        m_title = new Label();

        m_audience = new Label();

        m_coverage_spatial = new Label();
        m_coverageSpatialRef = new Label();
        m_coverage_postcode = new Label();
        m_coverage_unit = new Label();

        m_date_created = new Label();
        m_date_issued = new Label();
        m_date_modified = new Label();

        m_temporal_begin = new Label();
        m_temporal_end = new Label();
        m_dateValid = new Label();

        m_disposalReview = new Label();
        m_documentType = new Label();

        m_creator_owner = new Label();
        m_creator_contact = new Label();

        m_description = new Label();
        m_publisher = new Label();
        m_rights = new Label();

        m_language = new Label();

        m_keywords = new Label();

        ListCellRenderer simpleCellRenderer = new SimpleCellRenderer();
        m_categories = new List(new CategoryListModelBuilder(itemModel,
                                                             "subject"));
        m_categories.setCellRenderer(simpleCellRenderer);

        m_interaction = new List(new CategoryListModelBuilder(itemModel,
                                                              "interaction"));
        m_interaction.setCellRenderer(simpleCellRenderer);

	if (DublinCoreItem.getConfig().getUseCCNPortalMetadata()) {
	    m_ccn_portal_instance = new Label();
	}

        add(new Label("Title:", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_title);

        add(new Label("Audience:", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_audience);

        add(new Label("Coverage:", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_coverage_spatial);

        add(new Label("Coverage (postcode):", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_coverage_postcode);

        add(new Label("Coverage (spatial reference number):", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_coverageSpatialRef);

        add(new Label("Coverage (unit):", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_coverage_unit);

        add(new Label("Coverage (temporal begin):", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_temporal_begin);

        add(new Label("Coverage (temporal end):", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_temporal_end);

        add(new Label("Creator (owner):", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_creator_owner);

        add(new Label("Creator (contact):", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_creator_contact);

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

        add(new Label("Disposal (review):", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_disposalReview);

        add(new Label("Language:", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_language);

        add(new Label("Keywords:", Label.BOLD),
            ColumnPanel.RIGHT);
        add(m_keywords);

        add(new Label("Subject Categories:", Label.BOLD),
            ColumnPanel.RIGHT | ColumnPanel.TOP);
        add(m_categories);

        add(new Label("Interaction:", Label.BOLD),
            ColumnPanel.RIGHT | ColumnPanel.TOP);
        add(m_interaction);

	if (DublinCoreItem.getConfig().getUseCCNPortalMetadata()) {
	    add(new Label("Include page in portal:", Label.BOLD),
		ColumnPanel.RIGHT);
	    add(m_ccn_portal_instance);
	}
    }

    public void generateXML(PageState state,
                            Element parent) {
        ContentPage item = (ContentPage)m_itemModel.getSelectedObject(state);
        DublinCoreItem dcItem = DublinCoreItem.findByOwner(item);

        m_title.setLabel(item.getDisplayName(), state);

        if (dcItem != null) {
            m_audience.setLabel(dcItem.getAudience(), state);
            m_coverage_spatial.setLabel(dcItem.getCoverage(), state);
            m_coverage_postcode.setLabel(dcItem.getCoveragePostcode(), state);
            m_coverageSpatialRef.setLabel(dcItem.getCoverageSpatialRef(), state);
            m_coverage_unit.setLabel(dcItem.getCoverageUnit(), state);
	    if (DublinCoreItem.getConfig().getUseCCNPortalMetadata()) {
		m_ccn_portal_instance.setLabel(dcItem.getCcnPortalInstance(), state);
	    }
        }

        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

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

        String creationDate = (item.getCreationDate() != null ? dateFormat.format( item.getCreationDate()) : "" );
        String modifiedDate = (latest.getLastModifiedDate() != null ? dateFormat.format( latest.getLastModifiedDate()) : "");

        m_date_created.setLabel( creationDate, state );
        m_date_issued.setLabel( issueDate, state );
        m_date_modified.setLabel( modifiedDate, state );

        if (dcItem != null) {
            m_temporal_begin.setLabel((dcItem.getTemporalBegin() == null ? null : dateFormat.format(dcItem.getTemporalBegin())), state);
            m_temporal_end.setLabel((dcItem.getTemporalEnd() == null ? null : dateFormat.format(dcItem.getTemporalEnd())), state);
            
            m_creator_owner.setLabel(dcItem.getCreatorOwner(), state);
            m_creator_contact.setLabel(dcItem.getCreatorContact(), state);
	}

	m_description.setLabel(item.getSearchSummary(), state);

	if (dcItem != null) {
            m_publisher.setLabel(dcItem.getPublisher(), state);
            m_rights.setLabel(dcItem.getRights(), state);
        }
            
        m_documentType.setLabel(item.getContentType().getLabel(), state);

        m_language.setLabel(item.getLanguage(), state);
            
        if (dcItem != null) {
            m_dateValid.setLabel(dcItem.getDateValid(), state);
            m_disposalReview.setLabel(dcItem.getDisposalReview(), state);
            
            m_keywords.setLabel(dcItem.getKeywords(), state);
        }

        super.generateXML(state,
                          parent);
    }

    protected class CategoryListModelBuilder extends LockableImpl
        implements ListModelBuilder
    {
        private ItemSelectionModel m_itemModel;
        private String m_context;

        public CategoryListModelBuilder(ItemSelectionModel itemModel,
                                        String context) {
            m_itemModel = itemModel;
            m_context = context;
        }

        public ListModel makeModel( List l, PageState state ) {
            ContentPage item = (ContentPage) m_itemModel.getSelectedObject(state);
            return new CategoryIteratorListModel(item.getCategories(m_context));
        }
    }

    protected class SimpleCellRenderer implements ListCellRenderer {
        public Component getComponent(List list, PageState state, Object value,
                                      String key, int index,
                                      boolean isSelected) {
            return new Label(value.toString());
        }
    };
}

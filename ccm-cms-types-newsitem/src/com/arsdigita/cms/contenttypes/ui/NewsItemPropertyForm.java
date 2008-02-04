/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes.ui;


import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.NewsItem;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.contenttypes.util.NewsItemGlobalizationUtil;

/**
 * Form to edit the basic properties of a <code>news item</code>. These are 
 * name, title, item date and reference code. 
 * Used by <code>NewsItemPropertiesStep</code> authoring kit step.
 * <br />
 *
 * This form can be extended to create forms for NewsItem subclasses.
 **/
public class NewsItemPropertyForm extends BasicPageForm
    implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private NewsItemPropertiesStep m_step;

    /**  lead parameter name */
    public static final String LEAD = "lead";
    /** Item date parameter name */
    public static final String NEWS_DATE = "news_date";
    public static final String IS_HOMEPAGE = "isHomepage";
    /** Name of this form */
    public static final String ID = "news_item_edit";

    /**
     * Creates a new form to edit the NewsItem object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    NewsItem to work on
     */
    public NewsItemPropertyForm( ItemSelectionModel itemModel ) {
        this( itemModel, null );
    }

    /**
     * Creates a new form to edit the NewsItem object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    NewsItem to work on
     * @param step The NewsItemPropertiesStep which controls this form.
     */
    public NewsItemPropertyForm( ItemSelectionModel itemModel, NewsItemPropertiesStep step ) {
        super( ID, itemModel );
        m_step = step;
        addSubmissionListener(this);
    }


    /**
     * Adds widgets to the form.
     */
    protected void addWidgets() {
        super.addWidgets();

        // summary  (lead)
        add(new Label((String)NewsItemGlobalizationUtil.globalize
                ("cms.contenttypes.ui.newsitem.lead").localize()));
        ParameterModel leadParam = new StringParameter(LEAD);
        //leadParam
        //    .addParameterListener(new NotNullValidationListener());
        TextArea lead = new TextArea(leadParam);
        lead.setCols(50);
        lead.setRows(5);
        add(lead);

        // newsitem on homepage?
        if (!NewsItem.getConfig().getHideHomepageField()) {
            RadioGroup homepageWidget = new RadioGroup(IS_HOMEPAGE);
            homepageWidget.addOption(new Option("true", 
                                     new Label( (String)NewsItemGlobalizationUtil.globalize
                                                ("cms.ui.yes").localize())));
            homepageWidget.addOption(new Option("false", 
                                     new Label( (String)NewsItemGlobalizationUtil.globalize
                                                ("cms.ui.no").localize())));
            
            add(new Label( (String)NewsItemGlobalizationUtil.globalize
                           ("cms.contenttypes.ui.newsitem.homepage").localize()));
            add(homepageWidget);
        }
        
        // publication date
        add(new Label((String)NewsItemGlobalizationUtil.globalize
                ("cms.contenttypes.ui.newsitem.date").localize()));
        ParameterModel newsDateParam = new DateParameter(NEWS_DATE);
        newsDateParam
            .addParameterListener(new NotNullValidationListener());
        com.arsdigita.bebop.form.Date newsDate
            = new com.arsdigita.bebop.form.Date(newsDateParam );
        add(newsDate);
    }

    /** Form initialisation hook. Fills widgets with data. */
    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        NewsItem item = (NewsItem) super.initBasicWidgets(fse);

        // set a default item date, if none set
        java.util.Date newsDate = item.getNewsDate();
        if(newsDate == null) {
            // new Date is initialised to current time
            newsDate = new java.util.Date();
        }

        data.put(NEWS_DATE,   newsDate);
        data.put(LEAD,        item.getLead());
        if (!NewsItem.getConfig().getHideHomepageField()) {
            data.put(IS_HOMEPAGE, item.isHomepage());
        }
    }

    /** Cancels streamlined editing. */
    public void submitted( FormSectionEvent fse ) {
        if (m_step != null &&
            getSaveCancelSection().getCancelButton()
            .isSelected( fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    /** Form processing hook. Saves NewsItem object. */
    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();

        NewsItem item = (NewsItem) super.processBasicWidgets(fse);

        // save only if save button was newsed
        if(item != null
           && getSaveCancelSection().getSaveButton()
           .isSelected(fse.getPageState())) {

            item.setNewsDate((java.util.Date) data.get(NEWS_DATE));
            item.setLead((String) data.get(LEAD));
            if (!NewsItem.getConfig().getHideHomepageField()) {
                String isHomepage = (String) data.get(IS_HOMEPAGE);
                item.setIsHomepage(new Boolean(isHomepage));
            }
            item.save();
        }
        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}

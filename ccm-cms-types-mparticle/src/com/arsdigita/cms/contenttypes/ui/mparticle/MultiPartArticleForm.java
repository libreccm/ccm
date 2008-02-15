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
package com.arsdigita.cms.contenttypes.ui.mparticle;


import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.MultiPartArticle;
import com.arsdigita.cms.ui.authoring.NameValidationListener;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.cms.contenttypes.util.MPArticleGlobalizationUtil;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import java.util.Date;


/**
 * A form for editing MultiPartArticle and subclasses.
 *
 * @author <a href="mailto:dturner@arsdigita.com">Dave Turner</a>
 * @version $id$
 */
public abstract class MultiPartArticleForm extends FormSection
    implements FormInitListener, FormProcessListener, FormValidationListener
{
    protected ItemSelectionModel m_itemModel;
    protected SaveCancelSection  m_saveCancelSection;

    public static final String NAME    = "name";
    public static final String TITLE   = "title";
    public static final String SUMMARY = "summary";
    public static final String LAUNCH_DATE = ContentPage.LAUNCH_DATE;
    public static final String LANGUAGE = ContentItem.LANGUAGE;

    private static final Logger s_log = Logger.getLogger(MultiPartArticleForm.class);

    public MultiPartArticleForm ( String formName, ItemSelectionModel itemModel ) {
        super(new ColumnPanel(2));

        m_itemModel = itemModel;

        ColumnPanel panel = (ColumnPanel)getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("100%");

        addWidgets();

        addSaveCancelSection();

        addInitListener(this);
        addProcessListener(this);
        addValidationListener(this);
    }


    public void addSaveCancelSection () {
        m_saveCancelSection = new SaveCancelSection();
        add(m_saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
    }


    public SaveCancelSection getSaveCancelSection () {
        return m_saveCancelSection;
    }


	private Label m_script = new Label("<script language=\"javascript\" src=\"/javascript/manipulate-input.js\"></script>", false);
	


    protected void addWidgets () {
        add(new Label(MPArticleGlobalizationUtil.globalize("cms.contenttypes.ui.mparticle.title")));
        TextField titleWidget = new TextField(new TrimmedStringParameter(TITLE));
        titleWidget.addValidationListener(new NotNullValidationListener());
        titleWidget.setOnFocus("if (this.form." + NAME + ".value == '') { " +
                               " defaulting = true; this.form." + NAME +
                               ".value = urlize(this.value); }");
        titleWidget.setOnKeyUp(
            "if (defaulting) { this.form." + NAME +
            ".value = urlize(this.value) }"
            );
        add(titleWidget);

        add(new Label(MPArticleGlobalizationUtil.globalize("cms.contenttypes.ui.mparticle.name")));
        TextField nameWidget = new TextField(new TrimmedStringParameter(NAME));
        nameWidget.addValidationListener(new NameValidationListener());
        nameWidget.setOnFocus("defaulting = false");
        nameWidget.setOnBlur(
            "if (this.value == '') " +
            "{ defaulting = true; this.value = urlize(this.form." + TITLE +
            ".value) }"
            );
        add(nameWidget);

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            add(new Label(MPArticleGlobalizationUtil.globalize("cms.ui.authoring.page_launch_date")));
            ParameterModel launchDateParam = new DateParameter(LAUNCH_DATE);
            com.arsdigita.bebop.form.Date launchDate
                = new com.arsdigita.bebop.form.Date(launchDateParam);
            if (ContentSection.getConfig().getRequireLaunchDate()) {
                launchDate.addValidationListener(new NotNullValidationListener(MPArticleGlobalizationUtil.globalize("cms.contenttypes.ui.mparticle.no_launch_date")));
                // if launch date is required, help user by suggesting today's date
                launchDateParam.setDefaultValue(new Date());
            }
            add(launchDate);
        }

        add(new Label(MPArticleGlobalizationUtil.globalize("cms.contenttypes.ui.mparticle.summary")));
        TextArea summaryWidget = new TextArea(new TrimmedStringParameter(SUMMARY));
	if (ContentSection.getConfig().mandatoryDescriptions()) {
	    summaryWidget.addValidationListener(new NotEmptyValidationListener(GlobalizationUtil.globalize("cms.contenttypes.ui.description_missing")));
	}	
        summaryWidget.setRows(5);
        summaryWidget.setCols(30);
        add(summaryWidget);
    }

    public abstract void init     ( FormSectionEvent e ) throws FormProcessException;
    public abstract void process  ( FormSectionEvent e ) throws FormProcessException;
    public abstract void validate ( FormSectionEvent e ) throws FormProcessException;


    /** Utility method to initialize the name/title/summary widgets */
    public MultiPartArticle initBasicWidgets ( FormSectionEvent e ) {
        Assert.exists(m_itemModel, ItemSelectionModel.class);

        FormData data    = e.getFormData();
        PageState state  = e.getPageState();
        MultiPartArticle article =
            (MultiPartArticle)m_itemModel.getSelectedObject(state);

        if ( article != null ) {
            data.put(NAME,    article.getName());
            data.put(TITLE,   article.getTitle());
            if (!ContentSection.getConfig().getHideLaunchDate()) {
                data.put(LAUNCH_DATE, article.getLaunchDate());
            }
            data.put(SUMMARY, article.getSummary());
        }

        return article;
    }



    /** Utility method to process the name/title/summary widgets */
    public MultiPartArticle processBasicWidgets ( FormSectionEvent e ) {
        Assert.exists(m_itemModel, ItemSelectionModel.class);

        FormData data = e.getFormData();
        PageState state = e.getPageState();
        MultiPartArticle article = (MultiPartArticle)m_itemModel.getSelectedObject(state);

        if ( article != null ) {
            article.setName((String)data.get(NAME));
            article.setTitle((String)data.get(TITLE));
            if (!ContentSection.getConfig().getHideLaunchDate()) {
                article.setLaunchDate((Date)data.get(LAUNCH_DATE));
            }
            article.setSummary((String)data.get(SUMMARY));
        }

        return article;
    }

    /**
     * Ensure that the name of an item is unique within a folder.
     *
     * @param folder the folder in which to check
     * @param event the FormSectionEvent which was passed to the
     *  validation listener
     * @return true if the name is not null and unique, false otherwise
     */
    public boolean validateNameUniqueness ( Folder folder, FormSectionEvent event) {

        FormData data = event.getFormData();
        String name = (String)data.get(NAME);

        if ( name != null ) {
            final String query = "com.arsdigita.cms.validateUniqueItemName";
            DataQuery dq = SessionManager.getSession().retrieveQuery(query);
            dq.setParameter("parentId", folder.getID());
            dq.setParameter("name", name);

            return dq.size() == 0;
        }

        // false if name == null
        return false;
    }



    /**
     * Utility method to create a new MultiPartArticle and update the
     * selected model.  This can be called in the process method of a
     * ProcessListener.
     *
     * @param state the current page state
     * @return the new content item (or a proper subclass)
     */
    public MultiPartArticle createArticle ( PageState state )
        throws FormProcessException
    {
        Assert.exists(m_itemModel, ItemSelectionModel.class);

        MultiPartArticle article = null;

        try {
            article = (MultiPartArticle)m_itemModel.createItem();
        } catch(ServletException e) {
            s_log.error("Servlet Exception: " + e.getMessage(), e);
            throw new FormProcessException(e.getMessage(), e);

        }


        if ( m_itemModel.getSelectedKey(state) == null ) {
            m_itemModel.setSelectedObject(state, article);
        }

        return article;
    }

    public void generateXML(PageState ps, Element parent) {
        m_script.generateXML(ps, parent);
        super.generateXML(ps, parent);
    }



}

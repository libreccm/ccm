/*
 * Copyright (c) 2010 Jens Pelzetter
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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Publisher;
import com.arsdigita.cms.contenttypes.PublisherBundle;
import com.arsdigita.cms.ui.authoring.ApplyWorkflowFormSection;
import com.arsdigita.cms.ui.authoring.CreationSelector;
import com.arsdigita.cms.ui.authoring.LanguageWidget;
import com.arsdigita.cms.ui.authoring.PageCreate;
import com.arsdigita.util.Assert;
import java.util.Date;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublisherCreate extends PageCreate {

    public static final String PUBLISHER_NAME = Publisher.PUBLISHER_NAME;
    public static final String PLACE = Publisher.PLACE;

    public PublisherCreate(final ItemSelectionModel itemModel,
                           final CreationSelector parent) {
        super(itemModel, parent);
    }

    @Override
    protected void addWidgets() {

        ContentType type = getItemSelectionModel().getContentType();
        m_workflowSection = new ApplyWorkflowFormSection(type);
        add(m_workflowSection, ColumnPanel.INSERT);
        add(new Label(GlobalizationUtil.globalize(
                "cms.ui.authoring.content_type")));
        add(new Label(type.getName()));
        add(new Label(GlobalizationUtil.globalize("cms.ui.language.field")));
        add(new LanguageWidget(LANGUAGE));

        PublisherPropertyForm.addMandatoryFieldWidgets(this);

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            ParameterModel launchDateParam = new DateParameter(LAUNCH_DATE);
            com.arsdigita.bebop.form.Date launchDate =
                                          new com.arsdigita.bebop.form.Date(
                    launchDateParam);
            if (ContentSection.getConfig().getRequireLaunchDate()) {
                launchDate.addValidationListener(
                        new LaunchDateValidationListener());
                // if launch date is required, help user by suggesting today's date
                launchDateParam.setDefaultValue(new Date());
            }
            launchDate.setLabel(GlobalizationUtil.globalize(
                                "cms.ui.authoring.page_launch_date"));
            add(launchDate);
        }
    }
    
    @Override
    public void validate(FormSectionEvent fse) throws FormProcessException {       
        //We don't call super.validate(fse); here because the name 
        //(URL fragment) of a publisher is generated in a different manner
        //than the name (URL fragment) of other content items.
        Folder folder = m_parent.getFolder(fse.getPageState());
        Assert.exists(folder);
        validateNameUniqueness(folder, fse, Publisher.urlSave(getFullName(fse)));
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();
        final ContentSection section = m_parent.getContentSection(state);
        Folder folder = m_parent.getFolder(state);

        String fullName = getFullName(fse);
        Assert.exists(section, ContentSection.class);

        final ContentPage item = createContentPage(state);
        item.setLanguage((String) data.get(LANGUAGE));
        item.setName(Publisher.urlSave(fullName));
        item.setTitle(fullName);
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            item.setLaunchDate((Date) data.get(LAUNCH_DATE));
        }

        final PublisherBundle bundle = new PublisherBundle(item);
        bundle.setParent(folder);
        bundle.setContentSection(section);
        bundle.save();

        m_workflowSection.applyWorkflow(state, item);

        Publisher publisher = new Publisher(item.getOID());
        publisher.setPublisherName(data.getString(PUBLISHER_NAME));
        publisher.setPlace(data.getString(PLACE));
        publisher.save();

        m_parent.editItem(state, item);
    }

    private String getFullName(FormSectionEvent fse) {
        final FormData data = fse.getFormData();
        String publisherName = data.getString(PUBLISHER_NAME);
        String place = data.getString(PLACE);

        if (place == null) {
            place = "";
        }

        return String.format("%s %s", publisherName, place).trim();
    }
}

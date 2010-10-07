/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.ui.authoring.ApplyWorkflowFormSection;
import com.arsdigita.cms.ui.authoring.CreationSelector;
import com.arsdigita.cms.ui.authoring.LanguageWidget;
import com.arsdigita.cms.ui.authoring.PageCreate;
import com.arsdigita.util.Assert;
import java.util.Date;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class GenericPersonCreate extends PageCreate {

    private static final String SURNAME = GenericPerson.SURNAME;
    private static final String GIVENNAME = GenericPerson.GIVENNAME;
    private static final String TITLEPRE = GenericPerson.TITLEPRE;
    private static final String TITLEPOST = GenericPerson.TITLEPOST;

    public GenericPersonCreate(final ItemSelectionModel itemModel, final CreationSelector parent) {
        super(itemModel, parent);
    }

    @Override
    protected void addWidgets() {
        ContentType type = getItemSelectionModel().getContentType();
        m_workflowSection = new ApplyWorkflowFormSection(type);
        add(m_workflowSection, ColumnPanel.INSERT);
        add(new Label(GlobalizationUtil.globalize("cms.ui.authoring.content_type")));
        add(new Label(type.getLabel()));
        add(new Label(GlobalizationUtil.globalize("cms.ui.language.field")));
        add(new LanguageWidget(LANGUAGE));

        // Set all mandatory field widgets which will be used to generat the title and name
        GenericPersonPropertyForm.mandatoryFieldWidgets(this);

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            add(new Label(GlobalizationUtil.globalize("cms.ui.authoring.page_launch_date")));
            ParameterModel launchDateParam = new DateParameter(LAUNCH_DATE);
            com.arsdigita.bebop.form.Date launchDate = new com.arsdigita.bebop.form.Date(launchDateParam);
            if (ContentSection.getConfig().getRequireLaunchDate()) {
                launchDate.addValidationListener(new LaunchDateValidationListener());
                // if launch date is required, help user by suggesting today's date
                launchDateParam.setDefaultValue(new Date());
            }
            add(launchDate);
        }
    }

    // Validate: ensure name uniqueness
    @Override
    public void validate(FormSectionEvent e) throws FormProcessException {
        Folder f = m_parent.getFolder(e.getPageState());
        Assert.exists(f);
        validateNameUniqueness(f, e, urlSave(getFullname(e)));
    }

    // Process: save fields to the database
    @Override
    public void process(final FormSectionEvent e) throws FormProcessException {
        final FormData data = e.getFormData();
        final PageState state = e.getPageState();
        final ContentSection section = m_parent.getContentSection(state);
        Folder folder = m_parent.getFolder(state);

        String fullName = getFullname(e);
        Assert.exists(section, ContentSection.class);

        final ContentPage item = createContentPage(state);
        item.setLanguage((String) data.get(LANGUAGE));
        item.setName(urlSave(fullName));
        item.setTitle(fullName);
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            item.setLaunchDate((Date) data.get(LAUNCH_DATE));
        }

        final ContentBundle bundle = new ContentBundle(item);
        bundle.setParent(folder);
        bundle.setContentSection(m_parent.getContentSection(state));
        bundle.save();

        m_workflowSection.applyWorkflow(state, item);

        GenericPerson person = new GenericPerson(item.getOID());
        person.setTitlePre(data.getString(TITLEPRE));
        person.setGivenName(data.getString(GIVENNAME));
        person.setSurname(data.getString(SURNAME));
        person.setTitlePost(data.getString(TITLEPOST));
        person.save();

        m_parent.editItem(state, item);
    }

    //
    private String getFullname(FormSectionEvent e) {
        final FormData data = e.getFormData();
        return data.getString(TITLEPRE) + " " + data.getString(GIVENNAME) + " " + data.getString(SURNAME) + " " + data.getString(TITLEPOST);

    }

    // Create a ulr save version of the full name
    private String urlSave(String in) {

        // Replacement map
        String[][] replacements = {{"ä", "ae"}, {"Ä", "Ae"}, {"ö", "oe"}, {"Ö", "Oe"}, {"ü", "ue"}, {"Ü", "Ue"}, {"ß", "ss"}, {".", ""}};

        // Replace all spaces with dash
        String out = in.replace(" ", "-");

        // Replace all special chars defined in replacement map
        for (int i = 0; i < replacements.length; i++) {
            out = out.replace(replacements[i][0], replacements[i][1]);
        }

        // Replace all special chars that are not yet replaced with a dash
        return out.replaceAll("[^A-Za-z0-9-]", "_");
    }
}

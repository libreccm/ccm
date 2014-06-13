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
package com.arsdigita.cms.ui.templates;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.URLTokenValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.Template;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/**
 * The template editing component. Consists of a display component
 * which displays the name/title of the template, and a form which edits
 * those properties.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: TemplateEdit.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class TemplateEdit extends SimpleEditStep {

    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties int hte runtime environment
     *  and set com.arsdigita.cms.ui.templates.TemplateEdit=DEBUG 
     *  by uncommenting or adding the line.                                   */
    private static final Logger s_log = Logger.getLogger(TemplateEdit.class);

    /**
     * Construct a new TemplateEdit component
     *
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current item
     *
     * @param parent The parent wizard which contains the form. The form
     *   may use the wizard's methods, such as stepForward and stepBack,
     *   in its process listener.
     */
    public TemplateEdit(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        super(itemModel, parent);

        TemplateEditForm form = new TemplateEditForm(itemModel);
        add("edit", "Edit", new WorkflowLockedComponentAccess(form, itemModel),
            form.getSaveCancelSection().getCancelButton());

        //DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel, false);
        sheet.add(GlobalizationUtil.globalize("cms.ui.templates.name"), 
                ContentItem.NAME);
        sheet.add(GlobalizationUtil.globalize("cms.ui.templates.label"), 
                Template.LABEL);

        setDisplayComponent(sheet);
    }

    /**
     * A form for editing Template basic properties
     */
    private class TemplateEditForm extends BasicPageForm {

        /**
         * Construct a new TemplateEditForm
         *
         * @param itemModel The {@link ItemSelectionModel} which will
         *   be responsible for loading the current item
         *
         */
        public TemplateEditForm(ItemSelectionModel itemModel) {
            super("TemplateEditForm", itemModel);
        }

        /**
         * Create the widgets for this form
         */
        @Override
        protected void addWidgets() {
            add(new Label(GlobalizationUtil.globalize("cms.ui.templates.name")));
            TextField nameWidget =
                      new TextField(new TrimmedStringParameter(NAME));
            nameWidget.addValidationListener(new NotNullValidationListener());
            nameWidget.addValidationListener(new URLTokenValidationListener());
            add(nameWidget);

            add(new Label(GlobalizationUtil.globalize("cms.ui.templates.label")));
            TextField labelWidget =
                      new TextField(new TrimmedStringParameter(Template.LABEL));
            labelWidget.addValidationListener(new NotNullValidationListener());
            add(labelWidget);
        }

        /**
         * Load the item and preset the widgets.
         * 
         * @param e
         * @throws FormProcessException 
         */
        @Override
        public void init(FormSectionEvent e) throws FormProcessException {
            FormData data = e.getFormData();
            PageState state = e.getPageState();
            Template t = getTemplate(state);
            // Preset fields
            data.put(ContentItem.NAME, t.getName());
            data.put(Template.LABEL, t.getLabel());
        }

        /**
         * Save fields to the database.
         * 
         * @param e
         * @throws FormProcessException 
         */
        @Override
        public void process(FormSectionEvent e) throws FormProcessException {
            FormData data = e.getFormData();
            PageState state = e.getPageState();
            Template t = getTemplate(state);
            t.setName((String) data.get(NAME));
            t.setLabel((String) data.get(Template.LABEL));
            t.save();
        }

        /**
         * 
         * @param event
         * @throws FormProcessException 
         */
        @Override
        public void validate(FormSectionEvent event) throws FormProcessException {
            // Calling super.validate(e) here causes an exception because the
            // super method checks things which not available here.

            PageState state = event.getPageState();
            FormData data = event.getFormData();
            Template t = getTemplate(state);

            String newName = (String) data.get(NAME);
            String oldName = t.getName();

            // Validation passes if the item name is the same.
            if (!newName.equalsIgnoreCase(oldName)) {
                validateNameUniqueness((Folder) t.getParent(), event);
            }
        }

        /**
         * Get the current template.
         * 
         * @param state
         * @return 
         */
        public Template getTemplate(PageState state) {
            Template t =
                     (Template) getItemSelectionModel().getSelectedObject(state);
            Assert.exists(t);
            return t;
        }

    }
}

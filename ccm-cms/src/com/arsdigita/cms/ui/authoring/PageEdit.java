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
package com.arsdigita.cms.ui.authoring;


import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.ItemPropertySheet;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

import java.text.DateFormat;


/**
 * The article editing component. Consists of a display component
 * which displays the form metadata, and a form which edits the
 * metadata as well as text.
 * 
 * The {@link com.arsdigita.bebop.PropertySheet} class is often used
 * as the display component in the default authoring kit steps of
 * this class.
 *
 * @author Stanislav Freidin
 * @version $Revision: #14 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: PageEdit.java 2090 2010-04-17 08:04:14Z pboy $
 */
public class PageEdit extends SimpleEditStep {

    private static Logger s_log =
        Logger.getLogger(PageEdit.class);

    //XD: The output escaping of the values of the label value pairs
    //i.e., values of the attributes name and title
    public static final boolean VALUE_OUTPUT_ESCAPE = true;

    /**
     * Construct a new PageEdit component
     *
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current item
     *
     * @param parent The parent wizard which contains the form. The form
     *   may use the wizard's methods, such as stepForward and stepBack,
     *   in its process listener.
     */
    public PageEdit(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        super(itemModel, parent);

        PageEditForm form = new PageEditForm(itemModel);
        add("edit", "Edit", new WorkflowLockedComponentAccess(form, itemModel),
            form.getSaveCancelSection().getCancelButton());

        //DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);
        ItemPropertySheet sheet =
            new ItemPropertySheet(itemModel, VALUE_OUTPUT_ESCAPE);
        sheet.add((String) GlobalizationUtil.globalize("cms.ui.authoring.name").localize(),  ContentPage.NAME);
        sheet.add((String) GlobalizationUtil.globalize("cms.ui.authoring.title").localize(),  ContentPage.TITLE);
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add("Launch Date:",
                      ContentPage.LAUNCH_DATE,
                      new ItemPropertySheet.AttributeFormatter() {
                          public String format(ContentItem item,
                                               String attribute,
                                               PageState state) {
                              ContentPage page = (ContentPage) item;
                              if(page.getLaunchDate() != null) {
                                  return DateFormat.getDateInstance(DateFormat.LONG)
                                      .format(page.getLaunchDate());
                              } else {
                                  return "<i>unknown</i>";
                              }
                          }
                      });
        }
        setDisplayComponent(sheet);
    }



    /**
     * A form for editing Article items. This is your basic page editing
     * form with a file upload/text entry widget
     */
    private class PageEditForm extends BasicPageForm {

        /**
         * Construct a new PageEditForm
         *
         * @param itemModel The {@link ItemSelectionModel} which will
         *   be responsible for loading the current item
         *
         */
        public PageEditForm(ItemSelectionModel itemModel) {
            super("PageEditForm", itemModel);
        }

        // Init: load the item and preset the widgets
        public void init(FormSectionEvent e) throws FormProcessException {
            super.initBasicWidgets(e);
        }

        // Process: save fields to the database
        public void process(FormSectionEvent e) throws FormProcessException {
            PageState state = e.getPageState();
            ContentPage item = (ContentPage)super.processBasicWidgets(e);
            if ( item != null ) {
                item.save();
            }
        }

        public void validate(FormSectionEvent event) throws FormProcessException {
            super.validate(event);
            
            PageState state = event.getPageState();
            FormData data = event.getFormData();

            ContentItem item =
                (ContentItem) getItemSelectionModel().getSelectedObject(state);
            Assert.exists(item);

            String newName = (String) data.get(BasicPageForm.NAME);
            String oldName = item.getName();

            // Validation passes if the item name is the same.
            if ( !newName.equalsIgnoreCase(oldName) ) {
                validateNameUniqueness((Folder) item.getParent(), event);
            }
        }
    }

}

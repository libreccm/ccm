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


import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.util.Assert;

import javax.servlet.ServletException;
import java.util.Date;

/**
 * A form for editing subclasses of ContentPage. This is just a convenience
 * class.
 *
 * @author Stanislav Freidin (stas@arsdigita.com)
 * @version $Revision: #22 $ $DateTime: 2004/08/17 23:15:09 $
 **/
public abstract class BasicPageForm extends BasicItemForm {

    private FormSection m_widgetSection;

//    public static final String DESCRIPTION = ContentPage.DESCRIPTION;
    public static final String LAUNCH_DATE = ContentPage.LAUNCH_DATE;

    /**
     * Construct a new BasicPageForm
     *
     * @param formName the name of this form
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current item
     */
    public BasicPageForm(String formName, ItemSelectionModel itemModel) {
        super(formName, itemModel);
    }

    /**
     * Construct a new BasicPageForm with nothing on it
     *
     * @param formName the name of this form
     * @param columnPanel the columnpanel of the form
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current item
     */
    public BasicPageForm(String formName, 
                         ColumnPanel columnPanel, 
                         ItemSelectionModel itemModel) {
        super(formName, columnPanel, itemModel);
    }


    /**
     * Add various widgets to the form. Child classes should override
     * this method to perform all their widget-adding needs
     */
    @Override
    protected void addWidgets() {
        super.addWidgets();

//        add(new Label(GlobalizationUtil.globalize("cms.ui.authoring.page_description")));
//        ParameterModel descriptionParam = new StringParameter(DESCRIPTION);
//        TextArea description = new TextArea(descriptionParam);
//        description.setCols(50);
//        description.setRows(5);
//        add(description);
//
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            add(new Label(GlobalizationUtil.
                          globalize("cms.ui.authoring.page_launch_date")));
            ParameterModel launchDateParam = new DateParameter(LAUNCH_DATE);
            com.arsdigita.bebop.form.Date launchDate
                = new com.arsdigita.bebop.form.Date(launchDateParam);
            if (ContentSection.getConfig().getRequireLaunchDate()) {
                launchDate.addValidationListener(new LaunchDateValidationListener());
                // if launch date is required, help user by suggesting today's date
                launchDateParam.setDefaultValue(new Date());
            }
            add(launchDate);
        }
    }

    /**
     * Utility method to initialize the name/title widgets. Child classes
     * may call this method from the init listener
     *
     * @param e the {@link FormSectionEvent} which was passed to the
     *    init listener
     * @return the ContentPage instance which was extracted from the
     *   ItemSelectionModel
     */
    public ContentPage initBasicWidgets(FormSectionEvent e) {
        Assert.exists(getItemSelectionModel());

        FormData data = e.getFormData();
        PageState state = e.getPageState();
        ContentPage item =
            (ContentPage)getItemSelectionModel().getSelectedObject(state);

        if (item != null) {
            // Preset fields
            data.put(NAME, item.getName());
            data.put(TITLE, item.getTitle());
//            data.put(DESCRIPTION, item.getDescription());
            if (!ContentSection.getConfig().getHideLaunchDate()) {
                data.put(LAUNCH_DATE, item.getLaunchDate());
                // if launch date is required, help user by suggesting today's date
            	if (ContentSection.getConfig().getRequireLaunchDate() 
            			&& item.getLaunchDate() == null) {
            		data.put(LAUNCH_DATE, new Date());
            	}
            }
        }

        return item;
    }

    /**
     * Utility method to process the name/title widgets. Child classes
     * may call this method from the process listener.
     *
     * @param e the {@link FormSectionEvent} which was passed to the
     *    process listener
     */
    public ContentPage processBasicWidgets(FormSectionEvent e) {
        Assert.exists(getItemSelectionModel());

        FormData data = e.getFormData();
        PageState state = e.getPageState();
        ContentPage item =
            (ContentPage)getItemSelectionModel().getSelectedObject(state);

        if (item != null) {
            // Update attributes
            item.setName((String)data.get(NAME));
            item.setTitle((String)data.get(TITLE));
//            item.setDescription((String)data.get(DESCRIPTION));
            if (!ContentSection.getConfig().getHideLaunchDate()) {
                item.setLaunchDate((Date)data.get(LAUNCH_DATE));
            }
        }

        return item;
    }

    /**
     * A utility method that will create a new item and tell the selection
     * model to select the new item. Creation components may call this method
     * in the process listener of their form. See {@link PageCreate} for
     * an example.
     *
     * @param state the current page state
     * @return the new content item (or a proper subclass thereof)
     * @pre state != null
     * @post return != null
     */
    public ContentPage createContentPage(PageState state) 
          throws FormProcessException {

        ItemSelectionModel m = getItemSelectionModel();
        Assert.exists(m);

        ContentPage item = null;

        // Create new item
        try {
            item = (ContentPage) m.createItem();
        } catch (ServletException ex) {
            throw new 
                FormProcessException((String)GlobalizationUtil.globalize("cms.ui.authoring.couldnt_create_contentpage").localize(),  ex);
        }

        // Make sure the item will be remembered across requests
        m.setSelectedObject(state, item);

        return item;
    }

    /**
     * Constructs a new <code>LaunchDateValidationListener</code>.
     */
    public class LaunchDateValidationListener implements ParameterListener {

        @Override
    	public void validate(final ParameterEvent e) {
    		
    		final ParameterData data = e.getParameterData();
    		final Object value = data.getValue();

    		if (value == null) {
    			data.addError("launch date is required");
    			return;
    		}
    	}
    }
	    
}

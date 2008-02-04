/*
 * Created on 15-Jun-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.arsdigita.london.navigation.ui.admin;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.categorization.Category;
import com.arsdigita.london.navigation.NavigationFileResolver;
import com.arsdigita.util.StringUtils;

/**
 * @author chris.gilbert at westsussex.gov.uk
 *
 * Loads category path into request in format used by breadcrumb component.
 * Enables quick links administrators to navigate back to the front end category
 */
public class PopulatePathListener implements RequestListener {


    /* (non-Javadoc)
     * @see com.arsdigita.bebop.event.RequestListener#pageRequested(com.arsdigita.bebop.event.RequestEvent)
     */
    public void pageRequested(RequestEvent e) {
        HttpServletRequest request = e.getPageState().getRequest();
        String pathString = request.getParameter("path");
        String[] pathArray = StringUtils.split(pathString, '.');
        Category[] categoryArray = new Category[pathArray.length - 1];
        for (int i = 0; i < pathArray.length - 1; i++) {
            Category cat = new Category(new BigDecimal(pathArray[i]));
            categoryArray[i] = cat;
        }
        request.setAttribute(NavigationFileResolver.class + ".categoryPath", categoryArray);
    }

}

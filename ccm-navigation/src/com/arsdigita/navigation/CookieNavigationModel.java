
/* copyright (C) 2007 Chris Gilbert. All Rights Reserved.
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
 */

package com.arsdigita.navigation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.arsdigita.categorization.CategorizedCollection;
import com.arsdigita.categorization.Category;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.util.StringUtils;
import com.arsdigita.web.Web;

/**
 * Navigation model that looks for a path held in a cookie. 
 * 
 * Cookie is maintained by NavigationFileResolver, so it is 
 * updated each time user changes navigation category
 * 
 * If there is no path, or it is invalid for the current item 
 * an alternative path is retrieved. Subclasses may 
 * override getAlternativePath to implement their own logic
 * for retrieving an alternative.
 * 
 * Validation of the cookie depends on the categorised object.
 * Subclasses should ensure that loadObject is implemented
 * and optionally getCategorisedObject
 * 
 * @author chris.gilbert@westsussex.gov.uk
 * 
 */
public class CookieNavigationModel extends GenericNavigationModel {

    private static final Logger s_log =
	Logger.getLogger(CookieNavigationModel.class);

    protected Category[] loadCategoryPath() {
	return loadCategoryPath(true);
    }

    /**
     * 
     * @param validate ignore validation rules if false. This can be useful 
     * if implementation of getAlternativePath decides to accept the cookie 
     * path after all - it can then invoke loadCategoryPath (false) 
     * 
     *  @return
     */
    protected Category[] loadCategoryPath(boolean validateCategory) {
	Category[] catArray = null;
	HttpServletRequest request = Web.getRequest();
	Cookie[] cookies = request.getCookies();
	List catList = null;
	if (cookies != null) {

     	    for (int i = 0; i < cookies.length; i++) {
		s_log.debug("cookie found - "   + cookies[i].getName()
						+ " with value "
						+ cookies[i].getValue());
		if (cookies[i].getName().equals(NavigationFileResolver.PATH_COOKIE_NAME)) {
		    catList = new ArrayList();
		    String[] path = StringUtils.split(cookies[i].getValue(),
							NavigationFileResolver.PATH_COOKIE_SEPARATOR);

		    //	validity test 1 - is cookie from this site
		    if (!path[0].equals(Web.getConfig().getSiteName())) {
			// cookie has been set by a different Aplaws site. 
			// treat this as if there is no cookie.
			s_log.debug("cookie was set by " + path[0]);
			return getAlternativePath(false);
		    }

		    if (validateCategory) {
			// validity test 2 - is current object assigned to current category

			ACSObject catObj = getCategorisedObject();
			if (catObj != null) {
			    Category cat = new Category(new BigDecimal(path[path.length - 1]));
			    CategorizedCollection assignedObjects =
			    	cat.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE);
			    assignedObjects.addEqualsFilter(ACSObject.ID, catObj.getID());
			    if (assignedObjects.size() == 0) {
				s_log.debug("object is not categorised under final category in path");
				return getAlternativePath(true);
			    }

			}
			// no object available, so we can't check if the path is valid by this method
			// just assume it is valid
		    }

		    for (int j = 2; j < path.length; j++) {
			Category cat = new Category(new BigDecimal(path[j]));
			catList.add(cat);
			s_log.debug("adding to path in request attribute " + cat.getName());
		    }
		}
	    }
	}
	if (catList == null) {
	    s_log.debug("no cookie found");
	    return getAlternativePath(false);
	}
	s_log.debug("categories in list: " + catList.size());
	return (Category[]) catList.toArray(new Category[(int) catList.size()]);
    }

    /**
     * 
     * Invoked if the path specified in the cookie is not valid.
     * Default is to return the navigation root. Subclasses may override
     * to provide a more useful alternative.
     * 
     */
    protected Category[] getAlternativePath(boolean cookieExists) {
	s_log.debug("no category, delegating to parent impl");
	return super.loadCategoryPath();
    }

    /**
     * Current object is used to validate the path obtained from the cookie.
     * 
     * Default implementation returns the object specified in loadObject method.
     * For CookieNavigationModel this is null. Subclasses should provide a valid
     * implementation of loadObject and optionally override getCategorisedObject
     * (if the categorised object is not the same as the current object)
     * 
     * 
     */
    protected ACSObject getCategorisedObject() {
	// default is to retrieve the current object, but this is not always the case
	// eg in CMS the content bundle is categorised, but the specific item is the loaded object
	return getObject();
    }

}

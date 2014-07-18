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
package com.arsdigita.simplesurvey;


import com.arsdigita.bebop.PageState;

// import com.arsdigita.kernel.PackageInstance;
// import com.arsdigita.kernel.SiteNode;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.User;

import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.web.Application;
// import com.arsdigita.web.Web;


/**
 * A collection of static utility methods used by the Simple Survey
 * application. Most of these methods are not Simple Survey specific
 * and could deserve being put in the ACS core.
 *
 * @author <a href="mailto:pmarklun@arsdigita.com">Peter Marklund</a>
 * @version $Id: SimpleSurveyUtil.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class SimpleSurveyUtil {


    /**
     * 
     * @param request
     * @return 
     */
//  public static SiteNode getSiteNode(HttpServletRequest request) {

//      SiteNode siteNode;
//      try {
//       siteNode = SiteNode.getSiteNode(request.getRequestURI().toString(), true);
//	    } catch (com.arsdigita.domain.DataObjectNotFoundException e) {
//	        throw new com.arsdigita.util.UncheckedWrapperException(e);
//	    }

//	    return siteNode;
//    }

    /**
     * 
     * @param pageState
     * @return 
     */
//  public static SiteNode getSiteNode(PageState pageState) {
//      return getSiteNode(pageState.getRequest());
//  }

    /**
     * 
     * @param pageState
     * @return 
     */
//  public static PackageInstance getPackageInstance(PageState pageState) {
//      return getSiteNode(pageState).getPackageInstance();
//  }

    /**
     * 
     * @param pageState
     * @return 
     */
    public static boolean isUserAdmin(PageState pageState) {

        boolean admin_p = false;

        User user = (User)Kernel.getContext().getParty();

     // DEPRECATED! SiteNode is old style application an no longer used.
     // Can not provide any useful information as no applicagtion is created
     // as a SiteNode anymore.
     // SiteNode siteNode = SimpleSurveyUtil.getSiteNode(pageState);

     // PermissionDescriptor admin = new PermissionDescriptor
     //                                          (PrivilegeDescriptor.ADMIN, 
     //                                           siteNode, 
     //                                           user);

        // TODO: Replacement code not tested yet (both alternatives)!
        Application app = (Application)Kernel.getContext().getResource();
        PermissionDescriptor admin = new PermissionDescriptor
                                                 (PrivilegeDescriptor.ADMIN, 
                                                  app, 
                                                  user);

        if (PermissionService.checkPermission(admin)) {
            admin_p = true;
        }

        return admin_p;
    }

}

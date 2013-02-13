/*
 * Copyright (C) 2005 Chris Gilbert  All Rights Reserved.
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
package com.arsdigita.portlet.bookmarks;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.portal.PortletType;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;


/**
 * Just create the portlet type - includes Load parameter (that can be 
 * set via interactive load) for the type name, though this can be 
 * changed in the DB later if required in application_types table
 * 
 * @author cgyg9330
 * @version $Id: Loader.java,v 1.1 2005/02/25 08:41:56 cgyg9330 Exp $
 */
public class Loader extends PackageLoader {

    private StringParameter typeName = new StringParameter
			("uk.gov.westsussex.portlet.bookmarks.name", 
			 Parameter.REQUIRED, "My Links");

	public Loader() {
			register(typeName);
			
		}
    public void run(final ScriptContext ctx) {
        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                PortletType type = PortletType
		   .createPortletType((String)get(typeName), 
							  PortletType.WIDE_PROFILE,
							  BookmarksPortlet.BASE_DATA_OBJECT_TYPE);
	   type.setDescription("Allows users to maintain a list of internal and external links");
            }
        }.run();
    }


}

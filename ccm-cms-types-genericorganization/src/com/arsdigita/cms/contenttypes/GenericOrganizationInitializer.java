/*
 * Copyright (C) 2009 Jens Pelzetter, for the Center of Social Politics of the University of Bremen
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

package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.contenttypes.ContentTypeInitializer;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.domain.SimpleDomainObjectTraversalAdapter;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */

public class GenericOrganizationInitializer extends ContentTypeInitializer {
    
    public final static String versionId =
	"$Id: GenericOrganizationInitializer.java 1 2009-04-30 09:32:55Z jensp $" +
	"$Author: jensp $" +
	"$DateTime: 2009/04/30 11:33:39 $";

    private static final Logger s_log = Logger.getLogger(GenericOrganizationInitializer.class);

    public GenericOrganizationInitializer() {
	super("ccm-cms-types-genericorganization.pdl.mf",
	      GenericOrganization.BASE_DATA_OBJECT_TYPE);
    }

    public void init(DomainInitEvent evt) {
	super.init(evt);
    }

    public String[] getStylesheets() {
	return new String[] { "/static/content-types/com/arsdigita/cms/contenttypes/GenericOrganization.xsl" };
    }

    public String getTraversalXML() {
    return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/GenericOrganization.xml";
  }
       
}
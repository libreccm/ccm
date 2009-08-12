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

import org.apache.log4j.Logger;
import com.arsdigita.runtime.LegacyInitEvent;

/**
 * Initializer of the GenericOrganization content type.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationInitializer extends ContentTypeInitializer {

    /*public final static String versionId =
            "$Id: GenericOrganizationInitializer.java 1 2009-04-30 09:32:55Z jensp $" +
            "$Author: jensp $" +
            "$DateTime: 2009/04/30 11:33:39 $";*/
    private static final Logger s_log = Logger.getLogger(GenericOrganizationInitializer.class);

    /**
     * Constructor. calls only the constructor of the parent class with name of
     * the pdl.mf file of the content type an the BASIC_DATA_OBJECT_TYPE.
     */
    public GenericOrganizationInitializer() {
        super("ccm-cms-types-genericorganization.pdl.mf",
                GenericOrganization.BASE_DATA_OBJECT_TYPE);
    }

    /**
     *
     * @return path of the traversal-adapter XML file.
     */
    /*@Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/GenericOrganization.xml";
    }*/

    /**
     *
     * @return path of the XSL stylesheet file. The stylesheet is very generic, because this
     * contenttype will be used with the new mandalay theme only.
     */
    @Override
    public String[] getStylesheets() {
        return new String[] { "/static/content-types/com/arsdigita/cms/contenttypes/GenericOrganization.xsl" };
    }

    /**
     * Calls the init method of the parent class.
     *
     * @param evt The init event. LegacyInitEvent is marked deprecated. What should be used insted?
     */
    @Override
    public void init(LegacyInitEvent evt) {
        super.init(evt);

    /*	MetadataProviderRegistry.registerAdapter(OrganizationRole.BASE_DATA_OBJECT_TYPE,
    new OrganizationRoleMetadataProvider());*/
    /*URLService.registerFinder(OrganizationRole.BASE_DATA_OBJECT_TYPE,
    new OrganizationRoleURLFinder());

    ContentSection.registerExtraXMLGenerator(GenericOrganization.class.getName(),
    new OrganizationRolesPanel());*/
    }
}
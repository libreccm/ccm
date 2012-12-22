/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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

import com.arsdigita.cms.contenttypes.ui.PublicationGlobalizationUtil;
import com.arsdigita.cms.contenttypes.ui.PublisherPublicationsStep;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.runtime.DomainInitEvent;

/**
 * Executes at each system startup and initializes the Publisher content type,
 * part of the ScientificCMS extension.
 * 
 * Defines the content type specific properties and just uses the super class
 * methods to register the content type with the (transient) content type store
 * (map). This is done by runtimeRuntime startup method which runs the init()
 * methods of all initializers (this one just using the parent implementation).
 *
 * @author Jens Pelzetter
 */
public class PublisherInitializer extends ContentTypeInitializer {

    /**
     * Constructor, sets the PDL manifest file and object type string.
     * 
     * The pdl.mf file used here is empty, since the
     * {@link PublicationInitializer} loads all things using the pdl.mf file
     * of the module. Also, it may causes on silly errors in the load-bundle
     * step if the same pdl.mf file is used in more than one initializer.
     */
    public PublisherInitializer() {
        super("empty.pdl.mf", Publisher.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public void init(final DomainInitEvent event) {
        super.init(event);

        AuthoringKitWizard.registerAssetStep(
                Publisher.BASE_DATA_OBJECT_TYPE,
                PublisherPublicationsStep.class,
                PublicationGlobalizationUtil.globalize("publisher.ui.publications.title"),
                PublicationGlobalizationUtil.globalize("publisher.ui.publications.description"),
                10);
    }

    /**
     * Retrieve location of this content type's internal default theme 
     * stylesheet(s) which concomitantly serve as a fallback if a custom theme 
     * is engaged. 
     * 
     * Custom themes usually will provide their own stylesheet(s) and their own
     * access method, but may not support every content type.
     * 
     * Overwrites parent method with AgendaItem specific value for use by the 
     * parent class worker methods.
     * 
     * @return String array of XSL stylesheet files of the internal default theme
     */
    @Override
    public String[] getStylesheets() {
        return new String[]{
                    INTERNAL_THEME_TYPES_DIR + "sci/Publisher.xsl"};
    }

    /**
     * Retrieves fully qualified traversal adapter file name.
     * @return 
     */
    @Override
    public String getTraversalXML() {
        //return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/Publisher.xml";
        return "";
    }

}

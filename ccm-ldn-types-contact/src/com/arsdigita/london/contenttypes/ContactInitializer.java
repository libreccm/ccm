/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.london.contenttypes;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.contenttypes.ContentItemTraversalAdapter;
import com.arsdigita.cms.contenttypes.ContentTypeInitializer;
import com.arsdigita.london.contenttypes.ui.ContactPropertiesAddStep;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.runtime.DomainInitEvent;

import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * Executes at each system startup and initializes the Contact content type.
 * 
 * Defines the content type specific properties and just uses the super class
 * methods to register the content type with the (transient) content type store
 * (map). This is done by runtimeRuntime startup method which runs the init()
 * methods of all initializers (this one just using the parent implementation).
 * 
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 * @version $Id: ContactInitializer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ContactInitializer extends ContentTypeInitializer {

    /** Private Logger instance for debugging purpose.                        */
    private static final Logger logger = Logger.getLogger(
            ContactInitializer.class);

    /**
     * Constructor, sets the PDL manifest file and object type string.
     */
    public ContactInitializer() {
        super("ccm-ldn-types-contact.pdl.mf", Contact.BASE_DATA_OBJECT_TYPE);
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
            INTERNAL_THEME_TYPES_DIR + "ldn/Contact.xsl",};
    }

    /**
     * Retrieves fully qualified traversal adapter file name.
     * @return 
     */
    @Override
    public String getTraversalXML() {
        return 
        "/WEB-INF/traversal-adapters/com/arsdigita/london/contenttypes/Contact.xml";
    }
    
    private static ArrayList phoneTypesList = new ArrayList(10);
    static {
        logger.debug("Static initalizer starting...");
        phoneTypesList.add("Office");
        phoneTypesList.add("Mobile");
        phoneTypesList.add("Fax");
        phoneTypesList.add("Home");
        phoneTypesList.add("Minicom");
        logger.debug("Static initalizer finished.");
    }

    /**
     * Return the statically initialized list of phone types.
     */
    public static ArrayList getPhoneTypes() {
        return phoneTypesList;
    }

    // public void init(LegacyInitEvent evt) {
    @Override
    public void init(DomainInitEvent evt) {
        super.init(evt);

        if (ContentSection.getConfig().getHasContactsAuthoringStep()) {

            // Add the "Contact"authoring step
            AuthoringKitWizard.registerAssetStep(
                    getBaseType(),
                    getAuthoringStep(), getAuthoringStepLabel(),
                    getAuthoringStepDescription(), getAuthoringStepSortKey());

            // and sort out the display	- at the moment this is just the
            // basic properties, addresses and phones
            ContentItemTraversalAdapter associatedContactTraversalAdapter =
                                        new ContentItemTraversalAdapter();
            associatedContactTraversalAdapter.addAssociationProperty(
                    "/object/phones");
            associatedContactTraversalAdapter.addAssociationProperty(
                    "/object/contactAddress");

            ContentItemTraversalAdapter.registerAssetAdapter(
                    "associatedContactForItem",
                    associatedContactTraversalAdapter,
                    "com.arsdigita.cms.dispatcher.SimpleXMLGenerator");
        }

    }

    private int getAuthoringStepSortKey() {
        // TODO - workout what this does and possibly make it configurable
        return 1;
    }

    private GlobalizedMessage getAuthoringStepDescription() {
        return new GlobalizedMessage(
            "com.arsdigita.london.contenttypes.contact_authoring_step_description",
            "com.arsdigita.london.contenttypes.ContactResources");
    }

    private GlobalizedMessage getAuthoringStepLabel() {
        return new GlobalizedMessage(
            "com.arsdigita.london.contenttypes.contact_authoring_step_label",
            "com.arsdigita.london.contenttypes.ContactResources");
    }

    private Class getAuthoringStep() {
        return ContactPropertiesAddStep.class;
    }

    private String getBaseType() {
        return ContentPage.BASE_DATA_OBJECT_TYPE;
    }
}

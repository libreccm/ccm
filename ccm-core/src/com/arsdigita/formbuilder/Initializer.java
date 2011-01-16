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
package com.arsdigita.formbuilder;

import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.kernel.PackageType;
import com.arsdigita.kernel.PackageInstance;
import com.arsdigita.kernel.SiteNode;
// import com.arsdigita.kernel.Stylesheet;

import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;

import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;



import org.apache.log4j.Logger;

/**
 * Registers the formbuilder package type and creates an instance
 * that is mounted under formbuilder (this is done only once).
 *
 * @author Peter Marklund
 * @version $Id: Initializer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Initializer

    implements com.arsdigita.initializer.Initializer {

    private Configuration m_conf = new Configuration();

    private static final Logger log =
        Logger.getLogger(Initializer.class);

    /**
     * Constructor
     * 
     * @throws InitializationException
     */
    public Initializer() throws InitializationException {

    }

    /**
     * Returns the configuration object used by this initializer.
     */
    public Configuration getConfiguration() {
        return m_conf;
    }


    /**
     * Called on startup. Note. As you can not find a call
     * to this method in enterprise.ini, this method
     * may appear to execute mysteriously.
     * However, the process that runs through enterprise.ini
     * automitically calls the startup() method of any
     * class that implements com.arsdigita.util.initializer.Initializer
     * present in enterprise.ini
     */
    public void startup() {

        log.info("FormBuilder Initializer starting.");

        TransactionContext txn = SessionManager.getSession()
            .getTransactionContext();
        txn.beginTxn();
        
        checkFormBuilderSetup();

        txn.commitTxn();
    }

    private void checkFormBuilderSetup() {
        /* This checks to see if a package by this name
         * is present.  If it isn't, setupFormBuilder
         * will do the necessary setup such as add the
         * package type, package instance, site node
         * and style sheet.
         */
        try {
            log.debug("FormBuilder Initializer - verifying setup.");
            PackageType.findByKey("formbuilder");
        } catch (DataObjectNotFoundException e) {
            setupFormBuilder();
        }
    }


    private void setupFormBuilder() {
        log.debug("FormBuilder Initializer - setting up new package");

        /** Adding the package type to the installation
         */

        PackageType FormBuilderType = PackageType.create
            ("formbuilder", "Form Builder", "Form Builders",
             "http://arsdigita.com/formbuilder");
        log.debug("Just added package type FormBuilder");


        /** Adding the node and the package instance
         *  on that node.
         */

        SiteNode FormBuilderTypeNode = SiteNode.createSiteNode("formbuilder");
        PackageInstance formBuilder = FormBuilderType.createInstance("FormBuilderType");

        /** Specifying the URL stub for this package instance.
         */
        FormBuilderTypeNode.mountPackage(formBuilder);
        FormBuilderTypeNode.save();


        /** Adding a style sheet
         */
//      Stylesheet FormBuilderSheet = Stylesheet.createStylesheet ("/packages/formbuilder/xsl/formbuilder.xsl");
//      FormBuilderType.addStylesheet(FormBuilderSheet);


        /** Mapping the package type to a dispatcher
         *  class
         */
        FormBuilderType.setDispatcherClass("com.arsdigita.formbuilder.FormBuilderDispatcher");

        /** Saving changes
         */
        FormBuilderType.save();

    }

    /**
     * Called on shutdown. It's probably not a good idea to depend on this
     * being called.
     **/
    public void shutdown() {
    }

}

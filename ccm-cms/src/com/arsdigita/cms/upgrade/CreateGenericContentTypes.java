/*
 * Copyright (C) 2010 Sören Bernstein. All Rights Reserved.
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
package com.arsdigita.cms.upgrade;

import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.cms.ContentTypeLifecycleDefinition;
import com.arsdigita.cms.ContentTypeWorkflowTemplate;
import com.arsdigita.cms.contenttypes.XMLContentTypeHandler;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import com.arsdigita.util.cmd.Program;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.workflow.simple.WorkflowTemplate;
import com.arsdigita.xml.XML;
import java.math.BigDecimal;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author quasi
 * @version $Id: $
 */
public class CreateGenericContentTypes extends Program {

    private static Logger s_log = Logger.getLogger(CreateGenericContentTypes.class);

    /**
    /* Constructor
     */
    public CreateGenericContentTypes() {
        super("CreateGenericContentTypes", "1.0.0", "");
    }

    /**
     *
     * @param args
     */
    public static final void main(final String[] args) {
        new CreateGenericContentTypes().run(args);
    }

    /**
     * 
     * @param cmdLine
     */
    public void doRun(CommandLine cmdLine) {

        new KernelExcursion() {

            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                final Session session = SessionManager.getSession();

                final TransactionContext tc = session.getTransactionContext();

                // add new generic content types
                tc.beginTxn();

                String[] ctDefFiles = new String[]{"/WEB-INF/content-types/GenericAddress.xml",
                    "/WEB-INF/content-types/GenericArticle.xml",
                    "/WEB-INF/content-types/GenericContact.xml",
                    "/WEB-INF/content-types/GenericOrganizationalUnit.xml",
                    "/WEB-INF/content-types/GenericPerson.xml"};

                if (ctDefFiles != null) {
                    for (int i = 0; i < ctDefFiles.length; i++) {
                        String xmlFile = ctDefFiles[i];
                        s_log.debug("Processing contentTypes in: " + xmlFile);
                        XML.parseResource(xmlFile, new XMLContentTypeHandler());
                    }
                }

                tc.commitTxn();  // save database additions for re-reading

                tc.beginTxn();

                ContentTypeCollection ctc = ContentType.getAllContentTypes();

                s_log.debug("Starte content types update");

                while (ctc.next()) {

                    ContentType ct = ctc.getContentType();

                    s_log.debug("Verarbeite " + ct.getClassName());

                    createPedigree(ct);

                }
                tc.commitTxn();
            }
        }.run();


    }

    /**
     * Generates the pedigree for this content type
     * @param type The new content type
     */
    private void createPedigree(ContentType type) {

        // The parent content type
        ContentType parent = null;

        // Get all content types
        ContentTypeCollection cts = ContentType.getAllContentTypes();

        // This is a brute force method, but I can't come up with something
        // better atm without changing either all Loader or the xml-files.
        while (cts.next()) {
            ContentType ct = cts.getContentType();

            try {
                Class.forName(type.getClassName()).asSubclass(Class.forName(ct.getClassName()));
            } catch (Exception ex) {
                // This cast is not valid so type is not a sublacss of ct
                continue;
            }

            // Save the current ct as possible parent if we haven't found any parent yet
            // or if the current ancestor list is longer than that one from the possible
            // parent earlier found
            if (!type.getClassName().equals(ct.getClassName())
                    && (parent == null
                    || (parent.getAncestors() != null
                    && ct.getAncestors() != null
                    && parent.getAncestors().length() < ct.getAncestors().length()))) {
                parent = ct;
            }
        }

        // If there is a valid parent content type create the pedigree
        if (parent != null && !parent.getClassName().equals(type.getClassName())) {
            if (parent.getAncestors() != null) {
                String parentAncestors = parent.getAncestors();

                StringTokenizer strTok = new StringTokenizer(parentAncestors, "/");

                // Add parent ancestors to this content types ancestor list
                // Also while we iterate through the list, we also need to add
                // this content type as descendant to all entries in the ancestor list
                while (strTok.hasMoreElements()) {
                    BigDecimal ctID = new BigDecimal(strTok.nextToken());

                    // Get the current content type
                    try {
                        ContentType ct = new ContentType(ctID);
                        ct.addDescendants(ctID);
                    } catch (Exception ex) {
                        // The db is broken. There is no content type for this ID
                    }

                    // Add parent ancestor
                    type.addAncestor(ctID);
                }
            }

            // Add parent to ancestor list
            type.addAncestor(parent.getID());

            // Add this to parent descendants
            parent.addDescendants(type.getID());
        }
    }
}

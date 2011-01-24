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
import com.arsdigita.cms.installer.xml.XMLContentTypeHandler;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import com.arsdigita.packaging.Program;
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

            }
        }.run();


    }

}

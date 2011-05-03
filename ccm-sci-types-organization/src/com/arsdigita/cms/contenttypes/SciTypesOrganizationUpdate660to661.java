package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.installer.xml.XMLContentTypeHandler;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.xml.XML;

/**
 * Updates the authoring steps of SciOrganization, SciDepartment and
 * SciProject to reflect the changes made in version 6.6.1 (PWI SVN 
 * revision 887).
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciTypesOrganizationUpdate660to661 {

    private static final String SCI_ORGANIZATION =
                                "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/SciOrganization.xml";
    private static final String SCI_DEPARTMENT =
                                "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/SciDepartment.xml";
    private static final String SCI_PROJECT =
                                "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/SciProject.xml";

    public static void main(String[] args) {
        final com.arsdigita.runtime.Runtime runtime =
                                            new com.arsdigita.runtime.Runtime();
        runtime.startup();

        final Session session = SessionManager.getSession();
        final TransactionContext tc = session.getTransactionContext();

        try {
            tc.beginTxn();
            new KernelExcursion() {

                @Override
                protected void excurse() {
                    setEffectiveParty(Kernel.getSystemParty());

                    /*
                     * Reload content type definitions from XML config to add
                     * new authoring steps.
                     */
                    XMLContentTypeHandler handler = new XMLContentTypeHandler();
                    XML.parseResource(SCI_ORGANIZATION, handler);
                    XML.parseResource(SCI_DEPARTMENT, handler);
                    XML.parseResource(SCI_PROJECT, handler);
                }
            }.run();
            session.flushAll();
            tc.commitTxn();
        } catch (Exception ex) {
            System.out.println("Exception ocurred during update: ");
            ex.printStackTrace(System.out);
        } finally {
            if (tc.inTxn()) {
                tc.abortTxn();
            }
        }
    }
}

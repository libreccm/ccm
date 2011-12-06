package com.arsdigita.cms.contenttypes;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.runtime.Startup;
import com.arsdigita.xml.XML;

public class SiteProxyUpgrade630to631 {

    private final static String SITE_PROXY = "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/SiteProxy.xml";

    public static void main(String[] args) {
        new Startup().run();
        final Session session = SessionManager.getSession();
        final TransactionContext tc = session.getTransactionContext();
        try {
            tc.beginTxn();
            new KernelExcursion() {
                public void excurse() {
                    setEffectiveParty(Kernel.getSystemParty());
                    /*
                     * reload SiteProxy content type definition from XML config
                     * to add new authoring kit step
                     */
                    XMLContentTypeHandler handler = new XMLContentTypeHandler();
                    XML.parseResource(SITE_PROXY, handler);
                }
            }.run();
            session.flushAll();
            tc.commitTxn();
        } finally {
            if (tc.inTxn()) {
                tc.abortTxn();
            }
        }
    }
}

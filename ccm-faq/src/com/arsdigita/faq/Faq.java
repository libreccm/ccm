/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.faq;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.TypedText;
import com.arsdigita.web.Application;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * Application domain class for the Faq application.
 *
 * It manages creation of new Faq instances and provides getters and setters
 * for instance specific items.
 *
 * @version $Id: com/arsdigita/faq/Faq.java#5 $
 */

public class Faq extends Application {

    /** Private logger instance for debugging purpose                        */
    private static final Logger log = Logger.getLogger(Faq.class);

    // PDL stuff
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.faq.Faq";

    /**
     * 
     * @param oid
     * @throws DataObjectNotFoundException 
     */
    public Faq(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * 
     * @param key
     * @throws DataObjectNotFoundException 
     */
    public Faq(BigDecimal key)  throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, key));
    }

    /**
     * 
     * @param dataObject 
     */
    public Faq(DataObject dataObject) {
        super(dataObject);
    }

    /**
     * 
     * @return 
     */
    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }


    /**
     * Use this instead of the constructor to create new Faq objects
     */
    public static Faq create(String urlName, 
                             String title,
                             Application parent) {
        return (Faq) Application.createApplication(BASE_DATA_OBJECT_TYPE, 
                                                   urlName, 
                                                   title, 
                                                   parent);
    }

    /**
     * 
     * @param question
     * @param answer
     * @return
     */
    public QAPair createQuestion(String question, String answer) {
        return createQuestion(question,
                              new TypedText(answer, TypedText.TEXT_HTML));
    }

    public QAPair createQuestion(String question, TypedText answer) {

        Integer sortKey = getNextSortKey();
        QAPair qaPair = new QAPair();
        qaPair.setFaq(this);
        qaPair.setQuestion(question);
        qaPair.setAnswer(answer);
        qaPair.setSortKey(sortKey);
        qaPair.save();
        PermissionService.setContext(qaPair, this);

        return qaPair;
    }


    void removeQAPair(QAPair qaPair) {
        remove("questions", qaPair);
    }

    public DataAssociation getQAPairs() {
        return (DataAssociation) get("questions");
    }

    /*
     * XXX synchronization is not really enough.  need to lock the
     * database or risk an error.
     * (Actually, there's no unique constraint on sort key, so
     *  you won't get an error)
     */
    synchronized Integer getNextSortKey() {
        DataQuery nextVal = SessionManager.getSession().
            retrieveQuery("com.arsdigita.faq.nextSortKey");
        nextVal.setParameter("faqID", getID());

        Integer returnVal = null;
        if (nextVal.next()) {
            returnVal = (Integer) nextVal.get("nextSortKey");
        } else {
            // this should never happen
            throw new RuntimeException("No rows returned from a query "
                                       + "guaranteed to return a row");
        }

        nextVal.close();
        return returnVal;
    }

    /**
     * Swaps the order of two questions
     */
    void swapOrder(QAPair qaPairA, QAPair qaPairB) {

        if (qaPairA.getFaq().equals(this) && qaPairB.getFaq().equals(this)) {

            log.debug("Security passed, swapping: ");

            Integer temp = qaPairA.getSortKey();
            qaPairA.setSortKey(qaPairB.getSortKey());
            qaPairB.setSortKey(temp);

            qaPairA.save();
            qaPairB.save();

            log.debug("Swapped: ");
        }

    }


    /**
     * Returns the path name of the location of the applications servlet/JSP.
     *
     * The method overwrites the super class to provide an application specific
     * location for servlets/JSP. This is necessary if you whish to install the
     * package (application) along with others in one context. If you install
     * it into its own context (no longer recommended) you may use a standard
     * location.
     *
     * Usually it is a symbolic name/path, which will be mapped in the web.xml
     * to the real location in the file system. Example:
     * <servlet>
     *   <servlet-name>faq</servlet-name>
     *   <servlet-class>com.arsdigita.faq.Faq</servlet-class>
     * </servlet>
     *
     * <servlet-mapping>
     *   <servlet-name>faq</servlet-name>
     *   <url-pattern>/faq/*</url-pattern>
     * </servlet-mapping>
     *
     * The appended "/*" ensures BaseServlet will find additional JSP's (located
     * in URL.TEMPLATE_DIR/ccm-faq/*).
     * 
     * Ensure the url-pattern does NOT have a real counterpart in the file 
     * system! For virtual locations you may use URL.SERVLET_DIR as base
     * directory.
     * 
     * @return path name to the applications servlet/JSP
     */
    @Override
    public String getServletPath() {
        return "/faq";
    }

}

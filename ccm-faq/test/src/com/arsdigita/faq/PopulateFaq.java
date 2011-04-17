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

import java.util.List;

import com.arsdigita.faq.ui.FaqQuestionsPortlet;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.populate.Utilities;
import com.arsdigita.populate.apps.AbstractPopulateApp;
import com.arsdigita.populate.apps.PopulateApp;
import com.arsdigita.util.Assert;
import com.arsdigita.web.ApplicationType;

/**
 * @author bche
 */
public class PopulateFaq extends AbstractPopulateApp implements PopulateApp {
    private static final String ARGS_DESC = "1 PopulateFaq arg: numQuestions";

    /* (non-Javadoc)
     * @see com.arsdigita.populate.apps.PopulateApp#populateApp(java.util.List)
     */
    public void populateApp(List args) {        
        Session ses = SessionManager.getSession();
        TransactionContext txn = ses.getTransactionContext();

        Faq faq = (Faq) getApp();

        //validate the arguments
        this.validateArgs(args, 1, ARGS_DESC);

        int iQuestions = ((Integer)args.get(0)).intValue();
        Assert.isTrue(iQuestions >= 0, "iQuestions must be >= 0");
        
        //populate the FAQ
        for (int i=0; i < iQuestions; i++) {
            txn.beginTxn();
            String sQuestion = "This is question " + i;
            String sAnswer = Utilities.makeText(i);
            
            QAPair pair = faq.createQuestion(sQuestion, sAnswer);
            s_log.info("created FAQ question " + i);
            
            txn.commitTxn();
        }
        
    }

    /* (non-Javadoc)
     * @see com.arsdigita.populate.apps.PopulateApp#getArgsDescription()
     */
    public String getArgsDescription() {
        return ARGS_DESC;
    }

    /* (non-Javadoc)
     * @see com.arsdigita.populate.apps.PopulateApp#getAppType()
     */
    public ApplicationType getAppType() {        
        return ApplicationType.retrieveApplicationTypeForApplication(Faq.BASE_DATA_OBJECT_TYPE);        
    }

    /* (non-Javadoc)
     * @see com.arsdigita.populate.apps.AbstractPopulateApp#getPortletType()
     */
    protected String getPortletType() {
        return FaqQuestionsPortlet.BASE_DATA_OBJECT_TYPE;
    }

}

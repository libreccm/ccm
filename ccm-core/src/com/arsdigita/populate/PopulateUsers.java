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
package com.arsdigita.populate;


import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserAuthentication;
import com.arsdigita.kernel.UserCollection;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;

/**
 * @author bche
 */
public class PopulateUsers {
	private static final org.apache.log4j.Logger s_log =
		org.apache.log4j.Logger.getLogger(PopulateUsers.class.getName());
        
        private final static String s_sPassword = "test";
        private final static String s_sQuestion = "test";
        private final static String s_sAnswer = "test";
        
        private String m_sBaseStringSeed = null;
        private String m_sScreenNameStub = "";

        public void setBaseStringSeed(String sSeed) {
            m_sBaseStringSeed = sSeed;
        }
    
        public String getBaseStringSeed() {
            return m_sBaseStringSeed;
        }        
	
        /**
         * populates iUsers in the system
         * @param iUsers the number of users to populate
         */        
	public void populate(int iUsers) {
		if (iUsers < 0) {
			throw new IllegalArgumentException("Number of Users must be >= 0");
		}

		Session ses = SessionManager.getSession();
		TransactionContext txn = ses.getTransactionContext();
		
		String sUserBase = Utilities.getBaseString(m_sBaseStringSeed);

		//create users
		for (int i = 0; i < iUsers; i++) {
			txn.beginTxn();

			//create the user
                        m_sScreenNameStub ="testuser" + sUserBase; 
			String sScreenName =  m_sScreenNameStub + i;
			String sEmail = sScreenName + "@redhat.rhat";
			User newbie = new User();
			newbie.getPersonName().setGivenName("Test");
			newbie.getPersonName().setFamilyName("User" + sUserBase + i);
			newbie.setPrimaryEmail(new EmailAddress(sEmail));
			newbie.setScreenName(sScreenName);
			newbie.setURI("http://rhea.redhat.com");

			// note: newbie has to have primaryEmail set first,
			// before you can call UserAuthentication
			newbie.save();
			UserAuthentication auth = null;
			auth = UserAuthentication.createForUser(newbie);
			auth.setPassword(s_sPassword);
			auth.setPasswordQuestion(s_sQuestion);
			auth.setPasswordAnswer(s_sPassword);
			auth.save();
                        
			s_log.info(" Added User " + sEmail);

			txn.commitTxn();
		}
	}

        /**
         * Gets a collection of the users populated
         * @return a collection of the users populated
         */
	public UserCollection getPopulatedUsers() {
            if (m_sScreenNameStub.length() == 0) {
                return null;
            }
            
            UserCollection users = User.retrieveAll();
            users.addFilter("screenName like \'" + m_sScreenNameStub + "%\'");
            return users;
	}
}

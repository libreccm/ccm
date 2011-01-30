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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.arsdigita.kernel.KernelHelper;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserCollection;
import com.arsdigita.util.Assert;
import com.arsdigita.util.ResourceManager;


// ///////////////////////////////////////////////////////////////////////////
//
// NOT USED anywhere in the source tree.
// No documentation available.
// Retained temporarily until refactoring of test cases is completed.
//
// (pboy 2011.01.30)
//
// ///////////////////////////////////////////////////////////////////////////


/**
 * @author bche
 */
public class Utilities {
         /**
         * Returns a string guranteed to be unique for every second that this
         * method is run.  Appropriate for using in data fields that require
         * unique values
         * @return a string guranteed to be unique for every second that this
         * method is run.
         */
        public static String getUniqueBaseString() {
            return "_" + (new Date().getTime()/1000) + "_";
        }
        
        /**
         * Returns a string appropriate for using in data fields, based upon
         * the String sSeed.  If sSeed is null or empty, returns a random string.
         * Otherwise, always returns the same string based upon sSeed.
         * @param sSeed the seed String value for constructing the base String 
         * @return a base String based upon sSeed if sSeed is not null or empty.  
         * Otherwise, a random base String.
         */
        public static String getBaseString(String sSeed) {
            if (sSeed == null || sSeed.length() == 0) {
                return getUniqueBaseString();
            } else {
                return "_" + sSeed + "_";
            }            	
        }
        
        /**
         * Returns a binary (image/gif) file
         * @return a binary (image/gif) File
         */
        public static File getBinaryFile() {
            ResourceManager rm = ResourceManager.getInstance();
                   String sWebAppRoot = rm.getWebappRoot().getAbsolutePath();
                   String sImgPath =
                       sWebAppRoot
                       + java.io.File.separator
                       + "assets"
                       + java.io.File.separator
                       + "rhlogo.gif";
                   java.io.File imgFile = new java.io.File(sImgPath);
                   return imgFile;
        }
        
    /**
        * Method makeText.  Returns a text string
        *
        * @param iIndex an index to place at the beginning of the returned string
        * in order to make it unique
        * @return String
        */
       public static String makeText(int iIndex) {
           StringBuffer sb = new StringBuffer(5000);
           for (int i = 0; i < 20; i++) {
               sb.append(iIndex + " All work and no play makes Jack a dull boy.  ");
           }
           return sb.toString();
       }

       /**
        * Method makeText.  Returns a text string 
        *
        * @return String
        */
       public static String makeTextBody() {
           return makeText(0);
       }
        
        /**
         * Method getAdminUser.  Returns a system administrator user
         * @return User
         */
        public static  User getAdminUser() {
            UserCollection uc = User.retrieveAll();
            uc.filter(KernelHelper.getSystemAdministratorEmailAddress());
            uc.next();
            User sysadmin = uc.getUser();
            Assert.exists(sysadmin);
            uc.close();
    
            return sysadmin;
        }
        
        /**
         * Returns a list of at most numUsers BigDecimal User ID's
         * @param numUsers at most the number of User ID's to return 
         * @return a List of BigDecimal User ID's
         */
        public static List getUsersIDs(int numUsers) {
            UserCollection users = User.retrieveAll();
            users.setRange(new Integer(1), new Integer(numUsers+1));
            
            ArrayList list = new ArrayList(numUsers);
            while (users.next()) {
                list.add(users.getUser().getID());       
            }
                            
            users.close();            
            return list;
        }
}

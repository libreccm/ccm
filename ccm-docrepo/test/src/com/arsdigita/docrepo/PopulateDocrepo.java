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
package com.arsdigita.docrepo;

import java.math.BigDecimal;
import java.util.List;

import com.arsdigita.docrepo.ui.RecentUpdatedDocsPortlet;
import com.arsdigita.kernel.User;
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
public class PopulateDocrepo
	extends AbstractPopulateApp
	implements PopulateApp {

	private static final String ARGS_DESC =
		"3 PopulateDocrepo args: numFolderLevels, numFolders, numFiles";

	/* (non-Javadoc)
	 * @see com.arsdigita.populate.apps.PopulateApp#populateApp(java.util.List)
	 */
	public void populateApp(List args) {
		Session ses = SessionManager.getSession();
		TransactionContext txn = ses.getTransactionContext();

		Repository repo = (Repository) getApp();

		//      validate the arguments
		validateArgs(args, 3, ARGS_DESC);
		int iFolderLevels = ((Integer) args.get(0)).intValue();
		int iFolders = ((Integer) args.get(1)).intValue();
		int iFiles = ((Integer) args.get(2)).intValue();

		// Assert.assertTrue(iFolderLevels > 0, "iFolderLevels must be > 0");
		// Assert.assertTrue(iFolders >= 0, "iFolders must be >= 0");
		// Assert.assertTrue(iFiles > 0, "iFiles must be > 0");
		Assert.isTrue(iFolderLevels > 0, "iFolderLevels must be > 0");
		Assert.isTrue(iFolders >= 0, "iFolders must be >= 0");
		Assert.isTrue(iFiles > 0, "iFiles must be > 0");

		//get binary file for uploading
		java.io.File binaryFile = Utilities.getBinaryFile();
        
                //get a user for making the files
                User u = User.retrieve((BigDecimal)(Utilities.getUsersIDs(1).get(0)));
                s_log.debug("using user " + u.getName() + " to create files");

		Folder rootFolder = repo.getRoot();
                txn.beginTxn();
                rootFolder.setLastModifiedUser(u);
                rootFolder.setCreationUser(u);
                rootFolder.save();
                txn.commitTxn();

		String sBaseString = Utilities.getBaseString(getBaseStringSeed());              

		//populate the repository
		for (int i = 0; i < iFolders; i++) {
			txn.beginTxn();
			Folder parentFolder = rootFolder;
			Folder folder = null;

			//create sub-folders
			for (int j = 0; j < iFolderLevels; j++) {

				//create the folder
				String sFolderName = "Folder" + sBaseString + i + j;
				folder = new Folder(sFolderName, "", parentFolder);
                                folder.setCreationUser(u);
                                folder.setLastModifiedUser(u);
				folder.save();
				s_log.info(
					"Inserted folder "
						+ sFolderName
						+ ", child of "
						+ parentFolder.getName());

				//create files in this folder
				for (int k = 0; k < iFiles; k++) {
					String sFileName =
						"File" + sBaseString + i + j + k + ".gif";
					File file = new File(folder);
					file.setContent(
						binaryFile,
						sFileName,
						"uploaded file",
						"image/gif");
                                        file.setCreationUser(u);
                                        file.setLastModifiedUser(u);                                        
					file.save();
					s_log.info(
						"Inserted file "
							+ sFileName
							+ " in folder "
							+ folder.getName());                                        
				}
				//update parentFolder
				parentFolder = folder;
			}
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
		ApplicationType appType =
			ApplicationType.retrieveApplicationTypeForApplication(
				Repository.BASE_DATA_OBJECT_TYPE);
		if (s_log.isDebugEnabled()) {
			s_log.debug(
				"returning app type " + appType.getApplicationObjectType());
		}
		return appType;
	}

	/* (non-Javadoc)
	* @see com.arsdigita.populate.apps.AbstractPopulateApp#getPortletType()
	*/
	protected String getPortletType() {
		return RecentUpdatedDocsPortlet.BASE_DATA_OBJECT_TYPE;
	}  

}

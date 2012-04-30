/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.workflow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.log4j.Logger;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.util.SecurityConstants;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.util.cmd.Program;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.versioning.Transaction;


/**
 * 
 * @author chris.gilbert at westsussex.gov.uk
 *
 * Grant approve privilege on folders where the group specified on the command line already has edit privilege.
 * Currently, approval email are sent to users with edit privilege on the folder where the approve task event has
 * occurred. New implementation checks for new approve privilege, and so users who are currently approving must 
 * have edit privilege. 
 */
public class AddApprovePermission extends Program {
    private static final Logger s_log = Logger.getLogger( AddApprovePermission.class );

    public AddApprovePermission() {
        super("AddApprovePermission",
              "1.0.0",
              "");
        
        getOptions().addOption
            (OptionBuilder
             .hasArg(true)
             .isRequired()
             .withLongOpt("group-id")
             .withDescription("Grant approve item privilege to folders where this group has edit privilege")
             .create('g'));
        
    }

    public void doRun(CommandLine cmdLine) {
        
        BigDecimal groupID = new BigDecimal(cmdLine.getOptionValue("g"));
		try {
        SessionManager.getSession().getTransactionContext().beginTxn();
        Group group = new Group(groupID);
        DataCollection folders = SessionManager.getSession().retrieve(Folder.BASE_DATA_OBJECT_TYPE);
        PermissionService.filterObjects(folders, PrivilegeDescriptor.get(SecurityConstants.CMS_EDIT_ITEM), new OID(Group.BASE_DATA_OBJECT_TYPE, groupID));
        while (folders.next()) {
        	Folder folder = (Folder)DomainObjectFactory.newInstance(folders.getDataObject());
        	PermissionDescriptor approve = new PermissionDescriptor(PrivilegeDescriptor.get(SecurityConstants.CMS_APPROVE_ITEM), folder, group);
        	PermissionService.grantPermission(approve);
        	
        }
		SessionManager.getSession().getTransactionContext().commitTxn();
        
        } catch (Exception e) {
        	s_log.error("Failed to grant permissions for group " + groupID, e);
        }
        
    }
    
    public static void main(String[] args) {
        new AddApprovePermission().run(args);
    }
}

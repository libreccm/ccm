/*
 * Copyright (C) 2007 Chris Gilbert All Rights Reserved.
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
package com.arsdigita.portalworkspace.upgrade;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.GroupCollection;
import com.arsdigita.kernel.Party;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.portalworkspace.WorkspaceCollection;
import com.arsdigita.london.util.Program;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.web.ApplicationType;

/**
 * @author chris gilbert (chris.gilbert@westsussex.gov.uk)
 *
 * Tidy up portal groups so they are not all at root level
 *
 *
 */
public class CreateContainerGroups extends Program {

   public CreateContainerGroups() {
	super("CreateGroups", "1.0.0", "");
    }

    private static Logger s_log = Logger.getLogger(CreateContainerGroups.class);

    public void doRun(CommandLine cmdLine) {
	try {

	    final TransactionContext tc = SessionManager.getSession().getTransactionContext();

	    tc.beginTxn();

	    WorkspaceCollection existingWorkSpaces = Workspace.retrieveAll();
	    Group parentGroup = null;
	    while (existingWorkSpaces.next()) {
				
		Workspace workspace = existingWorkSpaces.getWorkspace();
		// do this inside the loop because we get the workspace application 
		// type from an instance of a workspace
		if (parentGroup == null) {
		    s_log.info("Creating group for workspace application type");
		    System.out.println("Creating group for workspace application type");
		    ApplicationType workspaceType = workspace.getApplicationType();
		    if (workspaceType.getGroup() == null) {
			workspaceType.createGroup();
		    }
		    parentGroup = workspaceType.getGroup();
		}
		Group workspaceGroup = null;
		Party party = workspace.getParty();
		if (party instanceof Group) {
		    workspaceGroup = (Group)party;
		} else {
		    GroupCollection groups = Group.retrieveAll();
		    groups.addEqualsFilter("name", workspace.getTitle());
		    if (groups.next()) {
			workspaceGroup = groups.getGroup();
		    }
		    groups.close();
		    groups.reset();
		}
				
		if (workspaceGroup != null) {
		    GroupCollection supergroups = workspaceGroup.getSupergroups();
		    supergroups.addEqualsFilter(Group.ID, parentGroup.getID());

            boolean isEmpty = false;
            try {
                supergroups.isEmpty();
            } catch (com.redhat.persistence.metadata.MetadataException e) {
                isEmpty = true;
            }

            if (isEmpty) { //XXX Exception thrown relating to this logic!
                        // com.redhat.persistence.metadata.MetadataException:
                        // com/arsdigita/kernel/Group.pdl: line 27, column 23:
                        // Query for: com.arsdigita.kernel.Group.name failed to
                        // return rows
                s_log.info("moving group for " + workspace.getTitle());
                System.out.println("moving group for " + workspace.getTitle());
                parentGroup.addSubgroup(workspaceGroup);
		    } else {
			    s_log.info("group for " + workspace.getTitle()
                          +" is already child of workspace type group");
			    System.out.println("group for " + workspace.getTitle()
                                  +" is already child of workspace type group");
		    }
		} else {
		    s_log.info("can't find group for " + workspace.getTitle());
		    System.out.println("can't find group for " + workspace.getTitle());
		}
				
	    }
	    tc.commitTxn();

	} catch (Throwable e) {
	    s_log.error("error occured", e);
	    System.out.println("error occured. Check logs for details");
	}
    }

    public static final void main(final String[] args) {
	new CreateContainerGroups().run(args);
    }

}

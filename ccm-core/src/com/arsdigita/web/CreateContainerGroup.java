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
package com.arsdigita.web;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.log4j.Logger;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Group;
import com.arsdigita.util.cmd.Program;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.workflow.simple.Task;
import com.arsdigita.workflow.simple.UserTask;
import com.arsdigita.workflow.simple.Workflow;

/**
 * @author chris.gilbert@westsussex.gov.uk
 * 
 * one off groups to be created retrospectively for application types
 * and applications. Some applications have programs to bulk create groups
 * (ccm-forum and ccm-ldn-portal)
 * 
 * Usage - specify id of application or application type and optionally 
 * specify the id of an existing group (if none specified, a new one will be created)
 *
 */
public class CreateContainerGroup extends Program {

    private static final Logger s_log =
	Logger.getLogger(CreateContainerGroup.class);

    public CreateContainerGroup() {
	super("CreateContainerGroup", "1.0.0", "");
	getOptions().addOption(
		OptionBuilder
		.hasArg(true)
		.withLongOpt("application-type-id")
		.withDescription("Specify the id of an application type for which you need a group created (find it in the database application_type_id column in application_types table)")
		.create('t'));
	getOptions().addOption(
		OptionBuilder
		.hasArg(true)
		.withLongOpt("application-id")
		.withDescription("Specify the id of an application for which you need a group created (find it in the database application_id column in applications table)")
		.create('a'));
	getOptions().addOption(
		OptionBuilder
		.hasArg(true)
		.withLongOpt("group-id")
		.withDescription("Specify the id of an existing group that is to be the container group for this application or application type (group_id column in groups table)")
		.create('g'));

    }

    public void doRun(CommandLine cmdLine) {

	BigDecimal applicationTypeID = null;
	BigDecimal applicationID = null;
	BigDecimal GroupID = null;
	try {
	    applicationTypeID =	cmdLine.getOptionValue("t") == null ? null
					: new BigDecimal(cmdLine.getOptionValue("t"));
	    applicationID = cmdLine.getOptionValue("a") == null	? null
					: new BigDecimal(cmdLine.getOptionValue("a"));
	    GroupID = cmdLine.getOptionValue("g") == null ? null
					: new BigDecimal(cmdLine.getOptionValue("g"));

	    // validate

	    if (applicationTypeID == null && applicationID == null) {
		System.out.println("You must specify either an application or an application type");
		return;
	    }
	    if (applicationTypeID != null && applicationID != null) {
		System.out.println("You must specify either an application or an application type - not both");
		return;
	    }

	} catch (NumberFormatException e) {
	    System.out.println("All arguments must be valid numbers");
	    return;
	}
	TransactionContext tc =	SessionManager.getSession().getTransactionContext();
	try {
	    tc.beginTxn();
	    Group group = GroupID == null ? null : new Group(GroupID);
	    if (applicationTypeID != null) {
		ApplicationType type = ApplicationType.retrieveApplicationType(applicationTypeID);
		if (group != null) {
		    type.setGroup(group);
		} else {
		    type.createGroup();
		}
	    } else {
		Application application = Application.retrieveApplication(applicationID);
		if (group != null) {
		    application.setGroup(group);
		} else {
		    application.createGroup();
		}
	    }

	    tc.commitTxn();
	} catch (Throwable e) {
	    System.out.println(e.getMessage());
	    tc.abortTxn();
	    return;
	}

    }

    public static void main(String[] args) {
	new CreateContainerGroup().run(args);
    }

}

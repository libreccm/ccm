/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.workflow;

import org.apache.log4j.Logger;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;

/**
 * This class represents enables fine grained control of the url that is 
 * generated for a task event. Each task type may have a TaskEventURLGenerator
 * object for any events that may occur on the task type. If there is 
 * no specific TaskEventURLGenerator for the event, then the default 
 * TaskURLGenerator specified in the CMSTaskType is used
 *
 *
 * @author chris.gilbert at westsussex.gov.uk
 * @version $Id: $
 **/
public class TaskEventURLGenerator extends DomainObject {

	public static final String BASE_DATA_OBJECT_TYPE =
		"com.arsdigita.cms.workflow.TaskEventURLGenerator";

	
	public static final String ID = "generatorID";
	public static final String EVENT = "event";
	public static final String URL_GENERATOR_CLASS =
		"urlGeneratorClass";
	public static final String CONTENT_TYPE = "contentType";
	
	private static final Logger s_log = Logger.getLogger(TaskEventURLGenerator.class);

	public TaskEventURLGenerator(Integer id) {
		super(new OID(BASE_DATA_OBJECT_TYPE, id));
	}

	public TaskEventURLGenerator(DataObject obj) {
		super(obj);
	}

	/**
	 * create a message object and associate it with the current application instance
	 *
	 */
	public TaskEventURLGenerator() {
		super(BASE_DATA_OBJECT_TYPE);
		setID();

	}
	
	public int getID(){		
			return ((Integer)get(ID)).intValue();		
		}
	
	/**
		 * allocate a unique integer to the message
		 *
		 */
	private void setID() {
		DataCollection allTypes =
			SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);
		allTypes.addOrder(ID + " desc");
		Integer id = new Integer(1);
		if (allTypes.next()) {
			TaskEventURLGenerator taskType =
				(TaskEventURLGenerator) DomainObjectFactory.newInstance(
					allTypes.getDataObject());
			int currentMaxID = taskType.getID();
			id = new Integer(currentMaxID + 1);
			allTypes.close();
		}
		set(ID, id);
	}
	
	
	
	public TaskURLGenerator getGenerator() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
			String generatorClassName = (String)get(URL_GENERATOR_CLASS);
			Class URLGenerator = Class.forName(generatorClassName);
			return (TaskURLGenerator)URLGenerator.newInstance();
							
	}
	
	
	

}

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
package com.arsdigita.workflow.simple;

import java.util.Iterator;
import java.util.Collections;
import java.math.BigDecimal;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.User;

/**
 * 
 * @version $Id: WorkflowTemplate.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class WorkflowTemplate extends Workflow {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.workflow.simple.WorkflowTemplate";

    /**
     * Creates a new workflow process definition.  Properties of this object
     * are not made persistent until the <code>save()</code> method is called.
     *
     * @param label the label
     * @param description the description
     *
     **/
    public WorkflowTemplate(String label, String description) {
        super(BASE_DATA_OBJECT_TYPE);
        initAttributes(label,description);
    }

    /**
     * Creates a new workflow process definition with the properties
     * <code>label</code> and <code>description</code> set to null.
     * Properties of this
     * object are not made persistent until the <code>save</code>
     * method is called. If save() is called
     * without setting these properties, an IllegalArgumentException
     * will be thrown.
     *
     **/
    public WorkflowTemplate() {
        super(BASE_DATA_OBJECT_TYPE);
    }


    /**
     * Restores a workflow process definition from task data object.
     *
     * @param workflowTemplateDataObject the template object to restore the process from
     *
     **/
    public WorkflowTemplate(DataObject workflowTemplateDataObject) {
        super(workflowTemplateDataObject);
    }

    /**
     * Constructor for setting the object type name.
     *
     * @param type the type name
     *
     **/
    protected WorkflowTemplate(ObjectType type) {
        super(type);
    }

    protected WorkflowTemplate(String typeName) {
        super(typeName);
    }

    /**
     * Restores a workflow process definition with an OID.
     *
     * @param oid the OID
     *
     **/
    public WorkflowTemplate(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Restores a workflow process definition with a BigDecimal.
     *
     * @param id the BigDecimal ID of this object. An OID will be
     * created implicitly with the BASE_DATA_OBJECT_TYPE constant
     * specified in this file.
     *
     **/
    public WorkflowTemplate(BigDecimal id)
        throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }
    /**
     * Creates a workflow based on the supplied workflow definition ID.
     * @return the workflow
     * */
    public Workflow instantiateNewWorkflow() {
        Workflow w = (Workflow)clone();
        if (w==null) return null;
        // save a reference to the template used to create the new Workflow
        w.setWorkflowTemplate(this);
        w.save();
        return w;
    }

    /**
     * Extends the workflow by appending the tasks from the
     * workflow template
     */
    public void extendWorkflow(Workflow workflow) {
        cloneTasks(workflow);
        startInternal();
    }

    /**
     * Adds a task to this process. (persistent operation)
     *
     * @param task the task to add to this process
     *
     * TODO: change this to use role relations. right now we just
     * change the parent of the task manually. This should be donevia
     * the PDL file.
     **/
    public void addTask(Task task) {
        task.setParent(this);
        task.save();
        //add(WF_TASKS, task);
    }
    /* We have to throw these exceptions because we decided that
       Workflow templates extend Workflows rather than that other way
       around.  */

    public void stop(User user) {

    }

    public void start(User user) {

    }

    public int getProcessState() {
        return Workflow.NONE;
    }

    public void setObject(ACSObject o) {

    }

    public Iterator getEnabledTasks() {
        return Collections.EMPTY_LIST.iterator();
    }

    public Iterator getFinishedTasks() {
        return Collections.EMPTY_LIST.iterator();
    }
    public Iterator getOverdueTasks() {
        return Collections.EMPTY_LIST.iterator();
    }
    public OID getObjectOID() {
        return null;
    }
}

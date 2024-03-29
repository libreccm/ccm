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
package com.arsdigita.cms.ui.workflow;



import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.workflow.simple.Task;
import com.arsdigita.workflow.simple.TaskCollection;


/**
 * Builds a list of workflow templates registered to the current
 * content section.
 *
 * @author Uday Mathur (umathur@arsdigita.com)
 * @version $Id: TaskCollectionOptionPrintListener.java 287 2005-02-22 00:29:02Z sskracic $
 */

public abstract class TaskCollectionOptionPrintListener implements PrintListener {

    abstract TaskCollection getCollection(PageState state);

    public void prepare(PrintEvent e) {
        PageState s = e.getPageState();
        OptionGroup w = (OptionGroup) e.getTarget();
        TaskCollection t = getCollection(s);

        while (t.next()) {
            Task task = (Task)t.getDomainObject();
            w.addOption(new Option(task.getID().toString(),
                                   task.getLabel()));
        }
    }

}

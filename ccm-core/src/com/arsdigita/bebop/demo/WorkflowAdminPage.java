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
package com.arsdigita.bebop.demo;


import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.demo.workflow.AdminPane;
import com.arsdigita.bebop.demo.workflow.Listing;


public class WorkflowAdminPage extends Page {


    public WorkflowAdminPage() {
        super("Workflow Mockup", makePanel());

        String title = "Workflow Templates Control Center";

        setTitle(title);
        Label t = new Label(title);
        t.setFontWeight(Label.BOLD);
        add(t, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        Listing listing = new Listing();
        SingleSelectionModel m = listing.getList().getSelectionModel();
        add(listing, ColumnPanel.TOP);
        add(new AdminPane(m, listing.getAddLink()));
    }

    private static ColumnPanel makePanel() {
        ColumnPanel result = new ColumnPanel(2);
        result.setColumnWidth(1, "25%");
        result.setBorder(false);
        result.setPadColor("white");
        result.setPadBorder(true);
        return result;
    }

}

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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.toolbox.ui.LayoutPanel;

/**
 * A wrapper around the {@link ItemSearchSection} which embedds the form section
 * in a form.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: ItemSearch.java 1940 2009-05-29 07:15:05Z terry $
 */
public class IdSearchTab extends LayoutPanel {

    private final Form filterform;
    private final Label formHeader;
    private final TextField textField;
    private final BoxPanel idSearchResultTablePanel;
    

    public IdSearchTab(String name) {
        super();

        filterform = new Form("IdSearchForm");
        formHeader = new Label("Item-id:");
        textField = new TextField("IdFeld");
        textField.addValidationListener(new NotNullValidationListener());

        filterform.add(textField);
        filterform.add(new Submit("search"));

        final SegmentedPanel left = new SegmentedPanel();
        left.addSegment(formHeader, filterform);

        setLeft(left);

        final BoxPanel body = new BoxPanel(BoxPanel.VERTICAL);

        final IdSearchResultTable idSearchResultTable = new IdSearchResultTable(
                textField);
        idSearchResultTable.setStyleAttr("min-width: 30em;");
        idSearchResultTablePanel = new BoxPanel(BoxPanel.VERTICAL);
        idSearchResultTablePanel.add(idSearchResultTable);
        body.add(idSearchResultTablePanel);

        setBody(body);

    }

}

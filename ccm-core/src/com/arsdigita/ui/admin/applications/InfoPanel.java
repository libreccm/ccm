/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.ui.admin.applications;

import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.ui.admin.GlobalizationUtil;

/**
 * A helper class for creating a column panel with two labels in each row.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class InfoPanel extends GridPanel {

    public InfoPanel() {
        super(2);
    }

    public void addLine(final String labelKey, final String data) {
        addLine(labelKey, data, false);
    }

    public void addLine(final String labelKey, final String data, final boolean globalizeData) {
        add(new Label(GlobalizationUtil.globalize(labelKey)));
        if (globalizeData) {
            add(new Label(GlobalizationUtil.globalize(data)));
        } else {
            add(new Label(data));
        }
    }

}

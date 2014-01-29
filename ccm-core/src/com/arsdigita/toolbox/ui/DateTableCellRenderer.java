/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.toolbox.ui;


import com.arsdigita.toolbox.util.GlobalizationUtil ; 

import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.globalization.Globalized;
import java.util.Date;

/**
 * 
 * This displays the date in a standard format within a TableCell.
 *
 * @author Randy Graebner (randyg@alum.mit.edu)
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 * @version $Id: DateTableCellRenderer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class DateTableCellRenderer implements Globalized, TableCellRenderer {

    public Component getComponent(Table table, PageState state, Object value,
                                  boolean isSelected, Object key,
                                  int row, int column) {
        if (value == null) {
            // if there is not a value then there is nothing to display
            return new Label(GlobalizationUtil.globalize("toolbox.ui.na"));
        } else {
            return new Label(FormatStandards.formatDate((Date)value));
        }
    }
}

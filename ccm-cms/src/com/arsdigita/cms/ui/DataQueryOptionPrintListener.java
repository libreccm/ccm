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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.persistence.DataQuery;

/**
 *
 * @version $Id: DataQueryOptionPrintListener.java 287 2005-02-22 00:29:02Z sskracic $
 */
public abstract class DataQueryOptionPrintListener implements PrintListener {

    public DataQueryOptionPrintListener() {
    }

    protected abstract DataQuery getDataQuery (PageState s);

    public void prepare(PrintEvent e) {
        PageState s = e.getPageState();
        OptionGroup w = (OptionGroup) e.getTarget();
        DataQuery dataQuery = getDataQuery(s);
        while (dataQuery.next()) {
            w.addOption(new Option(getKey(dataQuery),
                                   getValue(dataQuery)));
        }
    }

    public abstract String getKey(DataQuery d);

    public String getValue(DataQuery d) {
        return getKey(d);
    }
}

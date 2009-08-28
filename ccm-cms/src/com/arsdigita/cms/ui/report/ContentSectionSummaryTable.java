/*
 * Copyright (C) 2009 Permeance Technologies Pty Ltd. All Rights Reserved.
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
 */

package com.arsdigita.cms.ui.report;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Table;
import com.arsdigita.cms.util.GlobalizationUtil;

/**
 * Table component for content section summary report.
 *   
 * @author <a href="https://sourceforge.net/users/thomas-buckel/">thomas-buckel</a>
 */
public class ContentSectionSummaryTable extends Table
{

    private static final String[] s_fixedReportColumns = new String[] { lz("cms.ui.reports.css.folder"),
            lz("cms.ui.reports.css.subfolderCount"), lz("cms.ui.reports.css.contentType"),
            lz("cms.ui.reports.css.draft"), lz("cms.ui.reports.css.live"), };

    public ContentSectionSummaryTable()
    {
        super(new ContentSectionSummaryReportTableModelBuilder(), s_fixedReportColumns);
        setEmptyView(new Label(lz("cms.ui.reports.css.emptyResult")));
    }

    private static String lz(final String key)
    {
        return (String) GlobalizationUtil.globalize(key).localize();
    }

}

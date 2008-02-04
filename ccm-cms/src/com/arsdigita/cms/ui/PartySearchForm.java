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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.util.Assert;

/**
 * Form to search for parties to be added to a staff group.
 *
 * @author Scott Seago &lt;sseago@redhat.com&gt;
 * @version $Id: PartySearchForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class PartySearchForm extends BaseForm {
    public static final String versionId =
        "$Id: PartySearchForm.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private final static String SEARCH_LABEL = "Search";

    private final TextField m_search;

    public PartySearchForm() {
        super("SearchParties", gz("cms.ui.search"));

        addComponent(new Label(gz("cms.ui.search_prompt")));

        m_search = new TextField(new StringParameter("query"));
        m_search.setSize(40);
        addComponent(m_search);

        addAction(new Submit("finish", gz("cms.ui.search")));
        addAction(new Cancel());

    }

    public final void register(final Page page) {
        super.register(page);

        Assert.assertTrue(page.stateContains(this));
    }

    public TextField getSearchWidget() {
        return m_search;
    }
}

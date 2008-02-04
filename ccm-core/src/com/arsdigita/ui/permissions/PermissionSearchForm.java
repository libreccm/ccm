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
package com.arsdigita.ui.permissions;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * A Form that allows to search for objects by name
 *
 * @author Stefan Deusch 
 * @version $Id: PermissionSearchForm.java 287 2005-02-22 00:29:02Z sskracic $
 */

class PermissionSearchForm extends Form
    implements FormProcessListener,
               PermissionsConstants
{
    private static final String FORM_NAME = "permissionSearch";
    private static final String SEARCH_QUERY_PARAM_NAME = "searchQuery";

    /**
     * Constructor
     */

    public PermissionSearchForm() {
        this(new SimpleContainer());
    }

    /**
     * Constructor
     */

    public PermissionSearchForm(SimpleContainer container) {
        super(FORM_NAME, container);
        setMethod(Form.POST);

        add(new Label(new GlobalizedMessage("permissions.searchForm.label")));
        add(new TextField(SEARCH_QUERY_PARAM_NAME));

        Submit submit = new Submit("submit");
        submit.setButtonLabel(SEARCH_BUTTON);
        add(submit);
        addProcessListener(this);
    }

    /**
     * Sends a redirect to the one-permissions page.
     */

    public void process(FormSectionEvent event) throws FormProcessException {
        final String searchQuery = (String)
            event.getFormData().get(SEARCH_QUERY_PARAM_NAME);

        final ParameterMap params = new ParameterMap();
        params.setParameter(OBJECT_ID, searchQuery);

        final URL url = URL.there(event.getPageState().getRequest(),
                                  "/permissions/one",
                                  params);

        throw new RedirectSignal(url, true);
    }

}

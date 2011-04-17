/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.faq.ui;

import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.persistence.DataObject;

import com.arsdigita.portal.apportlet.AppPortlet;

public class FaqQuestionsPortlet extends AppPortlet {
    public static final String versionId = "$Id: //apps/faq/dev/src/com/arsdigita/faq/ui/FaqQuestionsPortlet.java#4 $ by $Author: dennis $, $DateTime: 2004/08/17 23:26:27 $";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.faq.FaqQuestionsPortlet";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public FaqQuestionsPortlet(DataObject dataObject) {
        super(dataObject);
    }

    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new FaqQuestionsPortletRenderer(this);
    }
}

/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataQuery;

/**
 * 
 * @version $Id: DataQueryOptionPrintListener.java,v 1.1 2004/12/15 14:27:54 awux7820 Exp $
 */
public abstract class DataCollectionOptionPrintListener implements PrintListener {

    public DataCollectionOptionPrintListener() {
    }

    protected abstract DataCollection getDataCollection (PageState s);

    public void prepare(PrintEvent e) {
        PageState s = e.getPageState();
        OptionGroup w = (OptionGroup) e.getTarget();
        DataCollection collection = getDataCollection(s);
        while (collection.next()) {
        	DomainObject object = DomainObjectFactory.newInstance(collection.getDataObject());
            w.addOption(new Option(getKey(object),
                                   getValue(object)));
        }
    }

    public abstract String getKey(DomainObject d);

    public String getValue(DomainObject d) {
        return getKey(d);
    }
}

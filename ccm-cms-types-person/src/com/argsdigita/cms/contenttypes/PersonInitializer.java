/*
 * Copyright (C) 2009 University Bremen, Center for Social Politics, Parkallee 39, 28209 Bremen.
 *
 */
package com.argsdigita.cms.contenttypes;

import com.arsdigita.cms.contenttypes.ContentTypeInitializer;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.runtime.DomainInitEvent;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class PersonInitializer extends ContentTypeInitializer {

    public final static String versionId =
            "$Id: AddressInitializer.java 1 2009-03-19 08:30:26Z jensp $" +
            "$Author: jensp $" +
            "$DateTime: 2009/03/19 09:30:00 $";

    private static final Logger s_log = Logger.getLogger(PersonInitializer.class);

    public PersonInitializer() {
        super("ccm-cms-types-person.pdl.mf",
              Person.BASE_DATA_OBJECT_TYPE);
    }

    public void init(DomainInitEvent evt) {
        super.init(evt);
    }

    public String[] getStylesheets() {
        return new String[] { "" };
    }
}

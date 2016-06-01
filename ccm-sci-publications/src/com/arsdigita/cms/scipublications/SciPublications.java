/*
 * Copyright (c) 2010 Jens Pelzetter
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
package com.arsdigita.cms.scipublications;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.Application;
import java.math.BigDecimal;

/**
 * This application provides the main entry point for all functions of
 * the SciPublications module. The application will be mounted at
 * <code>/ccm/scipubliations/</code>. The functions are accessed using
 * an additional URL fragment. For example, if you want to access the 
 * exportUsers function, you will use the URL
 * <code>/ccm/scipublications/exportUsers/</code>.
 *
 * @author Jens Pelzetter
 */
public class SciPublications extends Application {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.scipublications.SciPublications";

    public SciPublications(final DataObject dobj) {
        super(dobj);
    }

    public SciPublications(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public SciPublications(BigDecimal key) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, key));
    }

    @Override
    public String getServletPath() {
        return "/scipublications/";
    }
}

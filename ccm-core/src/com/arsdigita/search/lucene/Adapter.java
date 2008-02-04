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
package com.arsdigita.search.lucene;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.Party;
import com.arsdigita.persistence.metadata.ObjectType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

/**
 * Adapter.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2004/08/16 $
 **/

public abstract class Adapter {

    public final static String versionId =
        "$Id: Adapter.java 287 2005-02-22 00:29:02Z sskracic $" +
        " by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";


    /**
     * Gets the ID for the DomainObject
     *
     * @param dobj
     * @return ID of the object
     *
     * @post $retval != null
     */
    public abstract BigDecimal getID(DomainObject dobj);

    public abstract ObjectType getObjectType(DomainObject dobj);

    public abstract String getTypeSpecificInfo(DomainObject dobj);

    public abstract Locale getLocale(DomainObject dobj);

    /**
     * Gets the Title property for the DomainObject
     *
     * @param dobj
     * @return title of the object
     *
     * @post $retval != null
     */
    public abstract String getTitle(DomainObject dobj);

    public abstract String getSummary(DomainObject dobj);

    public abstract String getContent(DomainObject dobj);

    public abstract Date getCreationDate(DomainObject dobj);

    public abstract Party getCreationParty(DomainObject dobj);

    public abstract Date getLastModifiedDate(DomainObject dobj);

    public abstract Party getLastModifiedParty(DomainObject dobj);

}

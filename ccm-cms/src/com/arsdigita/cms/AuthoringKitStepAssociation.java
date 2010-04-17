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
package com.arsdigita.cms;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;

/**
 * This class associates an {@link com.arsdigita.cms.AuthoringKit
 * authoring kit} with {@link com.arsdigita.cms.AuthoringStep
 * authoring steps} in a particular order.
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Id: AuthoringKitStepAssociation.java 287 2005-02-22 00:29:02Z sskracic $
 */
class AuthoringKitStepAssociation extends DomainObject {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.AuthoringKitStepAssociation";

    protected static final String KIT_ID = "kitId";
    protected static final String STEP_ID = "stepId";
    protected static final String ORDERING = "ordering";


    public AuthoringKitStepAssociation() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public AuthoringKitStepAssociation(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public AuthoringKitStepAssociation(DataObject obj) {
        super(obj);
    }


    public BigDecimal getKitID() {
        return (BigDecimal) get(KIT_ID);
    }

    public void setKit(AuthoringKit kit) {
        set(KIT_ID, kit.getID());
    }

    public BigDecimal getStepID() {
        return (BigDecimal) get(STEP_ID);
    }

    public void setStep(AuthoringStep step) {
        set(STEP_ID, step.getID());
    }

    public BigDecimal getOrdering() {
        return (BigDecimal) get(ORDERING);
    }

    public void setOrdering(BigDecimal ordering) {
        set(ORDERING, ordering);
    }
}

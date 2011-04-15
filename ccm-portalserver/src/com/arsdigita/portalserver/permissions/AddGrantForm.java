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
package com.arsdigita.portalserver.permissions;


import java.math.BigDecimal;

import com.arsdigita.persistence.*;
import com.arsdigita.kernel.*;
import com.arsdigita.kernel.permissions.*;
import com.arsdigita.domain.*;
import com.arsdigita.bebop.*;
import com.arsdigita.bebop.parameters.*;
import com.arsdigita.bebop.event.*;
import org.apache.log4j.Logger;




class AddGrantForm extends Form {

    // Select value used to indicate 'all types' in situations
    // where type-specific grants are possible
    final static String ALL_TYPES = "ALL_TYPES";

    private BigDecimalParameter m_objectParameter;
    private BigDecimalParameter m_partyParameter;
    private StringParameter m_privilegeParameter;
    private StringParameter m_typeParameter;
    private RequestLocal m_errorMessageRL;

    private RequestLocal m_grantPermissionRL;
    private static final Logger s_log = Logger.getLogger(AddGrantForm.class);

    BigDecimalParameter getObjectParameter() {
        return m_objectParameter;
    }

    BigDecimalParameter getPartyParameter() {
        return m_partyParameter;
    }

    StringParameter getPrivilegeParameter() {
        return m_privilegeParameter;
    }

    StringParameter getTypeParameter() {
        return m_typeParameter;
    }


    AddGrantForm(String name,
                 Container container,
                 RequestLocal errorMessageRL) {
        super(name, container);
        setRedirecting(true);

        // These make sure that fireValidate and fireProcess get
        // called even when no validation or process listeners are
        // added
        forwardValidation();
        forwardProcess();

        m_objectParameter = new BigDecimalParameter("objectID");
        m_partyParameter = new BigDecimalParameter("partyID");
        m_privilegeParameter = new StringParameter("privName");
        m_typeParameter = new StringParameter("typeName");
        m_errorMessageRL = errorMessageRL;

        m_grantPermissionRL = new RequestLocal();
    }


    protected void fireValidate(FormSectionEvent ev) {
        s_log.debug("AddGrantForm.fireValidate running");

        PageState ps = ev.getPageState();

        // Retrieve parameter values
        FormData fd = ev.getFormData();
        BigDecimal objectID =
            (BigDecimal)fd.get(m_objectParameter.getName());
        BigDecimal partyID =
            (BigDecimal)fd.get(m_partyParameter.getName());
        String typeName =
            (String)fd.get(m_typeParameter.getName());
        String privName =
            (String)fd.get(m_privilegeParameter.getName());

        // Get the OIDs we need for dealing with permissions
        OID partyOID =
            new OID(Party.BASE_DATA_OBJECT_TYPE, partyID);
        OID objectOID =
            new OID(ACSObject.BASE_DATA_OBJECT_TYPE, objectID);

        // Determine what privileges we care about
        PrivilegeDescriptor testPriv = Grant.s_interestingPrivileges[0];

        PrivilegeDescriptor newPriv =
            PrivilegeDescriptor.get(privName);

        PermissionDescriptor testPerm =
            new PermissionDescriptor(testPriv, objectOID, partyOID);

        if (PermissionService.checkDirectPermission(testPerm)) {
            String partyName;
            try {
                Party party = (Party)
                    DomainObjectFactory.newInstance(partyOID);
                partyName = party.getName();
            } catch (DataObjectNotFoundException ex) {
                throw new IllegalStateException("Bad Party");
            }

            String objectName;
            try {
                ACSObject object = (ACSObject)
                    DomainObjectFactory.newInstance(objectOID);
                objectName = object.getDisplayName();
            } catch (DataObjectNotFoundException ex) {
                throw new IllegalStateException("Bad Object");
            }

            String errMsg;
            if (typeName == null) {
                errMsg =
                    "\"" + partyName + "\"" + " already has a defined right " +
                    "on \"" + objectName + "\".";
            } else if (typeName.equals(ALL_TYPES)) {
                errMsg =
                    "\"" + partyName + "\" already has a defined right on \"" +
                    objectName + "\" and its contents.";
            } else {
                // FIXME: get type plural pretty name and use
                // it instead of typeName in this message.
                errMsg =
                    "\"" + partyName + "\" already has a defined right on " +
                    "the " + typeName + "s contained in \"" + objectName +
                    "\".";
            }
            m_errorMessageRL.set(ps, errMsg);
            fd.addError(errMsg);
        } else {
            PermissionDescriptor perm =
                new PermissionDescriptor(newPriv, objectOID, partyOID);
            m_grantPermissionRL.set(ps, perm);
        }

        if (s_log.isDebugEnabled()) {
            if (fd.isValid()) {
                s_log.debug("VALID");
            } else {
                s_log.debug("INVALID");
            }

        }


        super.fireValidate(ev);
    }


    protected void fireProcess(FormSectionEvent ev)
        throws FormProcessException {

        PermissionDescriptor perm =
            (PermissionDescriptor)m_grantPermissionRL.get(ev.getPageState());
        PermissionService.grantPermission(perm);

        // copy logic in GrantsTable
        PrivilegeDescriptor pd = perm.getPrivilegeDescriptor();
        if (pd.equals(PrivilegeDescriptor.EDIT)
            || pd.equals(PrivilegeDescriptor.ADMIN)) {
            PermissionService.grantPermission
                (new PermissionDescriptor
                 (PrivilegeDescriptor.CREATE,
                  perm.getACSObjectOID(),
                  perm.getPartyOID()));
        }

        super.fireProcess(ev);
    }
}

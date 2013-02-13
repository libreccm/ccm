/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.portalworkspace.ui.admin;

import com.arsdigita.portalworkspace.util.GlobalizationUtil;

import com.arsdigita.persistence.*;
import com.arsdigita.kernel.*;
import com.arsdigita.kernel.permissions.*;
import com.arsdigita.domain.*;
import com.arsdigita.bebop.*;
import com.arsdigita.bebop.form.*;
import com.arsdigita.bebop.parameters.*;
import com.arsdigita.bebop.event.*;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

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
        //m_typeParameter = new StringParameter("typeName");
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
        //String typeName =
        //    (String)fd.get(m_typeParameter.getName());
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

//         if ((typeName != null) && !typeName.equals(ALL_TYPES)) {
//             testPriv = ParameterizedPrivilege
//                 .createPrivilege(testPriv, typeName, "");
//             newPriv = ParameterizedPrivilege
//                 .createPrivilege(newPriv, typeName, "");
//         }

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
//             if (typeName == null) {
                errMsg =
                    "\"" + partyName + "\"" + " already has a defined right " +
                    "on \"" + objectName + "\".";
//             } else if (typeName.equals(ALL_TYPES)) {
//                 errMsg =
//                     "\"" + partyName + "\" already has a defined right on \"" +
//                     objectName + "\" and its contents.";
//             } else {
//                 // FIXME: get type plural pretty name and use
//                 // it instead of typeName in this message.
//                 errMsg =
//                     "\"" + partyName + "\" already has a defined right on " +
//                     "the " + typeName + "s contained in \"" + objectName +
//                     "\".";
//             }

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

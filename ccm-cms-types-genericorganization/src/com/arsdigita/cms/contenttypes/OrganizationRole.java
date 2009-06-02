package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.TextAsset;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import org.apache.log4j.Logger;

import java.math.BigDecimal;

public class OrganizationRole extends ContentPage {

    private static final Logger logger = Logger.getLogger(OrganizationRole.class);
    public static final String ROLENAME = "rolename";
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.OrganizationRole";

    public OrganizationRole() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public OrganizationRole(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public OrganizationRole(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    public OrganizationRole(DataObject obj) {
        super(obj);
    }

    public OrganizationRole(String type) {
        super(type);
    }

    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    //Accessors
    public String getRolename() {
        return (String) get(ROLENAME);
    }

    public void setRolename(String rolename) {
        set(ROLENAME, rolename);
    }

    @Override
    public ContentSection getContentSection() {
        ContentSection ct = super.getContentSection();

        if (ct != null) {
            return ct;
        } else {
            ACSObject parent = getParent();
            if ((parent != null) && parent instanceof GenericOrganization) {
                ct = ((ContentItem) parent).getContentSection();
                return ct;
            }
        }

        return null;
    }
}
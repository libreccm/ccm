package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import org.apache.log4j.Logger;

import java.math.BigDecimal;


/**
 * ContentItem reprenting a role in a organization, e.g. CEO.
 * 
 * @author Jens Pelzetter
 */
public class OrganizationRole extends ContentPage {

    private static final Logger logger = Logger.getLogger(OrganizationRole.class);
    /**
     * Name of the role.
     */
    public static final String ROLENAME = "rolename";
    /**
     * Type identifier.
     */
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.OrganizationRole";

    /**
     * Default Constructor
     */
    public OrganizationRole() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Trys to find the role with the given id in the database.
     *
     * @param id of the role to find.
     * @throws com.arsdigita.domain.DataObjectNotFoundException
     */
    public OrganizationRole(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Trys to find the role with the given id in the database.
     * 
     * @param id
     * @throws com.arsdigita.domain.DataObjectNotFoundException
     */
    public OrganizationRole(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    /**
     *  Creates an OrganizationRole object based on a DataObject
     *
     * @param obj A object of DataObject class.
     */
    public OrganizationRole(DataObject obj) {
        super(obj);
    }

    /**
     *
     * @param type
     */
    public OrganizationRole(String type) {
        super(type);
    }

    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    //Accessors

    /**
     *
     * @return The name of the role.
     */
    public String getRolename() {
        return (String) get(ROLENAME);
    }

    /**
     * Sets the name of the role.
     *
     * @param rolename New name of the role.
     */
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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.runtime.DomainInitEvent;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrganizationRoleInitializer extends ContentTypeInitializer {

    private final static Logger logger = Logger.getLogger(OrganizationRoleInitializer.class);

    public OrganizationRoleInitializer() {
        super("ccm-cms-types-organizationrole.pdl.mf", OrganizationRole.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public void init(DomainInitEvent evt) {
        super.init(evt);
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{"/static/content-types/com/arsdigita/cms/contenttypes/OrganizationRole.xsl"};
    }
}

package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciInstitute;
import com.arsdigita.domain.DataObjectNotFoundException;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciInstituteMemberAddForm
        extends GenericOrganizationalUnitPersonAddForm {

    private final Logger logger = Logger.getLogger(
            SciInstituteMemberAddForm.class);

    public SciInstituteMemberAddForm(
            final ItemSelectionModel itemModel,
            final GenericOrganizationalUnitPersonSelector personSelector) {
        super(itemModel, personSelector);
    }

    @Override
    protected String getPersonType() {
        String personType = SciInstitute.getConfig().getPermittedPersonType();

        try {
            ContentType.findByAssociatedObjectType(personType);
        } catch (DataObjectNotFoundException ex) {
            logger.error(String.format("No content type for object type '%s'. "
                                       + "Falling back to '%s'.",
                                       personType,
                                       GenericPerson.class.getName()),
                         ex);
            personType = GenericPerson.class.getName();
        }

        return personType;
    }
    
     @Override
    protected String getRoleAttributeName() {
        return SciInstitute.ROLE_ENUM_NAME;
    }
}

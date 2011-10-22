package com.arsdigita.cms.contenttypes.ui;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectMembersTab extends GenericOrgaUnitMembersTab {

    @Override
    protected String getXmlElementName() {
        return "members";
    }

    @Override
    protected boolean isMergingMembers() {
        return true;
    }

    @Override
    protected List<String> getAssocTypesToMerge() {
        final List<String> assocTypes = new ArrayList<String>();
        assocTypes.add(SciProjectSubProjectsStep.ASSOC_TYPE);
        return assocTypes;        
    }

    @Override
    protected int getPageSize() {
        return 25;
    }

    @Override
    protected List<String> getRolesToInclude() {
        final List<String> roles = new ArrayList<String>();        
        return roles;
    }

    @Override
    protected List<String> getStatusesToInclude() {
        final List<String> statuses = new ArrayList<String>();        
        return statuses;
    }            
}

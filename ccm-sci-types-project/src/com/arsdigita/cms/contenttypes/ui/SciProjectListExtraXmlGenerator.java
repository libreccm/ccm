package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciProjectListExtraXmlGenerator implements ExtraXMLGenerator {

   
    
    @Override
    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        if (!(item instanceof SciProject)) {
            throw new IllegalArgumentException(
                    "This ExtraXMLGenerator supports items of type "
                    + "'com.arsdigita.cms.contenttypes.SciProject' only.");
        }

        final SciProject project = (SciProject) item;
        final GenericOrganizationalUnitPersonCollection members = project.
                getPersons();

        final Element membersElem = element.newChildElement("members");
        while (members.next()) {
            generateMemberXml(membersElem, 
                              members.getPerson(), 
                              members.getRoleName());            
        }

    }

    @Override
    public void addGlobalStateParams(final Page page) {
        //Nothing for now
    }

    @Override
    public void setListMode(final boolean listMode) {
        //nothing
    }
    
    private void generateMemberXml(final Element membersElem,
                                   final GenericPerson member,
                                   final String roleName) {
        final Element memberElem = membersElem.newChildElement("member");
        memberElem.addAttribute("role", roleName);
        
        final Element surnameElem = memberElem.newChildElement("surname");
        surnameElem.setText(member.getSurname());
        final Element givenNameElem = memberElem.newChildElement("givenName");
        givenNameElem.setText(member.getGivenName());
        final Element titlePreElem = memberElem.newChildElement("titlePre");
        titlePreElem.setText(member.getTitlePre());
        final Element titlePostElem = memberElem.newChildElement("titlePost");
        titlePostElem.setText(member.getTitlePost());
    }
}

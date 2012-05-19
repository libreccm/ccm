package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.SciProjectConfig;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectExtraXmlGenerator
        extends GenericOrgaUnitExtraXmlGenerator {

    private boolean listMode = false;

    @Override
    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        super.generateXML(item, element, state);

        if (listMode) {
            if (!(item instanceof SciProject)) {
                throw new IllegalArgumentException(
                        "This ExtraXMLGenerator supports items of type "
                        + "'com.arsdigita.cms.contenttypes.SciProject' only.");
            }

            final SciProject project = (SciProject) item;
            final GenericOrganizationalUnitPersonCollection members = project.getPersons();

            final Element membersElem = element.newChildElement("members");
            while (members.next()) {
                generateMemberXml(membersElem,
                                  members.getPerson(),
                                  members.getRoleName());
            }
        }
    }

    @Override
    public String getTabConfig() {
        final SciProjectConfig config = SciProject.getConfig();
        return config.getTabs();
    }

    @Override
    public void setListMode(final boolean listMode) {
        super.setListMode(listMode);
        this.listMode = listMode;
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

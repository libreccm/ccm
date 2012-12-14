package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.SciProjectConfig;
import com.arsdigita.xml.Element;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

        if (!(item instanceof SciProject)) {
            throw new IllegalArgumentException(
                    "This ExtraXMLGenerator supports items of type "
                    + "'com.arsdigita.cms.contenttypes.SciProject' only.");
        }

        final SciProject project = (SciProject) item;
        final Element finished = element.newChildElement("finished");
        finished.setText(getFinishedValue(project));

        if (listMode) {

            final GenericOrganizationalUnitPersonCollection members = project.getPersons();

            final Element membersElem = element.newChildElement("members");
            while (members.next()) {
                generateMemberXml(membersElem,
                                  members.getPerson(),
                                  members.getRoleName(),
                                  members.getStatus());
            }
        }
    }

    private String getFinishedValue(final SciProject project) {
        final Date endDate = project.getEnd();
        if (endDate == null) {
            return "false";
        }

        final Calendar endCal = new GregorianCalendar();
        endCal.setTime(endDate);
        final int endYear = endCal.get(Calendar.YEAR);
        final int endMonth = endCal.get(Calendar.MONTH);
        final int endDay = endCal.get(Calendar.DAY_OF_MONTH);

        final Calendar nowCal = new GregorianCalendar();
        final int nowYear = nowCal.get(Calendar.YEAR);
        final int nowMonth = nowCal.get(Calendar.MONTH);
        final int nowDay = nowCal.get(Calendar.DAY_OF_MONTH);

        if (nowYear > endYear) {
            return "true";
        } else if (nowMonth > endMonth) {
            return "true";
        } else if (nowDay > endDay) {
            return "true";
        } else {
            return "false";
        }
    }

    private void createFinishedElem(final SciProject project, final Element parent) {
        final Element finished = parent.newChildElement("finished");
        final Date endDate = project.getEnd();

        if (endDate == null) {
            finished.setText("false");
            return;
        }

        final Calendar endCal = new GregorianCalendar();
        endCal.setTime(endDate);
        final Date end = new GregorianCalendar(endCal.get(Calendar.YEAR),
                                               endCal.get(Calendar.MONTH),
                                               endCal.get(Calendar.DAY_OF_MONTH)).getTime();
        final Date todayDate = Calendar.getInstance().getTime();

        if (todayDate.before(end)) {
            finished.setText("false");
        } else {
            finished.setText("true");
        }
        /*if (end.after(todayDate)) {
         finished.setText("true");
         } else {
         finished.setText("false");
         }*/
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
                                   final String roleName,
                                   final String status) {
        final Element memberElem = membersElem.newChildElement("member");
        memberElem.addAttribute("role", roleName);
        memberElem.addAttribute("status", status);

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

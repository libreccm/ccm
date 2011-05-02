/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.SciOrganizationConfig;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.SciProjectSubProjectsCollection;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.xml.Element;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class SciProjectPanel extends SciOrganizationBasePanel {

    private static final Logger s_log = Logger.getLogger(SciProjectPanel.class);
    public static final String SHOW_DESCRIPTION = "description";
    public static final String SHOW_SUBPROJECTS = "subprojects";
    public static final String SHOW_SUBPROJECTS_ONGOING = "subprojectsOngoing";
    public static final String SHOW_SUBPROJECTS_FINISHED = "subprojectsFinished";
    public static final String SHOW_PUBLICATIONS = "publications";
    private boolean displayDescription = true;
    private boolean displaySubProjects = true;
    private boolean displayPublications = true;

    @Override
    protected String getDefaultForShowParam() {
        return SHOW_DESCRIPTION;
    }

    @Override
    protected Class<? extends ContentItem> getAllowedClass() {
        return SciProject.class;
    }

    public boolean isDisplayDescription() {
        return displayDescription;
    }

    public void setDisplayDescription(boolean displayDescription) {
        this.displayDescription = displayDescription;
    }

    public boolean isDisplayPublications() {
        return displayPublications;
    }

    public void setDisplayPublications(boolean displayPublications) {
        this.displayPublications = displayPublications;
    }

    public boolean isDisplaySubProjects() {
        return displaySubProjects;
    }

    public void setDisplaySubProjects(boolean displaySubProjects) {
        this.displaySubProjects = displaySubProjects;
    }

    protected boolean hasMembers(final SciProject project) {
        return project.hasMembers(SciProject.getConfig().
                getOrganizationMembersMerge(),
                                  SciProject.MemberStatus.ALL);
    }

    protected boolean hasActiveMembers(final SciProject project) {
        return project.hasMembers(SciProject.getConfig().
                getOrganizationMembersMerge(),
                                  SciProject.MemberStatus.ACTIVE);
    }

    protected boolean hasAssociatedMembers(final SciProject project) {
        return project.hasMembers(SciProject.getConfig().
                getOrganizationMembersMerge(),
                                  SciProject.MemberStatus.ASSOCIATED);
    }

    protected boolean hasFormerMembers(final SciProject project) {
        return project.hasMembers(SciProject.getConfig().
                getOrganizationMembersMerge(),
                                  SciProject.MemberStatus.FORMER);
    }

    protected boolean hasSubProjects(final SciProject project) {
        return project.hasSubProjects();        
    }
        
    protected void generateSubProjectsXML(final SciProject project,
                                          final Element parent,
                                          final PageState state,
                                          final List<String> filters) {
        SciProjectSubProjectsCollection subProjects;
        subProjects = project.getSubProjects();
        for (String filter : filters) {
            subProjects.addFilter(filter);
        }
        subProjects.addOrder("begin desc, end desc");

        long pageNumber = getPageNumber(state);

        Element subProjectsElem = parent.newChildElement("subProjects");

        long pageCount = getPageCount(subProjects.size());
        long begin = getPaginatorBegin(pageNumber);
        long count = getPaginatorCount(begin, subProjects.size());
        long end = getPaginatorEnd(begin, count);
        pageNumber = normalizePageNumber(pageCount, pageNumber);

        createPaginatorElement(parent, pageNumber, pageCount, begin, end, count,
                               subProjects.size());
        subProjects.setRange((int) begin, (int) end);


        while (subProjects.next()) {
            SciProject subProject;
            subProject = subProjects.getSubProject();

            generateProjectXML(subProject, subProjectsElem, state);
        }
    }

    protected void mergeMembers(
            final SciProjectSubProjectsCollection subProjects,
            final List<MemberListItem> members,
            final boolean active,
            final boolean associated,
            final boolean former) {
        while (subProjects.next()) {
            SciProject subProject = subProjects.getSubProject();
            GenericOrganizationalUnitPersonCollection projectMembers;
            projectMembers = subProject.getPersons();

            while (projectMembers.next()) {
                addMember(projectMembers.getPerson(),
                          projectMembers.getRoleName(),
                          projectMembers.getStatus(),
                          members);
            }

            SciProjectSubProjectsCollection subSubProjects;
            subSubProjects = subProject.getSubProjects();
            if ((subSubProjects != null) && (subSubProjects.size() > 0)) {
                mergeMembers(subProjects, members, active, associated, former);
            }
        }
    }

    protected void generateMembersXML(final SciProject project,
                                      final Element parent,
                                      final PageState state,
                                      final boolean active,
                                      final boolean associated,
                                      final boolean former) {
        GenericOrganizationalUnitPersonCollection projectMembers;
        projectMembers = project.getPersons();

        List<MemberListItem> members;
        members = new LinkedList<MemberListItem>();

        while (projectMembers.next()) {
            addMember(projectMembers.getPerson(),
                      projectMembers.getRoleName(),
                      projectMembers.getStatus(),
                      members);
        }

        if (SciProject.getConfig().getProjectMembersMerge()) {

            SciProjectSubProjectsCollection subProjects;
            subProjects = project.getSubProjects();

            mergeMembers(subProjects, members, active, associated, former);
        }

        generateMembersListXML(members, parent, state);
    }

    @Override
    public void generateXML(ContentItem item,
                            Element element,
                            PageState state) {
        Element content = generateBaseXML(item, element, state);

        Element availableData = content.newChildElement("availableData");

        SciProject project = (SciProject) item;

        SciOrganizationConfig config = SciProject.getConfig();

        if ((project.getProjectDescription() != null)
            && !project.getProjectDescription().isEmpty()
            && displayDescription) {
            availableData.newChildElement("description");
        }
        if (project.hasContacts()
            && isDisplayContacts()) {
            availableData.newChildElement("contacts");
        }
        if (hasSubProjects(project)
            && displaySubProjects) {
            availableData.newChildElement("subProjects");
        }
        if (config.getProjectMembersAllInOne()) {
            if (hasMembers(project)
                && isDisplayMembers()) {
                availableData.newChildElement("members");
            }
        } else {
            if (hasActiveMembers(project)
                && isDisplayMembers()) {
                availableData.newChildElement("activeMembers");
            }
            if (hasAssociatedMembers(project)
                && isDisplayMembers()) {
                availableData.newChildElement("associatedMembers");
            }
            if (hasFormerMembers(project)
                && isDisplayMembers()) {
                availableData.newChildElement("formerMembers");
            }
        }
        if (project.hasPublications()
                && displayPublications) {
            availableData.newChildElement("publications");
        }         

        String show = getShowParam(state);

        if (SHOW_DESCRIPTION.equals(show)) {
            Element description = content.newChildElement("description");
            description.setText(project.getProjectDescription());

            Element funding = content.newChildElement("funding");
            funding.setText(project.getFunding());
        } else if (SHOW_CONTACTS.equals(show)) {
            generateContactsXML(project, content, state);
        } else if (SHOW_SUBPROJECTS.equals(show)) {
            generateSubProjectsXML(project, content, state,
                                   new LinkedList<String>());
        } else if (SHOW_SUBPROJECTS_ONGOING.equals(show)) {
            generateSubProjectsXML(project, content, state,
                                   getFiltersForOngoingProjects());
        } else if (SHOW_SUBPROJECTS_FINISHED.equals(show)) {
            generateSubProjectsXML(project, content, state,
                                   getFiltersForFinishedProjects());
        } else if (SHOW_MEMBERS.equals(show)) {
            generateMembersXML(project, content, state, true, true, true);
        } else if (SHOW_PUBLICATIONS.equals(show)) {
            generatePublicationsXML(
                    RelatedLink.getRelatedLinks(item,
                                                "SciProjectPublications"),
                    content,
                    state);
        }
    }
}

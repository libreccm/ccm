package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.xml.Element;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter (jensp)
 * @version $Id$
 */
public class PersonalProjects implements ContentGenerator {

    private final static String CURRENT_PROJECTS = "currentProjects";
    private final static String FINISHED_PROJECTS = "finishedProjects";
    private final static PersonalProjectsConfig config =
                                                new PersonalProjectsConfig();
    private final static Logger logger =
                                Logger.getLogger(PersonalProjects.class);

    static {
        config.load();
    }

    public void generateContent(final Element parent,
                                final GenericPerson person,
                                final PageState state) {
        final List<SciProject> projects = collectProjects(person);


        if ((projects == null) || projects.size() == 0) {
            final Element projectsElem = parent.newChildElement("projects");
            projectsElem.newChildElement("noProjects");

            return;
        } else {
            final List<SciProject> currentProjects = new ArrayList<SciProject>();
            final List<SciProject> finishedProjects =
                                   new ArrayList<SciProject>();

            processProjects(projects, currentProjects, finishedProjects);
            generateGroupsXml(parent, currentProjects, finishedProjects);
            generateProjectsXml(parent,
                                currentProjects,
                                finishedProjects,
                                state);
        }
    }

    private List<SciProject> collectProjects(final GenericPerson person) {
        final List<SciProject> projects = new ArrayList<SciProject>();

        final DataCollection collection = (DataCollection) person.get(
                "organizationalunit");
        DomainObject obj;
        while (collection.next()) {
            obj = DomainObjectFactory.newInstance(collection.getDataObject());
            if (obj instanceof SciProject) {
                projects.add((SciProject) obj);
            }
        }

        if (person.getAlias() != null) {
            collectProjects(person.getAlias(), projects);

        }


        return projects;
    }

    private void collectProjects(final GenericPerson alias,
                                 final List<SciProject> projects) {
        final DataCollection collection = (DataCollection) alias.get(
                "organizationalunit");
        DomainObject obj;
        while (collection.next()) {
            obj = DomainObjectFactory.newInstance(collection.getDataObject());
            if (obj instanceof SciProject) {
                projects.add((SciProject) obj);
            }
        }

        if (alias.getAlias() != null) {
            collectProjects(alias.getAlias(), projects);

        }
    }

    private void processProjects(final List<SciProject> projects,
                                 final List<SciProject> currentProjects,
                                 final List<SciProject> finishedProjects) {
        final Calendar today = new GregorianCalendar();
        final Date todayDate = today.getTime();       
        for(SciProject project : projects) {                        
            if (project.getEnd().before(todayDate)) {
                finishedProjects.add(project);
            } else {
                currentProjects.add(project);
            }
        }

        final ProjectComparator comparator = new ProjectComparator();
        Collections.sort(currentProjects, comparator);
        Collections.sort(finishedProjects, comparator);
    }

    private void generateGroupsXml(final Element parent,
                                   final List<SciProject> currentProjects,
                                   final List<SciProject> finishedProjects) {
        final Element availableGroups = parent.newChildElement(
                "availableProjectGroups");

        if (currentProjects.size() > 0) {
            createAvailableProjectGroupXml(availableGroups, CURRENT_PROJECTS);
        }

        if (finishedProjects.size() > 0) {
            createAvailableProjectGroupXml(availableGroups, FINISHED_PROJECTS);
        }
    }

    private void createAvailableProjectGroupXml(final Element parent,
                                                final String name) {
        final Element group = parent.newChildElement("availableProjectGroup");
        group.addAttribute("name", name);
    }

    private void generateProjectsXml(final Element parent,
                                     final List<SciProject> currentProjects,
                                     final List<SciProject> finishedProjects,
                                     final PageState state) {
        final Element projectsElem = parent.newChildElement("projects");

        final int numberOfProjects = currentProjects.size()
                                     + finishedProjects.size();
        final int groupSplit = config.getGroupSplit();

        if (numberOfProjects < groupSplit) {
            projectsElem.addAttribute("all", "all");

            generateProjectsGroupXml(projectsElem,
                                     CURRENT_PROJECTS,
                                     currentProjects,
                                     state);
            generateProjectsGroupXml(projectsElem,
                                     FINISHED_PROJECTS,
                                     finishedProjects,
                                     state);
        } else {
            final HttpServletRequest request = state.getRequest();

            String groupToShow = request.getParameter("group");
            if (groupToShow == null) {
                groupToShow = CURRENT_PROJECTS;
            }

            if (currentProjects.isEmpty()
                && CURRENT_PROJECTS.equals(groupToShow)) {
                groupToShow = FINISHED_PROJECTS;
            }

            if (CURRENT_PROJECTS.equals(groupToShow)) {
                generateProjectsGroupXml(projectsElem,
                                         CURRENT_PROJECTS,
                                         currentProjects,
                                         state);
            } else if (FINISHED_PROJECTS.equals(groupToShow)) {
                generateProjectsGroupXml(projectsElem,
                                         FINISHED_PROJECTS,
                                         finishedProjects,
                                         state);
            }
        }

    }

    private void generateProjectsGroupXml(final Element projectsElem,
                                          final String groupName,
                                          final List<SciProject> projects,
                                          final PageState state) {
        if (projects == null) {
            return;
        }

        final Element groupElem = projectsElem.newChildElement("projectGroup");
        groupElem.addAttribute("name", groupName);

        for (SciProject project : projects) {
            generateProjectXml(groupElem, project, state);
        }
    }

    private void generateProjectXml(final Element projectGroupElem,
                                    final SciProject project,
                                    final PageState state) {
        final PublicPersonalProfileXmlGenerator generator =
                                                new PublicPersonalProfileXmlGenerator(
                project);
        generator.generateXML(state, projectGroupElem, "");
    }

    private class ProjectComparator implements Comparator<SciProject> {

        public int compare(final SciProject project1,
                           final SciProject project2) {
            return project1.getTitle().compareTo(project2.getTitle());
        }
    }
}

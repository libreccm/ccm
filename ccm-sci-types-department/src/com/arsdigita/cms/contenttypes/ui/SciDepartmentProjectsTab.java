package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.ui.panels.CompareFilter;
import com.arsdigita.cms.contenttypes.ui.panels.Paginator;
import com.arsdigita.cms.contenttypes.ui.panels.TextFilter;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentProjectsTab implements GenericOrgaUnitTab {

    private final Logger logger = Logger.getLogger(
            SciDepartmentProjectsTab.class);
    private static final SciDepartmentProjectsTabConfig config =
                                                        new SciDepartmentProjectsTabConfig();
    private static final String STATUS_PARAM = "projectStatus";
    private static final String TITLE_PARAM = "projectTitle";
    private final CompareFilter statusFilter = new CompareFilter(STATUS_PARAM,
                                                                 "projectEnd",
                                                                 false,
                                                                 false,
                                                                 true);
    private final TextFilter titleFilter = new TextFilter(TITLE_PARAM,
                                                          ContentPage.TITLE);

    static {
        config.load();
    }

    public boolean hasData(final GenericOrganizationalUnit orgaunit) {
        final long start = System.currentTimeMillis();
        final ContentTypeCollection types = ContentType.getAllContentTypes();
        types.addFilter(
                "associatedObjectType = 'com.arsdigita.cms.contenttypes.SciProject'");

        if (types.size() == 0) {
            return false;
        }
        types.close();

        final boolean result = !getData(orgaunit).isEmpty();

        logger.debug(String.format("Needed %d ms to determine if department "
                                   + "'%s' has projects.",
                                   System.currentTimeMillis() - start,
                                   orgaunit.getName()));

        return result;
    }

    public void generateXml(final GenericOrganizationalUnit orgaunit,
                            final Element parent,
                            final PageState state) {
        final long start = System.currentTimeMillis();
        final DataQuery projects = getData(orgaunit);
        final HttpServletRequest request = state.getRequest();

        final Element depProjectsElem = parent.newChildElement(
                "departmentProjects");
        final Element filtersElem = depProjectsElem.newChildElement(
                "filters");

        statusFilter.generateXml(filtersElem);

        if (((request.getParameter(STATUS_PARAM) == null)
             || (request.getParameter(STATUS_PARAM).trim().isEmpty()))
            && ((request.getParameter(TITLE_PARAM) == null)
                || request.getParameter(TITLE_PARAM).trim().isEmpty())) {

            depProjectsElem.newChildElement("greeting");

            projects.addOrder("projectBegin");
            projects.addOrder("title");

            projects.setRange(1, config.getGreetingSize() + 1);

            titleFilter.generateXml(filtersElem);

        } else {
            projects.addOrder("title");


            applyStatusFilter(projects, request);
            applyTitleFilter(projects, request);

            final Paginator paginator = new Paginator(request,
                                                      (int) projects.size(),
                                                      config.getPageSize());

            if (paginator.getPageCount() > config.getEnableSearchLimit()) {
                titleFilter.generateXml(filtersElem);
            }

            paginator.applyLimits(projects);
            paginator.generateXml(depProjectsElem);
        }

        while (projects.next()) {
            generateProjectXml((BigDecimal) projects.get("projectId"),
                               depProjectsElem,
                               state);
        }

        logger.debug(String.format("Generated projects list of department '%s' "
                                   + "in %d ms.",
                                   orgaunit.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected DataQuery getData(final GenericOrganizationalUnit orgaunit) {
        final long start = System.currentTimeMillis();

        if (!(orgaunit instanceof SciDepartment)) {
            throw new IllegalArgumentException(String.format(
                    "This tab can only process instances of "
                    + "'com.arsdigita.cms.contenttypes.SciDepartment'. Provided "
                    + "object is of type '%s'",
                    orgaunit.getClass().getName()));
        }

        final DataQuery projectsQuery = SessionManager.getSession().
                retrieveQuery(
                "com.arsdigita.cms.contenttypes. getIdsOfProjectsOfOrgaUnit");
        final StringBuffer projectsFilter = new StringBuffer();

        if (config.isMergingProjects()) {
            final DataQuery subDepartmentsQuery =
                            SessionManager.getSession().retrieveQuery(
                    "com.arsdigita.cms.contenttypes.getIdsOfSubordinateOrgaUnitsRecursivlyWithAssocType");
            subDepartmentsQuery.setParameter("orgaunitId",
                                             orgaunit.getID().toString());
            subDepartmentsQuery.setParameter("assocType",
                                             SciDepartmentSubDepartmentsStep.ASSOC_TYPE);

            while (subDepartmentsQuery.next()) {
                if (projectsFilter.length() > 0) {
                    projectsFilter.append(" or ");
                }
                projectsFilter.append(String.format("orgaunitId = %s",
                                                    subDepartmentsQuery.get(
                        "orgaunitId").toString()));
            }
        } else {
            projectsFilter.append(String.format("orgaunitId = %s",
                                                orgaunit.getID().toString()));
        }

        projectsQuery.addFilter(projectsFilter.toString());

        logger.debug(String.format(
                "Got projects of department '%s'"
                + "in '%d ms'. MergeProjects is set to '%b'.",
                orgaunit.getName(),
                System.currentTimeMillis() - start,
                config.isMergingProjects()));
        return projectsQuery;
    }

    private void applyStatusFilter(final DataQuery projects,
                                   final HttpServletRequest request) {
        final String statusValue = request.getParameter(STATUS_PARAM);
        if ((statusValue != null) && !(statusValue.trim().isEmpty())) {
            statusFilter.setValue(statusValue);
        }

        projects.addFilter(statusFilter.getFilter());
    }

    private void applyTitleFilter(final DataQuery projects,
                                  final HttpServletRequest request) {
        final String titleValue = request.getParameter(TITLE_PARAM);
        if ((titleValue != null) && !(titleValue.trim().isEmpty())) {
            titleFilter.setValue(titleValue);
        }

        projects.addFilter(titleFilter.getFilter());
    }

    private void generateProjectXml(final BigDecimal projectId,
                                    final Element parent,
                                    final PageState state) {
        final long start = System.currentTimeMillis();
        final ContentPage project = (ContentPage) DomainObjectFactory.
                newInstance(new OID(
                "com.arsdigita.cms.contenttypes.SciProject", projectId));
        logger.debug(String.format("Got domain object for project '%s' "
                                   + "in %d ms.",
                                   project.getName(),
                                   System.currentTimeMillis() - start));
        generateProjectXml(project, parent, state);
    }

    private void generateProjectXml(final ContentPage project,
                                    final Element parent,
                                    final PageState state) {
        final long start = System.currentTimeMillis();
        final XmlGenerator generator = new XmlGenerator(project);
        generator.generateXML(state, parent, "");
        logger.debug(String.format("Generated XML for project '%s' in %d ms.",
                                   project.getName(),
                                   System.currentTimeMillis() - start));
    }

    private class XmlGenerator extends SimpleXMLGenerator {

        private final ContentItem item;

        public XmlGenerator(final ContentItem item) {
            super();
            this.item = item;
        }

        @Override
        protected ContentItem getContentItem(final PageState state) {
            return item;
        }
    }
}
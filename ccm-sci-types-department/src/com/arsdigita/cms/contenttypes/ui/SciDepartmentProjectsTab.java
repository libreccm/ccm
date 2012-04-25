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
import com.arsdigita.globalization.Globalization;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
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
                                                                 false,
                                                                 true);
    private final TextFilter titleFilter = new TextFilter(TITLE_PARAM,
                                                          ContentPage.TITLE);

    static {
        config.load();
    }

    public SciDepartmentProjectsTab() {
        final Calendar now = new GregorianCalendar();
        final String today = String.format("%d-%02d-%02d",
                                           now.get(Calendar.YEAR),
                                           now.get(Calendar.MONTH) + 1,
                                           now.get(Calendar.DAY_OF_MONTH));

        statusFilter.addOption("currentProjects",
                               CompareFilter.Operators.GTEQ,
                               today,
                               true);
        statusFilter.addOption("finishedProjects",
                               CompareFilter.Operators.LT,
                               today,
                               false);
    }

    @Override
    public boolean hasData(final GenericOrganizationalUnit orgaunit,
                           final PageState state) {
        final long start = System.currentTimeMillis();

        //Check if SciProject content type is installed
        final ContentTypeCollection types = ContentType.getAllContentTypes();
        types.addFilter(
                "associatedObjectType = 'com.arsdigita.cms.contenttypes.SciProject'");

        if (types.size() == 0) {
            return false;
        }
        types.close();

        //Check if we have projects to show
        final DataCollection data = getData(orgaunit);
        final boolean result =  (data != null) && !data.isEmpty();

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
        final DataCollection projects = getData(orgaunit);
        final HttpServletRequest request = state.getRequest();

        final Element depProjectsElem = parent.newChildElement(
                "departmentProjects");
        final Element filtersElem = depProjectsElem.newChildElement(
                "filters");

        projects.addOrder("case when (projectBegin is null) "
                          + "then '0001-01-01' "
                          + "else projectBegin "
                          + "end desc");
        projects.addOrder("case when (projectEnd is null) "
                          + "then '0001-01-01' "
                          + "else projectEnd "
                          + "end desc");
        projects.addOrder("title asc");

        if (((Globalization.decodeParameter(request, STATUS_PARAM) == null)
             || Globalization.decodeParameter(request, STATUS_PARAM).trim().
             isEmpty()
             || CompareFilter.NONE.equals(
             Globalization.decodeParameter(request, STATUS_PARAM)))
            && ((Globalization.decodeParameter(request, TITLE_PARAM) == null)
                || Globalization.decodeParameter(request, TITLE_PARAM).trim().
                isEmpty())) {

            statusFilter.generateXml(filtersElem);

            depProjectsElem.newChildElement("greeting");

            final Calendar now = new GregorianCalendar();
            final String today = String.format("%d-%02d-%02d",
                                               now.get(Calendar.YEAR),
                                               now.get(Calendar.MONTH) + 1,
                                               now.get(Calendar.DAY_OF_MONTH));

            projects.addFilter(String.format(
                    "(projectEnd >= '%s') or (projectEnd is null)", today));
            projects.setRange(1, config.getGreetingSize() + 1);

            titleFilter.generateXml(filtersElem);

        } else {           
            applyStatusFilter(projects, request);
            applyTitleFilter(projects, request);

            statusFilter.generateXml(filtersElem);

            if (projects.isEmpty()) {
                titleFilter.generateXml(filtersElem);

                depProjectsElem.newChildElement("noProjects");

                return;
            } else {
                final Paginator paginator = new Paginator(request,
                                                          (int) projects.size(),
                                                          config.getPageSize());

                if ((paginator.getPageCount() > config.getEnableSearchLimit())
                    || ((Globalization.decodeParameter(request, TITLE_PARAM)
                         != null)
                        || !(Globalization.decodeParameter(request, TITLE_PARAM).
                             trim().isEmpty()))) {
                    titleFilter.generateXml(filtersElem);
                }

                paginator.applyLimits(projects);
                paginator.generateXml(depProjectsElem);
            }
        }

        while (projects.next()) {
            generateProjectXml((BigDecimal) projects.get("id"),
                               depProjectsElem,
                               state);
        }

        logger.debug(String.format("Generated projects list of department '%s' "
                                   + "in %d ms.",
                                   orgaunit.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected DataCollection getData(final GenericOrganizationalUnit orgaunit) {
        final long start = System.currentTimeMillis();
                    
        if (!(orgaunit instanceof SciDepartment)) {
            throw new IllegalArgumentException(String.format(
                    "This tab can only process instances of "
                    + "'com.arsdigita.cms.contenttypes.SciDepartment'. Provided "
                    + "object is of type '%s'",
                    orgaunit.getClass().getName()));
        }

        final DataQuery projectBundlesQuery = SessionManager.getSession().
                retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfProjectsOfOrgaUnit");
        final List<String> orgaunitIds = new ArrayList<String>();

        if (config.isMergingProjects()) {
            final DataQuery subDepartmentsQuery =
                            SessionManager.getSession().retrieveQuery(
                    "com.arsdigita.cms.contenttypes.getIdsOfSubordinateOrgaUnitsRecursivlyWithAssocType");
            subDepartmentsQuery.setParameter("orgaunitId",
                                             orgaunit.getContentBundle().getID().toString());
            subDepartmentsQuery.setParameter("assocType",
                                             SciDepartmentSubDepartmentsStep.ASSOC_TYPE);

            while (subDepartmentsQuery.next()) {
                orgaunitIds.add(subDepartmentsQuery.get("orgaunitId").toString());
            }
        } else {
            orgaunitIds.add(orgaunit.getID().toString());
        }
        projectBundlesQuery.setParameter("orgaunitIds", orgaunitIds);
                      
        final StringBuilder filterBuilder = new StringBuilder();
        while (projectBundlesQuery.next()) {
            if (filterBuilder.length() > 0) {
                filterBuilder.append(",");
            }
            filterBuilder.append(projectBundlesQuery.get("projectId").toString());
        }
        final DataCollection projectsQuery = SessionManager.getSession().retrieve(
                "com.arsdigita.cms.contenttypes.SciProject");

        if (filterBuilder.length() == 0) {
            //No Projects, return null to indicate
            return null;
        } else {
            projectsQuery.addFilter(String.format("parent.id in (%s)", filterBuilder.toString()));
        }        

        if (Kernel.getConfig().languageIndependentItems()) {
            final FilterFactory filterFactory = projectsQuery.getFilterFactory();
            final Filter filter = filterFactory.or().
                    addFilter(filterFactory.equals("language", GlobalizationHelper.getNegotiatedLocale().getLanguage())).
                    addFilter(filterFactory.and().
                    addFilter(filterFactory.equals("language", GlobalizationHelper.LANG_INDEPENDENT)).
                    addFilter(filterFactory.notIn("parent", "com.arsdigita.navigation.getParentIDsOfMatchedItems").set(
                    "language", GlobalizationHelper.getNegotiatedLocale().getLanguage())));
            projectsQuery.addFilter(filter);
        } else {
            projectsQuery.addEqualsFilter("language", GlobalizationHelper.getNegotiatedLocale().getLanguage());
        }

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
        final String statusValue = Globalization.decodeParameter(request,
                                                                 STATUS_PARAM);
        if ((statusValue != null) && !(statusValue.trim().isEmpty())) {
            statusFilter.setValue(statusValue);
        }

        if ((statusFilter.getFilter() != null)
            && !(statusFilter.getFilter().trim().isEmpty())) {
            projects.addFilter(statusFilter.getFilter());
        }
    }

    private void applyTitleFilter(final DataQuery projects,
                                  final HttpServletRequest request) {
        final String titleValue = Globalization.decodeParameter(request,
                                                                TITLE_PARAM);
        if ((titleValue != null) && !(titleValue.trim().isEmpty())) {
            titleFilter.setValue(titleValue);
        }

        if ((titleFilter.getFilter() != null)
            && !(titleFilter.getFilter().trim().isEmpty())) {
            projects.addFilter(titleFilter.getFilter());
        }
    }

    private void generateProjectXml(final BigDecimal projectId,
                                    final Element parent,
                                    final PageState state) {
        final long start = System.currentTimeMillis();
        final ContentPage project = (ContentPage) DomainObjectFactory.newInstance(new OID(
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
        generator.setItemElemName("project", "");
        generator.setUseExtraXml(false);
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

package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.xml.Element;
import com.arsdigita.cms.contenttypes.SciMember;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.Session;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.ui.panels.Filter;
import com.arsdigita.cms.contenttypes.ui.panels.TextFilter;
import com.arsdigita.cms.contenttypes.ui.panels.SelectFilter;
import com.arsdigita.cms.contenttypes.ui.panels.CollectionSortField;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 */
public class SciMemberDemoPanel extends CompoundContentItemPanel {

    private static final Logger logger = Logger.getLogger(
            SciMemberDemoPanel.class);
    private static final String SHOW_PUBLICATIONS = "publications";
    private static final String SHOW_PROJECTS = "projects";
    private static final String TITLE = "title";
    private static final String AUTHORS = "authors";
    private static final String YEAR_OF_PUBLICATION = "yearOfPublication";
    private static final String YEAR_ASC = "yearAsc";
    private static final String YEAR_DESC = "yearDesc";
    private static final String TYPE = "type";
    private String show;
    private final Map<String, Filter> filters =
                                      new LinkedHashMap<String, Filter>();
    private final Map<String, CollectionSortField> sortFields =
                                                   new LinkedHashMap<String, CollectionSortField>();
    private String sortByKey;

    public SciMemberDemoPanel() {
        filters.put(TITLE, new TextFilter(TITLE, TITLE));
        filters.put(AUTHORS, new TextFilter(AUTHORS, "authors.surname"));
        SelectFilter yearFilter = new SelectFilter(YEAR_OF_PUBLICATION,
                                                   YEAR_OF_PUBLICATION,
                                                   true,
                                                   true,
                                                   true,
                                                   true);
        filters.put(YEAR_OF_PUBLICATION, yearFilter);
        SelectFilter typeFilter = new SelectFilter(TYPE,
                                                   "objectType",
                                                   false,
                                                   true,
                                                   true,
                                                   false);
        filters.put(TYPE, typeFilter);
        sortFields.put(TITLE,
                       new CollectionSortField(TITLE, "title"));
        sortFields.put(YEAR_ASC,
                       new CollectionSortField(YEAR_ASC, "year asc"));
        sortFields.put(YEAR_DESC,
                       new CollectionSortField(YEAR_DESC, "year desc"));
    }

    @Override
    protected String getDefaultShowParam() {
        return "publications";
    }

    @Override
    protected Class<? extends ContentItem> getAllowedClass() {
        return SciMember.class;
    }

    protected void generatePublicationsXml(final SciMember member,
                                           final Element parent,
                                           final PageState state) {

        Element controls = parent.newChildElement("filterControls");
        controls.addAttribute("customName", "sciOrganizationPublications");
        controls.addAttribute("show", show);

        DataQuery yearQuery =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getAllYearsOfPublicationForAuthor");
        yearQuery.setParameter("author", member.getID());
        ((SelectFilter) filters.get(YEAR_OF_PUBLICATION)).setDataQuery(yearQuery,
                                                                       "yearOfPublication");
        DataQuery typeQuery =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getAllPublicationTypesForAuthor");
        typeQuery.setParameter("author", member.getID());
        ((SelectFilter) filters.get(TYPE)).setDataQuery(typeQuery, "objectType");

        DataCollection publicationsData = (DataCollection) member.get(
                "publication");
        DomainCollection publications = new DomainCollection(publicationsData);
//        publications.addOrder("yearOfPublication desc");

        applyPublicationsFilter(publications, state.getRequest());
        applyPublicationSortFields(publications, state.getRequest());

        long pageNumber = getPageNumber(state);
        long pageCount = getPageCount(publications.size());
        long begin = getPaginatorBegin(pageNumber);
        long count = getPaginatorCount(begin, publications.size());
        long end = getPaginatorEnd(begin, count);
        pageNumber = normalizePageNumber(pageCount, pageNumber);

        generatePublicationFiltersXml(controls);
        generatePublicationSortFieldsXml(controls);
        createPaginatorElement(parent, pageNumber, pageCount, begin, end, count,
                               publications.size());
        System.out.printf("\n\n\npublications.size = %d\n", publications.size());
        if ((publications.size() <= 1) || publications.isEmpty()) {
            return;
        }

        if (publications.size() == 1) {
            publications.setRange((int) begin, (int) end);
        } else {
            publications.setRange((int) begin + 1, (int) end);
        }

        while (publications.next()) {
            /*Publication publication = (Publication) DomainObjectFactory.
            newInstance(publications.getDomainObject().getOID());*/
            PublicationXmlHelper xmlHelper =
                                 new PublicationXmlHelper(parent,
                                                          (Publication) publications.
                    getDomainObject());
            xmlHelper.generateXml();
        }
    }

    protected void generateProjectsXml(final SciMember member,
                                       final Element parent,
                                       final PageState state) {
        /*DataCollection projectsData = (DataCollection) member.get("project");
        DomainCollection projects = new DomainCollection(projectsData);
        projects.addOrder("title");
        
        long pageNumber = getPageNumber(state);
        long pageCount = getPageCount(projects.size());
        long begin = getPaginatorBegin(pageNumber);
        long count = getPaginatorCount(begin, projects.size());
        long end = getPaginatorEnd(begin, count);
        pageNumber = normalizePageNumber(pageCount, pageNumber);
        
        createPaginatorElement(parent, pageNumber, pageCount, begin, end, count,
        projects.size());*/
    }

    protected void generatePublicationFiltersXml(final Element element) {
        final Element filterElement = element.newChildElement("filters");

        for (Map.Entry<String, Filter> filterEntry : filters.entrySet()) {
            filterEntry.getValue().generateXml(filterElement);
        }
    }

    protected void generatePublicationSortFieldsXml(final Element element) {
        final Element sortFieldsElement = element.newChildElement("sortFields");
        sortFieldsElement.addAttribute("sortBy", sortByKey);
        for (Map.Entry<String, CollectionSortField> sortField : sortFields.
                entrySet()) {
            sortField.getValue().generateXml(sortFieldsElement);
        }
    }

    protected void applyPublicationsFilter(final DomainCollection publications,
                                           final HttpServletRequest request) {
        for (Map.Entry<String, Filter> filterEntry : filters.entrySet()) {
            String value = request.getParameter(
                    filterEntry.getValue().getLabel());

            if ((value != null) && !(value.trim().isEmpty())) {
                filterEntry.getValue().setValue(value);
            }
        }

        final StringBuilder filterBuilder = new StringBuilder();
        for (Map.Entry<String, Filter> filterEntry : filters.entrySet()) {
            if ((filterEntry.getValue().getFilter() == null)
                || (filterEntry.getValue().getFilter().isEmpty())) {
                continue;
            }

            if (filterBuilder.length() > 0) {
                filterBuilder.append(" AND ");
            }
            filterBuilder.append(filterEntry.getValue().getFilter());
            logger.debug(String.format("filters: %s", filterBuilder));
            if (filterBuilder.length() > 0) {
                publications.addFilter(filterBuilder.toString());
            }
        }
    }

    protected void applyPublicationSortFields(
            final DomainCollection publications,
            final HttpServletRequest request) {
        sortByKey = request.getParameter("sort");
        if (!sortFields.containsKey(sortByKey)) {
            sortByKey = new ArrayList<String>(sortFields.keySet()).get(0);
        }

        publications.addOrder(sortFields.get(sortByKey).getField());
    }

    protected void generateAvailableDataXml(final SciMember member,
                                            final Element element,
                                            final PageState state) {
        Session session = SessionManager.getSession();
        DataQuery hasPublicationsQuery =
                  session.retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfPublicationsOfSciMember");
        hasPublicationsQuery.setParameter("author", member.getID().toString());

        if (hasPublicationsQuery.size() > 0) {
            element.newChildElement("publications");
        }
        hasPublicationsQuery.close();

        DataQuery hasProjectsQuery = session.retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfProjectsOfSciMember");
        hasProjectsQuery.setParameter("member", member.getID().toString());

        if (hasProjectsQuery.size() > 0) {
            element.newChildElement("projects");
        }
        hasProjectsQuery.close();

    }

    public void generateDataXml(final SciMember member,
                                final Element element,
                                final PageState state) {
        show = getShowParam(state);

        if (SHOW_PUBLICATIONS.equals(show)) {
            generatePublicationsXml(member, element, state);
        } else if (SHOW_PROJECTS.equals(show)) {
            generateProjectsXml(member, element, state);
        }

    }

    @Override
    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        Element content = generateBaseXML(item, element, state);

        SciMember member = (SciMember) item;
        Element availableData = content.newChildElement("availableData");

        if (!(isShowOnlyDefault())) {
            generateAvailableDataXml(member, availableData, state);
        }

        generateDataXml(member, content, state);
    }
}

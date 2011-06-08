/*
 * Copyright (c) 2011 Jens Pelzetter,
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
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.SciDepartmentPublicationsCollection;
import com.arsdigita.cms.contenttypes.SciDepartmentSubDepartmentsCollection;
import com.arsdigita.cms.contenttypes.SciDepartmentWithPublications;
import com.arsdigita.cms.contenttypes.SciOrganization;
import com.arsdigita.cms.contenttypes.SciOrganizationDepartmentsCollection;
import com.arsdigita.cms.contenttypes.SciOrganizationPublicationsCollection;
import com.arsdigita.cms.contenttypes.SciOrganizationWithPublications;
import com.arsdigita.cms.contenttypes.SciOrganizationWithPublicationsConfig;
import com.arsdigita.cms.contenttypes.SciPublicationTitleComparator;
import com.arsdigita.cms.contenttypes.SciPublicationYearAscComparator;
import com.arsdigita.cms.contenttypes.SciPublicationYearDescComparator;
import com.arsdigita.cms.contenttypes.ui.panels.Filter;
import com.arsdigita.cms.contenttypes.ui.panels.SelectFilter;
import com.arsdigita.cms.contenttypes.ui.panels.SortField;
import com.arsdigita.cms.contenttypes.ui.panels.TextFilter;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 */
public class SciOrganizationWithPublicationsPanel extends SciOrganizationPanel {

    private static final Logger logger =
                                Logger.getLogger(
            SciOrganizationWithPublicationsPanel.class);
    public static final String SHOW_PUBLICATIONS = "publications";
    public static final String SHOW_WORKING_PAPERS = "workingPapers";
    private static final String SORT = "sort";
    private static final String TITLE = "title";
    private static final String AUTHORS = "authors";
    private static final String YEAR_OF_PUBLICATION = "yearOfPublication";
    private static final String YEAR_ASC = "yearAsc";
    private static final String YEAR_DESC = "yearDesc";
    private String show;
    private boolean displayPublications = true;
    private boolean displayWorkingPapers = true;
    private final Map<String, Filter> filters =
                                      new LinkedHashMap<String, Filter>();
    private final Map<String, SortField<Publication>> sortFields =
                                                      new LinkedHashMap<String, SortField<Publication>>();
    private String sortByKey;

    public SciOrganizationWithPublicationsPanel() {
        filters.put(TITLE, new TextFilter(TITLE, TITLE));
        filters.put(AUTHORS, new TextFilter(AUTHORS, "authors.surname"));
        SelectFilter yearFilter = new SelectFilter(YEAR_OF_PUBLICATION,
                                                   YEAR_OF_PUBLICATION,
                                                   true,
                                                   true,
                                                   true,
                                                   true);
        DataQuery query = SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getAllYearsOfPublication");
        yearFilter.setDataQuery(query, "yearOfPublication");
        filters.put(YEAR_OF_PUBLICATION, yearFilter);
        sortFields.put(TITLE,
                       new SortField<Publication>(TITLE,
                                                  new SciPublicationTitleComparator()));
        sortFields.put(YEAR_ASC,
                       new SortField<Publication>(YEAR_ASC,
                                                  new SciPublicationYearAscComparator()));
        sortFields.put(YEAR_DESC,
                       new SortField<Publication>(YEAR_DESC,
                                                  new SciPublicationYearDescComparator()));
        /*sortFields.put(AUTHORS,
        new SortField<Publication>(AUTHORS,
        new SciPublicationAuthorComparator()));*/
    }

    @Override
    protected Class<? extends ContentItem> getAllowedClass() {
        return SciOrganizationWithPublications.class;
    }

    @Override
    protected String getPanelName() {
        return SciOrganization.class.getSimpleName();
    }

    public boolean isDisplayPublications() {
        return displayPublications;
    }

    public void setDisplayPublications(final boolean displayPublications) {
        this.displayPublications = displayPublications;
    }

    public boolean isDisplayWorkingPapers() {
        return displayWorkingPapers;
    }

    public void setDisplayWorkingPapers(final boolean displayWorkingPapers) {
        this.displayWorkingPapers = displayWorkingPapers;
    }

    @Override
    protected void generateAvailableDataXml(
            final GenericOrganizationalUnit organization,
            final Element element,
            final PageState state) {
        super.generateAvailableDataXml(organization, element, state);

        SciOrganizationWithPublicationsConfig config;
        config = SciOrganizationWithPublications.getConfig();

        SciOrganizationWithPublications orga =
                                        (SciOrganizationWithPublications) organization;

        long start = System.currentTimeMillis();
        if ((orga.hasPublications(config.getOrganizationPublicationsMerge()))
            && displayPublications) {
            element.newChildElement("publications");
        }
        if ((orga.hasWorkingPapers(config.getOrganizationPublicationsMerge()))
            && displayWorkingPapers
            && config.getOrganizationPublicationsSeparateWorkingPapers()) {
            element.newChildElement("workingPapers");
        }

        System.out.printf(
                "\n\nNeeded %d ms to determine if organization has publications\n\n",
                System.currentTimeMillis() - start);
    }

    protected void mergePublications(
            final SciOrganizationDepartmentsCollection departments,
            final Collection<Publication> publications,
            final boolean workingPapersOnly,
            final PageState state) {
        while (departments.next()) {
            SciDepartment dep;
            SciDepartmentWithPublications department;
            SciDepartmentPublicationsCollection departmentPublications;

            dep = departments.getDepartment();
            if (!(dep instanceof SciDepartmentWithPublications)) {
                continue;
            }
            department = (SciDepartmentWithPublications) dep;
            departmentPublications = department.getPublications();
            applyPublicationFilters(departmentPublications, state.getRequest());
            if (workingPapersOnly) {
                departmentPublications.addFilter(
                        "objectType = 'com.arsdigita.cms.contenttypes.WorkingPaper'");
            } else if (SciOrganizationWithPublications.getConfig().
                    getOrganizationPublicationsSeparateWorkingPapers()) {
                departmentPublications.addFilter(
                        "objectType != 'com.arsdigita.cms.contenttypes.WorkingPaper'");
            }

            if (publications instanceof ArrayList) {
                ((ArrayList<Publication>) publications).ensureCapacity(
                        publications.size()
                        + (int) departmentPublications.size());
            }

            Publication publication;
            while (departmentPublications.next()) {
                publication = departmentPublications.getPublication();
                publications.add(publication);
            }

            SciDepartmentSubDepartmentsCollection subDepartments;
            subDepartments = dep.getSubDepartments();

            if ((subDepartments != null) && subDepartments.size() > 0) {
                mergePublications(departments,
                                  publications,
                                  workingPapersOnly,
                                  state);
            }
        }
    }

    protected void generatePublicationFiltersXml(
            final List<Publication> publications,
            final Element element) {
        final Element filterElement = element.newChildElement("filters");

        for (Map.Entry<String, Filter> filterEntry : filters.entrySet()) {
            filterEntry.getValue().generateXml(filterElement);
        }
    }

    protected void generatePublicationSortFieldsXml(final Element element) {
        final Element sortFieldsElement = element.newChildElement("sortFields");
        sortFieldsElement.addAttribute("sortBy", sortByKey);
        for (Map.Entry<String, SortField<Publication>> sortField :
             sortFields.entrySet()) {
            sortField.getValue().generateXml(sortFieldsElement);
        }
    }

    protected void applyPublicationSortFields(
            final List<Publication> publications,
            final HttpServletRequest request) {
        sortByKey = request.getParameter("sort");
        if (!sortFields.containsKey(sortByKey)) {
            sortByKey = new ArrayList<String>(sortFields.keySet()).get(0);
        }

        if (sortFields.containsKey(sortByKey)) {
            Collections.sort(publications, sortFields.get(sortByKey).
                    getComparator());
        } else {
            Collections.sort(publications, new SciPublicationTitleComparator());
        }
    }

    protected void applyPublicationFilters(
            final DomainCollection publications,
            final HttpServletRequest request) {
        //Get parameters from HTTP request
        for (Map.Entry<String, Filter> filterEntry : filters.entrySet()) {
            String value = request.getParameter(
                    filterEntry.getValue().getLabel());

            if ((value != null) && !(value.trim().isEmpty())) {
                filterEntry.getValue().setValue(value);
            }
        }

        //Apply filters to DomainCollection
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

    protected void generatePublicationsXml(final SciOrganization organization,
                                           final Element parent,
                                           final PageState state,
                                           final boolean workingPapersOnly) {
        final SciOrganizationWithPublications orga =
                                              (SciOrganizationWithPublications) organization;

        Element controls = parent.newChildElement("filterControls");
        controls.addAttribute("customName", "sciOrganizationPublications");
        controls.addAttribute("show", show);

        if (SciOrganizationWithPublications.getConfig().
                getOrganizationPublicationsMerge()) {
            long start = System.currentTimeMillis();
            List<Publication> publications;
            SciOrganizationPublicationsCollection orgaPublications;
            orgaPublications = orga.getPublications();
            applyPublicationFilters(orgaPublications, state.getRequest());
            if (workingPapersOnly) {
                orgaPublications.addFilter(
                        "objectType = 'com.arsdigita.cms.contenttypes.WorkingPaper'");
            } else if (SciOrganizationWithPublications.getConfig().
                    getOrganizationPublicationsSeparateWorkingPapers()) {
                orgaPublications.addFilter(
                        "objectType != 'com.arsdigita.cms.contenttypes.WorkingPaper'");
            }
            publications = new ArrayList<Publication>((int) orgaPublications.
                    size());

            Publication publication;
            while (orgaPublications.next()) {
                publication = orgaPublications.getPublication();
                publications.add(publication);
            }

            System.out.printf("Got publications of organization in %d ms\n",
                              System.currentTimeMillis() - start);

            SciOrganizationDepartmentsCollection departments = organization.
                    getDepartments();
            long mergeStart = System.currentTimeMillis();
            mergePublications(departments,
                              publications,
                              workingPapersOnly,
                              state);
            System.err.printf("Merged publications in %d ms\n", System.
                    currentTimeMillis() - mergeStart);

            long sortStart = System.currentTimeMillis();
            Set<Publication> publicationsSet;
            List<Publication> publicationsWithoutDoubles;
            publicationsSet = new HashSet<Publication>(publications);
            publicationsWithoutDoubles = new LinkedList<Publication>(
                    publicationsSet);


            applyPublicationSortFields(publicationsWithoutDoubles, state.
                    getRequest());

            //Collections.sort(publicationsWithoutDoubles,
            //               new SciPublicationTitleComparator());
            System.out.printf("Sorted publications in %d ms\n", System.
                    currentTimeMillis() - sortStart);

            long paginatorStart = System.currentTimeMillis();
            long pageNumber = getPageNumber(state);
            long pageCount = getPageCount(publicationsWithoutDoubles.size());
            long begin = getPaginatorBegin(pageNumber);
            long count = getPaginatorCount(begin,
                                           publicationsWithoutDoubles.size());
            long end = getPaginatorEnd(begin, count);
            pageNumber = normalizePageNumber(pageCount, pageNumber);

            generatePublicationFiltersXml(publicationsWithoutDoubles, controls);
            generatePublicationSortFieldsXml(controls);
            createPaginatorElement(parent, pageNumber, pageCount, begin, end,
                                   count, publicationsWithoutDoubles.size());
            System.out.printf("Created paginator in %d ms", System.
                    currentTimeMillis() - paginatorStart);
            List<Publication> publicationsToShow = publicationsWithoutDoubles.
                    subList((int) begin, (int) end);

            System.out.printf(
                    "\n\nCreated list of publications to show in %d ms.\n\n",
                    System.currentTimeMillis() - start);

            start = System.currentTimeMillis();

            for (Publication pub : publicationsToShow) {
                PublicationXmlHelper xmlHelper =
                                     new PublicationXmlHelper(parent,
                                                              pub);
                xmlHelper.generateXml();
            }

            System.out.printf("\n\nGenerated publications XML in %d ms\n\n",
                              System.currentTimeMillis() - start);
        } else {
            SciOrganizationPublicationsCollection orgaPublications;
            orgaPublications = orga.getPublications();
            applyPublicationFilters(orgaPublications, state.getRequest());
            if (workingPapersOnly) {
                orgaPublications.addFilter(
                        "objectType = 'com.arsdigita.cms.contenttypes.WorkingPaper'");
            } else if (SciOrganizationWithPublications.getConfig().
                    getOrganizationPublicationsSeparateWorkingPapers()) {
                orgaPublications.addFilter(
                        "objectType != 'com.arsdigita.cms.contenttypes.WorkingPaper'");
            }

            List<Publication> publications = new LinkedList<Publication>();

            while (orgaPublications.next()) {
                publications.add(orgaPublications.getPublication());
            }

            applyPublicationSortFields(publications, state.getRequest());

            //Collections.sort(publications, new SciPublicationTitleComparator());

            long pageNumber = getPageNumber(state);
            long pageCount = getPageCount(publications.size());
            long begin = getPaginatorBegin(pageNumber);
            long count = getPaginatorCount(begin, publications.size());
            long end = getPaginatorEnd(begin, count);
            pageNumber = normalizePageNumber(pageCount, pageNumber);

            generatePublicationFiltersXml(publications, controls);
            generatePublicationSortFieldsXml(controls);
            createPaginatorElement(parent, pageNumber, pageCount, begin, end,
                                   count, publications.size());
            List<Publication> publicationsToShow = publications.subList(
                    (int) begin, (int) end);

            for (Publication publication : publicationsToShow) {
                PublicationXmlHelper xmlHelper =
                                     new PublicationXmlHelper(parent,
                                                              publication);
                xmlHelper.generateXml();
            }
        }
    }

    @Override
    protected void generateDataXml(final GenericOrganizationalUnit organization,
                                   final Element element,
                                   final PageState state) {
        show = getShowParam(state);

        if (SHOW_PUBLICATIONS.equals(show)) {
            generatePublicationsXml((SciOrganization) organization,
                                    element,
                                    state,
                                    false);
        } else if (SHOW_WORKING_PAPERS.equals(show)) {
            generatePublicationsXml((SciOrganization) organization,
                                    element,
                                    state,
                                    true);
        } else {
            super.generateDataXml(organization, element, state);
        }
    }
}

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
public class SciDepartmentWithPublicationsPanel extends SciDepartmentPanel {

    private static final Logger logger =
                                Logger.getLogger(
            SciDepartmentWithPublicationsPanel.class);
    public static final String SHOW_PUBLICATIONS = "publications";
    public static final String SHOW_WORKING_PAPERS = "workingPapers";
    private static final String SORT = "sort";
    private static final String TITLE = "title";
    private static final String AUTHORS = "authors";
    private static final String YEAR_OF_PUBLICATION = "yearOfPublication";
    private static final String YEAR_ASC = "yearAsc";
    private static final String YEAR_DESC = "yearAsc";
    private String show;
    private boolean displayPublications = true;
    private boolean displayWorkingPapers = true;
    private final Map<String, Filter> filters =
                                      new LinkedHashMap<String, Filter>();
    private final Map<String, SortField<Publication>> sortFields =
                                                      new LinkedHashMap<String, SortField<Publication>>();
    private String sortByKey;

    public SciDepartmentWithPublicationsPanel() {
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
    public Class<? extends ContentItem> getAllowedClass() {
        return SciDepartmentWithPublications.class;
    }

    @Override
    protected String getPanelName() {
        return SciDepartment.class.getSimpleName();
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
    protected void generateAvailableDataXml(final GenericOrganizationalUnit orga,
                                            final Element element,
                                            final PageState state) {
        SciDepartment department = (SciDepartment) orga;

        super.generateAvailableDataXml(department, element, state);

        SciOrganizationWithPublicationsConfig config =
                                              SciOrganizationWithPublications.
                getConfig();

        SciDepartmentWithPublications dep =
                                      (SciDepartmentWithPublications) department;

        if ((dep.hasPublications(
             config.getOrganizationPublicationsMerge()))
            && displayPublications) {
            element.newChildElement("publications");
        }
        if ((dep.hasWorkingPapers(config.getOrganizationPublicationsMerge())
             && displayWorkingPapers
             && config.getDepartmentPublicationsSeparateWorkingPapers())) {
            element.newChildElement("workingPapers");
        }
    }

    protected void mergePublications(
            final SciDepartmentSubDepartmentsCollection subDepartments,
            final List<Publication> publications,
            final boolean workingPapersOnly,
            final PageState state) {
        while (subDepartments.next()) {
            SciDepartment dep;
            SciDepartmentWithPublications department;
            SciDepartmentPublicationsCollection departmentPublications;

            dep = subDepartments.getSubDepartment();
            if (!(dep instanceof SciDepartmentWithPublications)) {
                continue;
            }
            department = (SciDepartmentWithPublications) dep;
            departmentPublications = department.getPublications();
            applyFilters(departmentPublications, state.getRequest());
            if (workingPapersOnly) {
                departmentPublications.addFilter(
                        "objectType = 'com.arsdigita.cms.contenttypes.WorkingPaper'");
            } else if (SciOrganizationWithPublications.getConfig().
                    getDepartmentPublicationsSeparateWorkingPapers()) {
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

            SciDepartmentSubDepartmentsCollection subSubDepartments = dep.
                    getSubDepartments();

            if ((subSubDepartments != null) && subSubDepartments.size() > 0) {
                mergePublications(subDepartments,
                                  publications,
                                  workingPapersOnly,
                                  state);
            }
        }
    }

    protected void generateFiltersXml(
            final List<Publication> publications,
            final Element element) {
        final Element sortFieldsElement = element.newChildElement("sortFields");
        sortFieldsElement.addAttribute("sortBy", sortByKey);
        for (Map.Entry<String, SortField<Publication>> sortField :
             sortFields.entrySet()) {
            sortField.getValue().generateXml(sortFieldsElement);
        }
    }

    protected void generateSortFieldsXml(final Element element) {
        final Element sortFieldsElement = element.newChildElement("sortFields");
        sortFieldsElement.addAttribute("sortBy", sortByKey);
        for (Map.Entry<String, SortField<Publication>> sortField :
             sortFields.entrySet()) {
            sortField.getValue().generateXml(sortFieldsElement);
        }
    }

    protected void applySortFields(
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

    protected void applyFilters(
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

    protected void generatePublicationsXml(final SciDepartment department,
                                           final Element parent,
                                           final PageState state,
                                           final boolean workingPapersOnly) {
        final SciDepartmentWithPublications dep =
                                            (SciDepartmentWithPublications) department;

        Element controls = parent.newChildElement("filterControls");
        controls.addAttribute("customName", "sciDepartmentPublications");
        controls.addAttribute("show", show);

        if (SciOrganizationWithPublications.getConfig().
                getOrganizationPublicationsMerge()) {
            List<Publication> publications;
            SciDepartmentPublicationsCollection departmentPublications;
            departmentPublications = dep.getPublications();
            applyFilters(departmentPublications, state.getRequest());
            if (workingPapersOnly) {
                departmentPublications.addFilter(
                        "objectType = 'com.arsdigita.cms.contenttypes.WorkingPaper'");
            } else if (SciOrganizationWithPublications.getConfig().
                    getDepartmentPublicationsSeparateWorkingPapers()) {
                departmentPublications.addFilter(
                        "objectType != 'com.arsdigita.cms.contenttypes.WorkingPaper'");
            }
            publications =
            new ArrayList<Publication>((int) departmentPublications.size());

            Publication publication;
            while (departmentPublications.next()) {
                publication = departmentPublications.getPublication();
                publications.add(publication);
            }

            mergePublications(department.getSubDepartments(),
                              publications,
                              workingPapersOnly,
                              state);

            Set<Publication> publicationsSet;
            List<Publication> publicationsWithoutDoubles;
            publicationsSet = new HashSet<Publication>(publications);
            publicationsWithoutDoubles = new LinkedList<Publication>(
                    publicationsSet);

            applySortFields(publications, state.getRequest());

            //Collections.sort(publicationsWithoutDoubles,
            //               new SciPublicationTitleComparator());

            long pageNumber = getPageNumber(state);
            long pageCount = getPageCount(publicationsWithoutDoubles.size());
            long begin = getPaginatorBegin(pageNumber);
            long count = getPaginatorCount(begin, publicationsWithoutDoubles.
                    size());
            long end = getPaginatorEnd(begin, count);
            pageNumber = normalizePageNumber(pageCount, pageNumber);

            generateFiltersXml(publicationsWithoutDoubles, controls);
            generateSortFieldsXml(controls);
            createPaginatorElement(parent, pageNumber, pageCount, begin, end,
                                   count, publicationsWithoutDoubles.size());
            List<Publication> publicationsToShow = publicationsWithoutDoubles.
                    subList((int) begin, (int) end);

            for (Publication pub : publicationsToShow) {
                PublicationXmlHelper xmlHelper = new PublicationXmlHelper(parent,
                                                                          pub);
                xmlHelper.generateXml();
            }
        } else {
            SciDepartmentPublicationsCollection departmentPublications;
            departmentPublications = dep.getPublications();
            applyFilters(departmentPublications, state.getRequest());

            List<Publication> publications = new LinkedList<Publication>();
            if (workingPapersOnly) {
                departmentPublications.addFilter(
                        "objectType = 'com.arsdigita.cms.contenttypes.WorkingPaper'");
            } else if (SciOrganizationWithPublications.getConfig().
                    getDepartmentPublicationsSeparateWorkingPapers()) {
                departmentPublications.addFilter(
                        "objectType != 'com.arsdigita.cms.contenttypes.WorkingPaper'");
            }

            while (departmentPublications.next()) {
                publications.add(departmentPublications.getPublication());
            }

            applySortFields(publications, state.getRequest());

            //Collections.sort(publications, new SciPublicationTitleComparator());

            long pageNumber = getPageNumber(state);
            long pageCount = getPageCount(publications.size());
            long begin = getPaginatorBegin(pageNumber);
            long count = getPaginatorCount(begin, publications.size());
            long end = getPaginatorEnd(begin, count);
            pageNumber = normalizePageNumber(pageCount, pageNumber);

            generateFiltersXml(publications, controls);
            generateSortFieldsXml(controls);
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
    public void generateDataXml(final GenericOrganizationalUnit orga,
                                final Element element,
                                final PageState state) {
        show = getShowParam(state);

        if (SHOW_PUBLICATIONS.equals(show)) {
            generatePublicationsXml((SciDepartment) orga, element, state, false);
        } else if (SHOW_WORKING_PAPERS.equals(show)) {
            generatePublicationsXml((SciDepartment) orga, element, state, true);
        } else {
            super.generateDataXml(orga, element, state);
        }
    }
}

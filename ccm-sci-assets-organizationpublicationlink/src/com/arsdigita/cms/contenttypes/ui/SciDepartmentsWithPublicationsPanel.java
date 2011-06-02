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
import com.arsdigita.cms.ContentItemXMLRenderer;
import com.arsdigita.cms.contentassets.SciOrganizationPublicationCollection;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.SciDepartmentSubDepartmentsCollection;
import com.arsdigita.cms.contenttypes.SciDepartmentWithPublications;
import com.arsdigita.cms.contenttypes.SciOrganizationWithPublications;
import com.arsdigita.cms.contenttypes.SciOrganizationWithPublicationsConfig;
import com.arsdigita.cms.contenttypes.SciPublicationComparator;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.xml.Element;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Jens Pelzetter 
 */
public class SciDepartmentsWithPublicationsPanel extends SciDepartmentPanel {

    public static final String SHOW_PUBLICATIONS = "publications";
    private boolean displayPublications = true;

    public boolean isDisplayPublications() {
        return displayPublications;
    }

    public void setDisplayPublications(final boolean displayPublications) {
        this.displayPublications = displayPublications;
    }

    @Override
    protected void generateAvailableDataXml(final SciDepartment department,
                                            final Element element,
                                            final PageState state) {
        super.generateAvailableDataXml(department, element, state);

        SciOrganizationWithPublicationsConfig config =
                                              SciOrganizationWithPublications.
                getConfig();

        SciDepartmentWithPublications dep = new SciDepartmentWithPublications(
                department);

        if ((dep.hasPublications(config.getOrganizationPublicationsMerge()))
            && displayPublications) {
            element.newChildElement("publications");
        }
    }

    protected void mergePublications(
            final SciDepartmentSubDepartmentsCollection subDepartments,
            final List<Publication> publications) {
        while (subDepartments.next()) {
            SciDepartment dep;
            SciDepartmentWithPublications department;
            SciOrganizationPublicationCollection departmentPublications;

            dep = subDepartments.getSubDepartment();
            department = new SciDepartmentWithPublications(dep);
            departmentPublications = department.getPublications();

            while (departmentPublications.next()) {
                publications.add(departmentPublications.getPublication());
            }

            SciDepartmentSubDepartmentsCollection subSubDepartments = dep.
                    getSubDepartments();

            if ((subSubDepartments != null) && subSubDepartments.size() > 0) {
                mergePublications(subDepartments, publications);
            }
        }
    }

    protected void generatePublicationsXml(final SciDepartment department,
                                           final Element parent,
                                           final PageState state) {
        final SciDepartmentWithPublications dep =
                                            new SciDepartmentWithPublications(
                department);

        if (SciOrganizationWithPublications.getConfig().
                getOrganizationPublicationsMerge()) {
            List<Publication> publications;
            publications = new LinkedList<Publication>();
            SciOrganizationPublicationCollection departmentPublications;
            departmentPublications = dep.getPublications();

            while (departmentPublications.next()) {
                publications.add(departmentPublications.getPublication());
            }

            mergePublications(department.getSubDepartments(), publications);

            Set<Publication> publicationsSet;
            List<Publication> publicationsWithoutDoubles;
            publicationsSet = new HashSet<Publication>(publications);
            publicationsWithoutDoubles = new LinkedList<Publication>(
                    publicationsSet);

            Collections.sort(publicationsWithoutDoubles,
                             new SciPublicationComparator());

            long pageNumber = getPageNumber(state);
            long pageCount = getPageCount(publicationsWithoutDoubles.size());
            long begin = getPaginatorBegin(pageNumber);
            long count = getPaginatorCount(begin, publicationsWithoutDoubles.
                    size());
            long end = getPaginatorEnd(begin, count);
            pageNumber = normalizePageNumber(pageCount, pageNumber);

            createPaginatorElement(parent, pageNumber, pageCount, begin, end,
                                   count, publicationsWithoutDoubles.size());
            List<Publication> publicationsToShow = publicationsWithoutDoubles.
                    subList((int) begin, (int) end);

            final Element publicationsElem = parent.newChildElement(
                    "publications");
            final ContentItemXMLRenderer renderer = new ContentItemXMLRenderer(
                    publicationsElem);
            renderer.setWrapAttributes(true);
            for (Publication publication : publicationsToShow) {
                renderer.walk(publication, SimpleXMLGenerator.class.getName());
            }
        } else {
            SciOrganizationPublicationCollection departmentPublications;
            departmentPublications = dep.getPublications();

            List<Publication> publications = new LinkedList<Publication>();

            while (departmentPublications.next()) {
                publications.add(departmentPublications.getPublication());
            }

            Collections.sort(publications, new SciPublicationComparator());

            long pageNumber = getPageNumber(state);
            long pageCount = getPageCount(publications.size());
            long begin = getPaginatorBegin(pageNumber);
            long count = getPaginatorCount(begin, publications.size());
            long end = getPaginatorEnd(begin, count);
            pageNumber = normalizePageNumber(pageCount, pageNumber);

            createPaginatorElement(parent, pageNumber, pageCount, begin, end,
                                   count, publications.size());
            List<Publication> publicationsToShow = publications.subList(
                    (int) begin, (int) end);

            final Element publicationsElem = parent.newChildElement(
                    "publications");
            final ContentItemXMLRenderer renderer = new ContentItemXMLRenderer(
                    publicationsElem);
            renderer.setWrapAttributes(true);
            for (Publication publication : publicationsToShow) {
                renderer.walk(publication, SimpleXMLGenerator.class.getName());
            }
        }
    }

    @Override
    public void generateDataXml(final SciDepartment department,
                                final Element element,
                                final PageState state) {
        String show = getShowParam(state);

        if (SHOW_PUBLICATIONS.equals(show)) {
            generatePublicationsXml(department, element, state);
        } else {
            super.generateDataXml(department, element, state);
        }
    }
}

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
import com.arsdigita.cms.contenttypes.SciOrganization;
import com.arsdigita.cms.contenttypes.SciOrganizationDepartmentsCollection;
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
public class SciOrganizationWithPublicationsPanel extends SciOrganizationPanel {

    public static final String SHOW_PUBLICATIONS = "publications";
    private boolean displayPublications = true;

    public boolean isDisplayPublications() {
        return displayPublications;
    }

    public void setDisplayPublications(final boolean displayPublications) {
        this.displayPublications = displayPublications;
    }
    
    @Override
    protected void generateAvailableDataXml(final SciOrganization organization,
                                            final Element element,
                                            final PageState state) {
        super.generateAvailableDataXml(organization, element, state);

        SciOrganizationWithPublicationsConfig config;
        config = SciOrganizationWithPublications.getConfig();

        SciOrganizationWithPublications orga =
                                        new SciOrganizationWithPublications(
                organization);

        if ((orga.hasPublications(config.getOrganizationPublicationsMerge()))
                && displayPublications) {
            element.newChildElement("publications");
        }
    }

    protected void mergePublications(
            final SciOrganizationDepartmentsCollection departments,
            final List<Publication> publications) {
        while (departments.next()) {
            SciDepartment dep;
            SciDepartmentWithPublications department;
            SciOrganizationPublicationCollection departmentPublications;

            dep = departments.getDepartment();
            department = new SciDepartmentWithPublications(dep);
            departmentPublications = department.getPublications();

            while (departmentPublications.next()) {
                publications.add(departmentPublications.getPublication());
            }

            SciDepartmentSubDepartmentsCollection subDepartments;
            subDepartments = dep.getSubDepartments();

            if ((subDepartments != null) && subDepartments.size() > 0) {
                mergePublications(departments, publications);
            }
        }
    }

    protected void generatePublicationsXml(final SciOrganization organization,
                                           final Element parent,
                                           final PageState state) {
        final SciOrganizationWithPublications orga =
                                              new SciOrganizationWithPublications(
                organization);

        if (SciOrganizationWithPublications.getConfig().
                getOrganizationPublicationsMerge()) {
            List<Publication> publications;
            publications = new LinkedList<Publication>();
            SciOrganizationPublicationCollection orgaPublications;
            orgaPublications = orga.getPublications();

            while (orgaPublications.next()) {
                publications.add(orgaPublications.getPublication());
            }

            mergePublications(organization.getDepartments(),
                              publications);

            Set<Publication> publicationsSet;
            List<Publication> publicationWithoutDoubles;
            publicationsSet = new HashSet<Publication>(publications);
            publicationWithoutDoubles = new LinkedList<Publication>(
                    publicationsSet);

            Collections.sort(publicationWithoutDoubles,
                             new SciPublicationComparator());

            long pageNumber = getPageNumber(state);
            long pageCount = getPageCount(publicationWithoutDoubles.size());
            long begin = getPaginatorBegin(pageNumber);
            long count = getPaginatorCount(begin,
                                           publicationWithoutDoubles.size());
            long end = getPaginatorEnd(begin, count);
            pageNumber = normalizePageNumber(pageCount, pageNumber);

            createPaginatorElement(parent, pageNumber, pageCount, begin, end,
                                   count, publicationWithoutDoubles.size());
            List<Publication> publicationsToShow = publicationWithoutDoubles.
                    subList((int) begin, (int) end);

            final Element publicationsElem = parent.newChildElement(
                    "publications");
            final ContentItemXMLRenderer renderer =
                                         new ContentItemXMLRenderer(
                    publicationsElem);
            renderer.setWrapAttributes(true);
            for (Publication publication : publicationsToShow) {
                renderer.walk(publication, SimpleXMLGenerator.class.getName());
            }
        } else {
            SciOrganizationPublicationCollection orgaPublications;
            orgaPublications = orga.getPublications();

            List<Publication> publications = new LinkedList<Publication>();

            while (orgaPublications.next()) {
                publications.add(orgaPublications.getPublication());
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
            final ContentItemXMLRenderer renderer =
                                         new ContentItemXMLRenderer(
                    publicationsElem);
            renderer.setWrapAttributes(true);
            for (Publication publication : publicationsToShow) {
                renderer.walk(publication, SimpleXMLGenerator.class.getName());
            }
        }
    }

    @Override
    protected void generateDataXml(final SciOrganization organization,
                                   final Element element,
                                   final PageState state) {
        String show = getShowParam(state);

        if (SHOW_PUBLICATIONS.equals(show)) {
            generatePublicationsXml(organization, element, state);
        } else {
            super.generateDataXml(organization, element, state);
        }
    }
}

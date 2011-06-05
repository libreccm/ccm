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
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.xml.Element;
import java.util.ArrayList;
import java.util.Collection;
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
                                        (SciOrganizationWithPublications) organization;

        long start = System.currentTimeMillis();
        if ((orga.hasPublications(config.getOrganizationPublicationsMerge()))
            && displayPublications) {
            element.newChildElement("publications");
        }
        System.out.printf(
                "\n\nNeeded %d ms to determine if organization has publications\n\n",
                System.currentTimeMillis() - start);
    }

    protected void mergePublications(
            final SciOrganizationDepartmentsCollection departments,
            final Collection<Publication> publications) {
        while (departments.next()) {
            SciDepartment dep;
            SciDepartmentWithPublications department;
            SciDepartmentPublicationsCollection departmentPublications;

            dep = departments.getDepartment();
            department = (SciDepartmentWithPublications) dep;
            departmentPublications = department.getPublications();

            if (publications instanceof ArrayList) {
                ((ArrayList<Publication>) publications).ensureCapacity(
                        publications.size()
                        + (int) departmentPublications.size());
            }

            Publication publication;
            while (departmentPublications.next()) {
                publication = (Publication) departmentPublications.getPublication().getLiveVersion();
                if (publication == null) {
                    continue;
                } else {
                publications.add(publication);
                }
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
                                              (SciOrganizationWithPublications) organization;

        if (SciOrganizationWithPublications.getConfig().
                getOrganizationPublicationsMerge()) {
            long start = System.currentTimeMillis();
            List<Publication> publications;
            SciOrganizationPublicationsCollection orgaPublications;
            orgaPublications = orga.getPublications();
            publications = new ArrayList<Publication>((int) orgaPublications.
                    size());

            Publication publication;
            while (orgaPublications.next()) {
                publication = (Publication) orgaPublications.getPublication().getLiveVersion();
                if (publication == null) {
                    continue;
                } else {
                publications.add(publication);
                }
            }

            SciOrganizationDepartmentsCollection departments = organization.
                    getDepartments();
            long mergeStart = System.currentTimeMillis();
            mergePublications(departments, publications);
            System.err.printf("Merged publications in %d ms\n", System.
                    currentTimeMillis() - mergeStart);

            long sortStart = System.currentTimeMillis();
            Set<Publication> publicationsSet;
            List<Publication> publicationWithoutDoubles;
            publicationsSet = new HashSet<Publication>(publications);
            //publicationWithoutDoubles = new LinkedList<Publication>(
            //      publicationsSet);
            publicationWithoutDoubles = new ArrayList<Publication>(
                    publicationsSet);


            Collections.sort(publicationWithoutDoubles,
                             new SciPublicationTitleComparator());
            System.out.printf("Sorted publications in %d ms\n", System.
                    currentTimeMillis() - sortStart);


            long paginatorStart = System.currentTimeMillis();
            long pageNumber = getPageNumber(state);
            long pageCount = getPageCount(publicationWithoutDoubles.size());
            long begin = getPaginatorBegin(pageNumber);
            long count = getPaginatorCount(begin,
                                           publicationWithoutDoubles.size());
            long end = getPaginatorEnd(begin, count);
            pageNumber = normalizePageNumber(pageCount, pageNumber);

            createPaginatorElement(parent, pageNumber, pageCount, begin, end,
                                   count, publicationWithoutDoubles.size());
            System.out.printf("Created paginator in %d ms", System.
                    currentTimeMillis() - paginatorStart);
            List<Publication> publicationsToShow = publicationWithoutDoubles.
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

            List<Publication> publications = new LinkedList<Publication>();

            while (orgaPublications.next()) {
                publications.add(orgaPublications.getPublication());
            }

            Collections.sort(publications, new SciPublicationTitleComparator());

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

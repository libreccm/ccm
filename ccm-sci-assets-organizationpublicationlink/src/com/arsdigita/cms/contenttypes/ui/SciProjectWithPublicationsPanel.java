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
import com.arsdigita.cms.contenttypes.SciOrganizationWithPublications;
import com.arsdigita.cms.contenttypes.SciOrganizationWithPublicationsConfig;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.SciProjectSubProjectsCollection;
import com.arsdigita.cms.contenttypes.SciProjectWithPublications;
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
public class SciProjectWithPublicationsPanel extends SciProjectPanel {

    public static final String SHOW_PUBLICATIONS = "publications";
    private boolean displayPublications = true;

    public boolean isDisplayPublications() {
        return displayPublications;
    }

    public void setDisplayPublications(final boolean displayPublications) {
        this.displayPublications = displayPublications;
    }

    @Override
    public void generateAvailableDataXml(final SciProject project,
                                         final Element element,
                                         final PageState state) {
        super.generateAvailableDataXml(project, element, state);

        SciOrganizationWithPublicationsConfig config =
                                              SciOrganizationWithPublications.
                getConfig();

        SciProjectWithPublications proj =
                                   new SciProjectWithPublications(project);

        if ((proj.hasPublications(config.getOrganizationPublicationsMerge()))
            && displayPublications) {
            element.newChildElement("publications");
        }
    }

    protected void mergePublications(
            final SciProjectSubProjectsCollection subProjects,
            final List<Publication> publications) {
        while (subProjects.next()) {
            SciProject proj;
            SciProjectWithPublications project;
            SciOrganizationPublicationCollection projectPublications;

            proj = subProjects.getSubProject();
            project = new SciProjectWithPublications(proj);
            projectPublications = project.getPublications();

            while (projectPublications.next()) {
                publications.add(projectPublications.getPublication());
            }

            SciProjectSubProjectsCollection subSubProjects =
                                            proj.getSubProjects();

            if ((subSubProjects != null) && subSubProjects.size() > 0) {
                mergePublications(subSubProjects, publications);
            }
        }
    }

    protected void generatePublicationsXml(final SciProject project,
                                           final Element parent,
                                           final PageState state) {
        final SciProjectWithPublications proj = new SciProjectWithPublications(
                project);

        if (SciOrganizationWithPublications.getConfig().
                getOrganizationPublicationsMerge()) {
            List<Publication> publications = new LinkedList<Publication>();
            SciOrganizationPublicationCollection projectPublications = proj.
                    getPublications();

            while (projectPublications.next()) {
                publications.add(projectPublications.getPublication());
            }

            mergePublications(project.getSubProjects(), publications);

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
            SciOrganizationPublicationCollection projectPublications = proj.
                    getPublications();

            List<Publication> publications = new LinkedList<Publication>();

            while (projectPublications.next()) {
                publications.add(projectPublications.getPublication());
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
    public void generateDataXml(final SciProject project,
                                final Element element,
                                final PageState state) {
        String show = getShowParam(state);
        
        if (SHOW_PUBLICATIONS.equals(show)) {
            generatePublicationsXml(project, element, state);
        } else {
            super.generateDataXml(project, element, state);
        }
    }
}

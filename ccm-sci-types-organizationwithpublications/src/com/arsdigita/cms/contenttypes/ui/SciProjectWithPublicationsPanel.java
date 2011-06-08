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
import com.arsdigita.cms.contenttypes.SciOrganizationWithPublications;
import com.arsdigita.cms.contenttypes.SciOrganizationWithPublicationsConfig;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.SciProjectPublicationsCollection;
import com.arsdigita.cms.contenttypes.SciProjectSubProjectsCollection;
import com.arsdigita.cms.contenttypes.SciProjectWithPublications;
import com.arsdigita.cms.contenttypes.SciPublicationTitleComparator;
import com.arsdigita.xml.Element;
import java.util.ArrayList;
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
    public static final String SHOW_WORKING_PAPERS = "workingPapers";
    private boolean displayPublications = true;
    private boolean displayWorkingPapers = true;

    public SciProjectWithPublicationsPanel() {
    }

    @Override
    public Class<? extends ContentItem> getAllowedClass() {
        return SciProjectWithPublications.class;
    }

    @Override
    protected String getPanelName() {
        return SciProject.class.getSimpleName();
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
    public void generateAvailableDataXml(final GenericOrganizationalUnit orga,
                                         final Element element,
                                         final PageState state) {
        SciProject project = (SciProject) orga;

        super.generateAvailableDataXml(project, element, state);

        SciOrganizationWithPublicationsConfig config =
                                              SciOrganizationWithPublications.
                getConfig();

        SciProjectWithPublications proj =
                                   (SciProjectWithPublications) project;

        if ((proj.hasPublications(config.getOrganizationPublicationsMerge()))
            && displayPublications) {
            element.newChildElement("publications");
        }
        if ((proj.hasWorkingPapers(config.getOrganizationPublicationsMerge()))
            && displayWorkingPapers
            && config.getProjectPublicationsSeparateWorkingPapers()) {
            element.newChildElement("workingPapers");
        }
    }

    protected void mergePublications(
            final SciProjectSubProjectsCollection subProjects,
            final List<Publication> publications,
            final boolean workingPapersOnly) {
        while (subProjects.next()) {
            SciProject proj;
            SciProjectWithPublications project;
            SciProjectPublicationsCollection projectPublications;

            proj = subProjects.getSubProject();
            project = (SciProjectWithPublications) proj;
            projectPublications = project.getPublications();
            if (workingPapersOnly) {
                projectPublications.addFilter(
                        "objectType = 'com.arsdigita.cms.contenttypes.WorkingPaper'");
            } else if (SciOrganizationWithPublications.getConfig().
                    getProjectPublicationsSeparateWorkingPapers()) {
                projectPublications.addFilter(
                        "objectType != 'com.arsdigita.cms.contenttypes.WorkingPaper'");
            }

            if (publications instanceof ArrayList) {
                ((ArrayList<Publication>) publications).ensureCapacity(
                        publications.size() + (int) projectPublications.size());
            }

            Publication publication;
            while (projectPublications.next()) {
                publication = projectPublications.getPublication();
                publications.add(publication);
            }

            SciProjectSubProjectsCollection subSubProjects =
                                            proj.getSubProjects();

            if ((subSubProjects != null) && subSubProjects.size() > 0) {
                mergePublications(subSubProjects, publications,
                                  workingPapersOnly);
            }
        }
    }

    protected void generatePublicationsXml(final SciProject project,
                                           final Element parent,
                                           final PageState state,
                                           final boolean workingPapersOnly) {
        final SciProjectWithPublications proj =
                                         (SciProjectWithPublications) project;

        if (SciOrganizationWithPublications.getConfig().
                getOrganizationPublicationsMerge()) {
            List<Publication> publications;
            SciProjectPublicationsCollection projectPublications = proj.
                    getPublications();
            if (workingPapersOnly) {
                projectPublications.addFilter(
                        "objectType = 'com.arsdigita.cms.contenttypes.WorkingPaper'");
            } else if (SciOrganizationWithPublications.getConfig().
                    getProjectPublicationsSeparateWorkingPapers()) {
                projectPublications.addFilter(
                        "objectType != 'com.arsdigita.cms.contenttypes.WorkingPaper'");
            }

            publications =
            new ArrayList<Publication>((int) projectPublications.size());

            Publication publication;
            while (projectPublications.next()) {
                publication = projectPublications.getPublication();
                publications.add(publication);
            }

            mergePublications(project.getSubProjects(),
                              publications,
                              workingPapersOnly);

            Set<Publication> publicationsSet;
            List<Publication> publicationsWithoutDoubles;
            publicationsSet = new HashSet<Publication>(publications);
            publicationsWithoutDoubles = new LinkedList<Publication>(
                    publicationsSet);
            Collections.sort(publicationsWithoutDoubles,
                             new SciPublicationTitleComparator());

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

            for (Publication pub : publicationsToShow) {
                PublicationXmlHelper xmlHelper = new PublicationXmlHelper(parent,
                                                                          pub);
                xmlHelper.generateXml();
            }
        } else {
            SciProjectPublicationsCollection projectPublications = proj.
                    getPublications();
            if (workingPapersOnly) {
                projectPublications.addFilter(
                        "objectType = 'com.arsdigita.cms.contenttypes.WorkingPaper'");
            } else if (SciOrganizationWithPublications.getConfig().
                    getProjectPublicationsSeparateWorkingPapers()) {
                projectPublications.addFilter(
                        "objectType != 'com.arsdigita.cms.contenttypes.WorkingPaper'");
            }

            List<Publication> publications = new LinkedList<Publication>();

            while (projectPublications.next()) {
                publications.add(projectPublications.getPublication());
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
        String show = getShowParam(state);

        if (SHOW_PUBLICATIONS.equals(show)) {
            generatePublicationsXml((SciProject) orga, element, state, false);
        } else if (SHOW_WORKING_PAPERS.equals(show)) {
            generatePublicationsXml((SciProject) orga, element, state, true);
        } else {
            super.generateDataXml(orga, element, state);
        }
    }
}

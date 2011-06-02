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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.contentassets.SciOrganizationPublicationCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter 
 */
public class SciProjectWithPublications extends SciProject {

    private GenericOrganizationalUnitWithPublications projectWithPublications;

    private SciProjectWithPublications() {
    }

    private SciProjectWithPublications(final BigDecimal id) {
    }

    private SciProjectWithPublications(final OID oid) {
    }

    private SciProjectWithPublications(final DataObject dobj) {
    }

    private SciProjectWithPublications(final String type) {
    }

    public SciProjectWithPublications(final SciProject project) {
        projectWithPublications =
        new GenericOrganizationalUnitWithPublications(project.getID());
    }

    public boolean hasPublications(final boolean merge) {
        DataQuery query =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contentassets.getIdsOfPublicationsOfSciProject");
        query.setParameter("projectId", getID());

        if (query.size() > 0) {
            query.close();
            return true;
        } else {
            if (merge) {
                query.close();
                DataQuery subProjectsQuery =
                          SessionManager.getSession().retrieveQuery(
                        "com.arsdigita.cms.contenttypes.getIdsOfSubProjectsOfSciProject");
                subProjectsQuery.setParameter("project", getID());

                if (subProjectsQuery.size() > 0) {
                    BigDecimal subProjectId;
                    boolean result = false;
                    while (subProjectsQuery.next()) {
                        subProjectId = (BigDecimal) subProjectsQuery.get(
                                "projectId");
                        result = hasPublications(subProjectId, merge);

                        if (result) {
                            break;
                        }
                    }

                    subProjectsQuery.close();
                    return result;
                } else {
                    subProjectsQuery.close();
                    return false;
                }
            } else {
                query.close();
                return false;
            }
        }
    }

    private boolean hasPublications(final BigDecimal projectId,
                                    final boolean merge) {
        DataQuery query =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contentassets.getIdsOfPublicationsOfSciProject");
        query.setParameter("projectId", projectId);

        if (query.size() > 0) {
            query.close();
            return true;
        } else {
            if (merge) {
                query.close();
                DataQuery subProjectsQuery =
                          SessionManager.getSession().retrieveQuery(
                        "com.arsdigita.cms.contenttypes.getIdsOfSubProjectsOfSciProject");
                subProjectsQuery.setParameter("project", projectId);

                if (subProjectsQuery.size() > 0) {
                    BigDecimal subProjectId;
                    boolean result = false;
                    while (subProjectsQuery.next()) {
                        subProjectId = (BigDecimal) subProjectsQuery.get(
                                "projectId");
                        result = hasPublications(subProjectId, merge);

                        if (result) {
                            break;
                        }
                    }

                    subProjectsQuery.close();
                    return result;
                } else {
                    subProjectsQuery.close();
                    return false;
                }
            } else {
                query.close();
                return false;
            }
        }
    }
    
    public SciOrganizationPublicationCollection getPublications() {
        return projectWithPublications.getPublications();
    }
}

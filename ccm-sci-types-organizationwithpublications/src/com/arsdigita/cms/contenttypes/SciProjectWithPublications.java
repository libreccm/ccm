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

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter 
 */
public class SciProjectWithPublications extends SciProject {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.SciProjectWithPublications";
    public static final String PUBLICATIONS = "publications";

    public SciProjectWithPublications() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public SciProjectWithPublications(final BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SciProjectWithPublications(final OID oid) {
        super(oid);
    }

    public SciProjectWithPublications(final DataObject dobj) {
        super(dobj);
    }

    public SciProjectWithPublications(final String type) {
        super(type);
    }

    public boolean hasPublications(final boolean merge) {
        DataQuery query =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfPublicationsOfSciProject");
        query.setParameter("project", getID());

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
                "com.arsdigita.cms.contenttypes.getIdsOfPublicationsOfSciProject");
        query.setParameter("project", projectId);

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

    public boolean hasWorkingPapers(final boolean merge) {
        DataQuery query =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfWorkingPapersOfSciProject");
        query.setParameter("project", getID());

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

    private boolean hasWorkingPapers(final BigDecimal projectId,
                                     final boolean merge) {
        DataQuery query =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfWorkingPapersOfSciProject");
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

    public SciProjectPublicationsCollection getPublications() {
        return new SciProjectPublicationsCollection((DataCollection) get(
                PUBLICATIONS));
    }

    public void addPublication(final Publication publication) {
        Assert.exists(publication, Publication.class);

        DataObject link = add(PUBLICATIONS, publication);
        link.set("publicationOrder", Integer.valueOf((int) getPublications().
                size()));
        link.save();
    }

    public void removePublication(final Publication publication) {
        Assert.exists(publication, Publication.class);

        remove(PUBLICATIONS, publication);
    }
}

/*
 * Copyright (c) 2010 Jens Pelzetter
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
package com.arsdigita.cms.contentassets;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;

/**
 * This class provides some convenient methods for dealing with the About asset/mixin.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsAboutService {

    public static final String DISCUSSED_BY = "discussedBy";
    public static final String DISCUSSING = "discusses";
    private static final String PUBLICATIONS_ABOUT = "publicationsAbout";

    /**
     * Get all publications which are discussed by the provided publication.
     *
     * @param discussing
     *
     * @return
     */
    public PublicationCollection getDiscussedPublications(final Publication discussing) {
        final PublicationBundle bundle = discussing.getPublicationBundle();

        final DataCollection dataCollection = (DataCollection) bundle.get(DISCUSSING);

        return new PublicationCollection(dataCollection);
    }

    /**
     * Add a discussed publication.
     *
     * @param discussing The publications which is discussing the other publication.
     * @param discussed  The publication discussed by the other publication
     */
    public void addDiscussedPublication(final Publication discussing, final Publication discussed) {
        final PublicationBundle discussingBundle = discussing.getPublicationBundle();
        final PublicationBundle discussedBundle = discussed.getPublicationBundle();

        discussingBundle.add(DISCUSSING, discussedBundle);

        final DataObject discussingAboutDobj = (DataObject) discussing.get(PUBLICATIONS_ABOUT);
        if (discussingAboutDobj == null) {
            SciPublicationsAbout.create(discussing);
        } else {
            final SciPublicationsAbout discussingAbout = new SciPublicationsAbout(
                discussingAboutDobj);
            discussingAbout.update();
        }

        final DataObject discussedAboutDobj = (DataObject) discussed.get(PUBLICATIONS_ABOUT);
        if (discussedAboutDobj == null) {
            SciPublicationsAbout.create(discussed);
        } else {
            final SciPublicationsAbout discussedAbout = new SciPublicationsAbout(discussedAboutDobj);
            discussedAbout.update();
        }
    }

    /**
     * Remove a discussed publication.
     *
     * @param discussing The publications which is discussing the other publication.
     * @param discussed  The publication discussed by the other publication
     */
    public void removeDiscussedPublication(final Publication discussing,
                                           final Publication discussed) {
        final PublicationBundle discussingBundle = discussing.getPublicationBundle();
        final PublicationBundle discussedBundle = discussed.getPublicationBundle();

        discussingBundle.remove(DISCUSSING, discussedBundle);
    }

    /**
     * Retrieves all publications which are discussing the provided publication.
     *
     * @param discussed
     *
     * @return
     */
    public PublicationCollection getDiscussingPublications(final Publication discussed) {
        final PublicationBundle bundle = discussed.getPublicationBundle();

        final DataCollection dataCollection = (DataCollection) bundle.get(DISCUSSED_BY);

        return new PublicationCollection(dataCollection);
    }

    public void addDiscussingPublication(final Publication discussed,
                                         final Publication discussing) {
        final PublicationBundle discussedBundle = discussed.getPublicationBundle();
        final PublicationBundle discussingBundle = discussing.getPublicationBundle();

        discussedBundle.add(DISCUSSED_BY, discussingBundle);
    }

    public void removeDiscussingPublication(final Publication discussed,
                                            final Publication discussing) {
        final PublicationBundle discussedBundle = discussed.getPublicationBundle();
        final PublicationBundle discussingBundle = discussing.getPublicationBundle();

        discussedBundle.remove(DISCUSSED_BY, discussingBundle);
    }

}

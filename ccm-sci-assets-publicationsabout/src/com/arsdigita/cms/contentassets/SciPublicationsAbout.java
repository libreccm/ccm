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

import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsAbout extends ACSObject {

    public static final String BASE_DATA_OBJECT_TYPE
                               = "com.arsdigita.cms.contentassets.SciPublicationsAbout";

    public static final String DISCUSSES = "discusses";
    public static final String DISCUSSED_BY = "discussedBy";
    public static final String OWNER = "owner";

    public SciPublicationsAbout() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public SciPublicationsAbout(final String type) {
        super(type);
    }

    public SciPublicationsAbout(final DataObject dataObject) {
        super(dataObject);
    }

    public static SciPublicationsAbout create(final Publication owner) {
        final PublicationBundle ownerBundle = owner.getPublicationBundle();
        final ItemCollection instances = ownerBundle.getInstances();

        Publication instance;
        while (instances.next()) {
            instance = (Publication) instances.getContentItem();
            createForInstance(instance);
        }

        return new SciPublicationsAbout((DataObject) owner.get(PUBLICATIONS_ABOUT));
    }
    public static final String PUBLICATIONS_ABOUT = "publicationsAbout";

    private static void createForInstance(final Publication instance) {
        final SciPublicationsAbout about = new SciPublicationsAbout();
        about.set("owner", instance);
        about.update();

        about.save();
    }

    public String getDiscussedPublications() {
        return (String) get(DISCUSSES);
    }

    public String getDiscussingPublications() {
        return (String) get(DISCUSSED_BY);
    }

    protected void update() {

        final Publication owner = new Publication((DataObject) get(OWNER));
        final SciPublicationsAboutService service = new SciPublicationsAboutService();

        final PublicationCollection discussedPublications = service.getDiscussedPublications(owner);
        final StringBuilder discussedBuilder = new StringBuilder();
        while (discussedPublications.next()) {
            discussedBuilder.append(discussedPublications.getPublication().getTitle());
            discussedBuilder.append(';');
        }

        final PublicationCollection discussingPublications = service.
            getDiscussingPublications(owner);
        final StringBuilder discussingBuilder = new StringBuilder();
        while (discussingPublications.next()) {
            discussingBuilder.append(discussingPublications.getPublication().getTitle());
            discussingBuilder.append(';');
        }

        final PublicationBundle bundle = owner.getPublicationBundle();
        final ItemCollection instances = bundle.getInstances();
        
        Publication instance;
        SciPublicationsAbout about;
        while(instances.next()) {
            instance = (Publication) instances.getContentItem();
            about = new SciPublicationsAbout((DataObject) instance.get(PUBLICATIONS_ABOUT));
            
            about.set(DISCUSSES, discussedBuilder.toString());
            about.set(DISCUSSED_BY, discussingBuilder.toString());
        }
    }

}

/*
 * Copyright (c) 2014 Jens Pelzetter
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
 * Helper class/asset used to provide an easy and efficient method for filtering publications
 * for discussed publications. For this purpose, this asset contains two string properties which
 * will contain the concatenated titles of the publications which are discussing the owing 
 * publications and the concatenated titles of the publications which are discussed by the owing
 * publication. This allows it to use a simple substring (LIKE in SQL) to filter a list of 
 * publications for discussed or discussing publications.
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
    public static final String PUBLICATIONS_ABOUT = "publicationsAbout";

    /**
     * Default constructor for a new item
     */
    public SciPublicationsAbout() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor which would be called from the parameterless constructor of a subtype.
     * 
     * @param type The base data object type of the new object.
     */
    public SciPublicationsAbout(final String type) {
        super(type);
    }

    /**
     * Creates a new domain object of this class using the data object provided.
     * 
     * @param dataObject A {@link DataObject} representing an object of this class.
     */
    public SciPublicationsAbout(final DataObject dataObject) {
        super(dataObject);
    }

    /**
     * Helper method for creating a new asset of this type. 
     * 
     * @param owner The owner of the asset
     * @return The new asset
     */
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
    
    /**
     * Helper method for {@link #create(com.arsdigita.cms.contenttypes.Publication)}.
     * 
     * @param instance 
     */
    private static void createForInstance(final Publication instance) {
        final SciPublicationsAbout about = new SciPublicationsAbout();
        about.set(OWNER, instance);
        about.update();

        about.save();
    }

    /**
     * 
     * @return The discussed publications string.
     */
    public String getDiscussedPublications() {
        return (String) get(DISCUSSES);
    }

    /**
     *
     * 
     * @return The discussing publications string.
     */
    public String getDiscussingPublications() {
        return (String) get(DISCUSSED_BY);
    }

    /**
     * This method is invoked by the {@link SciPublicationsAboutService} class when a discussing or
     * discussed publication is added or removed to update the string properties. 
     * 
     */
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

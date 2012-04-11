/*
 * Copyright (c) 2010 Jens Pelzetter,
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

import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.ui.PublicationWithPublisherExtraXmlGenerator;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * <p>
 * This class defines an content type which represents a publication with a
 * publisher. The content type has three additional properties:
 * </p>
 * <ul>
 * <li>ISBN</li>
 * <li>volume</li>
 * <li>numberOfVolumes</li>
 * <li>numberOfPages</li>
 * <li>edition</li>
 * <li>Publisher</li>
 * </ul>
 * <p>
 * For more details please refer to the documentation of the getter methods for
 * these attributes.
 * </p>
 *
 * @author Jens Pelzetter
 */
public class PublicationWithPublisher extends Publication {

    public final static String ISBN = "isbn";
    public static final String VOLUME = "volume";
    public static final String NUMBER_OF_VOLUMES = "numberOfVolumes";
    public static final String NUMBER_OF_PAGES = "numberOfPages";
    public static final String EDITION = "edition";
    public final static String PUBLISHER = "publisher";
    public final static String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.PublicationWithPublisher";
    private static final Logger s_log = Logger.getLogger(
            PublicationWithPublisher.class);

    public PublicationWithPublisher() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public PublicationWithPublisher(BigDecimal id)
            throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public PublicationWithPublisher(OID oid)
            throws DataObjectNotFoundException {
        super(oid);
    }

    public PublicationWithPublisher(DataObject obj) {
        super(obj);
    }

    public PublicationWithPublisher(String type) {
        super(type);
    }

    public PublicationWithPublisherBundle getPublicationWithPublisherBundle() {
        return (PublicationWithPublisherBundle) getContentBundle();
    }

    /**
     * Returns the ISBN of the publication.
     *
     * @return The ISBN of the publication.
     */
    public String getISBN() {
        return (String) get(ISBN);
    }

    /**
     * Sets the ISBN. Attention: This method does not check if the ISBN is 
     * valid yet!
     *
     * @param isbn New ISBN
     */
    public void setISBN(String isbn) {
        set(ISBN, isbn);
    }

    public Integer getVolume() {
        return (Integer) get(VOLUME);
    }

    public void setVolume(Integer volume) {
        set(VOLUME, volume);
    }

    public Integer getNumberOfVolumes() {
        return (Integer) get(NUMBER_OF_VOLUMES);
    }

    public void setNumberOfVolumes(Integer numberOfVolumes) {
        set(NUMBER_OF_VOLUMES, numberOfVolumes);
    }

    public Integer getNumberOfPages() {
        return (Integer) get(NUMBER_OF_PAGES);
    }

    public void setNumberOfPages(Integer numberOfPages) {
        set(NUMBER_OF_PAGES, numberOfPages);
    }

    public String getEdition() {
        return (String) get(EDITION);
    }

    public void setEdition(String edition) {
        set(EDITION, edition);
    }

    /**
     * Retrieves the publisher of the publication.
     *
     * @return The publisher of the publication.
     */
    public Publisher getPublisher() {
//        DataCollection collection;
//
//        collection = (DataCollection) get(PUBLISHER);
//
//        if (0 == collection.size()) {
//            return null;
//        } else {
//            DataObject dobj;
//
//            collection.next();
//            dobj = collection.getDataObject();
//            collection.close();
//
//            return (Publisher) DomainObjectFactory.newInstance(dobj);
//        }        

        final PublisherBundle bundle = getPublicationWithPublisherBundle().
                getPublisher();
        if (bundle == null) {
            return null;
        } else {
            return (Publisher) bundle.getPrimaryInstance();
        }
    }

    public Publisher getPublisher(final String language) {
         final PublisherBundle bundle = getPublicationWithPublisherBundle().
                getPublisher();
        if (bundle == null) {
            return null;
        } else {
            return (Publisher) bundle.getInstance(language);
        }
    }

    /**
     * Links a publisher to the publication.
     *
     * @param publisher The publisher of the publication.
     */
    public void setPublisher(Publisher publisher) {
//        final Publisher oldPublisher;
//
//        oldPublisher = getPublisher();
//        if (oldPublisher != null) {
//            remove(PUBLISHER, oldPublisher);
//        }
//
//        if (null != publisher) {
//            Assert.exists(publisher, Publisher.class);
//            DataObject link = add(PUBLISHER, publisher);
//            link.set("publisherOrder", 1);
//            link.save();
//        }

        getPublicationWithPublisherBundle().setPublisher(publisher);
    }
    
    @Override
    public List<ExtraXMLGenerator> getExtraXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraXMLGenerators();
        generators.add(new PublicationWithPublisherExtraXmlGenerator());
        return generators;
    }
    
    @Override
    public List<ExtraXMLGenerator> getExtraListXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraXMLGenerators();
        generators.add(new PublicationWithPublisherExtraXmlGenerator());
        return generators;
    }
}

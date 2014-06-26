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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.CustomCopy;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.ItemCopier;
import com.arsdigita.cms.XMLDeliveryCache;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 * Special {@link ContentBundle} adding the associations of the Movie content type.
 *
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsMovieBundle extends PublicationBundle {

    public static final String BASE_DATA_OBJECT_TYPE
                                   = "com.arsdigita.cms.contenttypes.SciPublicationsMovieBundle";
    public static final String DIRECTOR = "director";
    public static final String DIRECTOR_ORDER = "directorOrder";
    public static final String PRODUCTION_COMPANY
                                   = "productionCompany";
    public static final String PRODUCTION_COMPANY_ORDER = "companyOrder";

    public SciPublicationsMovieBundle(final ContentItem primary) {

        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        setName(primary.getName());
    }

    public SciPublicationsMovieBundle(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public SciPublicationsMovieBundle(final BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SciPublicationsMovieBundle(final DataObject dataObject) {
        super(dataObject);
    }

    public SciPublicationsMovieBundle(final String type) {
        super(type);
    }

    /**
     * Used for publishing the item. Takes care of the special associations.
     *
     * @param source
     * @param property
     * @param copier
     *
     * @return
     */
    @Override
    public boolean copyProperty(final CustomCopy source,
                                final Property property,
                                final ItemCopier copier) {

        final String attribute = property.getName();
        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {

            final PublicationBundle pubBundle = (PublicationBundle) source;

            if (DIRECTOR.equals(attribute)) {

                final DataCollection directors = (DataCollection) pubBundle.get(DIRECTOR);

                while (directors.next()) {
                    createDirectorAssoc(directors);
                }

                return true;

            } else if (PRODUCTION_COMPANY.equals(attribute)) {

                final DataCollection companies = (DataCollection) pubBundle.get(PRODUCTION_COMPANY);

                while (companies.next()) {
                    createProductionCompanyAssoc(companies);
                }

                return true;

            } else {
                return super.copyProperty(source, property, copier);
            }
        } else {
            return super.copyProperty(source, property, copier);
        }
    }

    /**
     * Internal method used in the publication process.
     *
     * @param directors
     */
    private void createDirectorAssoc(final DataCollection directors) {

        final GenericPersonBundle draftDirector = (GenericPersonBundle) DomainObjectFactory
            .newInstance(
                directors.getDataObject());
        final GenericPersonBundle liveDirector = (GenericPersonBundle) draftDirector
            .getLiveVersion();

        if (liveDirector != null) {
            final DataObject link = add(DIRECTOR, liveDirector);

            link.set(DIRECTOR_ORDER, directors.get(SciPublicationsDirectorCollection.LINK_ORDER));

            link.save();
        }

    }

    /**
     * Internal method used in the publication process.
     *
     * @param companies
     */
    private void createProductionCompanyAssoc(final DataCollection companies) {

        final GenericOrganizationalUnitBundle draftCompany
                                                  = (GenericOrganizationalUnitBundle) DomainObjectFactory
            .newInstance(
                companies.getDataObject());
        final GenericOrganizationalUnitBundle liveCompany
                                                  = (GenericOrganizationalUnitBundle) draftCompany
            .getLiveVersion();

        if (liveCompany != null) {
            final DataObject link = add(PRODUCTION_COMPANY, liveCompany);

            link.set(PRODUCTION_COMPANY_ORDER, companies.get(
                     SciPublicationsProductionCompanyCollection.LINK_ORDER));

            link.save();
        }

    }

    /**
     * Used in the publication process.
     *
     * @param source
     * @param liveItem
     * @param property
     * @param copier
     *
     * @return
     */
    @Override
    public boolean copyReverseProperty(final CustomCopy source,
                                       final ContentItem liveItem,
                                       final Property property,
                                       final ItemCopier copier) {

        final String attribute = property.getName();
        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {

            if (("directedMovie".equals(attribute)) && (source instanceof GenericPersonBundle)) {

                final GenericPersonBundle directorBundle = (GenericPersonBundle) source;
                final DataCollection movies = (DataCollection) directorBundle.get("directedMovie");

                while (movies.next()) {
                    createDirectorMovieAssociation(movies, (GenericPersonBundle) liveItem);
                }

                return true;
            } else if (("producedMovie".equals(attribute))
                           && (source instanceof GenericOrganizationalUnitBundle)) {

                final GenericOrganizationalUnitBundle companyBundle
                                                          = (GenericOrganizationalUnitBundle) source;
                final DataCollection movies = (DataCollection) companyBundle.get("producedMovie");

                while (movies.next()) {
                    createCompanyMovieAssociation(movies, (GenericOrganizationalUnitBundle) liveItem);
                }

                return true;
            } else {
                return super.copyReverseProperty(source, liveItem, property, copier);
            }

        } else {
            return super.copyReverseProperty(source, liveItem, property, copier);
        }

    }

    private void createDirectorMovieAssociation(final DataCollection movies,
                                                final GenericPersonBundle director) {

        final PublicationBundle draftMovie = (PublicationBundle) DomainObjectFactory.newInstance(
            movies.getDataObject());
        final PublicationBundle liveMovie = (PublicationBundle) draftMovie.getLiveVersion();

        if (liveMovie != null) {
            final DataObject link = director.add("directedMovie", liveMovie);
            link.set("directorOrder", movies.get("link.directorOrder"));
            link.save();

            XMLDeliveryCache.getInstance().removeFromCache(liveMovie.getOID());
        }

    }

    private void createCompanyMovieAssociation(final DataCollection movies,
                                               final GenericOrganizationalUnitBundle company) {

        final PublicationBundle draftMovie = (PublicationBundle) DomainObjectFactory
            .newInstance(movies.getDataObject());
        final PublicationBundle liveMovie = (PublicationBundle) draftMovie
            .getLiveVersion();

        if (liveMovie != null) {

            final DataObject link = company.add("producedMovie", liveMovie);
            link.set("companyOrder", movies.get("link.companyOrder"));
            link.save();

            XMLDeliveryCache.getInstance().removeFromCache(liveMovie.getOID());
        }

    }

    /**
     * Get the director of the movie.
     *
     * @return
     */
    public GenericPersonBundle getDirector() {
        final DataCollection collection = (DataCollection) get(DIRECTOR);

        if (collection.size() == 0) {
            return null;
        } else {
            final DataObject dataObject;

            collection.next();
            dataObject = collection.getDataObject();
            collection.close();

            return (GenericPersonBundle) DomainObjectFactory.newInstance(dataObject);
        }
    }

    /**
     * Set the director of the movie.
     *
     * @param director
     */
    public void setDirector(final GenericPerson director) {
        final GenericPersonBundle oldDirector = getDirector();

        if (oldDirector != null) {
            remove(DIRECTOR, oldDirector);
        }

        if (director != null) {
            Assert.exists(director, GenericPerson.class);

            final DataObject link = add(DIRECTOR,
                                        director.getGenericPersonBundle());
            link.set(DIRECTOR_ORDER, Integer.valueOf(1));
            link.save();
        }
    }

    /**
     * Internal method. It is necessary to work with a collection even for a 1:1 association due to
     * a bug in PDL.
     *
     * @return
     */
    protected SciPublicationsDirectorCollection getDirectors() {
        return new SciPublicationsDirectorCollection((DataCollection) get(DIRECTOR));
    }

    /**
     * Internal method. It is necessary to work with a collection even for a 1:1 association due to
     * a bug in PDL.
     *
     * @param director
     */
    protected void addDirector(final GenericPerson director) {
        Assert.exists(director, GenericPerson.class);

        final DataObject link = add(DIRECTOR, director.getGenericPersonBundle());

        link.set(DIRECTOR_ORDER, Integer.valueOf((int) getDirectors().size()));

        updateDirectorsStr();
    }

    /**
     * Internal method. It is necessary to work with a collection even for a 1:1 association due to
     * a bug in PDL.
     *
     * @param director
     */
    protected void removeDirector(final GenericPerson director) {
        Assert.exists(director, GenericPerson.class);

        remove(DIRECTOR, director.getContentBundle());

        updateDirectorsStr();
    }

    /**
     * Updates the directorStr property in all instances which contains a string with the names of
     * all directors for easy filtering.
     */
    protected void updateDirectorsStr() {

        final SciPublicationsDirectorCollection directors = getDirectors();
        final StringBuilder builder = new StringBuilder();
        while (directors.next()) {
            if (builder.length() > 0) {
                builder.append("; ");
            }
            builder.append(directors.getSurname());
            builder.append(", ");
            builder.append(directors.getGivenName());
        }

        final String directorStr = builder.toString();

        final ItemCollection instances = getInstances();

        SciPublicationsMovie movie;
        while (instances.next()) {
            movie = (SciPublicationsMovie) instances.getDomainObject();
            movie.set(SciPublicationsMovie.DIRECTORS_STR, directorStr);
        }

    }

    /**
     * Retrieves the production company of a movie.
     *
     * @return
     */
    public GenericOrganizationalUnitBundle getProductionCompany() {
        final DataCollection collection = (DataCollection) get(PRODUCTION_COMPANY);

        if (collection.size() == 0) {
            return null;
        } else {
            final DataObject dataObject;

            collection.next();
            dataObject = collection.getDataObject();
            collection.close();

            return (GenericOrganizationalUnitBundle) DomainObjectFactory.newInstance(dataObject);
        }
    }

    /**
     * Sets the production company of a movie.
     *
     * @param productionCompany
     */
    public void setProductionCompany(final GenericOrganizationalUnit productionCompany) {
        final GenericOrganizationalUnitBundle oldCompany = getProductionCompany();

        if (oldCompany != null) {
            remove(PRODUCTION_COMPANY, oldCompany);
        }

        if (productionCompany != null) {
            Assert.exists(productionCompany, GenericOrganizationalUnit.class);

            final DataObject link = add(PRODUCTION_COMPANY,
                                        productionCompany.getGenericOrganizationalUnitBundle());
            link.set(PRODUCTION_COMPANY_ORDER, Integer.valueOf(1));
            link.save();
        }
    }

    /**
     * Internal method. It is necessary to work with a collection even for a 1:1 association due to
     * a bug in PDL.
     *
     * @return
     */
    protected SciPublicationsProductionCompanyCollection getProductionCompanies() {

        return new SciPublicationsProductionCompanyCollection((DataCollection) get(
            PRODUCTION_COMPANY));

    }

    /**
     * Internal method. It is necessary to work with a collection even for a 1:1 association due
     * to a bug in PDL.
     * @param company 
     */
    protected void addProductionCompany(final GenericOrganizationalUnit company) {

        Assert.exists(company, GenericOrganizationalUnit.class);

        final DataObject link = add(PRODUCTION_COMPANY,
                                    company.getGenericOrganizationalUnitBundle());
        link.set(PRODUCTION_COMPANY, Integer.valueOf((int) getProductionCompanies().size()));
        link.save();

    }

    /**
     * Internal method. It is necessary to work with a collection even for a 1:1 association due
     * to a bug in PDL.
     * @param company 
     */
    protected void removeProductionCompany(final GenericOrganizationalUnit company) {

        Assert.exists(company, GenericOrganizationalUnit.class);

        remove(PRODUCTION_COMPANY, company.getGenericOrganizationalUnitBundle());

    }

    /**
     * Helper method for retrieving all movies directed by a person.
     * 
     * @param director The director (person) which movies shall be retrieved.
     * @return A collection of a movies directed by the person.
     */
    public static PublicationBundleCollection getDirectedMovies(final GenericPerson director) {

        final GenericPersonBundle directorBundle = director.getGenericPersonBundle();

        final DataCollection collection = (DataCollection) directorBundle.get("directedMovie");

        return new PublicationBundleCollection(collection);

    }

    /**
     * Helper method for retrieving all movies produced a company.
     * 
     * @param company The company 
     * @return A collection of all movies produced by the company/organisation.
     */
    public static PublicationBundleCollection getProducedMovies(
        final GenericOrganizationalUnit company) {

        final GenericOrganizationalUnitBundle companyBundle = company
            .getGenericOrganizationalUnitBundle();

        final DataCollection collection = (DataCollection) companyBundle.get("producedMovie");

        return new PublicationBundleCollection(collection);

    }

    /**
     * Gets the primary instance of the movie.
     * 
     * @return 
     */
    public SciPublicationsMovie getMovie() {
        return (SciPublicationsMovie) getPrimaryInstance();
    }

    /**
     * Gets a specific language variant of the movie.
     * 
     * @param language
     * @return 
     */
    public SciPublicationsMovie getMovie(final String language) {

        SciPublicationsMovie result = (SciPublicationsMovie) getInstance(language);
        if (result == null) {
            result = getMovie();
        }

        return result;

    }

}

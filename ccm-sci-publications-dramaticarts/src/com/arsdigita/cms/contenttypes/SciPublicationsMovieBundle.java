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
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsMovieBundle extends PublicationBundle {

    public static final String BASE_BASE_OBJECT_TYPE
                                   = "com.arsdigita.cms.contenttypes.SciPublicationsMovieBundle";
    public static final String DIRECTOR = "director";
    public static final String DIRECTOR_ORDER = "directorOrder";
    public static final String PRODUCATION_COMPANY
                                   = "producationCompany";
    public static final String PRODUCATION_COMPANY_ORDER = "companyOrder";

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
        this(new OID(BASE_BASE_OBJECT_TYPE, id));
    }

    public SciPublicationsMovieBundle(final DataObject dataObject) {
        super(dataObject);
    }

    public SciPublicationsMovieBundle(final String type) {
        super(type);
    }

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

            } else if (PRODUCATION_COMPANY.equals(attribute)) {

                final DataCollection companies = (DataCollection) pubBundle.get(PRODUCATION_COMPANY);

                while (companies.next()) {
                    createProductionCompanyAssoc(companies);
                }

                return true;

            } else {
                return super.copyProperty(source, null, copier);
            }
        } else {
            return super.copyProperty(source, null, copier);
        }
    }

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

    private void createProductionCompanyAssoc(final DataCollection companies) {

        final GenericOrganizationalUnitBundle draftCompany
                                                  = (GenericOrganizationalUnitBundle) DomainObjectFactory
            .newInstance(
                companies.getDataObject());
        final GenericOrganizationalUnitBundle liveCompany
                                                  = (GenericOrganizationalUnitBundle) draftCompany
            .getLiveVersion();

        if (liveCompany != null) {
            final DataObject link = add(PRODUCATION_COMPANY, liveCompany);

            link.set(PRODUCATION_COMPANY_ORDER, companies.get(
                     SciPublicationsProductionCompanyCollection.LINK_ORDER));

            link.save();
        }

    }

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

    public SciPublicationsDirectorCollection getDirectors() {
        return new SciPublicationsDirectorCollection((DataCollection) get(DIRECTOR));
    }
    
    public void addDirector(final GenericPerson director) {
        Assert.exists(director, GenericPerson.class);
        
        final DataObject link = add(DIRECTOR, director.getGenericPersonBundle());
        
        link.set(DIRECTOR_ORDER, Integer.valueOf((int) getDirectors().size()));
        
        updateDirectorsStr();
    }
    
    public void removeDirector(final GenericPerson director) {
        Assert.exists(director, GenericPerson.class);
        
        remove(DIRECTOR, director.getContentBundle());
        
        updateDirectorsStr();
    }
    
    protected void updateDirectorsStr() {
        
        final SciPublicationsDirectorCollection directors = getDirectors();
        final StringBuilder builder = new StringBuilder();
        while(directors.next()) {
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
        while(instances.next()) {
            movie = (SciPublicationsMovie) instances.getDomainObject();
            movie.set(SciPublicationsMovie.DIRECTORS_STR, directorStr);
        }

        
    }
    
    public SciPublicationsProductionCompanyCollection getProductionCompanies() {
        
        return new SciPublicationsProductionCompanyCollection((DataCollection) get(PRODUCATION_COMPANY));
        
    }
    
    public void addProducationCompany(final GenericOrganizationalUnit company) {
        
        Assert.exists(company, GenericOrganizationalUnit.class);
        
        final DataObject link = add(PRODUCATION_COMPANY, 
                                    company.getGenericOrganizationalUnitBundle());
        link.set(PRODUCATION_COMPANY, Integer.valueOf((int) getProductionCompanies().size()));
        link.save();
        
    }
    
    public void removeProductionCompany(final GenericOrganizationalUnit company) {
        
        Assert.exists(company, GenericOrganizationalUnit.class);
        
        remove(PRODUCATION_COMPANY, company.getGenericOrganizationalUnitBundle());
        
    }
    
    public static PublicationBundleCollection getDirectedMovies(final GenericPerson director) {
        
        final GenericPersonBundle directorBundle = director.getGenericPersonBundle();
        
        final DataCollection collection = (DataCollection) directorBundle.get("directedMovie");
        
        return new PublicationBundleCollection(collection);
        
    }
    
    public static PublicationBundleCollection getProducedMovies(
        final GenericOrganizationalUnit company) {
        
        final GenericOrganizationalUnitBundle companyBundle = company.getGenericOrganizationalUnitBundle();
        
        final DataCollection collection = (DataCollection) companyBundle.get("producedMovie");
        
        return new PublicationBundleCollection(collection);
        
    }
    
    public SciPublicationsMovie getMovie() {
        return (SciPublicationsMovie) getPrimaryInstance();
    }
    
    public SciPublicationsMovie getMovie(final String language) {
        
        SciPublicationsMovie result = (SciPublicationsMovie) getInstance(language);
        if (result == null) {
            result = getMovie();
        }
        
        return result;
        
    }
}

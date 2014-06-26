/*
 * Copyright (c) 2014 Jens Pelzetter,
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

import com.arsdigita.cms.contenttypes.ui.SciPublicationsMovieExtraXMLGenerator;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import java.util.List;

/**
 * Domain class for the the SciPublications/Movie content type. The content type represents a movie
 * which can be managed like other publication types. Most of the properties are inherited from
 * {@link Publication}. The only properties added by this content type are the director of the movie
 * and the production company of the movie.
 * 
 * Note: The authors of (the script of the) movie are usually called writers and not authors, but
 * the keep this implementation and the inheritance hierarchy clean we don't define a writers 
 * property here. 
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsMovie extends Publication {

    public static final String BASE_DATA_OBJECT_TYPE
                                   = "com.arsdigita.cms.contenttypes.SciPublicationsMovie";
    public static final String DIRECTORS_STR = "directorsStr";

    public SciPublicationsMovie() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public SciPublicationsMovie(final BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SciPublicationsMovie(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public SciPublicationsMovie(final DataObject obj) {
        super(obj);
    }

    public SciPublicationsMovie(final String type) {
        super(type);
    }

    /**
     * Get the special content bundle of this instance.
     * 
     * @return 
     */
    public SciPublicationsMovieBundle getSciPublicationsMovieBundle() {
        return (SciPublicationsMovieBundle) getContentBundle();
    }

    /**
     * Retrieves the director of the movie from the bundle. The method will return the primary
     * instance of person item which represents the director.
     * 
     * @return 
     */
    public GenericPerson getDirector() {
        final GenericPersonBundle bundle = getSciPublicationsMovieBundle().getDirector();
        if (bundle == null) {
            return null;
        } else {
            return (GenericPerson) bundle.getPrimaryInstance();
        }
    }

    /**
     * Retrieves the director of the movie from the bundle. This method returns the instance 
     * of the person item which represents the director for a specific language.
     * 
     * @see ContentBundle#getInstance(String)
     * 
     * @param language
     * @return 
     */
    public GenericPerson getDirector(final String language) {
        final GenericPersonBundle bundle = getSciPublicationsMovieBundle().getDirector();
        if (bundle == null) {
            return null;
        } else {
            return (GenericPerson) bundle.getInstance(language);
        }
    }

    /**
     * Convenient method for setting the director.
    */
    public void setDirector(final GenericPerson director) {
        getSciPublicationsMovieBundle().setDirector(director);
    }

    /**
     * Retrieves the production of the movie from the bundle. The method will return the primary
     * instance of generic organizational unit item which represents the producation company. 
     * 
     * @return 
     */
    public GenericOrganizationalUnit getProductionCompany() {
        final GenericOrganizationalUnitBundle bundle = getSciPublicationsMovieBundle()
            .getProductionCompany();

        if (bundle == null) {
            return null;
        } else {
            return (GenericOrganizationalUnit) bundle.getPrimaryInstance();
        }
        
    }
    
    /**
     * Retrieves the producation company of the movie from the bundle. This method returns the 
     * instance of the generic organizational unit item which represents the production company for 
     * a specific language.
     * 
     * @see ContentBundle#getInstance(String)
     * 
     * @param language
     * @return 
     */
    public GenericOrganizationalUnit getProductionCompany(final String language) {
        final GenericOrganizationalUnitBundle bundle = getSciPublicationsMovieBundle()
            .getProductionCompany();

        if (bundle == null) {
            return null;
        } else {
            return (GenericOrganizationalUnit) bundle.getInstance(language);
        }
        
    }
    
    /**
     * Convenient method for setting the production company.
     * 
     * @param company 
     */
    public void setProductionCompany(final GenericOrganizationalUnit company) {
        getSciPublicationsMovieBundle().setProductionCompany(company);
    }

    /**
     * Internal method. Using a collection even for the 1:1 association is necessary due to a
     * bug in PDL.
     * 
     * @return 
     */
    private SciPublicationsDirectorCollection getDirectors() {
        return getSciPublicationsMovieBundle().getDirectors();
    }

    /**
     * Internal method. Using a collection even for the 1:1 association is necessary due to a
     * bug in PDL.
     * 
     * @param director 
     */
    private void addDirector(final GenericPerson director) {
        getSciPublicationsMovieBundle().addDirector(director);
    }

    /**
     * Internal method. Using a collection even for the 1:1 association is necessary due to a
     * bug in PDL.
     * 
     * @param director 
     */
    private void removeDirector(final GenericPerson director) {
        getSciPublicationsMovieBundle().removeDirector(director);
    }

    /**
     * Internal method. Using a collection even for the 1:1 association is necessary due to a
     * bug in PDL.
     * 
     * @param director 
     */
    private void swapWithPreviousDirector(final GenericPerson director) {

        getDirectors().swapWithPrevious(director);
        getSciPublicationsMovieBundle().updateDirectorsStr();

    }

    /**
     * Internal method. Using a collection even for the 1:1 association is necessary due to a
     * bug in PDL.
     * 
     * @param director 
     */
    private void swapWithNextDirector(final GenericPerson director) {

        getDirectors().swapWithNext(director);
        getSciPublicationsMovieBundle().updateDirectorsStr();

    }

    /**
     * Internal method. Using a collection even for the 1:1 association is necessary due to a
     * bug in PDL.
     */
    private boolean hasDirectors() {
        return !getDirectors().isEmpty();
    }

    /**
     * Internal method. Using a collection even for the 1:1 association is necessary due to a
     * bug in PDL.
     */
    private SciPublicationsProductionCompanyCollection getProductionCompanies() {
        return getSciPublicationsMovieBundle().getProductionCompanies();
    }

    /**
     * Internal method. Using a collection even for the 1:1 association is necessary due to a
     * bug in PDL.
     * 
     * @param company 
     */
    private void addProductionCompany(final GenericOrganizationalUnit company) {
        getSciPublicationsMovieBundle().addProductionCompany(company);
    }

    /**
     * Internal method. Using a collection even for the 1:1 association is necessary due to a
     * bug in PDL.
     * 
     * @param company 
     */
    private void removeProductionCompany(final GenericOrganizationalUnit company) {
        getSciPublicationsMovieBundle().removeProductionCompany(company);
    }

    /**
     * Internal method. Using a collection even for the 1:1 association is necessary due to a
     * bug in PDL.
     * @return 
     */
    private boolean hasProductionCompanies() {
        return !getProductionCompanies().isEmpty();
    }

    @Override
    public List<ExtraXMLGenerator> getExtraXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraXMLGenerators();
        generators.add(new SciPublicationsMovieExtraXMLGenerator());
        return generators;
    }

    @Override
    public List<ExtraXMLGenerator> getExtraListXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraListXMLGenerators();
        generators.add(new SciPublicationsMovieExtraXMLGenerator());
        return generators;
    }

    @Override
    public String getSearchSummary() {
        return String.format("%s %s", super.getSearchSummary(), get(DIRECTORS_STR));
    }

}

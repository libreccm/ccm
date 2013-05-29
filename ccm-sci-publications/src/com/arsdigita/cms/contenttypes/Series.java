/*
 * Copyright (c) 2010 Jens Pelzetter, for the Center of Social Politics of the University of Bremen
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

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.ui.SeriesExtraXmlGenerator;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Jens Pelzetter
 */
public class Series extends ContentPage {

    public static final String EDITORS = "editors";
    public static final String EDITOR_FROM = "dateFrom";
    public static final String EDITOR_TO = "dateTo";
    public static final String EDITOR_ORDER = "editor_order";
    public static final String PUBLICATIONS = "publications";
    public static final String VOLUME_OF_SERIES = "volumeOfSeries";
    public static final String ABSTRACT  ="abstract";

    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.Series";

    public Series() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public Series(BigDecimal id)  throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Series(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Series(DataObject obj ) {
        super(obj);
    }

    public Series(String type) {
        super(type);
    }

    public String getAbstract() {
        return (String) get(ABSTRACT);
    }

    public void setAbstract(String abstractStr) {
        set(ABSTRACT, abstractStr);
    }

    public SeriesBundle getSeriesBundle() {
        return (SeriesBundle) getContentBundle();
    }
    
    public EditshipCollection getEditors() {      
        return getSeriesBundle().getEditors();
    }

    public void addEditor(final GenericPerson editor,
                          final Date from,
                          final Boolean fromSkipMonth,
                          final Boolean fromSkipDay,
                          final Date to,
                          final Boolean toSkipMonth,
                          final Boolean toSkipDay) {        
        getSeriesBundle().addEditor(editor, 
                                    from, 
                                    fromSkipMonth, 
                                    fromSkipDay, 
                                    to,
                                    toSkipMonth, 
                                    toSkipDay);
    }

    public void removeEditor(final GenericPerson editor) {      
        getSeriesBundle().removeEditor(editor);
    }

    public boolean hasEditors() {
        return !this.getEditors().isEmpty();
    }

    public VolumeInSeriesCollection getVolumes() {        
        return getSeriesBundle().getVolumes();
    }

    public void addVolume(final Publication publication, final String volume) {
        getSeriesBundle().addVolume(publication, volume);
    }

    public void removeVolume(final Publication publication) {        
        getSeriesBundle().removeVolume(publication);
    }

    public boolean hasVolumes() {
        return !this.getVolumes().isEmpty();
    }
    
    @Override
    public List<ExtraXMLGenerator> getExtraXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraXMLGenerators();
        generators.add(new SeriesExtraXmlGenerator());
        return generators;
    }
    
    @Override
    public List<ExtraXMLGenerator> getExtraListXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraListXMLGenerators();
        final ExtraXMLGenerator generator = new SeriesExtraXmlGenerator();
        generator.setListMode(true);
        return generators;
    }

}

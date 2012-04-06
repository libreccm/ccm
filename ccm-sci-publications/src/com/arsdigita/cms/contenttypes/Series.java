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
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import java.util.Date;

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
        //return new EditshipCollection((DataCollection) get(EDITORS));
        return getSeriesBundle().getEditors();
    }

    public void addEditor(final  GenericPerson editor, 
                          final Date from, 
                          final Date to) {
//        Assert.exists(editor, GenericPerson.class);
//
//        DataObject link = add(EDITORS, editor);
//        link.set(EDITOR_FROM, from);
//        link.set(EDITOR_TO, to);
//        link.set(EDITOR_ORDER, Integer.valueOf((int)getEditors().size()));
        
        getSeriesBundle().addEditor(editor, from, to);
    }

    public void removeEditor(final GenericPerson editor) {
        //Assert.exists(editor, GenericPerson.class);
        //remove(EDITORS, editor);
        getSeriesBundle().removeEditor(editor);
    }

    public boolean hasEditors() {
        return !this.getEditors().isEmpty();
    }

    public VolumeInSeriesCollection getVolumes() {
        //return new VolumeInSeriesCollection((DataCollection) get(PUBLICATIONS));
        return getSeriesBundle().getVolumes();
    }

    public void addVolume(final Publication publication, final Integer volume) {
//        Assert.exists(publication, Publication.class);
//
//        DataObject link = add(PUBLICATIONS, publication);
//
//        link.set(VOLUME_OF_SERIES, volume);
        getSeriesBundle().addVolume(publication, volume);
    }

    public void removeVolume(final Publication publication) {
        //Assert.exists(publication, Publication.class);
        //remove(PUBLICATIONS, publication);
        getSeriesBundle().removeVolume(publication);
    }

    public boolean hasVolumes() {
        return !this.getVolumes().isEmpty();
    }

}

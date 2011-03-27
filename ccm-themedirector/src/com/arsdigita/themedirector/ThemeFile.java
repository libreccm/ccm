/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.themedirector;

import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;


/**
 *  This represents a single file within a theme and is used as a way
 *  of storing the files in the database so that they can be shared
 *  across multiple servers
 *
 *  @author Randy Graebner %lt;randyg@alum.mit.edu&gt;
 */
public class ThemeFile extends DomainObject {
    
    /** A logger instance.  */
    private static final Logger s_log =
                                Logger.getLogger(ThemeFile.class);

    public static final String BASE_DATA_OBJECT_TYPE = 
        "com.arsdigita.themedirector.ThemeFile";

    public static final String ID = "id";
    public static final String FILE_PATH = "filePath";
    public static final String VERSION = "version";
    public static final String LAST_MODIFIED_DATE = "lastModifiedDate";
    public static final String CONTENT = "content";
    public static final String THEME = "theme";
    public static final String DELETED = "deleted";


    /**
     *  These are the two possible values for the Version
     */
    public static final String LIVE = "live";
    public static final String DRAFT = "draft";


    /**
     * Constructor
     */
    public ThemeFile() {
        this(BASE_DATA_OBJECT_TYPE);
    }
    
    /**
     * Constructor
     * @param type
     */
    public ThemeFile(String type) {
        super(type);
    }

    /** 
     * Constructor
     * @param obj
     */
    public ThemeFile(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor
     * @param theme
     * @param filePath
     */
    public ThemeFile(Theme theme, String filePath) {
        this();
        setTheme(theme);
        setFilePath(filePath);
    }

    public ThemeFile(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public ThemeFile(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Called from base class (DomainObject) constructors.
     */
    @Override
    protected void initialize() {
        super.initialize();
        if (isNew()) {
            if (get(ID) == null) {
                set(ID, generateID());
            }
            setDeleted(false);
        }
    }

    @Override
    protected void beforeSave() {
        super.beforeSave();
        if (getLastModifiedDate() == null) {
            setLastModifiedDate(new Date());
        }
    }

    static BigDecimal generateID() throws PersistenceException {
        try {
            return Sequences.getNextValue();
        } catch (SQLException e) {
            final String errorMsg = "Unable to generate a unique " +
                "ThemeFile id.";
            s_log.error(errorMsg);
            throw PersistenceException.newInstance(errorMsg, e);
        }
    }

    public BigDecimal getID() {
        return (BigDecimal)get(ID);
    }

    public final void setFilePath(String filePath) {
        set(FILE_PATH, filePath);
    }

    public String getFilePath() {
        return(String)get(FILE_PATH);
    }

    public void setVersion(String version) {
        Assert.isTrue(LIVE.equals(version) || DRAFT.equals(version), 
                "The version must be either " + LIVE + " or " + DRAFT +
                " but was actually " + version);
        set(VERSION, version);
    }

    public String getVersion() {
        return(String)get(VERSION);
    }

    public void setContent(byte[] content) {
        set(CONTENT, content);
    }

    public byte[] getContent() {
        return(byte[])get(CONTENT);
    }

    public void setLastModifiedDate(Date lastModified) {
        set(LAST_MODIFIED_DATE, lastModified);
    }

    public Date getLastModifiedDate() {
        return(Date)get(LAST_MODIFIED_DATE);
    }

    public final void setTheme(Theme theme) {
        setAssociation(THEME, theme);
    }

    public Theme getTheme() {
        DataObject object = (DataObject)get(THEME);
        if (object != null) {
            return new Theme(object);
        } else {
            return null;
        }
    }

    public void setDeleted(boolean deleted) {
        set(DELETED, new Boolean(deleted));
    }

    @Override
    public boolean isDeleted() {
        return Boolean.TRUE.equals(get(DELETED));
    }
}

/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.shortcuts;

import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.Session;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * A shortcut
 * @author <a href="mailto:tzumainn@arsdigita.com">Tzu-Mainn Chen</a>
 * @version $Id: Shortcut.java 796 2005-09-12 15:06:53Z fabrice $
 */
public class Shortcut extends DomainObject {

    public static final String SHORTCUT_ID = "shortcutID";
    public static final String URL_KEY = "urlKey";
    public static final String REDIRECT = "redirect";

    public static final String BASE_DATA_OBJECT_TYPE = 
                               "com.arsdigita.shortcuts.Shortcut";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public Shortcut() {
	this(BASE_DATA_OBJECT_TYPE);
    }
    
    public Shortcut(String type) {
	super(type);
    }
    
    public Shortcut(DataObject dataObject) {
        super(dataObject);
    }

    protected Shortcut(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Shortcut(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public static Shortcut create(String url,
				  String target) {
	Shortcut shortcut = new Shortcut();
	
	try {
	    shortcut.setID(Sequences.getNextValue("ss_shortcuts_seq"));
	} catch (SQLException e) {
	    throw new UncheckedWrapperException(e);
        }
	shortcut.setUrlKey(url);
	shortcut.setRedirect(target);
	
	return shortcut;
    }
    
    public static ShortcutCollection retrieveAll() {
        Session session = SessionManager.getSession();
        DataCollection shortcuts = session.retrieve(BASE_DATA_OBJECT_TYPE);
        shortcuts.addOrder(URL_KEY);
		return new ShortcutCollection(shortcuts);
    }

    public static Shortcut findByURL(String url)
	throws DataObjectNotFoundException {
	
        Session session = SessionManager.getSession();
        DataCollection shortcuts = session.retrieve(BASE_DATA_OBJECT_TYPE);
	
        shortcuts.addEqualsFilter(URL_KEY, url);
        
        if (shortcuts.next()) {
            DataObject obj = shortcuts.getDataObject();
            shortcuts.close();
            return new Shortcut(obj);
        }
        
        throw new DataObjectNotFoundException("cannot find shortcut");
    }

    public BigDecimal getID() {
	return (BigDecimal) get(SHORTCUT_ID);
    }

    public void setID(BigDecimal shortcutID) {
        set(SHORTCUT_ID,shortcutID);
    }

    public String getUrlKey() {
	return (String) get(URL_KEY);
    }

    public void setUrlKey(String urlKey) {
        set(URL_KEY, urlKey);
    }

    public String getRedirect() {
	return (String) get(REDIRECT);
    }

    public void setRedirect(String redirect) {
        set(REDIRECT, redirect);
    }
}

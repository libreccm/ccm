/*
 * Copyright (C) 2008 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.london.atoz;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.arsdigita.categorization.Category;
import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.metadata.ObjectType;

public class AtoZCategoryAlias extends DomainObject {
	private static final Logger s_log = Logger.getLogger(AtoZCategoryAlias.class);
	
	public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.london.atoz.AtoZCategoryAlias";
	
    public static final String ID = "id";
    public static final String PROVIDER = "provider";
	public static final String CATEGORY = "category";
	public static final String LETTER = "letter";
	public static final String TITLE = "title";
	
	public AtoZCategoryAlias() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    protected AtoZCategoryAlias(String type) {
        super(type);
    }

    public AtoZCategoryAlias(DataObject obj) {
        super(obj);
    }

    public AtoZCategoryAlias(OID oid) {
        super(oid);
    }

    public void setup(Category category, String letter, String title) {
    	setCategory(category);
    	setLetter(letter);
    	setTitle(title);
    }
    
    /**
     * Called from base class (DomainObject) constructors.
     */
    protected void initialize() {
        super.initialize();

        if (isNew() && get(ID) == null)
                set(ID, generateID());
    }

    public AtoZCategoryProvider getProvider() {
        return (AtoZCategoryProvider)get(PROVIDER);
    }

    public Category getCategory() {
        return new Category((DataObject)get(CATEGORY));
    }

    public void setCategory(Category category) {
        set(CATEGORY, category);
    }

    public String getLetter() {
        return (String)get(LETTER);
    }

    public void setLetter(String letter) {
        set(LETTER, letter);
    }
    
    public String getTitle() {
        return (String)get(TITLE);
    }

    public void setTitle(String title) {
        set(TITLE, title);
    }

    static BigDecimal generateID() throws PersistenceException {
        try {
            return Sequences.getNextValue();
        } catch (SQLException e) {
            final String errorMsg = "Unable to generate a unique " +
                "ACSObject id.";
            s_log.error(errorMsg);
            throw PersistenceException.newInstance(errorMsg, e);
        }
    }
}

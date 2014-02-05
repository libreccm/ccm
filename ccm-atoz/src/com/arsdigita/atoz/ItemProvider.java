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
package com.arsdigita.atoz;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;

import com.arsdigita.categorization.Category;

/**
 *
 *
 */
public class ItemProvider extends AtoZProvider {

  public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.atoz.ItemProvider";

  public static final String CATEGORY = "category";
  public static final String LOAD_PATHS = "loadPaths";

  public static final String ATOMIC_ENTRIES = "com.arsdigita.atoz.getAtomicItemEntries";

  /**
   * Constructor
   */
  public ItemProvider() {
    this(BASE_DATA_OBJECT_TYPE);
  }

  /**
   * Constructor
   * 
   * @param type
   */
  protected ItemProvider(String type) {
    super(type);
  }

  /**
   * Constructor
   * 
   * @param obj
   */
  public ItemProvider(DataObject obj) {
    super(obj);
  }

  /**
   * Constructor
   * 
   * @param oid
   */
  public ItemProvider(OID oid) {
    super(oid);
  }

  public static ItemProvider create(String title,
                                    String description,
                                    Category category) {
    ItemProvider provider = new ItemProvider();
    provider.setup(title, description, category);
    return provider;
  }

  public DataQuery getAtomicEntries() {
    DataQuery items = SessionManager.getSession().retrieveQuery(ATOMIC_ENTRIES);
    items.setParameter("providerID", getID());
    return items;
  }

  protected void setup(String title, String description, Category category) {
    super.setup(title, description);
    setCategory(category);
  }

  public void setCategory(Category category) {
    Assert.exists(category, Category.class);
    set(CATEGORY, category);
  }

  public Category getCategory() {
    if (get(CATEGORY) == null) {
      return null;
    } else {
      return new Category((DataObject) get(CATEGORY));
    }
  }

  public DomainCollection getAliases() {
    DomainCollection aliases = new DomainCollection(SessionManager.getSession()
            .retrieve(ItemAlias.BASE_DATA_OBJECT_TYPE));
    aliases.addFilter("itemProvider = :providerId").set("providerId", getID());
    aliases.addOrder("title");
    return aliases;
  }

  /**
   *
   * @param loadPaths
   */
  public void setLoadPaths(String loadPaths) {
    set(LOAD_PATHS, loadPaths);
  }

  public String getLoadPaths() {
    return (String) get(LOAD_PATHS);
  }

  @Override
  public AtoZGenerator getGenerator() {
    return new ItemGenerator(this);
  }

}

package com.arsdigita.london.cms.freeform.asset;

import com.arsdigita.cms.Asset;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataCollection;
import java.math.BigDecimal;
import com.arsdigita.domain.DomainObjectFactory;



/**
 *
 * Meant to hold assets that I retrieve from compositions 
 * with {@link FreeformContenItem}s.
 *
 * @author slater@arsdigita.com
 *
 **/

public class AssetCollection extends DomainCollection {

  /**
   * Standard Collection
   *
   **/
  public AssetCollection(DataCollection dataCollection) {
    super(dataCollection);
  }


  /**
   * @return the current object in its most specific form (AplawsBinaryAsset, AplawsTextAsset)
   *
   * Despite what the javadoc says, {@link DomainObjectFactory.newInstance()} doesn't 
   * need a registered DomainObjectInstantiator.  As long as it extends ACSObject, 
   * it can use reflection to do its dirty business
   *
   **/


    public Asset getAsset() {
      return (Asset) DomainObjectFactory.newInstance(m_dataCollection.getDataObject());
    }


    /**
   * Return the named property for the current position in the collection. 
   *
   * @param property the property whose value should be returned 
   * @return the value of the property for the current position in the
   * collection.
   */
  public final Object get(String property) {
    return m_dataCollection.get(property);
  }



  /**
   * Return the object ID for the content item at the current position in
   * the collection.
   *
   * @return the object ID for the content item at the current position in
   * the collection.  
   */
  public BigDecimal getID() {
    return (BigDecimal) get(ACSObject.ID);
  }



  /**
   * Filter items by name and leave only those in the collection whose name
   * equals the given value.
   *
   * @param name the name for which items should be filtered.  
   */
  public void addNameFilter(String name) {
    m_dataCollection.addEqualsFilter(ContentItem.NAME, name);
  }



}





package org.libreccm.pagemodel;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoryTreeComponent extends ComponentModel {
    
    public static final String BASE_DATA_OBJECT_TYPE = "org.libreccm.pagemodel.CategoryTreeComponent";
    
    public static final String SHOW_FULL_TREE = "showFullTree";
    
    public CategoryTreeComponent(final DataObject dataObject) {
        
        super(dataObject);
    }
    
    public CategoryTreeComponent(final OID oid) {
        
        super(oid);
    }
    
    public CategoryTreeComponent(final BigDecimal componentModelId) {
        
        this(new OID(BASE_DATA_OBJECT_TYPE, componentModelId));
    }
    
    public Boolean isShowFullTree() {
        
        return (Boolean) get(SHOW_FULL_TREE);
    }
    
    public void setShowFullTree(final Boolean showFullTree) {
        
        set(SHOW_FULL_TREE, showFullTree);
    }
    
}

package org.libreccm.pagemodel;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ComponentModel extends DomainObject {
    
    public static final String BASE_DATA_OBJECT_TYPE = "org.libreccm.pagemodel.ComponentModel";
    
    public static final String COMPONENT_MODEL_ID = "componentModelId";
    
    public static final String UUID = "uuid";
    
    public static final String MODEL_UUID = "modelUuid";
    
    public static final String ID_ATTRIBUTE = "idAttribute";
                                                            
    public static final String CLASS_ATTRIBUTE = "classAttribute";
    
    public static final String STYLE_ATTRIBUTE = "styleAttribute";
    
    public static final String KEY = "key";
    
    public ComponentModel(final DataObject dataObject) {
        super(dataObject);
    }
    
    public ComponentModel(final OID oid) {
        super(oid);
    }
    
    public ComponentModel(final BigDecimal componentModelId) {
        
        this(new OID(BASE_DATA_OBJECT_TYPE, componentModelId));
    }
    
    public BigDecimal getComponentModelId() {
        return (BigDecimal) get(COMPONENT_MODEL_ID);
    }
    
    protected void setComponentModelId(final BigDecimal componentModelId) {
        set(COMPONENT_MODEL_ID, componentModelId);
    }
    
    public String getUuid() {
        return (String) get(UUID);
    }
    
    protected void setUuid(final String uuid) {
        set(UUID, uuid);
    }
    
    public String getModelUuid() {
        return (String) get(MODEL_UUID);
    }
    
    protected void setModelUuid(final String modelUuid) {
        set(MODEL_UUID, modelUuid);
    }
    
    public String getIdAttribute() {
        
        return (String) get(ID_ATTRIBUTE);
    }
    
    public void setIdAttribute(final String idAttribute) {
        
        set(ID_ATTRIBUTE, idAttribute);
    }
    
    public String getClassAttribute() {
        
        return (String) get(CLASS_ATTRIBUTE);
    }
    
    public void setClassAttribute(final String classAttribute) {
        
        set(CLASS_ATTRIBUTE, classAttribute);
    }
    
    public String getStyleAttribute(final String styleAttribute) {
        
        return (String) get(STYLE_ATTRIBUTE);
    }
    
    public void setStyleAttribute(final String styleAttribute) {
        
        set(STYLE_ATTRIBUTE, styleAttribute);
    }
    
    public String getKey() {
        
        return (String) get(KEY);
    }
    
    public void setKey(final String key) {
        
        set(KEY, key);
    }
    
}

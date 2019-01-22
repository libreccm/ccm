/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.pagemodel;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContainerModel extends DomainObject {
    
    public static final String BASE_DATA_OBJECT_TYPE = "org.libreccm.pagemodel.ContainerModel";
    
    public static final String CONTAINER_ID = "containerId";
    
    public static final String UUID = "uuid";
    
    public static final String CONTAINER_UUID = "containerUuid";
    
    public static final String KEY = "key";
    
    public static final String PAGE_MODEL = "pageModel";
    
    public static final String COMPONENT_MODEL = "componentModel";
    
    public ContainerModel(final DataObject dataObject) {
        super(dataObject);
    }
    
    public ContainerModel(final OID oid) {
        super(oid);
    }
    
    public ContainerModel(final BigDecimal containerModelId) {
        this(new OID(BASE_DATA_OBJECT_TYPE, containerModelId));
    }
    
    public BigDecimal getContainerModelId() {
        return (BigDecimal) get(CONTAINER_ID);
    }
    
    protected void setContainerModelId(final BigDecimal containerModelId) {
        
        set(CONTAINER_ID, containerModelId);
    }
    
    public String getUuid() {
        return (String) get(UUID);
    }
    
    protected void setUuid(final String uuid) {
        
        set(UUID, uuid);
    }
    
    public String getContainerUuid() {
        
        return (String) get(CONTAINER_UUID);
    }
    
    protected void setContainerUuid(final String containerUuid) {
        
        set(CONTAINER_UUID, containerUuid);
    }
    
    public String getKey() {
        
        return (String) get(KEY);
    }
    
    public void setKey(final String key) {
        
        set(KEY, key);
    }
    
    public PageModel getPageModel() {
        
        return (PageModel) get(PAGE_MODEL);
    }
    
    protected void setPageModel(final PageModel pageModel) {
        
        setAssociation(PAGE_MODEL, pageModel);
    }
    
    public ComponentModelCollection getComponents() {
        
        final DataCollection dataCollection = (DataCollection) get(
            COMPONENT_MODEL);
        
        return new ComponentModelCollection(dataCollection);
    }
    
    protected void addComponent(final ComponentModel component) {
        
        add(COMPONENT_MODEL, component);
    }
    
    protected void removeComponent(final ComponentModel component) {
        
        remove(COMPONENT_MODEL, component);
    }
    
}

package org.libreccm.pagemodel;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.Application;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class PageModel extends DomainObject {
    
    public static final String BASE_DATA_OBJECT_TYPE = "org.libreccm.pagemodel.PageModel";
    
    public static final String PAGE_MODEL_ID = "pageModelId";
    
    public static final String UUID = "uuid";
    
    public static final String MODEL_UUID = "modelUuid";
    
    public static final String NAME = "name";
    
    public static final String PAGE_MODEL_VERSION = "pageModelVersion";
    
    public static final String LAST_MODIFIED = "lastModified";
    
    public static final String TITLE = "title";
    
    public static final String DESCRIPTION = "description";
    
    public static final String TYPE = "type";
    
    public static final String APPLICATION = "application";
    
    public static final String CONTAINER_MODELS = "containerModels";
    
    public PageModel(final OID oid) {
        super(oid);
    }
    
    public PageModel(final BigDecimal pageModelId) {
        this(new OID(BASE_DATA_OBJECT_TYPE, pageModelId));
    }
    
    public PageModel(final DataObject dataObject) {
        super(dataObject);
    }
    
    public BigDecimal getPageModelId() {
        return (BigDecimal) get(PAGE_MODEL_ID);
    }
    
    protected void setPageModelId(final BigDecimal pageModelId) {
        set(PAGE_MODEL_ID, pageModelId);
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
    
    public String getName() {
        return (String) get(NAME);
    }
    
    public void setName(final String name) {
        set(NAME, name);
    }
    
    public String getVersion()  {
        return (String) get(PAGE_MODEL_VERSION);
    }
    
    protected void setVersion(final String pageModelVersion) {
        
        set(PAGE_MODEL_VERSION, pageModelVersion);
    }
    
    public Date getLastModified() {
        return (Date) get(LAST_MODIFIED);
    }
    
    protected void setLastModified(final Date lastModified) {
        set(LAST_MODIFIED, lastModified);
    }
    
    public String getTitle() {
        return (String) get(TITLE);
    }
    
    protected void setTitle(final String title) {
        set(TITLE, title);
    }
    
    public String getDescription() {
        return (String) get(DESCRIPTION);
    }
    
    public void setDescription(final String description) {
        set(DESCRIPTION, description);
    }
    
    public Application getApplication() {
        return (Application) get(APPLICATION);
    }
    
    public void setApplication(final Application application) {
        setAssociation(APPLICATION, application);
    }
    
    public String getType() {
        return (String) get(TYPE);
    }
    
    public void setType(final String type) {
        set(TYPE, type);
    }
    
    public ContainerModelCollection getContainerModels() {
        final DataCollection dataCollection = (DataCollection) get(
            CONTAINER_MODELS);
        
        return new ContainerModelCollection(dataCollection);
    }
    
    protected void addContainerModel(final ContainerModel containerModel) {
        
        add(CONTAINER_MODELS, containerModel);
    }
    
    protected void removeContainerModel(final ContainerModel containerModel) {
        
        remove(CONTAINER_MODELS, containerModel);
    }
}

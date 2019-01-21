/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.pagemodel;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContainerModel extends DomainObject {
    
    public static final String BASE_DATA_OBJECT_TYPE = "org.libreccm.pagemodel.ContainerModel";
    
    public ContainerModel(final DataObject dataObject) {
        super(dataObject);
    }
    
    public ContainerModel(final OID oid) {
        super(oid);
    }
    
    public ContainerModel(final BigDecimal containerModelId) {
        this(new OID(BASE_DATA_OBJECT_TYPE, containerModelId));
    }
    
    
    
}

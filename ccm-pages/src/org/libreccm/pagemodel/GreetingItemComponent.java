package org.libreccm.pagemodel;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class GreetingItemComponent extends ContentItemComponent {

    public static final String BASE_DATA_OBJECT_TYPE
                               = "org.libreccm.pagemodel.GreetingItemComponent";

    public GreetingItemComponent(final DataObject dataObject) {

        super(dataObject);
    }

    public GreetingItemComponent(final OID oid) {
        super(oid);
    }

    public GreetingItemComponent(final BigDecimal componentModelId) {
        this(new OID(BASE_DATA_OBJECT_TYPE, componentModelId));
    }

}

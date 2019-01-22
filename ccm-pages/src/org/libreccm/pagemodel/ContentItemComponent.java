package org.libreccm.pagemodel;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContentItemComponent extends ComponentModel {

    public static final String BASE_DATA_OBJECT_TYPE
                               = "org.libreccm.pagemodel.ContentItemComponent";

    public static final String MODE = "mode";

    public ContentItemComponent(final DataObject dataObject) {

        super(dataObject);
    }

    public ContentItemComponent(final OID oid) {

        super(oid);
    }

    public ContentItemComponent(final BigDecimal componentModelId) {

        this(new OID(BASE_DATA_OBJECT_TYPE, componentModelId));
    }

    public String getMode() {
        return (String) get(MODE);
    }

    public void setMode(final String mode) {

        set(MODE, mode);
    }

}

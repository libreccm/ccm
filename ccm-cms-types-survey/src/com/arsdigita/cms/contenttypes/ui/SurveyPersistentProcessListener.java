package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;

import com.arsdigita.formbuilder.PersistentProcessListener;

import java.math.BigDecimal;

public class SurveyPersistentProcessListener extends PersistentProcessListener {

    public static final String BASE_DATA_OBJECT_TYPE =
            "com.arsdigita.cms.contenttypes.ui.SurveyPersistentProcessListener";

    public SurveyPersistentProcessListener() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public SurveyPersistentProcessListener(String typeName) {
        super(typeName);
    }

    public SurveyPersistentProcessListener(ObjectType type) {
        super(type);
    }

    public SurveyPersistentProcessListener(DataObject obj) {
        super(obj);
    }

    public SurveyPersistentProcessListener(BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SurveyPersistentProcessListener(OID oid) {
        super(oid);
    }

    public static SurveyPersistentProcessListener create(String name, String description) {

        SurveyPersistentProcessListener l = new SurveyPersistentProcessListener();
        l.setup(name, description);

        return l;
    }

    @Override
    protected void setup(String name, String description) {
        super.setup(name, description);
    }

    // XXX hack to get around some wierd issues
    // with mdsql associations where the object
    // type in question is a subtype of the
    // one named in the association definition
    @Override
    public boolean isContainerModified() {
        return false;
    }

    @Override
    public FormProcessListener createProcessListener() {
        return new SurveyProcessListener();
    }
}

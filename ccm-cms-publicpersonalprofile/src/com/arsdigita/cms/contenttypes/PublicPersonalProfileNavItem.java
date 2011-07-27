package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileNavItem extends DomainObject {

    public static final String ID = "id";
    public static final String KEY = "key";
    public static final String LANG = "lang";
    public static final String LABEL = "label";
    public static final String ORDER = "navItemOrder";
    public static final String GENERATOR_CLASS = "generatorClass";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.PublicPersonalProfileNavItem";

    public PublicPersonalProfileNavItem() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public PublicPersonalProfileNavItem(
            final BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public PublicPersonalProfileNavItem(final OID oid) {
        super(oid);
    }

    public PublicPersonalProfileNavItem(final DataObject dobj) {
        super(dobj);
    }

    public PublicPersonalProfileNavItem(final String typeName) {
        super(typeName);
    }

    public PublicPersonalProfileNavItem(final String key,
                                        final String lang,
                                        final String label,
                                        final Integer order) {
        this();
        setKey(key);
        setLang(lang);
        setLabel(label);
        setOrder(order);
        save();
    }

    @Override
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public final BigDecimal getId() {
        return(BigDecimal) get(ID);
    }

    public final String getKey() {
        return (String) get(KEY);
    }

    public final void setKey(final String key) {
        set(KEY, key);
    }

    public final String getLang() {
        return (String) get(LANG);
    }

    public final void setLang(final String lang) {
        set(LANG, lang);
    }

    public final String getLabel() {
        return (String) get(LABEL);
    }

    public final void setLabel(final String label) {
        set(LABEL, label);
    }

    public final Integer getOrder() {
        return (Integer) get(ORDER);
    }

    public final void setOrder(final Integer order) {
        set(ORDER, order);
    }

    public final String getGeneratorClass() {
        return (String) get(GENERATOR_CLASS);
    }

    public final void setGeneratorClass(final String generatorClass) {
        set(GENERATOR_CLASS, generatorClass);
    }
}

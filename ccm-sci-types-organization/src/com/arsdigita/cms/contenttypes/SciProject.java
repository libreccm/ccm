package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Jens Pelzetter
 */
public class SciProject extends GenericOrganizationalUnit{

    public static final String BEGIN = "projectbegin";
    public static final String END = "projectend";
    public static final String DESCRIPTION = "description";
    public static final String FUNDING = "funding";
    public static final String BASE_DATA_OBJECT_TYPE = 
            "com.arsdigita.cms.contenttypes.SciProject";
    
    public SciProject() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public SciProject(BigDecimal id ) throws DataObjectNotFoundException{
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SciProject(OID oid) {
        super(oid);
    }

    public SciProject(DataObject obj) {
        super(obj);
    }

    public SciProject(String type) {
        super(type);
    }

    public Date getBegin() {
        return (Date) get(BEGIN);
    }

    public void setBegin(Date begin) {
        set(BEGIN, begin);
    }

    public Date getEnd() {
        return (Date) get(END);
    }

    public void setEnd(Date end) {
        set(END, end);
    }

    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }

    public String getFunding() {
        return (String) get(FUNDING);
    }

    public void setFunding(String funding) {
        set(FUNDING, funding);
    }

}

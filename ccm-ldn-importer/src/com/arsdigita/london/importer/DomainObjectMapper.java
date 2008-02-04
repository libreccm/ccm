package com.arsdigita.london.importer;

import com.arsdigita.persistence.OID;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;

import com.arsdigita.util.Assert;

import org.apache.log4j.Logger;

/**
 *  A helper class to facilitate usage of {@link RemoteOidMapping}.
 *
 * @see com.arsdigita.london.importer
 */
public class DomainObjectMapper {

    private static final Logger s_log =
        Logger.getLogger(DomainObjectMapper.class);

    private String m_systemID;

    public DomainObjectMapper() {
    }


    public boolean objectExists(OID src) {
        boolean exists = RemoteOidMapping.exists(getSystemID(), src);
        if (s_log.isDebugEnabled()) {
            s_log.debug("Checking existance of " + src +
                        " from " + getSystemID() + ": " + exists);
        }
        return exists;
    }

    public DomainObject getObject(OID src) {
       String oid = RemoteOidMapping.retrieveDstOid(getSystemID(), src);
       if (s_log.isDebugEnabled()) {
           s_log.debug("Object " + src + " from " +
                       getSystemID() + " is " + oid);
       }

       if (oid == null) {
           return null;
       }

       return DomainObjectFactory.newInstance(OID.valueOf(oid));
    }

    public void setObject(OID src,
                          DomainObject dst) {
        if (s_log.isInfoEnabled()) {
            s_log.info("Mapping " + src + " from " +
                       getSystemID() + " to " + dst);
        }

        RemoteOidMapping mapping = new RemoteOidMapping(
            getSystemID(),
            src.toString(),
            dst.getOID().toString());
        //mapping.save();
    }

    public String getSystemID() {
        Assert.exists(m_systemID, String.class);

        return m_systemID;
    }

    void setSystemID(String systemID) {
        if (s_log.isInfoEnabled()) {
            s_log.info("Setting system id to " + systemID);
        }
        m_systemID = systemID;
    }
}

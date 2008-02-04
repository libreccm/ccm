
package com.arsdigita.london.importer;

import com.arsdigita.domain.DomainObject;
import java.io.File;


/**
 *  Base class for TagParser implementations handling
 * import of {@link DomainObject} instances.
 *
 * @see com.arsdigita.london.importer
 */
public abstract class DomainObjectParser extends AbstractTagParser {

    private String m_objectType;
    private DomainObject m_object;
    private File m_lobDir;
    private DomainObjectMapper m_mapper;

    /**
     *  Main constructor.
     *
     * @param tagName  name of the XML tag this parser handles
     * @param tagURI   URI of the XML tag namespace
     * @param objectType the persistence object type this parser is about to import
     * @param lobDir   the directory where importer will search for BLOBs to import
     * @param mapper   the mapping between source OID and OID of the domain object
     *                 created during import process
     */
    public DomainObjectParser(String tagName,
                              String tagURI,
                              String objectType,
                              File lobDir,
                              DomainObjectMapper mapper) {
        super(tagName, tagURI);

        m_objectType = objectType;
        m_lobDir = lobDir;
        m_mapper = mapper;
    }

    /**
     *  Used by parser to temporarily store the domain object imported from
     * XML source.
     *
     * @see #getDomainObject()
     */
    protected void setDomainObject(DomainObject obj) {
        m_object = obj;
    }

    /**
     *  A hook for caller to retrieve the domain object imported
     * by this parser.
     *
     * @see #setDomainObject(DomainObject)
     */
    public DomainObject getDomainObject() {
        return m_object;
    }

    /**
     *  Gets the directory where importer will look for BLOBs.
     */
    public File getLobDirectory() {
        return m_lobDir;
    }

    public DomainObjectMapper getObjectMapper() {
        return m_mapper;
    }

}


package com.arsdigita.london.importer;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 *  TagParser implementation which handles the top-level &lt;import&gt; tag.
 *
 * @see com.arsdigita.london.importer
 */
public class ImportParser extends AbstractTagParser {
    private static final Logger s_log =
        Logger.getLogger(ImportParser.class);

    public static final String TAG_NAME = "import";
    public static final String SOURCE_ATTR = "source";

    private DomainObjectMapper m_mapper;

    public ImportParser(DomainObjectMapper mapper) {
        this(TAG_NAME,
             "http://xmlns.redhat.com/waf/london/importer/1.0",
             mapper);
    }

    public ImportParser(String tagName,
                        String tagURI,
                        DomainObjectMapper mapper) {
        super(tagName, tagURI);

        m_mapper = mapper;
    }

    protected void startTag(String name,
                            String uri,
                            Attributes atts) {
        if (!TAG_NAME.equals(name)) {
            s_log.warn("Unexpected tag " + name + " " + uri);
            return;
        }

        String sourceID = atts.getValue(SOURCE_ATTR);
        s_log.debug("Setting source system ID to: " + sourceID);
        m_mapper.setSystemID(sourceID);
    }

    protected void endTag(String name, String uri) {
        if (!TAG_NAME.equals(name)) {
            s_log.warn("Unexpected tag " + name + " " + uri);
            return;
        }
    }

    /*
    //
    //  If subblock is an instance of CMS asset, persist it.
    //
    protected void endSubBlock(TagParser subparser) {
        if (subparser instanceof CmsAssetParser) {
            s_log.debug("Importing asset: ");
            CmsAssetParser assetParser = (CmsAssetParser) subparser;
            Asset asset = assetParser.importAsset();
            s_log.debug("Asset imported: " + asset);
        }
    }
    */
}

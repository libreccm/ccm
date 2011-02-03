package com.arsdigita.cms.scipublications.exporter;

import com.arsdigita.cms.contenttypes.Publication;

/**
 *
 * @author jensp
 */
public interface SciPublicationsExporter {

    PublicationFormat getSupportedFormat();

    String exportPublication(Publication publication);

}

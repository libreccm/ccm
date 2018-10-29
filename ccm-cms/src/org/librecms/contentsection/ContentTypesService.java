package org.librecms.contentsection;

import com.arsdigita.cms.ContentType;
import com.arsdigita.domain.DomainService;
import com.arsdigita.persistence.DataCollection;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
final class ContentTypesService extends DomainService {

    protected static DataCollection getContentSections(
        final ContentType section) {

        return (DataCollection) get(section, "associatedContentSectionsForType");

    }

}

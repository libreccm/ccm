package com.arsdigita.cms.contenttypes;

import com.arsdigita.runtime.LegacyInitEvent;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class ProjectInitializer extends ContentTypeInitializer {

    private final static Logger s_log = Logger.getLogger(ProjectInitializer.class);

    public ProjectInitializer() {
        super("ccm-cms-types-project.pdl.mf", Project.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{"/static/content-types/com/arsdigita/cms/contenttypes/Project.xsl"};
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/Project.xml";
    }
}

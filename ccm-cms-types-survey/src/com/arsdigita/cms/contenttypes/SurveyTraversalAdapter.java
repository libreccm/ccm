package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.domain.SimpleDomainObjectTraversalAdapter;
import com.arsdigita.domain.DomainObject;

/**
 * An adapter for Survey allowing pluggable
 * assets to extend the traversal.
 *
 * This is a modified copy of ContentItemTraversalAdapter to allow the
 * Survey objects to show the survey form only during active survey time
 */
public class SurveyTraversalAdapter extends ContentItemTraversalAdapter {

    private static final Logger s_log =
            Logger.getLogger(SurveyTraversalAdapter.class);

    public SurveyTraversalAdapter() {
        super();
    }

    public SurveyTraversalAdapter(SimpleDomainObjectTraversalAdapter adapter) {
        super(adapter);
    }

    /**
     * If the path references an asset, then delegates
     * to the asset's adapter, otherwise delegates to
     * the content item's primary adapter
     */
    @Override
    public boolean processProperty(DomainObject obj, String path, Property prop, String context) {

        if (obj instanceof Survey) {

            if (s_log.isDebugEnabled()) {
                s_log.debug("Found a Survey CT. Using own SurveyTraversalAdapter.");
            }

            // Test if we are processing a form property
            if (prop.getName().equals(Survey.FORM)) {

                // If the Survey is active or not live (preview), show the form. Otherwise dismiss it.
                return ((Survey) obj).isActive() || !((Survey) obj).isLiveVersion();
            }
        }

        // In all other cases delegate to parent class
        return super.processProperty(obj, path, prop, context);
    }
}

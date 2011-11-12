package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciInstitutePublicationsTabConfig extends AbstractConfig {

    private final Parameter greetingSize;
    private final Parameter pageSize;
    private final Parameter enableSearchLimit;
    private final Parameter mergePublications;
    private final Parameter oneRowPerAuthor;

    public SciInstitutePublicationsTabConfig() {
        greetingSize =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.tabs.publications.greeting_number",
                Parameter.REQUIRED,
                10);

        pageSize =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.tabs.publications.page_size",
                Parameter.REQUIRED,
                30);

        enableSearchLimit =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.sciinstitute.tabs.publications.enable_search_limit",
                Parameter.REQUIRED,
                2);

        mergePublications =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciinstitutes.tabs.publications.merge",
                Parameter.REQUIRED,
                Boolean.TRUE);

        oneRowPerAuthor =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.sciinstitutes.tabs.publications.one_row_per_author",
                Parameter.REQUIRED,
                Boolean.FALSE);

        register(greetingSize);
        register(pageSize);
        register(enableSearchLimit);
        register(mergePublications);
        register(oneRowPerAuthor);

        loadInfo();
    }

    public final int getGreetingSize() {
        return (Integer) get(greetingSize);
    }

    public final int getPageSize() {
        return (Integer) get(pageSize);
    }

    public final int getEnableSearchLimit() {
        return (Integer) get(enableSearchLimit);
    }

    public final boolean isMergingPublications() {
        return (Boolean) get(mergePublications);
    }

    public final boolean getOneRowPerAuthor() {
        return (Boolean) get(oneRowPerAuthor);
    }
}

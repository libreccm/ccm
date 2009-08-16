/*
 * Copyright (C) 2009 Permeance Technologies Pty Ltd. All Rights Reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package com.arsdigita.london.terms.indexing;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;

/**
 * Indexing configuration.
 * 
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
public class IndexingConfig extends AbstractConfig {

    private final BooleanParameter m_disallowInternalPeriods;
    private final BooleanParameter m_keyphraseFrequencyEnabled;
    private final IntegerParameter m_minPhraseLength;
    private final IntegerParameter m_maxPhraseLength;
    private final IntegerParameter m_minPhraseOccurrences;
    private final BooleanParameter m_checkForProperNouns;
    private final IntegerParameter m_maxTrainingItems;

    private static IndexingConfig s_config = null;

    public static IndexingConfig getInstance() {
        if (s_config == null) {
            s_config = new IndexingConfig();
            s_config.load();
        }
        return s_config;
    }

    public IndexingConfig() {
        m_disallowInternalPeriods = new BooleanParameter("com.arsdigita.london.terms.indexing.disallowInternalPeriods",
                Parameter.REQUIRED, Boolean.TRUE);

        m_keyphraseFrequencyEnabled = new BooleanParameter(
                "com.arsdigita.london.terms.indexing.keyphraseFrequencyEnabled", Parameter.REQUIRED, Boolean.FALSE);

        m_minPhraseLength = new IntegerParameter("com.arsdigita.london.terms.indexing.minPhraseLength",
                Parameter.REQUIRED, Integer.valueOf(1));

        m_maxPhraseLength = new IntegerParameter("com.arsdigita.london.terms.indexing.maxPhraseLength",
                Parameter.REQUIRED, Integer.valueOf(5));

        m_minPhraseOccurrences = new IntegerParameter("com.arsdigita.london.terms.indexing.minPhraseOccurrences",
                Parameter.REQUIRED, Integer.valueOf(2));

        m_checkForProperNouns = new BooleanParameter("com.arsdigita.london.terms.indexing.checkForProperNouns",
                Parameter.REQUIRED, Boolean.TRUE);

        m_maxTrainingItems = new IntegerParameter("com.arsdigita.london.terms.indexing.maxTrainingItems",
                Parameter.REQUIRED, Integer.valueOf(100));

        register(m_disallowInternalPeriods);
        register(m_keyphraseFrequencyEnabled);
        register(m_minPhraseLength);
        register(m_maxPhraseLength);
        register(m_minPhraseOccurrences);
        register(m_checkForProperNouns);
        register(m_maxTrainingItems);

        loadInfo();
    }

    public final boolean disallowInternalPeriods() {
        return ((Boolean) get(m_disallowInternalPeriods)).booleanValue();
    }

    public final boolean keyphraseFrequencyEnabled() {
        return ((Boolean) get(m_keyphraseFrequencyEnabled)).booleanValue();
    }

    public final int getMinPhraseLength() {
        return ((Integer) get(m_minPhraseLength)).intValue();
    }

    public final int getMaxPhraseLength() {
        return ((Integer) get(m_maxPhraseLength)).intValue();
    }

    public final int getMinPhraseOccurrences() {
        return ((Integer) get(m_minPhraseOccurrences)).intValue();
    }

    public final boolean checkForProperNouns() {
        return ((Boolean) get(m_checkForProperNouns)).booleanValue();
    }

    public final int getMaxTrainingItems() {
        return ((Integer) get(m_maxTrainingItems)).intValue();
    }
}

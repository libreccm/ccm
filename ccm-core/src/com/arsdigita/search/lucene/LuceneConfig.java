/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.search.lucene;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.runtime.CCMResourceManager;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * LuceneConfig
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: LuceneConfig.java 890 2005-09-21 17:09:00Z apevec $
 **/

public class LuceneConfig extends AbstractConfig {

    private static final Logger LOG = Logger.getLogger(LuceneConfig.class);

    private static LuceneConfig s_conf;

    private Class m_analyzerClass;

    private StringParameter m_location = new StringParameter
        ("waf.lucene.location", Parameter.REQUIRED,
         new File(CCMResourceManager.getWorkDirectory(), "lucene").getPath());
    private IntegerParameter m_interval = new IntegerParameter
        ("waf.lucene.interval", Parameter.REQUIRED, new Integer(2*60));
    private StringParameter m_analyzer = new StringParameter
        ("waf.lucene.analyzer", Parameter.REQUIRED,
         "org.apache.lucene.analysis.standard.StandardAnalyzer");

    /**
     * Constructor - don't use it directly! Use getconfig()
     */
    public LuceneConfig() {
        register(m_location);
        register(m_interval);
        register(m_analyzer);
        loadInfo();
    }

    static synchronized LuceneConfig getConfig() {
        if (s_conf == null) {
            s_conf = new LuceneConfig();
            s_conf.load();
        }

        return s_conf;
    }

    public String getIndexLocation() {
        return (String) get(m_location);
    }

    public int getIndexerInterval() {
        return ((Integer) get(m_interval)).intValue();
    }

    public Analyzer getAnalyzer() {

        if (m_analyzerClass == null) {
            String className = (String) get(m_analyzer);
            try {
                m_analyzerClass = Class.forName(className);
            } catch (Exception ex) {
                LOG.error("Unable to load "+className);
                throw new UncheckedWrapperException(ex);
            }
        }
        try {
            return (Analyzer) m_analyzerClass.newInstance();
        } catch (Exception ex) {
            LOG.error("Unable to create Lucene analyzer, using StandardAnalyzer", ex);
            return new StandardAnalyzer();
        }
    }

}

/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.tools.junit.results;

import com.arsdigita.util.UncheckedWrapperException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *  ResultFileSetLoader
 *
 *  @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 *  @version $Revision: #7 $ $Date Nov 6, 2002 $
 */
public class ResultFileSetLoader {

    private static Logger s_log = Logger.getLogger(ResultFileSetLoader.class);
    private static final FilenameFilter s_testFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            final boolean isTestFile = name.startsWith("TEST") && name.endsWith(".xml");
            return isTestFile;
        }
    };

    private SAXBuilder m_builder;

    public ResultFileSetLoader() {
        m_builder = new SAXBuilder();
        m_builder.setFactory(new ResultJDOMFactory());
    }

    Map loadResultFiles(String path) {
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        String[] testFiles = new File(path).list(s_testFilter);
        Map tests = new HashMap();
        for (int i = 0; i < testFiles.length; i++) {
            String testFile = testFiles[i];
            XMLResult result = loadResult(path + testFile);
            if (null != result) {
                tests.put(testFile, result);
            }
        }
        return tests;

    }

    /**
     * Loads the results into memory.
     * @param filename
     * @return
     */
    private XMLResult loadResult(String filename) {
        try {
            Document doc = m_builder.build(filename);
            XMLResult res = (XMLResult) doc.getRootElement();
            return res;
        } catch(JDOMException e) {
            // This is likely due to an empty document
            s_log.warn("JDOM error: " + e.getMessage(), e);
            EmptyXMLResult res = new EmptyXMLResult(filename);
            return res;
        } catch(IOException e) {
            s_log.error("Error loading file: " + e.getMessage(), e);
            throw new UncheckedWrapperException(e);
        }
    }

}

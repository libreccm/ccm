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
package com.arsdigita.packaging;

import com.arsdigita.packaging.LoadCenter.LoadType;
import com.arsdigita.xml.XML;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * LoaderInfo
 * 
 * Helper class: 
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 * @version $Id: LoaderInfo.java 287 2005-02-22 00:29:02Z sskracic $
 */
class LoaderInfo {

    private List m_requiredTables = new ArrayList();
    private List m_providedTables = new ArrayList();
    private List m_requiredInitializers = new ArrayList();
    private List m_providedInitializers = new ArrayList();
    private List m_schemaScripts = new ArrayList();
    private List m_dataScripts = new ArrayList();
    private List m_dataLoadScripts = new ArrayList();
    private List m_dataUnloadScripts = new ArrayList();
    private List m_requiredPackages = new ArrayList();

    /**
     * Constructor.
     * @param is 
     */
    public LoaderInfo(InputStream is) {
        XML.parse(is, new LoaderInfoHandler());
    }

    public List getRequiredTables() {
        return m_requiredTables;
    }

    public List getProvidedTables() {
        return m_providedTables;
    }

    public List getRequiredInitializers() {
        return m_requiredInitializers;
    }

    public List getProvidedInitializers() {
        return m_providedInitializers;
    }

    public List getRequiredPackages() {
        return m_requiredPackages;
    }

    public List getSchemaScripts() {
        return m_schemaScripts;
    }

    public List getDataScripts(LoadType scriptType) {
        switch (scriptType) {
            case LOAD:
                m_dataScripts = m_dataLoadScripts;
                break;
            case UNLOAD:
                m_dataScripts = m_dataUnloadScripts;
                break;
        }
        return m_dataScripts;
    }

    //Strings correspond to the tag names in the ".load"-files
    private static final String PROVIDES = "provides";
    private static final String REQUIRES = "requires";
    private static final String TABLE = "table";
    private static final String INITIALIZER = "initializer";
    private static final String PACKAGE = "package";
    private static final String SCRIPTS = "scripts";
    private static final String SCHEMA = "schema";
    private static final String DATA_LOAD = "data";
    private static final String DATA_UNLOAD = "data-unload";

    // attributes
    private static final String NAME = "name";
    private static final String CLASS = "class";
    private static final String DIRECTORY = "directory";

    private class LoaderInfoHandler extends DefaultHandler {

        private List m_context = new ArrayList();

        @Override
        public void startElement(String uri, String name, String qn,
                                 Attributes attrs) {
            if (name.equals(TABLE)) {
                String table = attrs.getValue(uri, NAME);
                if (table == null) {
                    throw new IllegalStateException
                        ("table element requires name attribute");
                }

                if (m_context.contains(REQUIRES)) {
                    m_requiredTables.add(table);
                } else if (m_context.contains(PROVIDES)) {
                    m_providedTables.add(table);
                } else {
                    throw new IllegalStateException
                        ("table element must appear inside " +
                         "requires or provides");
                }
            }

            if (name.equals(INITIALIZER)) {
                String init = attrs.getValue(uri, CLASS);
                if (init == null) {
                    throw new IllegalStateException
                        ("init element requires class attribute");
                }

                if (m_context.contains(REQUIRES)) {
                    m_requiredInitializers.add(init);
                } else if (m_context.contains(PROVIDES)) {
                    m_providedInitializers.add(init);
                } else {
                    throw new IllegalStateException
                        ("initializer element must appear inside " +
                         "requires or provides");
                }
            }

            if (name.equals(PACKAGE)) {
                String key = attrs.getValue(uri, NAME);
                if (key == null) {
                    throw new IllegalStateException
                        ("package element requires name attribute");
                }
                if (m_context.contains(REQUIRES)) {
                    m_requiredPackages.add(key);
                } else {
                    throw new IllegalStateException
                        ("package element must appear inside requires");
                }
            }

            if (name.equals(SCHEMA)) {
                if (!m_context.contains(SCRIPTS)) {
                    throw new IllegalStateException
                        ("schema element must appear inside scripts");
                }

                String dir = attrs.getValue(uri, DIRECTORY);
                if (dir == null) {
                    throw new IllegalStateException
                        ("schema element requires directory attribute");
                }

                m_schemaScripts.add(dir);
            }

            if (name.equals(DATA_LOAD)) {
                if (!m_context.contains(SCRIPTS)) {
                    throw new IllegalStateException
                        ("data element must appear inside scripts");
                }

                String klass = attrs.getValue(uri, CLASS);
                if (klass == null) {
                    throw new IllegalStateException
                        ("data element requires class attribute");
                }

                m_dataLoadScripts.add(klass);
            }
            
            if (name.equals(DATA_UNLOAD)) {
                if (!m_context.contains(SCRIPTS)) {
                    throw new IllegalStateException
                        ("data element must appear inside scripts");
                }

                String klass = attrs.getValue(uri, CLASS);
                if (klass == null) {
                    throw new IllegalStateException
                        ("data element requires class attribute");
                }

                m_dataUnloadScripts.add(klass);
            }
            
            m_context.add(name);
        }

        @Override
        public void endElement(String uri, String name, String qn) {
            m_context.remove(m_context.lastIndexOf(name));
        }
    }

}

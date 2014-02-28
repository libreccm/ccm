/*
 * Copyright (c) 2014 Jens Pelzetter
 *
 * This library is free software, you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY, without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library, if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.scipublications.exporter.csv;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

import static com.arsdigita.cms.scipublications.exporter.csv.CsvExporterConstants.*;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class CsvExporterConfig extends AbstractConfig {
    
    private Parameter columns;
    private Parameter separator;
    
    public CsvExporterConfig() {
        super();
        
        separator = new StringParameter("com.arsdigita.cms.scipublications.exporter.csv.separator",
        Parameter.REQUIRED,
        "\t");
        
        columns = new StringParameter("com.arsdigita.cms.scipublications.exporter.csv.columns", 
        Parameter.REQUIRED,
        PUBLICATION_ID + "," + 
        PUBLICATION_TYPE + "," + 
        AUTHORS + "," + 
        YEAR + "," + 
        YEAR_FIRST_PUBLISHED + "," +
        TITLE + "," + 
        PUBLISHER + "," + 
        PLACE + "," + 
        EDITION + "," + 
        ABSTRACT + "," + 
        MISC + "," + 
        LANGUAGE_OF_PUBLICATION + "," +
        REVIEWED + "," +
        VOLUME + "," +
        NUMBER + "," +
        NUMBER_OF_VOLUMES + "," +
        NUMBER_OF_PAGES + "," +
        VOLUME_OF_JOURNAL + "," +
        ISSUE_OF_JOURNAL + "," +
        JOURNAL + "," +
        JOURNAL_SYMBOL + "," +
        CHAPTER + "," +
        PUBLICATION_DATE + "," +
        COLLECTED_VOLUME + "," +
        COLLECTED_VOLUME_EDITORS + "," +
        CONFERENCE + "," +
        CONFERENCE_PLACE + "," + 
        CONFERENCE_DATE_FROM + "," + 
        CONFERENCE_DATE_TO + "," +
        IN_SERIES + "," + 
        VOLUME_OF_SERIES + "," + 
        EDITOR_OF_SERIES + "," +
        ORGANISATION + "," +
        URL + "," +
        ISBN + "," + 
        ISSN + "," +
        URN + "," +
        DOI + "," +
        LAST_ACCESS + ","); 
        
        register(columns);
        register(separator);
        
        loadInfo();
    }
    
    public String getColumns() {
        return (String) get(columns);
    }
    
    public String getSeparator() {
        return (String) get(separator);
    }
    
}

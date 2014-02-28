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

/**
 * Constants used in for CSV Exporter, most of them are the field names used in the maps from which
 * which the CSV lines are created.
 * 
 * The constants are ordered alphabetically.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public enum CsvExporterConstants {

    ABSTRACT,
    AUTHORS,

    CHAPTER,
    COLLECTED_VOLUME,
    COLLECTED_VOLUME_EDITORS,
    CONFERENCE,
    CONFERENCE_DATE_FROM,
    CONFERENCE_DATE_TO,
    CONFERENCE_PLACE,

    DOI,

    EDITION,
    EDITOR_OF_SERIES,

    IN_SERIES,
    ISBN,
    ISSN,
    ISSUE_OF_JOURNAL,

    JOURNAL,
    JOURNAL_SYMBOL,

    LANGUAGE_OF_PUBLICATION,
    LAST_ACCESS,

    MISC,

    NUMBER,
    NUMBER_OF_VOLUMES,
    NUMBER_OF_PAGES,

    ORGANISATION,

    PAGES_FROM,
    PAGES_TO,
    PLACE,
    PUBLICATION_DATE,
    PUBLICATION_ID,
    PUBLICATION_TYPE,
    PUBLISHER,

    REVIEWED,

    YEAR,
    YEAR_FIRST_PUBLISHED,

    TITLE,

    URL,
    URN,

    VOLUME,
    VOLUME_OF_JOURNAL,
    VOLUME_OF_SERIES

}

/*
 * Copyright (C) 2007 Chris Gilbert. All Rights Reserved.
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
package com.arsdigita.cms.search;

import org.apache.log4j.Logger;

import com.arsdigita.cms.ContentSection;
import com.arsdigita.search.intermedia.SimpleSearchSpecification;

/**
 * @author chris.gilbert@westsussex.gov.uk
 *
 * Search Specification that utilises Oracle's INPATH operator to 
 * allow higher weighting for keywords and titles
 */
public class PathSearchSpecification extends SimpleSearchSpecification {

	private static Logger s_log = Logger.getLogger(PathSearchSpecification.class);
	
	public PathSearchSpecification(String object_type,
										 String searchString) {
			super(object_type, searchString);
			
		}
		
	public PathSearchSpecification(String searchString) {
			this(null, searchString);
		}

		
	public static String containsClause(String sc,
                                        String searchString,
                                        String xml_label,
                                        String raw_label,
                                        String keyword_label,
                                        String title_label) {
        StringBuffer clause = new StringBuffer();
        String quotedSS = quote(searchString);

        if (xml_label != null) {
            clause.append("contains(" + sc + ".xml_content, " + quotedSS +
                ", " + xml_label + ") > 0");
        }
        if (raw_label != null) {
            if (!clause.toString().equals("")) {
                clause.append(" or ");
            }
            clause.append("contains(" + sc + ".raw_content, " + quotedSS +
                ", " + raw_label + ") > 0");
        }
        if (ContentSection.getConfig().scoreKeywordsAndTitle()) {
			if (!clause.toString().equals("")) {
				clause.append(" or ");
			}
			
			clause.append("contains(" + sc + ".xml_content, ");
			clause.append(quote("(" + searchString + ") INPATH (cms:item/dublinCore/dcKeywords)"));
			clause.append(", " + keyword_label + ") > 0");
			clause.append(" or contains(" + sc + ".xml_content, ");
			clause.append(quote("(" + searchString + ") INPATH (cms:item/title)"));
			clause.append(", " + title_label + ") > 0");
			
        	
        }
        String clauseString = clause.toString().equals("") ? "" : "(" + clause.toString() + ")";
        s_log.debug(clauseString);
        return clauseString;
    }

}

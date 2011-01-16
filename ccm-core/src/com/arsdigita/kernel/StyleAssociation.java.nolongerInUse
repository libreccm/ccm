/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.kernel;

import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.CompoundFilter;
import com.arsdigita.persistence.FilterFactory;

// Stylesheets
import java.util.Locale;
import java.util.ArrayList;

import org.apache.log4j.Logger;                // for logging

/**
 * 
 * @version $Id: StyleAssociation.java 287 2005-02-22 00:29:02Z sskracic $ 
 *
 * @deprecated without direct replacement. It is designed to work with
 * {@link com.arsdigita.templating.LegacyStylesheetResolver} which is
 * replaced by {@link com.arsdigita.templating.PatternStylesheetResolver}.
 * So thes method is just not used anymore. (pboy) 
 */
public class StyleAssociation {

    /** this class is static-only */
    private StyleAssociation() {};

    private static final Logger s_cat =
        Logger.getLogger(StyleAssociation.class.getName());

    /**
     * Gets an array with all applicable stylesheets.
     * @param style a DataAssociation that is the result from get("defaultStylesheet")
     * @param locale the locale of the request
     * @param outputType  the desired type of output (HTML, WML, XML, and so on)
     *
     * @return an array with all applicable stylesheets.
     *
     * @pre style != null && style instanceof DataAssociation
     * @deprecated  see above
     */
    public static Stylesheet[] getStylesheets(Object style,
                                              Locale locale,
                                              String outputType) {
        DataAssociationCursor da = ((DataAssociation)style).cursor();

        // DA can be both an iterator and a collection, so we make
        // sure that no one has iterated on it. This should go away
        // once persistence has been refactored to make DA just a
        // collection. -RDL, 17 July 2001
        da.reset();

        if (outputType == null) {
            outputType = Stylesheet.getDefaultOutputType();
        }

        com.arsdigita.globalization.Locale localeObject = null;

        if (locale != null) {
            localeObject = com.arsdigita.globalization.Locale
                .fromJavaLocaleBestMatch(locale);
        }

        ArrayList stylesheetList = new ArrayList();

        // we start with the requested locale (e.g., en_US) and find
        // stylesheets for this object {site node, package type} in
        // that locale/output type combination.  We add to the list
        // stylesheets in the same language but a different location
        // (e.g., en), and stylesheets that have no default language.
        //
        // This allows us to write stylesheets in a way such that we
        // factor out all of the common components for an application
        // that are constant across locales, so they don't have to be
        // duplicated verbatim for each language.

        // regardless of requested locale, everyone gets the default
        // no-locale sheet
        FilterFactory factory = da.getFilterFactory();
        CompoundFilter firstFilter = factory.or().addFilter
            (factory.equals("localeID", null));
        com.arsdigita.globalization.Locale localeObjectIter = localeObject;

        int counter = 0;
        while (localeObjectIter != null) {
            firstFilter
                .addFilter(factory.simple("localeID = :locale" + counter));
            firstFilter.set("locale" + counter, localeObjectIter.getID());
            localeObjectIter = localeObjectIter.fallback();
            counter++;
        }
        da.addFilter(factory.and()
                     .addFilter(firstFilter)
                     .addFilter(factory.equals
                                ("outputType", outputType)));

        // WRS 5/29: the stylesheet for a packageType might actually be
        // several stylesheets. Glom them all together and  return the
        // composed stylesheet.
        while (da.next()) {
            DataObject obj = da.getDataObject();
            boolean dup = false;
            for (int i = 0; i < stylesheetList.size() && !dup; i++) {
                Stylesheet test = (Stylesheet)stylesheetList.get(i);
                if (test.getPath().equals(obj.get("pathName"))) {
                    dup = true;
                }
            }
            if (!dup) {
                stylesheetList.add(new Stylesheet(obj));
            }
        }
        Stylesheet[] ss = new Stylesheet[stylesheetList.size()];
        return (Stylesheet[])stylesheetList.toArray(ss);
    }

    /** Gets the first stylesheet (best match) associated with this object.
     *  @param style  a DataAssociation that is the result from get("defaultStylesheet")
     *  @param locale the locale of the request
     *  @param outputType  the desired type of output (HTML, WML, XML, and so on)
     *
     *  @return the first stylesheet associated with this object.
     *
     *  @pre style != null && style instanceof DataAssociation
     * @deprecated see above
     */
    public static Stylesheet getStylesheet(Object style,
                                           Locale locale,
                                           String outputType) {
        Stylesheet[] sslist = getStylesheets(style, locale, outputType);
        if (sslist.length > 0) {
            return sslist[0];
        } else {
            return null;
        }
    }

}

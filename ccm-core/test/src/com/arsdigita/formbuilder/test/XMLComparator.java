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
package com.arsdigita.formbuilder.test;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.util.HttpServletDummyRequest;
import com.arsdigita.util.HttpServletDummyResponse;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Document;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.oro.text.perl.Perl5Util;

/**
 * When testing persistent Bebop components, we use this XML helper class
 * to determine if two components generate identical XML.
 *
 * @author Peter Marklund
 * @version $Id: XMLComparator.java 1940 2009-05-29 07:15:05Z terry $
 *
 */
public class XMLComparator {


    private final static Logger s_log =
        Logger.getLogger(XMLComparator.class.getName());

    private static String generateXML(Component component) {

        Document document;

        Page page;
        PageState pageState;

        try {

            page = new Page("Test Page");

            Class widgetClass = Class.forName("com.arsdigita.bebop.form.Widget");

            // Widgets are a special case - they need to be added to a form
            // We also add Options and FormSections to Forms
            if (widgetClass.isAssignableFrom(component.getClass()) ||
                component instanceof com.arsdigita.bebop.form.Option ||
                (component instanceof com.arsdigita.bebop.FormSection &&
                 !(component instanceof com.arsdigita.bebop.Form))) {

                Form form = new Form("no_name");

                // Options need to be added to an option group
                if (component instanceof com.arsdigita.bebop.form.Option) {
                    SingleSelect select = new SingleSelect("test select");
                    select.addOption((Option)component);
                    form.add(select);

                } else {
                    form.add(component);
                }

                page.add(form);

            } else {

                page.add(component);
            }

            page.lock();

            HttpServletRequest request = new HttpServletDummyRequest();
            HttpServletResponse response = new HttpServletDummyResponse();

            pageState = new PageState(page, request, response);

            document = new Document();

        } catch (Exception e) {

            s_log.debug(e);

            throw new UncheckedWrapperException(e);
        }

        page.generateXML(pageState, document);

        return stripOffDebugInfo(document.toString());
    }

    /**
     * Have to strip it off since it contains the class name
     * and therefore we would never get equality
     */
    private static String stripOffDebugInfo(String xml) {
        Perl5Util perl = new Perl5Util();

        return perl.substitute("s/<bebop:structure[^>]*>[^<>]*<\\/bebop:structure>//", xml);
    }

    public static boolean haveEqualXML(Component component1, Component component2) {

        String xml1 = generateXML(component1);
        String xml2 = generateXML(component2);

        boolean xmlIsEqual = xml1.equals(xml2);

        if (!xmlIsEqual) {

            s_log.debug("component1 xml: " + xml1);
            s_log.debug("component2 xml: " + xml2);
        }

        return xmlIsEqual;
    }
}

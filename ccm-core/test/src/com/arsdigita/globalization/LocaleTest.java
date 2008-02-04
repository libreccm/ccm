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
package com.arsdigita.globalization;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.tools.junit.framework.BaseTestCase;

/**
 * <p>
 * Test for Locale DomainObject
 * </p>
 *
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */
public class LocaleTest extends BaseTestCase {
    public final static String versionId = "$Id: LocaleTest.java 743 2005-09-02 10:37:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Session m_ssn;

    /**
     * Construct a LocaleTest with the specified name.
     *
     * @param name
     */
    public LocaleTest(String name) {
        super(name);
    }

    public void setUp() {
        try {
            m_ssn = SessionManager.getSession();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            fail(e.getMessage());
        }
    }

    /**
     * Test persistence of Locale DomainObject.
     */
    public void testPersistence() throws Exception {
        String charsetName = "ISO-8859-1";
        String language = "es";
        String country = "CO";

        DataCollection charsets = m_ssn.retrieve("com.arsdigita.globalization.Charset");
        Filter f = charsets.addFilter("charset = :charset");
        f.set("charset", charsetName);

        Charset charset = null;
        if (charsets.next()) {
            charset = new Charset(charsets.getDataObject());
        } else {
            charset = new Charset();
            charset.setCharset(charsetName);
            charset.save();
        }

        if (charset == null)
            throw new IllegalStateException();

        // create a Locale object and save it to the database.
        Locale locale = new Locale();
        locale.setLanguage(language);
        locale.setCountry(country);
        locale.setDefaultCharset(charset);
        locale.save();

        OID localeOID = locale.getOID();

        // retrieve the Locale object we just created and make sure
        // all values are what we expect.
        try {
            locale = new Locale(localeOID);
        } catch (DataObjectNotFoundException e) {
            fail("the locale was not created properly or could not be retrieved");
        }

        assertEquals(
                     "language is not the same",
                     language,
                     locale.getLanguage()
                     );

        assertEquals(
                     "country is not the same",
                     country,
                     locale.getCountry()
                     );

        assertEquals(
                     "character set is not the same",
                     charset.getCharset(),
                     locale.getDefaultCharset().getCharset()
                     );
    }
}

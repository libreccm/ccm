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
package com.arsdigita.mimetypes;

import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.mimetypes.converters.ConvertFormat;
import com.arsdigita.mimetypes.converters.PostConvertHTML;
import com.arsdigita.mimetypes.converters.PreConvertHTML;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.util.StringUtils;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Process;
import java.lang.Runtime;
import java.math.BigDecimal;

/**
 * Test interMedia INSO filter converting of documents
 * to html.
 *
 * @author Jeff Teeters (teeters@arsdigita.com)
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 */

public class ConvertHTMLTest extends BaseTestCase {

    public static final String versionId = "$Id: ConvertHTMLTest.java 744 2005-09-02 10:43:19Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    // The last test in this file is only run if
    // MimeTypeInitializer.INSO_filter_works is not 0.
    // That test requires a working interMedia INSO Filter.
    // 8.1.6 should pass, 8.1.7 may fail.

    private Session m_session;

    public ConvertHTMLTest(String name) {
        super(name);
    }

    /**
     * Test creating and writing into a PreConvertHTML object
     */
    public void testPreConvertHTML() {
        // Doesn't run under Postgres
        if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
            return;
        }

        final String TEST_CONTENT = "Test pre-convert Content.";
        final BigDecimal EXP_ID = new BigDecimal(237);  // random id

        PreConvertHTML p, p1;

        BigDecimal pID;

        // Create a new PreConvertHTML object
        try {
            p = new PreConvertHTML();
            p.setId(EXP_ID);
            p.setContent(TEST_CONTENT.getBytes());
            pID = EXP_ID;
            p.save();

            p1 = new PreConvertHTML
                (new OID(PreConvertHTML.BASE_DATA_OBJECT_TYPE, pID));
            String foundContent = new String(p1.getContent());
            assertTrue("Content, expected=" + TEST_CONTENT + " found=" +
                       foundContent, TEST_CONTENT.equals(foundContent));

        } catch (DataObjectNotFoundException e) {
            fail(e.getMessage());
            return;
        }

        //Delete object
        try {
            p.delete();

        } catch (Exception e) {
            fail(e.getMessage());
            return;
        }
        try {
            p1 = new PreConvertHTML
                (new OID(PreConvertHTML.BASE_DATA_OBJECT_TYPE, pID));

            //the above line should generate an exception
            fail("PreConvertHTML should have been deleted but still exists.");
        } catch (DataObjectNotFoundException e) {
            //good
        }
    }


    /**
     * Test creating and writing into a PostConvertHTML object
     */
    public void testPostConvertHTML() {
        final String TEST_CONTENT = "Test post-convert Content.";
        final BigDecimal EXP_ID = new BigDecimal(128);  // random id

        PostConvertHTML p, p1;

        BigDecimal pID;

        // Create a new PostConvertHTML object
        try {
            p = new PostConvertHTML();
            p.setId(EXP_ID);
            p.setContent(TEST_CONTENT);
            pID = EXP_ID;
            p.save();

            p1 = new PostConvertHTML
                (new OID(PostConvertHTML.BASE_DATA_OBJECT_TYPE, pID));
            String foundContent = p1.getContent();
            assertTrue("Content, expected=" + TEST_CONTENT + " found=" +
                       foundContent, TEST_CONTENT.equals(foundContent));

        } catch (DataObjectNotFoundException e) {
            fail(e.getMessage());
            return;
        }

        //Delete object
        try {
            p.delete();

        } catch (Exception e) {
            fail(e.getMessage());
            return;
        }
        try {
            p1 = new PostConvertHTML
                (new OID(PostConvertHTML.BASE_DATA_OBJECT_TYPE, pID));

            //the above line should generate an exception
            fail("PostConvertHTML should have been deleted but still exists.");
        } catch (DataObjectNotFoundException e) {
            //good
        }
    }

    /***
     * Get value of ACS_HOME environment variable
     ***/
    private String getACS_HOME() {
        String value = null;
        try {
            String[] cmd = { "/bin/sh", "-c", "echo $" + "ACS_HOME"};
            Process p = Runtime.getRuntime().exec(cmd);
            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            value = br.readLine();
        } catch (Exception e) {
            fail("Unable to get ACS_HOME environment variable.");
        }
        return value;
    }

    /***
     * Load a file from the cms/test/src/convertHTML directory
     ***/
    private byte [] loadFile(String fileName) throws IOException {


        String pathStub = System.getProperty("test.webapp.dir");
        String fullPath = pathStub + "/tests/com/arsdigita/mimetypes/convertHTML/" + fileName;

        byte [] file_bytes = null;
        InputStream fs = new FileInputStream(fullPath);
        file_bytes = new byte[fs.available()];
        fs.read(file_bytes);
        fs.close();
        return file_bytes;
    }


    /**
     * Test conversion of RTF document to html
     */

    public void testRtfConversion() throws Exception {
        // Don't do test if mime-type initializer not setup
        if (MimeTypeStatus.getMimeTypeStatus().getInsoFilterWorks().equals(new BigDecimal(0))) {
            return;
        }
        // First test stripWhiteSpace function
        String in = " <   H>   e \t\n ll/>   o  . \n   ";
        String expected_out = "< H> e ll/> o .";
        String actual_out = StringUtils.stripWhiteSpace(in);
        assertTrue("stripWhiteSpace failed.  Expected = '" +
                   expected_out + "', Found = '" + actual_out + "'",
                   actual_out.equals(expected_out));

        byte [] rtf_in = loadFile("rtftest.rtf");
        expected_out = new String(loadFile("rtftest.html"));
        BigDecimal id = new BigDecimal(1);
        actual_out = ConvertFormat.toHTML(rtf_in);
        if (actual_out == null) {
            fail("Unable to convert rtf document to html.");
        }
        // remove white space before doing matching.  In case version
        // of INSO filter changes.
        expected_out = StringUtils.stripWhiteSpace(expected_out);
        actual_out = StringUtils.stripWhiteSpace(actual_out);
        assertTrue("RTF document converted, but result does not " +
                   "match expected.  EXPECTED=" + expected_out + " FOUND=" +
                   actual_out, expected_out.equals(actual_out));
    }
}

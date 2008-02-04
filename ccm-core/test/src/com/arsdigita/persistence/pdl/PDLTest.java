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
package com.arsdigita.persistence.pdl;

import com.arsdigita.persistence.metadata.MetadataRoot;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

/**
 * PDLTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 */

public class PDLTest extends TestCase {

    public final static String versionId = "$Id: PDLTest.java 745 2005-09-02 10:50:34Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static Logger s_log =
        Logger.getLogger(PDLTest.class.getName());

    static final String PDL_FILE_ROOT_DIR = "com/arsdigita/persistence/pdl/";
    // This is a comma delimited format containing the names of bad PDL test files,
    // along with an optional error strings that should be in the exception thrown
    // by the PDL parser. The file name should not contain the .pdl extension. Ex:
    // foo,BAD
    // bar,Bad,Type2
    // gronk
    static final String BAD_PDL_FILE_INDEX = PDL_FILE_ROOT_DIR + "badpdl.csv";

    public PDLTest(String name) {
        super(name);
    }

    public void test() {
        MetadataRoot r = MetadataRoot.getMetadataRoot();
        PDL m = new PDL();
        try
            {
                m.loadResource("com/arsdigita/persistence/pdl/test1.pdl");
                m.loadResource("com/arsdigita/persistence/pdl/test2.pdl");
            }
        catch(PDLException p)
            {
                fail("Error loading pdl files. Should not fail. " + p.getMessage());
            }


        //s_log.debug(m.getAST());

        try
            {
                m.generateMetadata(r);
                //s_log.debug(r);

            } catch(Exception me)
                {
                    fail("Error generating meta data. Should not fail. " + me.getMessage());
                }

        try
            {
                PDL bad = new PDL();
                bad.loadResource("com/arsdigita/persistence/pdl/bad1.pdl");
                fail("PDL Failed to throw an exception on bad1.pdl!");
            }
        catch(PDLException p)
            {
                s_log.debug(p);
            }
    }

    // this should fail now due to object type/super type check
    public void FAILStestEmptyObject() throws Exception {
        getPDL("emptyObject").generateMetadata(MetadataRoot.getMetadataRoot());
    }

    private String makeResourceName(String baseName) {
        return PDL_FILE_ROOT_DIR + baseName + ".pdl";
    }

    private PDL getPDL(String baseName) throws Exception {
        PDL pdl = new PDL();
        pdl.loadResource(makeResourceName(baseName));
        return pdl;
    }

    /*  Loads the given PDL file, parses it, and checks to see if there are
     *  any errors. If there is an error, it must also contain any error
     *  codes in errorKeywords.
     *
     */
    private void checkErrorMessage(String basePDLName,
                                   Collection errorKeywords,
                                   Collection failures)
        throws Exception
    {
        s_log.info("Checking bad pdl: " + basePDLName);
        try {
            MetadataRoot r = MetadataRoot.getMetadataRoot();
            PDL pdl = getPDL(basePDLName);
            pdl.generateMetadata(r);
        } catch (Throwable e) {
            //s_log.info(basePDLName + " error " + e.getMessage());
            String s = e.toString();
            Iterator iter = errorKeywords.iterator();
            while(iter.hasNext())  {
                final String errorString = (String)iter.next();
                if (s.indexOf(errorString) < 0) {
                    failures.add("error message for " + basePDLName +
                                 " does not contain \"" + errorString
                                 + "\"; error message:\n" + s);
                }
            }
            return;
        }

        // This can't be in the try clause because then it would
        // be caught.
        failures.add("no exception for " + basePDLName);
    }

    /**
     *  This test method imports a list of PDL files contained in BAD_PDL_FILE_INDEX.
     *  Each PDL file is then loaded and evaluated. The files all contain errors,
     *  and the PDL processor should catch them. Each test in BAD_PDL_FILE_INDEX also
     *  has 0..N error messages that should be in the exception thrown by the parser.
     *  The test fails if no error is detected, or if the error does not report the
     *  correct information.
     */
    public void testBadPDLFiles() throws Exception {
        // File input reader for the list of bad pdl files.
        // Using LineNumberReader for convenience in reporting problems in
        // the file
        LineNumberReader reader = null;
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(BAD_PDL_FILE_INDEX);
            reader = new LineNumberReader(new InputStreamReader(is));

            LinkedList failures = new LinkedList();
            String line = null;
            while( (line = reader.readLine()) != null ) {
                final boolean lineIsCommented = line.startsWith("//");
                if( lineIsCommented ) {
                    continue;
                }
                StringTokenizer tokenizer = new StringTokenizer(line, ",");
                final boolean formatIsBad = (tokenizer.countTokens() < 1);
                if (formatIsBad) {
                    s_log.info("Warning: Incorrect format at line " + reader.getLineNumber()
                               + ". Line will not be parsed. Value is: " + line);
                } else {
                    final String fileName = tokenizer.nextToken().trim();
                    // optional list of error keywords that should be in an error message.
                    LinkedList errorKeywords = new LinkedList();
                    while (tokenizer.hasMoreElements()) {
                        errorKeywords.add(tokenizer.nextToken().trim());
                    }
                    checkErrorMessage( fileName, Collections.unmodifiableList(errorKeywords), failures );
                }
            }
            if( failures.size() > 0 ) {
                fail("Bad PDL Files parsed correctly! " + failures);
            }
        } finally {
            if( null != reader ) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

    }

    public void testIncrementalLoad() throws Exception {
        try {
            MetadataRoot root = MetadataRoot.getMetadataRoot();
            PDL pdl = new PDL();
            pdl.loadResource(
                             "com/arsdigita/persistence/pdl/IncrementalLoadPart1.pdl"
                             );
            pdl.generateMetadata(root);

            pdl = new PDL();
            pdl.loadResource(
                             "com/arsdigita/persistence/pdl/IncrementalLoadPart2.pdl"
                             );
            pdl.generateMetadata(root);

            pdl = new PDL();
            pdl.loadResource(
                             "com/arsdigita/persistence/pdl/IncrementalLoadPart3.pdl"
                             );
            pdl.generateMetadata(root);

            //root.getModel("incrementalLoad").outputPDL(System.out);
            //root.getModel("incrementalLoadOtherModel").outputPDL(System.out);
        } catch (PDLException e) {
            fail("Incremental load failed: " + e.getMessage());
        }
    }

    public void testMultipleStatementsInPropertyAdd() throws Exception {
        PDL pdl = getPDL("MultipleStatementsInPropertyAdd");
        pdl.generateMetadata(MetadataRoot.getMetadataRoot());
    }

}

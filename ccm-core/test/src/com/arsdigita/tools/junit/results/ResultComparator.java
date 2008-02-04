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

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;

/**
 * ResultComparator
 *
 * @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 *
 */
public class ResultComparator {
    public static void main(String[] args) throws Exception {
        String htmlOutputDir = args[0];
        String previousChangelist = args[1];
        String currentChangelist = args[2];

        diffAll(htmlOutputDir, previousChangelist, currentChangelist);
//          compareFiles("clean.xml", "TEST-com.arsdigita.persistence.PersistenceSuite.xml");
    }

    private static void diffAll(String htmlOutputDir, String previousChangelist, String currentChangelist) throws Exception {
        ResultFileSetLoader loader = new ResultFileSetLoader();
        Map currentTests = loader.loadResultFiles(".");

        FileTestImporter imp = new FileTestImporter();
        Map previousTests = imp.getTestsForChangelist(previousChangelist);

        ArrayList diffs = new ArrayList(currentTests.keySet().size());
        Object[] keys = currentTests.keySet().toArray();
        Arrays.sort(keys);

        for (int i = 0; i < keys.length; i++) {
            String testFile = (String) keys[i];


            XMLResult previous = (XMLResult) previousTests.get(testFile);
            if (null == previous) {
                System.out.println("Null Test file: " + testFile);
                previous = new EmptyXMLResult(testFile);
                previousTests.put(testFile, previous);
            }
            previous.setChangelist(previousChangelist);
            XMLResult current = (XMLResult) currentTests.get(testFile);
            current.setChangelist(currentChangelist);
            ResultDiff diff = new ResultDiff(previous, current);
            diffs.add(diff);
        }

		String databaseType = System.getProperty("database.key");
        ReportIndex index = new ReportIndex(previousChangelist, currentChangelist, databaseType);
        XMLOutputter out = new XMLOutputter("  ", true);
        final String ACS_HOME = System.getProperty("ACS_HOME");
        Transformer tran =  TransformerFactory.newInstance().newTransformer(new StreamSource(ACS_HOME + "/test/xsl/junit.xsl"));
        for (Iterator iterator = diffs.iterator(); iterator.hasNext();) {
            ResultDiff resultDiff = (ResultDiff) iterator.next();
            index.addResult(resultDiff);

            String htmlFile = resultDiff.getAttributeValue("name") + ".html";
            FileWriter file = new FileWriter(htmlOutputDir + "/" + htmlFile);
            JDOMResult html = new JDOMResult();
            tran.transform(new JDOMSource(new Document(resultDiff)), html);
            out.output(html.getDocument(), file);
        }

        JDOMResult indexHtml = new JDOMResult();
        tran =  TransformerFactory.newInstance().newTransformer(new StreamSource(ACS_HOME + "/test/xsl/index.xsl"));
        tran.transform(new JDOMSource(new Document(index)), indexHtml);
        FileWriter indexFile = new FileWriter(htmlOutputDir + "/index.html");
        out.output(indexHtml.getDocument(), indexFile);

        out.output(new Document(index), new FileOutputStream("report_index_" + currentChangelist + ".xml"));
    }

    private static void compareFiles(String canonical, String newFile) throws JDOMException, TransformerException, IOException {
        SAXBuilder builder = new SAXBuilder();
        builder.setFactory(new ResultJDOMFactory());

        XMLResult previous = (XMLResult) builder.build(canonical).getRootElement();
        XMLResult current = (XMLResult) builder.build(newFile).getRootElement();

        ResultDiff diff = new ResultDiff(previous, current);
        Document junitReport = new Document(diff);
        Transformer tran =  TransformerFactory.newInstance().newTransformer(new StreamSource("test/xsl/junit.xsl"));
//        JDOMResult html = new JDOMResult();
//        tran.transform(new JDOMSource(junitReport), html);
        XMLOutputter out = new XMLOutputter("  ", true);
//        out.output(html.getDocument(), System.out);
        out.output(junitReport, System.out);
    }
}

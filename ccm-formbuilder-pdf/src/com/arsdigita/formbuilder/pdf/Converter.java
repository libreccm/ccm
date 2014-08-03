/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.formbuilder.pdf;

import com.arsdigita.util.UncheckedWrapperException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.fop.apps.Driver;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * This servlet class uses Apache's FOP and format xml data in PDF Format.
 * 
 * Creation History Start
 * @Author Name:	CS Gupta
 * @Create Date:	23/11/2004
 * @Company: Infoaxon Technology
 * Creation History End
 */

public class Converter extends HttpServlet
{
    private static final Logger s_log = Logger.getLogger(Converter.class);
    public static final String BASE_DATA_OBJECT_TYPE =
            "com.arsdigita.formbuilder.pdf.Converter";

    private TransformerFactory transformerFactory;
    
    public Converter() { }

    @Override
    public void init() throws ServletException
    {
        transformerFactory = TransformerFactory.newInstance();
    }

    //Setup FOP
    @Override
    public void  doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        s_log.debug("Converter-doGet(HttpServletRequest,HttpServletResponse)-Enter");
        HttpSession session=req.getSession();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Using session: " + session.getId());
        }

        //Request form data from PDFListener. Form data is stored in Vector inside Vector
        //This is main Vecor that contains inner Vector
        Vector objOuterFormData = PDFListener.getFormData(session.getId());
        if (null == objOuterFormData) {
            s_log.error("Converter called with no FormData in session");
            return;
        }

        Iterator iter=objOuterFormData.iterator();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Dumping FormData");
            while (iter.hasNext()) {
                Vector inner = (Vector) iter.next();
                s_log.debug(inner.firstElement().toString() + ": " +
                            inner.lastElement().toString());
            }

            iter = objOuterFormData.iterator();
        }

        //This is inner Vector for Form Data that contains Caption and Value
        Vector objInnerFormData=null;

        byte[] bytes = "<?xml version='1.0' encoding='UTF-8'?><Document/>".getBytes();
        ByteArrayInputStream objByteArrayInputStream = new ByteArrayInputStream(bytes);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Document xmldoc;
        try {
            db = dbf.newDocumentBuilder();
            xmldoc = db.parse(objByteArrayInputStream);
        } catch (Exception ex) {
            throw new UncheckedWrapperException(ex);
        }

        Node grpRoot=xmldoc.getDocumentElement();
        //Iterating objOuterFormData Vector
        while (iter.hasNext())
        {
                objInnerFormData=new Vector();
                objInnerFormData=(Vector)iter.next();
                //This is for Caption
                String strkey =(String)objInnerFormData.firstElement();
                //This is for Value
                String strValue=(String)objInnerFormData.lastElement();

                Element root = xmldoc.createElement("Section");

                Node captionNode = xmldoc.createElement("Caption");
                Text captionText = xmldoc.createTextNode(strkey);
                captionNode.appendChild(captionText);

                Node valueNode = xmldoc.createElement("Value");
                Text valueText = xmldoc.createTextNode(strValue);
                valueNode.appendChild(valueText);

                root.appendChild(captionNode);
                root.appendChild(valueNode);
                grpRoot.appendChild(root);
        }

        res.setContentType("application/pdf");
        Driver driver = new Driver();
        driver.setRenderer(1);
        //Setup a buffer to obtain the content length
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        driver.setOutputStream(out);

        //Setup Transformer
        javax.xml.transform.Source xsltSrc =
            new StreamSource(new File(PDFConfig.retrieve().getXSLFile()));

        try {
            Transformer transformer =
                transformerFactory.newTransformer(xsltSrc);

            //Make sure the XSL transformation's result is piped through to FOP
            javax.xml.transform.Result result =
                new SAXResult(driver.getContentHandler());
            //Setup input and start the transformation and rendering process
            transformer.transform(new DOMSource(xmldoc), result);
        } catch (Exception ex) {
            throw new UncheckedWrapperException(ex);
        }

        //Prepare response
        res.setContentLength(out.size());
        //Send content to Browser
        res.getOutputStream().write(out.toByteArray());
        res.getOutputStream().flush();

        s_log.debug("Converter-doGet(HttpServletRequest,HttpServletResponse)-Exit");
    }
}

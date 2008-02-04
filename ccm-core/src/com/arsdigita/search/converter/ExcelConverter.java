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
package com.arsdigita.search.converter;

import com.arsdigita.mimetypes.MimeType;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import org.apache.log4j.Logger;

/**
 * This class provides the mechanism to perform a conversion from
 * Excel to standard text
 */
public class ExcelConverter extends BaseConverter {

    /**
     *  Returns a string array representing all of the files types
     *  used by the given converter
     */
    protected String[] getFileExtensions() {
        String[] extensions = {"xls"};
        return extensions;
    }

    /**
     *  This takes in a document represented as an InputStream and returns
     *  a text representation of that document.
     */
    public String convertDocument(InputStream stream) throws ConversionException {
        try {
            // create a new org.apache.poi.poifs.filesystem.Filesystem
            POIFSFileSystem poifs = new POIFSFileSystem(stream);
            // get the Workbook (excel part) stream in a InputStream
            InputStream din = poifs.createDocumentInputStream("Workbook");
            // construct out HSSFRequest object
            HSSFRequest req = new HSSFRequest();
            // lazy listen for ALL records with the listener shown above
            StringBuffer output = new StringBuffer();
            req.addListenerForAllRecords(new ExcelEvent(output));
            // create our event factory
            HSSFEventFactory factory = new HSSFEventFactory();
            // process our events based on the document input stream
            factory.processEvents(req, din);
            // once all the events are processed close our file input stream
            stream.close();
            // and our document input stream (don't want to leak these!)
            din.close();
            return output.toString();
        } catch (IOException ioe) {
            throw new ConversionException(ioe);
        } catch (Exception ex) {
            throw new ConversionException(ex);
        }
    }


    /**
     * This example shows how to use the event API for reading a file.
     */
    public class ExcelEvent implements HSSFListener {
        private SSTRecord sstrec;
        private Logger s_log = Logger.getLogger(MimeType.class);
        
        private StringBuffer m_output;

        public ExcelEvent(StringBuffer output) {
            m_output = output;
        }

        /**
         * This method listens for incoming records and handles 
         *  them as required.
         *
         *  A lot of this is taken from the POI example
         *
         * @param record    The record that was found while reading.
         */
        public void processRecord(Record record) {
            switch (record.getSid()) {
            case BOFRecord.sid:
                if (s_log.isDebugEnabled()) {
                    BOFRecord bof = (BOFRecord) record;
                    if (bof.getType() == bof.TYPE_WORKBOOK) {
                        s_log.debug("Encountered workbook");
                        // assigned to the class level member
                    } else if (bof.getType() == bof.TYPE_WORKSHEET) {
                        s_log.debug("Encountered sheet reference");
                    }
                }
                break;
            case NumberRecord.sid:
                NumberRecord numrec = (NumberRecord) record;
                m_output.append(" " + numrec.getValue());
                break;
            case SSTRecord.sid:
                sstrec = (SSTRecord) record;
                break;
            case LabelSSTRecord.sid:
                LabelSSTRecord lrec = (LabelSSTRecord) record;
                m_output.append(" " + sstrec.getString(lrec.getSSTIndex()));
                break;
            }
        }
    }
} 

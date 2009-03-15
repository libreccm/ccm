/*
 * Copyright (C) Permeance Technologies Pty Ltd. All Rights Reserved.
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
package com.arsdigita.bebop.parameters;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.ParameterEvent;

import junit.framework.TestCase;

public class URIValidationListenerTest extends TestCase {
    
    public void testNull() throws FormProcessException {
        doTest(null, false);
    }

    public void testEmpty() throws FormProcessException {
        doTest("", false);
    }
    
    public void testNoProtocol() throws FormProcessException {
        doTest("a", true);
        doTest("ab", true);
        doTest("abc", true);
        doTest("abcd", true);
        doTest("abcde", true);
    }
    
    public void testMissingHostname() throws FormProcessException {
        doTest("foo://", true);
    }
    
    public void testRfcExamples() throws FormProcessException {
        doTest("ftp://ftp.is.co.za/rfc/rfc1808.txt", false);
        doTest("gopher://spinaltap.micro.umn.edu/00/Weather/California/Los%20Angeles", false);
        doTest("http://www.math.uio.no/faq/compression-faq/part1.html", false);
        doTest("mailto:mduerst@ifi.unizh.ch", false);
        doTest("news:comp.infosystems.www.servers.unix", false);
        doTest("telnet://melvyl.ucop.edu/", false);
    }
    
    private void doTest(String value, boolean errorExpected) throws FormProcessException {
        URIValidationListener listener = new URIValidationListener();
        ParameterEvent e = new ParameterEvent(this, new ParameterData(null, value)); 
        assertFalse(e.getParameterData().getErrors().hasNext());
        listener.validate(e);
        if (errorExpected) {
            assertTrue(value + " should fail", e.getParameterData().getErrors().hasNext());
        } else {
            assertFalse(value + " shouldn't fail", e.getParameterData().getErrors().hasNext());
        }
    }
}

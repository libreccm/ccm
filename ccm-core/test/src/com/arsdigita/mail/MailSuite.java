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
package com.arsdigita.mail;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.CoreTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * MailSuite
 *
 * Test suite for the ACS Mail package.
 *
 * @version $Id: MailSuite.java 749 2005-09-02 12:11:57Z sskracic $
 */

public class MailSuite extends PackageTestSuite {

    public static final String versionId = "$Id: MailSuite.java 749 2005-09-02 12:11:57Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    public MailSuite() {
        super();
    }

    public MailSuite(Class theClass) {
        super(theClass);
    }

    public MailSuite(String name) {
        super(name);
    }

    public static Test suite() {
        MailSuite suite = new MailSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new CoreTestSetup(suite);
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(suite());
    }
}

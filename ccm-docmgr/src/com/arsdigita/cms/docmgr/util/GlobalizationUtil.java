
/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.docmgr.util;

import com.arsdigita.globalization.Globalized;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * <p>
 * .
 * Contains methods to simplify globalizing keys
 * </p>
 *
 * @author <a href="mailto:sarnold@redhat.com">sarnold@redhat.com</a>
 * @version $Revision: #1 $ $Date: 2003/07/28 $
 */

public class GlobalizationUtil implements Globalized {
    
    private static final String BUNDLE_NAME = "com.arsdigita.cms.docmgr.ui.DMResources";

    public static GlobalizedMessage globalize(String key) {
	 return new GlobalizedMessage(key, BUNDLE_NAME);
     }
    public static GlobalizedMessage globalize(String key, Object[] args) {
         return new GlobalizedMessage(key, BUNDLE_NAME, args);

  }
}

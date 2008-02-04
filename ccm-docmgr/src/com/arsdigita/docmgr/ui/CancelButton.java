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

package com.arsdigita.docmgr.ui;

import com.arsdigita.bebop.form.Submit;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Customized Cancel button that takes you 1 level back in
 * the browser history.
 */

class CancelButton extends Submit {
    public CancelButton(GlobalizedMessage label) {
        super(label);
        avoidDoubleClick(false);
        setAttribute(ON_CLICK,
                     "history.go(-1); return false;");
    }
}

/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.portalworkspace.ui.admin;

import com.arsdigita.bebop.*;
import com.arsdigita.bebop.event.*;
import com.arsdigita.bebop.util.Color;

class ErrorMessageDisplay extends Label {
    public ErrorMessageDisplay(final RequestLocal errorMessageRL) {
        setColor(Color.red);
        setClassAttr("pageErrorDisplay");
        addPrintListener(new PrintListener() {
                public void prepare(PrintEvent ev) {
                    Label target = (Label)ev.getTarget();
                    String errMsg =
                        (String)errorMessageRL.get(ev.getPageState());
                    if (errMsg != null) {
                        target.setLabel(errMsg);
                    }
                }
            });
    }
}

/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.util.cmd;

import com.arsdigita.logging.ErrorReport;

public class ProgramErrorReport extends ErrorReport {
    
    public ProgramErrorReport(Throwable throwable,
                              String[] args) {
        super(throwable);
        
        try {
            addArgs(args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }    
    
    private void addArgs(String[] args) {
        addSection("Command line arguments",
                   args);
    }
}

/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.packaging;

import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterContext;
import com.arsdigita.util.parameter.ParameterReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ParameterMap
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/08/16 $
 **/

class ParameterMap {

    public final static String versionId = 
            "$Id: ParameterMap.java 736 2005-09-01 10:46:05Z sskracic $" +
            " by $Author: sskracic $, " +
            "$DateTime: 2004/08/16 18:10:38 $";

    private List m_contexts = new ArrayList();
    private List m_parameters = new ArrayList();
    private Map m_containers = new HashMap();
    private Map m_parametersByName = new HashMap();

    public void addContext(ParameterContext context) {
        m_contexts.add(context);
        Parameter[] params = context.getParameters();
        for (int i = 0; i < params.length; i++) {
            m_parameters.add(params[i]);
            m_containers.put(params[i], context);
            m_parametersByName.put(params[i].getName(), params[i]);
        }
    }

    public void addContexts(Collection contexts) {
        for (Iterator it = contexts.iterator(); it.hasNext(); ) {
            addContext((ParameterContext) it.next());
        }
    }

    public List getContexts() {
        return m_contexts;
    }

    public List getParameters() {
        return m_parameters;
    }

    public Parameter getParameter(String name) {
        return (Parameter) m_parametersByName.get(name);
    }

    public ParameterContext getContainer(Parameter param) {
        return (ParameterContext) m_containers.get(param);
    }

    public Object get(Parameter param) {
        return getContainer(param).get(param);
    }

    public void set(Parameter param, Object obj) {
        getContainer(param).set(param, obj);
    }

    public void validate(ErrorList errs) {
        for (Iterator it = getContexts().iterator(); it.hasNext(); ) {
            ParameterContext ctx = (ParameterContext) it.next();
            ctx.validate(errs);
        }
    }

    public boolean validate(PrintStream out) {
        ErrorList errs = new ErrorList();
        validate(errs);
        if (errs.isEmpty()) {
            return true;
        } else {
            out.println(" *** Error ***");
            errs.report(new OutputStreamWriter(out));
            return false;
        }
    }

    public void load(ParameterReader reader, ErrorList errs) {
        for (Iterator it = getContexts().iterator(); it.hasNext(); ) {
            ParameterContext ctx = (ParameterContext) it.next();
            ctx.load(reader, errs);
        }
    }

    public boolean load(ParameterReader reader, PrintStream out) {
        ErrorList errs = new ErrorList();
        load(reader, errs);
        if (errs.isEmpty()) {
            return true;
        } else {
            out.println(" *** Error ***");
            errs.report(new OutputStreamWriter(out));
            return false;
        }
    }

}

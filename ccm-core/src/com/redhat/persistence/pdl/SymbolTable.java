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
package com.redhat.persistence.pdl;

import com.redhat.persistence.metadata.Model;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.pdl.nodes.FileNd;
import com.redhat.persistence.pdl.nodes.ImportNd;
import com.redhat.persistence.pdl.nodes.Node;
import com.redhat.persistence.pdl.nodes.ObjectTypeNd;
import com.redhat.persistence.pdl.nodes.TypeNd;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * SymbolTable
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 **/

class SymbolTable {

    

    private HashMap m_types = new HashMap();
    private ArrayList m_order = new ArrayList();
    private HashMap m_resolutions = new HashMap();
    private HashMap m_emitted = new HashMap();
    private ErrorReport m_report;
    private Root m_root;

    public SymbolTable(ErrorReport report, Root root) {
        m_report = report;
        m_root = root;
    }

    public void define(ObjectTypeNd type) {
        if (isDefined(type.getQualifiedName())) {
            ObjectTypeNd original = getObjectType(type.getQualifiedName());
	    if (original == null) {
		m_report.fatal(type, "already loaded");
	    } else {
		m_report.fatal
		    (type, "duplicate type definition for " +
                     type.getQualifiedName() + ", original definition: " +
		     original.getLocation());
	    }
        } else {
            m_types.put(type.getQualifiedName(), type);
            m_order.add(type);
        }
    }

    public String resolve(TypeNd type) {
        String result = null;

        if (type.isQualified()) {
            result = type.getQualifiedName();
        } else {
            FileNd file = type.getFile();
            Collection imps = file.getImports();

            ArrayList qnames = new ArrayList();


            // First check imports
            for (Iterator it = imps.iterator(); it.hasNext(); ) {
                ImportNd imp = (ImportNd) it.next();
                String qname = imp.qualify(type);
                if (qname != null && isDefined(qname)) {
                    qnames.add(qname);
                }
            }

            if (qnames.size() == 0) {
                String[] special = new String[] {
                    file.getModel().getName() + "." + type.getName(),
                    "global." + type.getName()
                };

                for (int i = 0; i < special.length; i++) {
                    if (isDefined(special[i])) {
                        result = special[i];
                        break;
                    }
                }
            } else if (qnames.size() > 1) {
                m_report.fatal(type, "ambiguous symbol, resolves to: " +
                               qnames);
                return null;
            } else {
                result = (String) qnames.get(0);
            }
        }

        if (result == null) {
            m_report.fatal(type, "unresolved symbol: " +
                           type.getName());
        } else {
            m_resolutions.put(type, result);
        }

        return result;
    }

    private boolean isDefined(String qualifiedName) {
        return m_types.containsKey(qualifiedName) ||
            m_emitted.containsKey(qualifiedName);
    }

    private ObjectTypeNd getObjectType(String qualifiedName) {
        return (ObjectTypeNd) m_types.get(qualifiedName);
    }

    public String lookup(TypeNd type) {
        return (String) m_resolutions.get(type);
    }

    public boolean sort() {
        HashSet defined = new HashSet();
        defined.addAll(m_emitted.keySet());

        ArrayList undefined = new ArrayList();
        undefined.addAll(m_order);
        ArrayList nwo = new ArrayList();

        HashSet circular = new HashSet();
        ArrayList circOrd = new ArrayList();

        int before;
        do {
            if (undefined.size() == 0) { break; }
            before = defined.size();
            for (Iterator it = undefined.iterator(); it.hasNext(); ) {
                ObjectTypeNd type = (ObjectTypeNd) it.next();
                if (!circular.contains(type) && isCircular(type)) {
                    circular.add(type);
                    circOrd.add(type);
                }
                if (type.getExtends() == null ||
                    defined.contains(lookup(type.getExtends()))) {
                    defined.add(type.getQualifiedName());
                    nwo.add(type);
                    it.remove();
                }
            }
        } while (defined.size() > before);

        for (Iterator it = circOrd.iterator(); it.hasNext(); ) {
            ObjectTypeNd ot = (ObjectTypeNd) it.next();
            m_report.fatal(ot, "circular type dependency: " +
                           ot.getQualifiedName());
        }

        if (undefined.size() > 0) {
            return false;
        } else {
            m_order = nwo;
            return true;
        }
    }

    private boolean isCircular(ObjectTypeNd type) {
        return isCircular(type, type, new HashSet());
    }

    private boolean isCircular(ObjectTypeNd type, ObjectTypeNd start,
                               HashSet visited) {
        if (visited.contains(type)) {
            return false;
        } else if (type.getExtends() == null) {
            return false;
        } else {
            ObjectTypeNd sup = getObjectType(lookup(type.getExtends()));
            if (sup == null) {
                return false;
            } else if (sup.equals(start)) {
                return true;
            } else {
                visited.add(type);
                return isCircular(sup, start, visited);
            }
        }
    }

    public Collection getObjectTypes() {
        return m_order;
    }

    public void addEmitted(ObjectType type) {
        m_emitted.put(type.getQualifiedName(), type);
    }

    public ObjectType getEmitted(String qname) {
        return (ObjectType) m_emitted.get(qname);
    }

    public ObjectType getEmitted(ObjectTypeNd type) {
        return (ObjectType) m_emitted.get(type.getQualifiedName());
    }

    public ObjectType getEmitted(TypeNd type) {
        return getEmitted(lookup(type));
    }

    public void emit() {
        for (Iterator it = m_order.iterator(); it.hasNext(); ) {
            ObjectTypeNd ot = (ObjectTypeNd) it.next();
            ObjectType sup = null;
            if (ot.getExtends() != null) {
                sup = getEmitted(ot.getExtends());
            }
            ObjectType type =
                new ObjectType
                    (Model.getInstance(ot.getFile().getModel().getName()),
                     ot.getName().getName(), sup);
            addEmitted(type);
            setLocation(type, ot);
        }
    }

    final void setLocation(Object element, Node nd) {
        m_root.setLocation
            (element, nd.getFile().getName(), nd.getLine(), nd.getColumn());
    }

}

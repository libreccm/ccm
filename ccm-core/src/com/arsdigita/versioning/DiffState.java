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
package com.arsdigita.versioning;

import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.AssertionError;

// new versioning

/**
 * This encapsulates most of the information about the state of the {@link
 * DataObjectDiff diff} object.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @version $Revision: #7 $ $DateTime: 2004/08/16 18:10:38 $
 * @since 2003-04-16
 */
final class DiffState implements Constants {

    private final VType m_vType;

    private CD m_cd;
    private ACE m_actualCurrent;
    private ECE m_expectedCurrent;
    private EPE m_previousEPE;
    private EPE m_expectedPast;

    DiffState(ObjectType objType) {
        m_cd = CD.UNKNOWN;
        m_actualCurrent = null;
        m_expectedCurrent = ECE.UNKNOWN;
        m_expectedPast = EPE.UNKNOWN;

        ObjectTypeMetadata otmd = ObjectTypeMetadata.getInstance();
        if ( otmd.isFullyVersioned(objType) ) {
            m_vType = VType.VERSIONED;
        } else if ( otmd.isRecoverable(objType) ) {
            m_vType = VType.RECOVERABLE;
        } else if ( otmd.isUnreachable(objType) ) {
            m_vType = VType.IGNORABLE;
        } else {
            throw new AssertionError("can't possibly get here: " + this);
        }
    }

    public boolean isVersioned() {
        return m_vType == VType.VERSIONED;
    }

    public boolean isRecoverable() {
        return m_vType == VType.RECOVERABLE;
    }

    public boolean isIgnorable() {
        return m_vType == VType.IGNORABLE;
    }

    public CD getCreateDeleteStatus() {
        return m_cd;
    }

    public void setCreateDeleteStatus(boolean consistent) {
        if ( consistent ) {
            m_cd = CD.CONSISTENT;
        } else {
            m_cd = CD.INCONSISTENT;
        }
    }

    public ACE getActualCurrent() {
        return m_actualCurrent;
    }

    public void setActualCurrent(boolean exists) {
        if ( exists ) {
            m_actualCurrent = ACE.ON;
        } else {
            m_actualCurrent = ACE.OFF;
        }
    }

    public ECE getExpectedCurrent() {
        return m_expectedCurrent;
    }

    public EPE getExpectedPast() {
        return m_expectedPast;
    }

    public void setExpectedPast(boolean existed) {

        if ( m_previousEPE == null ) {
            // this is the first call to setExpectedPast
            if ( existed ) {
                m_expectedCurrent = ECE.OFF;
            } else {
                m_expectedCurrent = ECE.ON;
            }
        }

        final EPE epe = existed ? EPE.ON : EPE.OFF;

        if ( epe == m_previousEPE) {
            m_cd = CD.INCONSISTENT;
        }

        m_previousEPE = m_expectedPast;
        m_expectedPast = epe;
    }

    public String toShortString() {
        final String comma = ",";
        StringBuffer sb = new StringBuffer(64);
        sb.append("(");
        sb.append(m_vType.shortName()).append(comma);
        sb.append(m_cd.shortName()).append(comma);
        sb.append(m_actualCurrent==null ?
                  "<null>" :
                  m_actualCurrent.shortName());
        sb.append(comma);
        sb.append(m_expectedCurrent.shortName()).append(comma);
        sb.append(m_expectedPast.shortName());
        sb.append(")");
        return sb.toString();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(128);
        sb.append("short state: ").append(toShortString()).append(LINE_SEP);
        sb.append(m_vType).append(LINE_SEP);
        sb.append(m_cd).append(LINE_SEP);
        sb.append(m_actualCurrent).append(LINE_SEP);
        sb.append(m_expectedCurrent).append(LINE_SEP);
        sb.append(m_expectedPast).append(LINE_SEP);
        return sb.toString();
    }

    /*
     * Helper classes for substate enums.
     */

    abstract static class Enum {
        private final String m_name;
        private final String m_string;

        protected Enum(String type, String instance) {
            m_name = instance;
            StringBuffer sb = new StringBuffer(64);
            sb.append(type).append(": ").append(instance);
            m_string = sb.toString();
        }

        public String shortName() {
            return m_name;
        }

        public String toString() {
            return m_string;
        }
    }

    /**
     * This enum represents the Create-Delete Consistency status.
     **/
    final static class CD extends Enum {

        public final static CD CONSISTENT   = new CD("consistent");
        public final static CD INCONSISTENT = new CD("inconsistent");
        public final static CD UNKNOWN      = new CD("unknown");

        private CD(String name) {
            super("Create-Delete", name);
        }
    }


    /**
     * This enum represents the Actual Current Existence status.
     **/
    final static class ACE extends Enum {
        public final static ACE OFF = new ACE("off");
        public final static ACE ON  = new ACE("on");

        private ACE(String name) {
            super("Actual Current Existence", name);
        }
    }

    /**
     * This enum represents the Expected Past Existence status.
     **/
    final static class ECE extends Enum {
        public final static ECE OFF     = new ECE("off");
        public final static ECE ON      = new ECE("on");
        public final static ECE UNKNOWN = new ECE("unknown");

        private ECE(String name) {
            super("Expected Current Existence", name);
        }
    }

    /**
     * This enum represents the Expected Past Existence status.
     **/
    final static class EPE extends Enum {
        public final static EPE OFF     = new EPE("off");
        public final static EPE ON      = new EPE("on");
        public final static EPE UNKNOWN = new EPE("unknown");

        private EPE(String name) {
            super("Expected Past Existence", name);
        }
    }

    private final static class VType extends Enum {
        public final static VType VERSIONED = new VType("versioned");
        public final static VType RECOVERABLE = new VType("recoverable");
        public final static VType IGNORABLE   = new VType("ignorable");

        private VType(String name) {
            super("V-Type", name);
        }
    }
}

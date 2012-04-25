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
package com.arsdigita.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 * This class allows you to intern objects in an efficient, thread-safe manner.
 *
 * <p>The classic example of interning is {@link java.lang.String#intern()}.
 * Interning confers two benefits:</p>
 *
 * <ul>
 *  <li><p>Reduced memory footprint.</p>
 *      <p>Instead of having multiple equal instances of an object
 *      scattered around in your application, you can use a single instance.
 *      </p>
 *  <li><p>Faster equality comparison</p>
 *      <p>If you know you are dealing with interned instances,
 *      you can compare them for equality using the <code>==</code> operator
 *      rather than the {@link java.lang.Object#equals(Object)} method.</p>
 * </ul>
 *
 * <p><span style="color: FireBrick; font-weight: bold">Note</span>: using this
 * class may cause a deadlock, if the methods {@link #get(Object)} and {@link
 * EntrySupplier#supply(Object)} are mutually recursive.  Please examine the
 * implementation before using this class. </p>
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2004-02-20
 * @version $Revision: #4 $ $DateTime: 2004/08/16 18:10:38 $
 **/
public final class ConcurrentDict {
    private static final Logger logger = Logger.getLogger(ConcurrentDict.class);
    // We may want to make it possible to specify the bucket size at
    // construction time.  Hardcoding it is good enough for now.
    private static final int N_BUCKETS = 64;
    private static final int BITMASK = N_BUCKETS - 1;
    private static final Map[] BUCKETS = new Map[N_BUCKETS];

    static {
        logger.debug("Static initalizer starting...");
        for (int ii=0; ii<N_BUCKETS; ii++) {
            BUCKETS[ii] = new HashMap();
        }
        logger.debug("Static initalizer finished.");
    }

    private final EntrySupplier m_supplier;

    /**
     * @throws NullPointerException if <code>supplier</code> is null
     **/
    public ConcurrentDict(EntrySupplier supplier) {
        if (supplier==null) { throw new NullPointerException("supplier"); }

        m_supplier = supplier;
    }

    /**
     * Returns the object mapped to this <code>key</code>.
     *
     * <p>If no object has been mapped to this key yet, this method will call
     * the {@link ConcurrentDict.EntrySupplier#supply(Object)} method of the
     * supplier provided to this dictionary at construction time in {@link
     * #ConcurrentDict(ConcurrentDict.EntrySupplier)}.</p>
     *
     * <p>Note that the <code>null</code> key is always mapped to
     * <code>null</code>.</p>
     *
     * @param key 
     * @return 
     * @see EntrySupplier#supply(Object)
     * @see #ConcurrentDict(ConcurrentDict.EntrySupplier)
     *
     * @post key!=null || return==null
     **/
    public Object get(Object key) {
        if (key==null) { return null; }

        Map dict = BUCKETS[key.hashCode() & BITMASK];

        synchronized(dict) {
            final Object value = dict.get(key);
            if ( value!=null ) {
                return value;
            }
            // value is null at this point.  Could be because key is not mapped
            // yet, or could be because it's mapped to a null value.
            if ( dict.containsKey(key) ) {
                return value;
            }

            // If we got here, key is not mapped to anything yet. Let's map it.
            final Object newEntry = m_supplier.supply(key);
            dict.put(key, newEntry);
            return newEntry;
        }
    }


    /**
     * Returns statistics showing the distribution of keys across buckets.
     **/
    String debugStats() {
        final NumberFormat fmt = new DecimalFormat("0.00%");
        final Pair[] buckets = new Pair[N_BUCKETS];

        int total = 0;
        for (int ii=0; ii<N_BUCKETS; ii++) {
            int size = BUCKETS[ii].size();
            buckets[ii] = new Pair(ii, size);
            total += size;
        }
        Arrays.sort(buckets);

        StringBuffer sb = new StringBuffer();
        sb.append("Total size: ").append(total).append("\n");
        for (int ii=0; ii<N_BUCKETS; ii++) {
            Pair pair = buckets[ii];
            sb.append(pair).append(" (");
            sb.append(fmt.format(pair.size() / (float) total));
            sb.append(")\n");
        }
        return sb.toString();
    }

    /**
     * @see ConcurrentDict#get(Object)
     **/
    public interface EntrySupplier {
        /**
         * Supplies the value to be mapped to the passed in key.
         *
         * @see ConcurrentDict#get(Object)
         **/
        Object supply(Object key);
    }

    private static class Pair implements Comparable {
        private final static int IDX_WIDTH =
            Integer.toHexString(N_BUCKETS-1).length();
        private final static String ZERO_PADDING  = "00000000";
        private final static String SPACE_PADDING = "        ";

        private final int m_idx;
        private final int m_size;

        Pair(int idx, int size) {
            m_idx = idx;
            m_size = size;
        }

        public int compareTo(Object obj) {
            Pair other = (Pair) obj;
            if (m_size<other.m_size) {
                return -1;
            } else if ( m_size==other.m_size) {
                return 0;
            } else {
                return 1;
            }
        }

        int size() {
            return m_size;
        }

        public String toString() {
            final StringBuffer sb = new StringBuffer();
            final String hex = Integer.toHexString(m_idx);
            sb.append(ZERO_PADDING.substring(0, IDX_WIDTH-hex.length()));
            sb.append(hex);
            sb.append(": ");

            final String size = String.valueOf(m_size);
            final int padLength = SPACE_PADDING.length()-size.length();
            sb.append(SPACE_PADDING.substring(0, padLength));
            sb.append(size);
            return sb.toString();
        }
    }
}

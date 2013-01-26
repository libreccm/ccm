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
package com.arsdigita.kernel.permissions;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.arsdigita.developersupport.DeveloperSupport;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import java.sql.ResultSet;
import com.arsdigita.util.Assert;

/**
 * Permissions Cache for reducing number of queries issued to the for permission
 * checks. The data in cache stored for duration of transaction. 
 *    Primary purpose of this class to reduce number of permission queries by 
 * caching all of party on object. So the subsequent calls to 
 * checkPermission don't issue queries against database even if permission 
 * check done for different PrivilegeDescriptors. The typical place to use this 
 * class is situation when UI build to show collection of objects with checks for 
 * delete, edit, etc links based on user permissions. 
 * 
 * In the following example there is only one query issued to db:
 * Party party - current User
 * ACSObject - some object to check permision
 * Privilege read -read privilege
 * Privilege write - write privilege 
 * privilege admin - admin privilege
 * 
 * { 
 *    PermissinCache permCache = PermissionCache.getInstance(); 
 *    permCache.checkPermission( New PermissionDescriptor(read, obj, party));
 *    permCache.checkPermission( New PermissionDescriptor(read, obj, party));
 *    permCache.checkPermission( New PermissionDescriptor(write, obj, party));
 *    permCache.checkPermission( New PermissionDescriptor(admin, obj, party));
 *    permCache.checkPermission( New PermissionDescriptor(write, obj, party));
 * }
 * 
 * @author aram@kananov.com
 */
public final class PermissionCache {

    private final static Logger s_log = Logger.getLogger(PermissionCache.class);

    private static PermissionCache s_instance = new PermissionCache();

    private CollectionTxnCache m_prTxnCache;

    //compose select query for getPrivilegesFromDB
    private static String privQuery;
    private static ArrayList privNamesList = new ArrayList();
    private static ArrayList privColumnList = new ArrayList();

    /**
     * Made private to enforce the singleton pattern.
     */
    private PermissionCache() {
        m_prTxnCache =
            new CollectionTxnCache(PermissionCache.class.getName() + "-perm:");
        
        DataQuery dq =
          SessionManager.getSession().retrieveQuery(
          "com.arsdigita.kernel.permissions.getAllPrivilegeColumnNameMap");

        privQuery = "select ";
        int len = (int) dq.size();
      
        while (dq.next()) {

            privNamesList.add(dq.getPosition()-1, dq.get("privilegeName"));
            privColumnList.add(dq.getPosition()-1, dq.get("columnName"));
        
            privQuery += " max(" + dq.get("columnName")  + ") as " + dq.get("columnName");
            privQuery += (dq.getPosition() < len) ? "," : "";
        }

        privQuery += " from dnm_object_1_granted_context dogc,"
            + "             dnm_granted_context dgc,"
            + "             dnm_permissions dp,"
            + "             dnm_group_membership dgm"
            + "          where dogc.pd_object_id = ?"
            + "          and dogc.pd_context_id = dgc.pd_object_id"
            + "          and dgc.pd_context_id = dp.pd_object_id"
            + "          and dgm.pd_member_id = ?"
            + "          and dp.pd_grantee_id = dgm.pd_group_id";
    }

    /**
     * Fetches the shared instance of the security manager.
     * 
     * @return The security manager
     */
    public static PermissionCache getInstance() {
        return s_instance;
    }

    /**
     * Caching of permission lookups within the txn
     * @return 
     */
    private static CollectionTxnCache getPermissionsCache() {
        return getInstance().m_prTxnCache;
    }

    /**
     * Checks the permission
     * represented by the passed in {@link PermissionDescriptor}.
     * First looks in cache for privileges of (party,obj) tuple , if tuple is not present
     * in the cache, only then issues query to get privileges for party on object. 
     *
     * @param permission the {@link PermissionDescriptor} to
     * provide service to
     *
     * @return <code>true</code> if the PermissionDescriptor's base object has the
     * specified permission; <code>false</code> otherwise.
     */
    public boolean checkPermission(PermissionDescriptor perm) {
        OID party = perm.getPartyOID();
        OID obj = perm.getACSObjectOID();
        PrivilegeDescriptor priv = perm.getPrivilegeDescriptor();

        CollectionTxnCache cache = getPermissionsCache();
        
        Boolean lookupResult = cache.lookup(party, obj, priv);
        if (lookupResult == null) {
	    s_log.debug("Cache miss.");
            HashMap permMap = getPrivilegesFromDB(party, obj);
            cache.cache(party, obj, permMap);
            lookupResult = cache.lookup(party, obj, priv);
            if (lookupResult == null)
                return false;
        } else {
             s_log.debug("Cache hit.");
        }
        
        return lookupResult.booleanValue();
    }

    /**
     * 
     * @param party
     * @param obj
     * @return 
     */
    private static HashMap getPrivilegesFromDB(
        OID party,
        OID obj) {
        HashMap prMap = null;
        PreparedStatement stmt;
        Connection conn;
        
        try {
            conn = SessionManager.getSession().getConnection();
            stmt = conn.prepareStatement(privQuery);
            stmt.setBigDecimal(1, (BigDecimal) obj.get("id"));
            stmt.setBigDecimal(2, (BigDecimal) party.get("id"));
            long m_start = System.currentTimeMillis();
            ResultSet res = stmt.executeQuery();

            HashMap bindVars = new HashMap();
            bindVars.put("1", obj.get("id"));
            bindVars.put("2", party.get("id"));
            DeveloperSupport.logQuery
                (conn.toString(),
                "executeQuery",
                privQuery,
                bindVars,
                System.currentTimeMillis() - m_start,
                null)
            ;

            if (res.next()) {
                prMap = new HashMap();
                for (int i = 0; i < privNamesList.size(); i++) {
                    if (res.getBigDecimal((String) privColumnList.get(i)) != null)
                        prMap.put((String)privNamesList.get(i), (Object) new Boolean(true));
                    else
                        prMap.put((String)privNamesList.get(i), (Object) new Boolean(false));
                }
            }
            res.close();
            stmt.close();
            return prMap;
        } catch (SQLException e) {
            s_log.error("Couldn't retrieve user privileges! Query:" + privQuery);
            return null;
        }

    }   
    
    
    
    
    /**
     * 
     */
    private class CollectionTxnCache {

        private String m_prefix;
        
        /**
         * 
         * @param prefix 
         */
        public CollectionTxnCache(String prefix) {
            m_prefix = prefix;
        }

        /**
         * 
         * @param party
         * @param object
         * @return 
         */
        private String attributeName(OID party, OID object) {
            return m_prefix + ":" + party.get("id") + ":" + object.get("id");
        }
        
        /**
         * 
         * @param party
         * @param object
         * @param privilegeMap 
         */
        public void cache(OID party, OID object, HashMap privilegeMap) {
            getTxn().setAttribute(attributeName(party, object), privilegeMap);
        }

        /**
         * 
         * @param party
         * @param object
         * @param privilege
         * @return 
         */
        public Boolean lookup(
            OID party,
            OID object,
            PrivilegeDescriptor privilege) {
            
            HashMap cache =
                (HashMap) getTxn().getAttribute(attributeName(party, object));
            if (cache == null) {
                return null;
            } else {
                Boolean result = (Boolean) cache.get(privilege.getName());
                if (result == null) {
                    s_log.error(
                                "Could'nt find key in the permission cache",
                                new Throwable());
                    return Boolean.FALSE;
                }
                return result;
            }
        }

        /**
         * 
         * @return 
         */
        private TransactionContext getTxn() {
            TransactionContext txn =
                SessionManager.getSession().getTransactionContext();
            Assert.exists(txn, txn.getClass());
            Assert.isTrue(txn.inTxn(), "Not in a transaction");
            return txn;
        }
    }
}

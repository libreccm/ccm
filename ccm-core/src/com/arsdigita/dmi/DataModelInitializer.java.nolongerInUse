/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.dmi;

import com.arsdigita.db.ConnectionManager;
import com.arsdigita.db.DbHelper;
import com.arsdigita.installer.LoadSQLPlusScript;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/*
 *
 * @author Bryan Che (bche@redhat.com)
 * @author Dennis Gregorovic (dgregor@redhat.com)
 * @version $Revision: #11 $ $Date: 2004/08/16 $
 * @since CCM Core 5.2
 *
 */

public class DataModelInitializer {

    public final static String versionId = "$Id: DataModelInitializer.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public static String SEQUENCE_NAME = "dmi_products_seq";

    private static final Logger s_log =
        Logger.getLogger(DataModelInitializer.class);
    private static HashMap s_queries = null;

    private Connection m_conn = null;
    private Integer m_database = new Integer (DbHelper.getDatabase());

    static {

        // Initialize the HashMap of HashMaps that will contain the SQL for each
        // database.

        s_queries = new HashMap();

        HashMap oracle = new HashMap();
        oracle.put ("checkDMIDataModelInstalled",
                    "select count(*) from user_tables where upper(table_name) = 'DMI_PRODUCTS'");

        HashMap postgres = new HashMap();
        postgres.put ("checkDMIDataModelInstalled",
                      "select count(*) from pg_tables where upper(table_name) = 'DMI_PRODUCTS'");

        s_queries.put (new Integer(DbHelper.DB_ORACLE), oracle);
        s_queries.put (new Integer(DbHelper.DB_POSTGRES), postgres);
    }

    public void setConnection (Connection conn) {
        m_conn = conn;
    }

    public Connection getConnection () {
        return m_conn;
    }

    /**
     *
     * Load the DMI data model if it has not been done already.
     *
     */
    public void initialize (String dmiDataModel) throws SQLException {
        //install the DMI data model if it isn't already installed
        if (!isDMIInstalled()) {
            s_log.warn("Installing DMI Data Model");
            loadDMIDataModel(dmiDataModel);
            s_log.warn("DMI Data Model Installed");
        }
    }

    public void run (File xmlFile, File rootDirectory) throws SQLException, ParserConfigurationException, SAXException, IOException {

        Application app = new Application(xmlFile, DbHelper.getDatabaseDirectory());

        BigDecimal productID = getProductId(app.getName());

        if (productID == null) {
            s_log.warn("Product '" + app.getName() + "' is not installed.  Installing...");

            //This product hasn't been installed yet, so install it make sure to
            //set the data model install files relative to webapp root

            Product p = Product.createProduct(app.getName(),
                                              app.getDescription(),
                                              app.getCurrentVersion(),
                                              rootDirectory + File.separator + app.getDataModelInstallFileName(),
                                              app.getDescription());

            //see if there were errors installing the data model
            String sErrMsg = p.getCurrentVersion().getInstallErrors();

            if (sErrMsg != null) {
                //there were errors installing the data model
                s_log.warn("Errors in installing the data model: " + sErrMsg);
                throw (new RuntimeException ("There were errors in installing the data model.  " +
                                             "Please review them and then either restart the server " +
                                             "or resolve the errors. " + sErrMsg));
            }

        } else {
            s_log.warn("Product '" + app.getName() + "' installed.  Checking data model...");

            //This product is installed, so compare its version with the
            //installed version and upgrade if necessary
            Product p = new Product(productID);
            ProductVersion pv = p.getCurrentVersion();

            String sDmVersionName = pv.getName();

            s_log.warn("Current data model version is " + pv.getName());

            //See if the current version is the same as the installed version
            if (sDmVersionName == app.getName()) {
                //the versions are the same
                s_log.warn("Data model is up to date");
            } else {
                //the versions are not the same, so upgrade the version
                s_log.warn("Data model is not up to date.  Upgrading...");

                //FIX
                String sErrMsg = upgradeDataModel(p, app.getUpgradePath(sDmVersionName), rootDirectory).trim();

                if (sErrMsg.length() > 0) {
                    //there were errors
                    s_log.warn("Errors in upgrading the data model: " + sErrMsg);
                    throw new RuntimeException("There were errors in upgrading the data model." +
                                               "Please review them and then either restart the server " +
                                               "or resolve the errors. " + sErrMsg);
                }
                s_log.warn("Finished Upgrading data model for " + app.getName());
            }
        }
    }

    /**
     *
     * Grab a new connection if our current one is empty
     *
     */
    private void initConnection () throws SQLException {
        if ( ( m_conn == null ) || ( m_conn.isClosed() ) ) {
            m_conn = ConnectionManager.getConnection();
        }
    }

    /**
     *
     * Is the data model for DMI installed
     *
     */
    private boolean isDMIInstalled() throws SQLException {
        initConnection();

        String query = lookupQuery ("checkDMIDataModelInstalled");
        Statement stmt = m_conn.createStatement();
        ResultSet rset = stmt.executeQuery(query);

        rset.next();

        return (rset.getBigDecimal(1).intValue() == 1);
    }

    private void loadDMIDataModel(String dmiDataModel) throws SQLException {
        initConnection();

        //load the data model for the DMI
        LoadSQLPlusScript ls = new LoadSQLPlusScript();
        ls.setConnection (m_conn);
        try {
            ls.loadSQLPlusScript(dmiDataModel);
        } catch (Exception e) {
            throw (new RuntimeException (e.getMessage()));
        }

    }

    /*
     * Given the name of a product, return the ID of that product from the
     * database, or null if it is not found.
     *
     * @param productName The name of the product to look up.
     * @return The ID of the product or null if it is not found.
     */
    private BigDecimal getProductId(String productName) throws SQLException {
        initConnection();

        String getProductIdFromNameSql =
            "select product_id from dmi_products where upper(product_name) = ?";

        PreparedStatement pstmt = m_conn.prepareStatement(getProductIdFromNameSql);
        pstmt.setString(1, productName.toUpperCase());
        ResultSet rset = pstmt.executeQuery();

        if (!rset.next()) {
            //this product is not installed, so return null
            return null;
        }

        return rset.getBigDecimal(1);
    }

    /*
     *
     *
     * @param productName The name of the product to look up.
     * @return The ID of the product or null if it is not found.
     */
    private String upgradeDataModel(Product p, ApplicationVersion avUpgradePath[], File rootDirectory) throws SQLException {
        String sErrMsgReturn = "";

        ProductVersion pv = null;
        for (int i=0; i < avUpgradePath.length; i++) {
            ApplicationVersion av = avUpgradePath[i];
            if (av.hasUpgradeDataModel()) {
                pv = ProductVersion.createProductVersion(p,
                                                         av.getName(),
                                                         rootDirectory + File.separator + av.getUpgradeFile(),
                                                         av.getDescription());

                //see if there were errors upgrading the data model
                String sErr = pv.getInstallErrors();
                if ((sErr != null) && (sErr.trim().length() > 0)) {
                    sErrMsgReturn += "Error upgrading to data model version " + pv.getName() + ": " + sErr + " ";
                }
            }
        }

        return sErrMsgReturn;
    }

    private String lookupQuery (String queryName) {
        HashMap queries = (HashMap)s_queries.get(m_database);

        if (queries == null) {
            return null;
        }

        return queries.get(queryName).toString();
    }
}

/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
import com.arsdigita.db.Sequences;
import com.arsdigita.installer.LoadSQLPlusScript;
import com.arsdigita.installer.ParseException;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import org.apache.log4j.Logger;

/*
 *
 * @author Bryan Che (bche@redhat.com)
 * @version $Revision: #12 $ $Date: 2004/08/16 $
 * @since CCM Core 5.2
 *
 */

public class ProductVersion {
    public final static String versionId = "$Id: ProductVersion.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(ProductVersion.class);

    private Product m_product = null;
    private BigDecimal m_ID = null;
    private String m_sName = null;
    private Date m_creationDate = null;
    private String m_sFileName = null;
    private String m_sErrors = null;
    private String m_sDesc = null;

    private static HashMap s_queries = null;

    static {

        // Initialize the HashMap of HashMaps that will contain the SQL for each
        // database.

        s_queries = new HashMap();

        HashMap defaultdb = new HashMap();
        defaultdb.put ("getProductVersionByIDSQL",
                       "select version_name, creation_date, install_file, install_errors, description " +
                       "from dmi_product_versions " +
                       "where version_id = ? and product_id = ?");
        defaultdb.put ("getProductVersionByNameSQL",
                       "select version_id, creation_date, install_file, install_errors, description " +
                       "from dmi_product_versions where upper(version_name) = ? and product_id = ?");
        defaultdb.put ("updateProductVersionSQL",
                       "update dmi_product+versions " +
                       "set version_name = ?, " +
                       "install_file = ?, " +
                       "description = ? " +
                       "where version_id = ?");
        defaultdb.put ("getProductVersionCreationDateSQL",
                       "select creation_date from dmi_product_versions where version_id = ?");
        defaultdb.put ("getPreviousVersionSQL",
                       "select previous_version_id from dmi_product_versions where version_id = ?");
        defaultdb.put ("getLatestProductVersionSQL",
                       "select version_id from dmi_product_versions where product_id = ? " +
                       "minus " +
                       "select previous_version_id from dmi_product_versions where product_id = ?");

        HashMap oracle = new HashMap();
        oracle.put ("insertProductVersionSQL",
                    "insert into dmi_product_versions " +
                    "(version_id, product_id, version_name, creation_date, install_file," +
                    " install_errors, description, previous_version_id) " +
                    "values " +
                    "(?, ?, ?, sysdate, ?, ?, ?, ?)");
        HashMap postgres = new HashMap();
        postgres.put ("insertProductVersionSQL",
                      "insert into dmi_product_versions " +
                      "(version_id, product_id, version_name, creation_date, install_file," +
                      " install_errors, description, previous_version_id) " +
                      "values " +
                      "(?, ?, ?, current_timestamp(), ?, ?, ?, ?)");

        s_queries.put (new Integer(DbHelper.DB_DEFAULT), defaultdb);
        s_queries.put (new Integer(DbHelper.DB_ORACLE), oracle);
        s_queries.put (new Integer(DbHelper.DB_POSTGRES), postgres);
    }

    //constructors

    /**
     * Constructs a new ProductVersion
     */
    public ProductVersion() {
    }

    /**
     * Constructs a new ProductVersion for Product p with the ID, versionID
     * @param p the Product for which to create a ProductVersion
     * @param productID the ID of the product version to instantiate
     * @exception throws a SQLException if there was a database error or if there is no
     * product version with ID, versionID
     */
    public ProductVersion(BigDecimal versionID, Product p) throws SQLException {
        Connection conn = ConnectionManager.getConnection();

        PreparedStatement pstmt = conn.prepareStatement(lookupQuery("getProductVersionByIDSQL"));
        pstmt.setBigDecimal(1, versionID);
        pstmt.setBigDecimal(2, p.getID());
        ResultSet rset = pstmt.executeQuery();

        if (!rset.next()) {
            throw new SQLException("Query did not return any rows");
        }

        m_product = p;
        m_sName = rset.getString(1);
        m_creationDate = rset.getDate(2);
        m_sFileName = rset.getString(3);
        m_sErrors = rset.getString(4);
        m_sDesc = rset.getString(5);

        m_ID = versionID;

        rset.close();
        pstmt.close();
    }

    /**
     * Constructs a new ProductVersion for Product p with the name, sName
     * @param p the Product for which to create a ProductVersion
     * @param sName the name of the product version to instantiate
     * @exception throws a SQLException if there was a database error or if there is no
     * product version with name, sName
     */
    public ProductVersion(String sName, Product p) throws SQLException {
        Connection conn = ConnectionManager.getConnection();

        PreparedStatement pstmt = conn.prepareStatement(lookupQuery("getProductVersionByNameSQL"));
        pstmt.setString(1, sName.toUpperCase());
        pstmt.setBigDecimal(2, p.getID());
        ResultSet rset = pstmt.executeQuery();

        if (!rset.next()) {
            throw new SQLException("Query did not return any rows");
        }

        m_product = p;
        m_ID = rset.getBigDecimal(1);
        m_creationDate = rset.getDate(2);
        m_sFileName = rset.getString(3);
        m_sErrors = rset.getString(4);
        m_sDesc = rset.getString(5);

        m_sName = sName;

        rset.close();
        pstmt.close();
    }

    /**
     * Creates and returns a new Product Version for p.
     * Installs the data model for this product version in the process
     * @param p the Product for which to create a new version
     * @param sName the name of the version
     * @param sInstallFile location of the SQLPlus-compatible file from which to install the product's data model
     * @param sDesc description of this version
     *
     * @return the Product that was created
     */
    public static ProductVersion createProductVersion(Product p, String sName, String sInstallFile, String sDesc) throws SQLException, IllegalStateException {
        ProductVersion pv = new ProductVersion();
        pv.setProduct(p);
        pv.setName(sName);
        pv.setInstallFileName(sInstallFile);
        pv.setDescription(sDesc);
        pv.save();

        return pv;

    }

    /**
     * Sets the product for this to be p
     */
    private void setProduct(Product p) {
        m_product = p;
    }

    /**
     * returns the Product for which this is a version
     * @return the Product for which this is a version
     */
    public Product getProduct() {
        return m_product;
    }

    /**
     * returns the ID of this version
     * @return the ID of this version
     */
    public BigDecimal getID() {
        return m_ID;
    }

    /**
     * returns the name of this version
     * @return the name of this version
     */
    public String getName() {
        return m_sName;
    }

    /**
     * sets this' name to sName
     */
    public void setName(String sName) {
        m_sName = sName;
    }

    /**
     * returns the date this version was installed
     * @return the date this version was installed
     */
    public Date getCreationDate() {
        return m_creationDate;
    }

    /**
     * returns the install file of this version's data model
     * @return the install file of this version's data model
     */
    public String getInstallFileName() {
        return m_sFileName;
    }

    /**
     * sets this's data model install file name
     */
    //make this private since we don't want to change this once it's been installed
    private void setInstallFileName(String sFileName) {
        m_sFileName = sFileName;
    }

    /**
     * returns the ID of this version
     * @return the ID of this version.  Null if there were no errors
     */
    public String getInstallErrors() {
        return m_sErrors;
    }

    /**
     * returns the ID of this version
     * @return the ID of this version
     */
    public String getDesc() {
        return m_sDesc;
    }

    /**
     * sets the description of this version to sDesc
     * @param sDesc the description of this version
     */
    public void setDescription(String sDesc) {
        m_sDesc = sDesc;
    }

    /**
     * Returns the previous version of this product which was installed.
     * Null if there was no previous version installed
     * @return the previous version of this product which was installed.
     * Null if there was no previous version installed
     */
    public ProductVersion getPreviousVersion() throws SQLException {
        Connection conn = ConnectionManager.getConnection();

        PreparedStatement pstmt = conn.prepareStatement(lookupQuery("getPreviousVersionSQL"));
        pstmt.setBigDecimal(1, m_ID);
        ResultSet rset = pstmt.executeQuery();

        if (!rset.next()) {
            return null;
        }

        BigDecimal prev_ID = rset.getBigDecimal(1);
        return new ProductVersion(prev_ID, m_product);
    }

    /**
     * Installs the data model for this version
     */
    private void installProductVersionDM() throws ClassNotFoundException, SQLException,
                                                  ParseException, FileNotFoundException,
                                                  IllegalAccessException, NoSuchMethodException {

        Connection conn = ConnectionManager.getConnection();

        LoadSQLPlusScript ls = new LoadSQLPlusScript();
        ls.setConnection (conn);
        ls.loadSQLPlusScript(m_sFileName);
    }

    /**
     * Returns the ID for the Product Version that is current
     * for this's Product.
     * @return the ID for the Product Version that is current
     * for this's Product
     */
    public BigDecimal getLatestProductVersionID() throws SQLException {
        Connection conn = ConnectionManager.getConnection();

        //get the latest product version for this' product
        PreparedStatement pstmt = conn.prepareStatement(lookupQuery("getLatestProductVersionSQL"));
        pstmt.setBigDecimal(1, m_product.getID());
        pstmt.setBigDecimal(2, m_product.getID());
        ResultSet rset = pstmt.executeQuery();

        if (!rset.next()) {
            return null;
        } else {
            return rset.getBigDecimal(1);
        }
    }

    /**
     * Inserts this product version into the db, and installs its data model
     */
    private BigDecimal insertProductVersion() throws SQLException {
        Connection conn = ConnectionManager.getConnection();
        BigDecimal prevID = getLatestProductVersionID();

        PreparedStatement pstmt = conn.prepareStatement(lookupQuery("insertProductVersionSQL"));
        BigDecimal ID = Sequences.getNextValue(DataModelInitializer.SEQUENCE_NAME);
        pstmt.setBigDecimal(1, ID);
        pstmt.setBigDecimal(2, m_product.getID());
        pstmt.setString(3, m_sName);
        pstmt.setString(4, m_sFileName);
        pstmt.setString(6, m_sDesc);
        if (prevID == null) {
            //there was no previous version because this is the first version
            pstmt.setNull(7, Types.INTEGER);
        } else {
            //set the previous version to be the latest version
            pstmt.setBigDecimal(7, prevID);
        }

        m_sErrors = null;
        try {
            //first, install the product version
            installProductVersionDM();
        } catch (Exception e) {
            m_sErrors = e.getMessage();
            s_log.error ("ERROR LOADING DATA MODEL: " + m_sErrors);

            //make sure that we have some kind of error here if e.getMessage() returns null
            if ((m_sErrors == null) || (m_sErrors.length() == 0)) {
                m_sErrors = "Error loading data model.  No message provided.";
            }
        } finally {
            if (m_sErrors != null) {
                //put the error message in the db
                pstmt.setString(5, m_sErrors);
            } else {
                //no error, so set the error field to null
                pstmt.setNull(5, Types.VARCHAR);
            }

        }

        pstmt.executeUpdate();
        pstmt.close();

        return ID;
    }

    /**
     * updates this product version's info in the database
     */
    private int updateProductVersion() throws SQLException {
        Connection conn = ConnectionManager.getConnection();

        PreparedStatement pstmt = conn.prepareStatement(lookupQuery("updateProductVersionSQL"));
        pstmt.setString(1, m_sName);
        pstmt.setString(2, m_sFileName);
        pstmt.setString(3, m_sDesc);
        pstmt.setBigDecimal(4, m_ID);

        int iReturn = pstmt.executeUpdate();
        pstmt.close();
        return iReturn;
    }

    /**
     * Saves this product version's information to the database.
     * @exception throws an IllegalStateException if this product version was created
     * with a default constructor and consequently does not have a Product
     */
    public void save() throws SQLException, IllegalStateException {
        Connection conn = ConnectionManager.getConnection();

        //This exception will be thrown if someone tries to create a ProductVersion object
        //with the default ProductVersion constructor, set its values, and then save it
        //We don't want this to happen, so we depend on the fact that only
        //ProductVersions created from a db lookup or createProductVersion() will have
        //a Product set
        if (m_product == null) {
            throw new IllegalStateException("Cannot save this ProductVersion because it has no Product set.");
        }
        boolean bAutoCommit = conn.getAutoCommit();
        if (bAutoCommit) {
            //Turn off auto-commit if it is on
            //If it is on, then assume that this save is not part of a larger transaction
            //So, we'll want to commit the transaction at the end
            conn.setAutoCommit(false);
            //m_conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        }

        if (m_ID == null) {
            //if this has no ID, then it wasn't retrieved from the DB--so, insert
            //we will only be in this branch when starting from createProductVersion()
            m_ID = insertProductVersion();

            //we need to update m_creationDate for this product version now that it's created
            PreparedStatement pstmt = conn.prepareStatement(lookupQuery("getProductVersionCreationDateSQL"));
            pstmt.setBigDecimal(1, m_ID);
            ResultSet rset = pstmt.executeQuery();
            rset.next();
            m_creationDate = rset.getDate(1);
            rset.close();
            pstmt.close();
        } else {
            updateProductVersion();
        }

        if (bAutoCommit) {
            //need to commit this transaction because we weren't called from a higher transaction
            conn.commit();
        }
    }

    private static String lookupQuery (String queryName) {
        HashMap queries;

        // Fetch the db-specific value if it exists
        queries = (HashMap)s_queries.get(new Integer(DbHelper.getDatabase()));
        if (queries != null) {
            Object dbSpecificValue = queries.get(queryName);
            if ( dbSpecificValue != null ) {
                s_log.warn ("Query specific lookup - key: " + queryName + " value: " + dbSpecificValue.toString());
                return dbSpecificValue.toString();
            }
        }

        // Otherwise, fetch the default value
        queries = (HashMap)s_queries.get(new Integer(DbHelper.DB_DEFAULT));
        if (queries != null) {
            Object dbDefaultValue = queries.get(queryName);
            if ( dbDefaultValue != null ) {
                s_log.warn ("Query default lookup - key: " + queryName + " value: " + dbDefaultValue.toString());
                return dbDefaultValue.toString();
            }
        }

        s_log.warn ("Query lookup failed for key: " + queryName);
        return null;
    }

}

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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;
import org.apache.log4j.Logger;

/*
 *
 * @author Bryan Che (bche@redhat.com)
 * @version $Revision: #11 $ $Date: 2004/08/16 $
 * @since CCM Core 5.2
 *
 */

public class Product {

    public final static String versionId = "$Id: Product.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(Product.class);

    private BigDecimal m_ID = null;
    private String m_sName = null;
    private Date m_creationDate = null;
    private String m_sDesc = null;

    private static HashMap s_queries = null;

    static {

        // Initialize the HashMap of HashMaps that will contain the SQL for each
        // database.

        s_queries = new HashMap();

        HashMap defaultdb = new HashMap();
        defaultdb.put ("getProductByIDSQL",
                       "select product_name, creation_date, description from dmi_products where product_id = ?");
        defaultdb.put ("getProductByNameSQL",
                       "select product_id, creation_date, description from dmi_products where upper(sName) = ?");
        defaultdb.put ("getCurrentVersionSQL",
                       "select version_id from dmi_product_versions where product_id = ? " +
                       "minus " +
                       "select previous_version_id from dmi_product_versions where product_id = ?");
        defaultdb.put ("getVersionsSQL",
                       "select version_id from dmi_product_versions where product_id = ? order by creation_date asc");
        defaultdb.put ("insertProductSQL",
                       "insert into dmi_products " +
                       "(product_id, product_name, creation_date, description) " +
                       "values " +
                       "(?, ?, sysdate, ?)");
        defaultdb.put ("updateProductSQL",
                       "update dmi_products " +
                       "set product_name = ?, " +
                       "description = ? " +
                       "where product_id = ?");
        defaultdb.put ("getProductsSQL",
                       "select product_id from dmi_products order by creation_date asc");
        defaultdb.put ("getProductCreationDateSQL",
                       "select creation_date from dmi_products where product_id = ?");

        HashMap oracle = new HashMap();
        HashMap postgres = new HashMap();

        s_queries.put (new Integer(DbHelper.DB_DEFAULT), defaultdb);
        s_queries.put (new Integer(DbHelper.DB_ORACLE), oracle);
        s_queries.put (new Integer(DbHelper.DB_POSTGRES), postgres);
    }


    //constructors

    /**
     * Constructs a new Product
     */
    public Product() {
    }

    /**
     * Constructs a new Product with the ID, productID
     * @param productID the ID of the product to instantiate
     * @exception throws a SQLException if there was a database error or if there is no
     * product with ID, productID
     */
    public Product(BigDecimal productID) throws SQLException {
        Connection conn = ConnectionManager.getConnection();

        //instantiate the product
        PreparedStatement pstmt = conn.prepareStatement(lookupQuery("getProductByIDSQL"));
        pstmt.setBigDecimal(1, productID);
        ResultSet rset = pstmt.executeQuery();

        if (!rset.next()) {
            throw new SQLException("Query did not return any rows");
        }
        m_sName = rset.getString(1);
        m_creationDate = rset.getDate(2);
        m_sDesc = rset.getString(3);

        m_ID = productID;

        rset.close();
        pstmt.close();
    }

    /**
     * Constructs a new Product with the name, sName
     * @param sName name of the product to instantiate
     * @exception throws a SQLException if there was a database error or if there is no
     * product with name, sName
     */
    public Product(String sName) throws SQLException {
        Connection conn = ConnectionManager.getConnection();

        //instantiate the product
        PreparedStatement pstmt = conn.prepareStatement(lookupQuery("getProductByNameSQL"));
        pstmt.setString(1, sName.toUpperCase());
        ResultSet rset = pstmt.executeQuery();

        if (!rset.next()) {
            throw new SQLException("Query did not return any rows");
        }
        m_ID = rset.getBigDecimal(1);
        m_creationDate = rset.getDate(2);
        m_sDesc = rset.getString(3);

        m_sName = sName;

        rset.close();
        pstmt.close();
    }

    /**
     * Creates and returns a new Product with initial version, sInitVersionName.
     * Installs the data model for this product in the process
     * @param sName name of the product
     * @param sProdDesc description of the product
     * @param sInitVersionName initial version of the product being created
     * @param sInstallFile location of the SQLPlus-compatible file from which to install the product's data model
     * @param sVersionDesc description of the initial version of the product
     *
     * @return the Product that was created
     */
    public static Product createProduct(String sName, String sProdDesc,
                                        String sInitVersionName, String sInstallFile, String sVersionDesc)
        throws SQLException {
        Connection conn = ConnectionManager.getConnection();

        //put this into a transaction
        conn.setAutoCommit(false);

        //first create the product
        Product p = null;
        try {
            p = new Product();
            p.setName(sName);
            p.setDescription(sProdDesc);
            p.save();

            //next create a version
            ProductVersion.createProductVersion(p, sInitVersionName, sInstallFile, sVersionDesc);
        } catch (SQLException e) {
            //roll back creating the product/version info to leave
            //that information in a consistent state, even though the product's data model
            //may have been changed
            conn.rollback();
            conn.setAutoCommit(true);
            throw e;
        } catch (IllegalStateException e) {
            //shouldn't be here because we are setting the connection explicitly
        }

        conn.commit();
        conn.setAutoCommit(true);

        return p;
    }

    /**
     * Returns the current version of the product
     * @return the current version of the product
     */
    public ProductVersion getCurrentVersion() throws SQLException {
        Connection conn = ConnectionManager.getConnection();

        //get the version
        PreparedStatement pstmt = conn.prepareStatement(lookupQuery("getCurrentVersionSQL"));
        pstmt.setBigDecimal(1, m_ID);
        pstmt.setBigDecimal(2, m_ID);
        ResultSet rset = pstmt.executeQuery();

        if (!rset.next()) {
            throw new SQLException("Query did not return any rows");
        }

        ProductVersion pv = new ProductVersion(rset.getBigDecimal(1), this);

        rset.close();
        pstmt.close();

        return pv;
    }

    /**
     * Returns a list of all the versions through which this product has been installed and upgraded
     * @return an array of all ProductVersions for this Product
     */
    public ProductVersion[] getVersions() throws SQLException {
        Connection conn = ConnectionManager.getConnection();

        //get all the versions for this product
        Vector vector = new Vector();

        PreparedStatement pstmt = conn.prepareStatement(lookupQuery("getVersionsSQL"));
        pstmt.setBigDecimal(1, m_ID);
        ResultSet rset = pstmt.executeQuery();

        while (rset.next()) {
            vector.add(new ProductVersion(rset.getBigDecimal(1), this));
        }

        rset.close();
        pstmt.close();

        ProductVersion versionArray[] = new ProductVersion[1];
        return (ProductVersion[])vector.toArray(versionArray);
    }

    /**
     * Returns the ID of this product
     * @return the ID of this product
     */
    public BigDecimal getID() {
        return m_ID;
    }

    /**
     * Returns the name of this product
     * @return the name of this product
     */
    public String getName() {
        return m_sName;
    }

    /**
     * Sets this product's name to sName
     * @param sName the new name of the product
     */
    public void setName(String sName) {
        m_sName = sName;
    }

    /**
     * Returns the date of this product was first installed
     * @return the date this product was first installed
     */
    public Date getCreationDate() {
        return m_creationDate;
    }

    /**
     * Returns a description of this product
     * @return a description of this product
     */
    public String getDesc() {
        return m_sDesc;
    }

    /**
     * Sets this product's description to sDesc
     * @param sName the new description of the product
     */
    public void setDescription(String sDesc) throws IllegalStateException {
        m_sDesc = sDesc;
    }

    /**
     * Saves this product's information to the database.
     */
    public void save() throws SQLException {
        if (m_ID == null) {
            //if this has no ID, then it wasn't retrieved from the DB--insert
            m_ID = insertProduct();

            //we need to update m_creationDate for this product now that it's
            //created
            m_creationDate = getCreationDateFromDB (m_ID);
        } else {
            updateProduct();
        }
    }

    /**
     * Returns all the products that are currently installed
     *
     * @return an array of Products
     */
    public static Product[] getProducts() throws SQLException {
        Connection conn = ConnectionManager.getConnection();

        Vector vector = new Vector();

        PreparedStatement pstmt = conn.prepareStatement(lookupQuery("getProductsSQL"));
        ResultSet rset = pstmt.executeQuery(lookupQuery("getProductsSQL"));

        while (rset.next()) {
            vector.add(new Product(rset.getBigDecimal(1)));
        }

        rset.close();
        pstmt.close();

        Product productArray[] = new Product[1];
        return (Product[])vector.toArray(productArray);
    }

    /**
     * Inserts this product's information into the database.
     * Returns the ID of the new product
     */
    private BigDecimal insertProduct() throws SQLException {
        Connection conn = ConnectionManager.getConnection();

        PreparedStatement pstmt = conn.prepareStatement(lookupQuery("insertProductSQL"));
        BigDecimal ID = Sequences.getNextValue(DataModelInitializer.SEQUENCE_NAME);
        pstmt.setBigDecimal(1, ID);
        pstmt.setString(2, m_sName);
        pstmt.setString(3, m_sDesc);
        pstmt.executeUpdate();
        return ID;
    }

    /**
     * Updates this product's information in the database
     * Returns the number of rows updated in the database for updating this product
     */
    private int updateProduct() throws SQLException {
        Connection conn = ConnectionManager.getConnection();

        PreparedStatement pstmt = conn.prepareStatement(lookupQuery("updateProductSQL"));
        pstmt.setString(1, m_sName);
        pstmt.setString(2, m_sDesc);
        pstmt.setBigDecimal(3, m_ID);
        return pstmt.executeUpdate();
    }

    private Date getCreationDateFromDB (BigDecimal ID) throws SQLException {
        Date date = null;
        Connection conn = ConnectionManager.getConnection();

        PreparedStatement pstmt = conn.prepareStatement(lookupQuery("getProductCreationDateSQL"));
        pstmt.setBigDecimal(1, ID);
        ResultSet rset = pstmt.executeQuery();
        rset.next();
        date = rset.getDate(1);
        rset.close();
        pstmt.close();

        return (date);
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

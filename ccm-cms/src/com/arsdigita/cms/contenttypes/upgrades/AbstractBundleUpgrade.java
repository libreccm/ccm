package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.util.jdbc.Connections;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public abstract class AbstractBundleUpgrade {
    
    protected abstract String getBundleTableName();

    protected abstract String getContentItemTableName();

    protected abstract String getIdColName();

    protected abstract String getBundleClassName();

    protected abstract String getPrimaryKeyConstraintName();

    protected abstract String getBundleContraintName();

    protected String getSuperBundleTable() {
        return "cms_bundles";
    }
     
    public void doUpgrade() {
        System.out.println("Starting upgrade...");

        System.out.println("Trying to get JDBC connection...");

        final Connection conn = Connections.acquire(RuntimeConfig.getConfig().
                getJDBCURL());
        try {
            conn.setAutoCommit(false);
        } catch (SQLException ex) {
            System.err.println("Failed to configure JDBC connection.");
            printStackTrace(ex);
            close(conn);
            return;
        }

        try {
            System.out.printf("Creating new table %s...\n", getBundleTableName());
            final Statement stmt = conn.createStatement();

            String createTable = String.format("CREATE TABLE %s ( bundle_id integer NOT NULL)",
                                        getBundleTableName());
            System.out.println(createTable);
            stmt.execute(createTable);

            String primaryKey = String.format("ALTER TABLE %s ADD CONSTRAINT %s PRIMARY KEY (bundle_id)",
                                        getBundleTableName(),
                                        getPrimaryKeyConstraintName());
            System.out.println(primaryKey);
            stmt.execute(primaryKey);
            
            String foreignKey = String.format("ALTER TABLE %s ADD CONSTRAINT %s FOREIGN KEY (bundle_id) REFERENCES %s(bundle_id)",
                                        getBundleTableName(),
                                        getBundleContraintName(),
                                        getSuperBundleTable());
            System.out.println(foreignKey);
            stmt.execute(foreignKey);

        } catch (SQLException ex) {
            System.err.printf("Failed to create table %s.\n",
                              getBundleTableName());
            printStackTrace(ex);
            rollback(conn);
            close(conn);
            return;
        }

        try {
            System.out.println("Filling new tables with data...");
            final Statement queryPersonsStmt = conn.createStatement();
            final Statement stmt = conn.createStatement();

            final ResultSet personsRs = queryPersonsStmt.executeQuery(String.format(
                    "SELECT parent_id "
                    + "FROM cms_items "
                    + "JOIN %s "
                    + "ON cms_items.item_id = %s.%s;",
                    getContentItemTableName(),
                    getContentItemTableName(),
                    getIdColName()));

            while (personsRs.next()) {
                stmt.addBatch(String.format("INSERT INTO %s (bundle_id) "
                                            + "VALUES (%d);",
                                            getBundleTableName(),
                                            personsRs.getInt(1)));
                stmt.addBatch(String.format(
                        "UPDATE acs_objects "
                        + "SET default_domain_class = '%s',"
                        + "object_type = '%s' "
                        + "WHERE object_id = %d;",
                        getBundleClassName(),
                        getBundleClassName(),
                        personsRs.getInt(1)));
            }

            stmt.executeBatch();
        } catch (SQLException ex) {
            System.err.println("Failed to fill tables.");
            printStackTrace(ex);
            rollback(conn);
            close(conn);
            return;
        }

        try {
            conn.commit();
        } catch (SQLException ex) {
            System.err.println("Failed to commiting modifications.");
            printStackTrace(ex);
            rollback(conn);
            return;
        }

        close(conn);
    }

    private void rollback(final Connection conn) {
        try {
            System.err.println("WARNING: Rollback.");
            conn.rollback();
        } catch (SQLException ex1) {
            System.err.println("Rollback failed.");
            ex1.printStackTrace(System.err);
        }
    }

    private void close(final Connection conn) {
        try {
            conn.close();
        } catch (SQLException ex) {
            System.err.println("Failed to close JDBC connection.");
            printStackTrace(ex);
        }
    }

    private void printStackTrace(final SQLException ex) {
        ex.printStackTrace(System.err);
        if (ex.getNextException() != null) {
            printStackTrace(ex.getNextException());
        }
    }

}

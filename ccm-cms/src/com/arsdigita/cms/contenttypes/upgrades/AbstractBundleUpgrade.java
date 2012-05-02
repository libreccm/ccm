package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.util.cmd.Program;
import com.arsdigita.util.jdbc.Connections;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public abstract class AbstractBundleUpgrade extends Program {

    public AbstractBundleUpgrade(final String name, final String version, final String usage) {
        super(name, version, usage);
    }

    protected abstract String getBundleTableName();

    protected abstract String getContentItemTableName();

    protected abstract String getIdColName();

    protected abstract String getBundleClassName();

    protected abstract String getPrimaryKeyConstraintName();

    protected abstract String getBundleContraintName();

    protected String getSuperBundleTable() {
        return "cms_bundles";
    }
    
    @Override
    public void doRun(final CommandLine cmdLine) {
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

            stmt.addBatch(String.format("CREATE TABLE %s ( "
                                        + "bundle_id integer NOT NULL)",
                                        getBundleTableName()));

            stmt.addBatch(String.format("ALTER TABLE ONLY %s "
                                        + "ADD CONSTRAINT %s "
                                        + "PRIMARY KEY (bundle_id);",
                                        getBundleTableName(),
                                        getPrimaryKeyConstraintName()));

            stmt.addBatch(String.format("ALTER TABLE ONLY %s "
                                        + "ADD CONSTRAINT %s "
                                        + "FOREIGN KEY (bundle_id) "
                                        + "REFERENCES %s(bundle_id);",
                                        getBundleTableName(),
                                        getBundleContraintName(),
                                        getSuperBundleTable()));

            stmt.executeBatch();

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
                    + "ON cms_items.item_id = %s.%s",
                    getBundleTableName(),
                    getBundleTableName(),
                    getIdColName()));

            while (personsRs.next()) {
                stmt.addBatch(String.format("INSERT INTO %s (bundle_id) "
                                            + "VALUES (%d)",
                                            getBundleClassName(),
                                            personsRs.getInt(1)));
                stmt.addBatch(String.format(
                        "UPDATE acs_objects "
                        + "SET default_domain_class = '%s',"
                        + "object_type = '%s' "
                        + "WHERE object_id = %d",
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

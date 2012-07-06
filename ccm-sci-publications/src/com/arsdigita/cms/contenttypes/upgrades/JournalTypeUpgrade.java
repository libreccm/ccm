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
public class JournalTypeUpgrade {
    
    protected void doUpgrade() {
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
            System.out.printf("Altering table ct_journal...");
            final Statement stmt = conn.createStatement();

            stmt.addBatch("ALTER TABLE ct_journal "
                          + "ADD COLUMN firstyear integer");
        } catch (SQLException ex) {
            System.err.println("Failed to alter table ct_journal.");
            printStackTrace(ex);
            rollback(conn);
            close(conn);
            return;
        }

        try {
            System.out.printf("Moving data from ct_publications to ct_journal...");
            final Statement stmt = conn.createStatement();
            final Statement queryStmt = conn.createStatement();

            final ResultSet dataRs = queryStmt.executeQuery("SELECT publication_id, year FROM ct_publications");

            while (dataRs.next()) {
                stmt.addBatch(String.format("UPDATE ct_journal "
                                            + "SET firstyear = %s "
                                            + "WHERE journal_id = %s",
                                            dataRs.getString("year"),
                                            dataRs.getString("publication_id")));
            }
        } catch (SQLException ex) {
            System.err.println("Failed to alter table ct_journal.");
            printStackTrace(ex);
            rollback(conn);
            close(conn);
            return;
        }

        try {
            System.out.println("Altering foreign key constraint...");
            final Statement stmt = conn.createStatement();

            stmt.addBatch("ALTER TABLE ct_journal REMOVE CONSTRAINT ct_journal_journal_id_f_akey7");
            stmt.addBatch("ALTER TABLE ct_journal ADD FOREIGN KEY (publication_id) REFERENCES cms_pages");

        } catch (SQLException ex) {
            System.err.println("Failed to alter table foreign key constraint.");
            printStackTrace(ex);
            rollback(conn);
            close(conn);
            return;
        }

        try {
            System.out.println("Altering content_types table...");

            final Statement stmt = conn.createStatement();
            final Statement queryStmt = conn.createStatement();

            final ResultSet dataRs = queryStmt.executeQuery(
                    "SELECT type_id "
                    + "FROM content_types "
                    + "WHERE object_type = 'com.arsdigita.cms.contenttypes.Journal'");

            final String journalTypeId;
            dataRs.next();
            journalTypeId = dataRs.getString("type_id");
            dataRs.close();

            stmt.addBatch("UPDATE content_types "
                          + "SET ancestors = null "
                          + "WHERE object_type = 'com.arsdigita.cms.contenttypes.Journal'");
            stmt.addBatch(String.format("UPDATE content_types "
                                        + "REPLACE(descendants, '%s', '' "
                                        + "WHERE object_type = 'com.arsdigita.cms.contenttypes.Publication'",
                                        journalTypeId));
            stmt.addBatch("UPDATE content_types "
                          + "REPLACE(descendants, '//', '/' "
                          + "WHERE object_type = 'com.arsdigita.cms.contenttypes.Publication'");


        } catch (SQLException ex) {
            System.err.println("Failed to content_types table.");

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

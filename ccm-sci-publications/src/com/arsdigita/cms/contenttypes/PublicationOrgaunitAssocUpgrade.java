/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes;

import com.arsdigita.util.cmd.Program;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.util.jdbc.Connections;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.CommandLine;

/**
 * * Upgrade for association between GenericPublication and GenericPerson 
 * (ccm-sci-publications 6.6.2 to 6.6.3)
 *  
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicationOrgaunitAssocUpgrade extends Program {

    public PublicationOrgaunitAssocUpgrade() {
        super("PublicationOrgaunitAssocUpgrade", "1.0.0", "");
    }

    public static void main(final String[] args) {
        new PublicationOrgaunitAssocUpgrade().run(args);
    }

    public void doRun(final CommandLine cmdLine) {
        System.out.println("Starting upgrade...");
        List<AssocEntry> oldData = new ArrayList<AssocEntry>();

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

        System.out.println("Retrieving old data...");
        try {
            final Statement stmt = conn.createStatement();
            final ResultSet oldAssocResult = stmt.executeQuery(
                    "SELECT publication_id, orgaunit_id, publication_order"
                    + "FROM cms_organizationalunits_publications_map "
                    + "JOIN cms_items ON orgaunit_id = item_id "
                    + "WHERE version = 'draft'");

            while (oldAssocResult.next()) {
                PublicationOrgaunitAssocUpgrade.AssocEntry entry =
                                                           new PublicationOrgaunitAssocUpgrade.AssocEntry();

                entry.setPublicationDraftId(oldAssocResult.getBigDecimal(1));
                entry.setPublicationBundleDraftId(getParentIdFor(
                        entry.getPublicationDraftId(), conn));
                entry.setOrgaunitDraftId(oldAssocResult.getBigDecimal(2));
                entry.setOrgaunitBundleDraftId(getParentIdFor(
                        entry.getOrgaunitDraftId(), conn));

                entry.setPublicationLiveId(getPublicIdFor(
                        entry.getPublicationDraftId(), conn));
                entry.setPublicationBundleLiveId(getPublicIdFor(
                        entry.getPublicationBundleDraftId(), conn));
                entry.setOrgaunitLiveId(getPublicIdFor(
                        entry.getOrgaunitDraftId(), conn));
                entry.setOrgaunitBundleLiveId(getPublicIdFor(
                        entry.getOrgaunitBundleDraftId(), conn));

                entry.setOrder(oldAssocResult.getInt(3));

                oldData.add(entry);
            }
        } catch (SQLException ex) {
            System.err.println("Failed to retrieve old data.");
            printStackTrace(ex);
            return;
        }

        try {
            System.out.println("Droping old table...");
            final Statement stmt = conn.createStatement();
            stmt.execute("DROP TABLE cms_organizationalunits_publications_map");
        } catch (SQLException ex) {
            System.err.println("Failed to drop old table.");
            printStackTrace(ex);
            rollback(conn);
            close(conn);
            return;
        }

        try {
            System.out.println("Creating new tables...");
            final Statement stmt = conn.createStatement();

            stmt.addBatch(
                    "CREATE TABLE cms_organizationalunits_publications_map ( "
                    + "orgaunit_id integer NOT NULL, "
                    + "publication_id integer NOT NULL, "
                    + "publication_order integer");

            stmt.addBatch("ALTER TABLE ONLY cms_organizationalunits_publications_map "
                          + "ADD CONSTRAINT cms_org_pub_map_org_id_p__dore "
                          + "PRIMARY KEY (publication_id, orgaunit_id);");

            stmt.addBatch(
                    "ALTER TABLE ONLY cms_organizationalunits_publications_map "
                    + "ADD CONSTRAINT cms_org_pub_map_org_id_f_pe406 "
                    + "FOREIGN KEY (orgaunit_id) "
                    + "REFERENCES cms_orgaunit_bundles(bundle_id)");

            stmt.addBatch(
                    "ALTER TABLE ONLY cms_organizationalunits_publications_map "
                    + "ADD CONSTRAINT cms_org_pub_map_pub_id_f_6udi3 "
                    + "FOREIGN KEY (publication_id) "
                    + "REFERENCES ct_publication_bundles(bundle_id);");

            stmt.executeBatch();

        } catch (SQLException ex) {
            System.err.println("Failed to create new table.");
            printStackTrace(ex);
            rollback(conn);
            close(conn);
            return;
        }

        try {
            System.out.println("Filling new tables with data...");

            final Statement queryPublicationStmt = conn.createStatement();
            final Statement stmt = conn.createStatement();

            final List<String> processedEntries = new ArrayList<String>();
            for (AssocEntry entry : oldData) {
                if (processedEntries.contains(String.format(
                        "%s-%s",
                        entry.getPublicationBundleDraftId(),
                        entry.getOrgaunitBundleDraftId()))) {
                    continue;
                }

                stmt.addBatch(String.format(
                        "INSERT INTO cms_organizationalunits_publications_map ("
                        + "publication_id,"
                        + "orgaunit_id,"
                        + "publication_order"
                        + "VALUES (%s, %s, %s)",
                        entry.getPublicationBundleDraftId().toString(),
                        entry.getOrgaunitBundleDraftId().toString(),
                        entry.getOrder().toString()));

                if ((entry.getPublicationBundleLiveId() != null)
                    && (entry.getOrgaunitBundleLiveId() != null)) {
                    stmt.addBatch(String.format(
                            "INSERT INTO cms_organizationalunits_publications_map ("
                            + "publication_id,"
                            + "organunit_id,"
                            + "publication_order) "
                            + "VALUES (%s %s %s)",
                            entry.getPublicationBundleLiveId().toString(),
                            entry.getOrgaunitBundleLiveId().toString(),
                            entry.getOrder().toString()));
                }

                if (entry.getPublicationBundleLiveId() != null) {
                    stmt.addBatch(String.format(
                            "DELETE FROM cms_published_links "
                            + "WHERE pending = %s "
                            + "AND draft_target = %s",
                            entry.getPublicationLiveId().toString(),
                            entry.getOrgaunitDraftId().toString()));
                }

                if (entry.getOrgaunitBundleLiveId() != null) {
                    stmt.addBatch(String.format(
                            "DELETE FROM cms_published_links "
                            + "WHERE pending = %s "
                            + "AND draft_target = %s",
                            entry.getOrgaunitLiveId().toString(),
                            entry.getPublicationDraftId().toString()));
                }

                processedEntries.add(String.format(
                        "%s-%s",
                        entry.getPublicationBundleDraftId().toString(),
                        entry.getOrgaunitBundleDraftId().toString()));
            }

            stmt.executeBatch();


        } catch (SQLException ex) {
            System.err.println("Failed to create new table.");
            printStackTrace(ex);
            rollback(conn);
            close(conn);
            return;
        }

        try {
            conn.commit();
        } catch (SQLException ex) {
            System.err.println("Failed to commiting changes.");
            printStackTrace(ex);
            rollback(conn);
            return;
        }

        close(conn);
    }

    private class AssocEntry {

        private BigDecimal publicationDraftId;
        private BigDecimal publicationLiveId;
        private BigDecimal publicationBundleDraftId;
        private BigDecimal publicationBundleLiveId;
        private BigDecimal orgaunitDraftId;
        private BigDecimal orgaunitLiveId;
        private BigDecimal orgaunitBundleDraftId;
        private BigDecimal orgaunitBundleLiveId;
        private Integer order;

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        public BigDecimal getOrgaunitBundleDraftId() {
            return orgaunitBundleDraftId;
        }

        public void setOrgaunitBundleDraftId(BigDecimal orgaunitBundleDraftId) {
            this.orgaunitBundleDraftId = orgaunitBundleDraftId;
        }

        public BigDecimal getOrgaunitBundleLiveId() {
            return orgaunitBundleLiveId;
        }

        public void setOrgaunitBundleLiveId(BigDecimal orgaunitBundleLiveId) {
            this.orgaunitBundleLiveId = orgaunitBundleLiveId;
        }

        public BigDecimal getOrgaunitDraftId() {
            return orgaunitDraftId;
        }

        public void setOrgaunitDraftId(BigDecimal orgaunitDraftId) {
            this.orgaunitDraftId = orgaunitDraftId;
        }

        public BigDecimal getOrgaunitLiveId() {
            return orgaunitLiveId;
        }

        public void setOrgaunitLiveId(BigDecimal orgaunitLiveId) {
            this.orgaunitLiveId = orgaunitLiveId;
        }

        public BigDecimal getPublicationBundleDraftId() {
            return publicationBundleDraftId;
        }

        public void setPublicationBundleDraftId(
                BigDecimal publicationBundleDraftId) {
            this.publicationBundleDraftId = publicationBundleDraftId;
        }

        public BigDecimal getPublicationBundleLiveId() {
            return publicationBundleLiveId;
        }

        public void setPublicationBundleLiveId(
                BigDecimal publicationBundleLiveId) {
            this.publicationBundleLiveId = publicationBundleLiveId;
        }

        public BigDecimal getPublicationDraftId() {
            return publicationDraftId;
        }

        public void setPublicationDraftId(BigDecimal publicationDraftId) {
            this.publicationDraftId = publicationDraftId;
        }

        public BigDecimal getPublicationLiveId() {
            return publicationLiveId;
        }

        public void setPublicationLiveId(BigDecimal publicationLiveId) {
            this.publicationLiveId = publicationLiveId;
        }
    }

    private BigDecimal getPublicIdFor(final BigDecimal id,
                                      final Connection conn)
            throws SQLException {
        final Statement stmt = conn.createStatement();

        final ResultSet rs = stmt.executeQuery(String.format(
                "SELECT item_id FROM cms_items WHERE master_id = %s",
                id.toString()));

        while (rs.next()) {
            return rs.getBigDecimal(1);
        }

        return null;
    }

    private BigDecimal getParentIdFor(final BigDecimal id,
                                      final Connection conn)
            throws SQLException {
        final Statement stmt = conn.createStatement();

        final ResultSet rs = stmt.executeQuery(String.format(
                "SELECT parent_id FROM cms_items WHERE item_id = %s",
                id.toString()));

        while (rs.next()) {
            return rs.getBigDecimal(1);
        }

        return null;
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

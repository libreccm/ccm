package com.arsdigita.cms.contenttypes;

import com.arsdigita.packaging.Program;
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
 
 * Upgrade for association between GenericPublication and GenericPerson 
 * (ccm-sci-publications 6.6.2 to 6.6.3)
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicationAuthorAssocUpgrade extends Program {

    public PublicationAuthorAssocUpgrade() {
        super("PublicationAuthorAssocUpgrade", "1.0.0", "");
    }

    public static void main(final String[] args) {
        new PublicationAuthorAssocUpgrade().run(args);
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
                    "SELECT publication_id, person_id, editor, authorship_order"
                    + "FROM ct_publications_authorship "
                    + "JOIN cms_items ON publication_id = item_id "
                    + "WHERE version = 'draft'");

            while (oldAssocResult.next()) {
                AssocEntry entry = new AssocEntry();

                entry.setPublicationDraftId(oldAssocResult.getBigDecimal(1));
                entry.setPublicationDraftBundleId(getParentIdFor(
                        entry.getPublicationDraftId(), conn));
                entry.setAuthorDraftId(oldAssocResult.getBigDecimal(2));
                entry.setAuthorBundleDraftId(getParentIdFor(
                        entry.getAuthorDraftId(), conn));

                entry.setPublicationLiveId(getPublicIdFor(
                        entry.getPublicationDraftId(), conn));
                entry.setPublicationLiveBundleId(getPublicIdFor(
                        entry.getPublicationDraftBundleId(), conn));
                entry.setAuthorLiveId(getPublicIdFor(
                        entry.getAuthorDraftId(), conn));
                entry.setAuthorBundleLiveId(getPublicIdFor(
                        entry.getAuthorBundleDraftId(), conn));

                entry.setEditor(oldAssocResult.getBoolean(3));
                entry.setAuthorOrder(oldAssocResult.getInt(4));

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
            stmt.execute("DROP TABLE ct_publications_authorship");
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

            stmt.addBatch("CREATE TABLE ct_publication_bundles ("
                          + "bundle_id integer NOT NULL)");

            stmt.addBatch("CREATE TABLE ct_publications_authorship ("
                          + "publication_id integer NOT NULL, "
                          + "person_id integer NOT NULL, "
                          + "editor boolean, "
                          + "authorship_order integer");

            stmt.addBatch("ALTER TABLE ONLY ct_publication_bundles "
                          + "ADD CONSTRAINT ct_publica_bund_bun_id_p_ivy3p "
                          + "PRIMARY KEY (bundle_id);");

            stmt.addBatch("ALTER TABLE ONLY ct_publication_bundles "
                          + "ADD CONSTRAINT ct_publica_bund_bun_id_f_bp022 "
                          + "FOREIGN KEY(bundle_id) "
                          + "REFERENCES cms_bundles (bundle_id)");

            stmt.addBatch("ALTER TABLE ct_publications_authorship "
                          + "ADD CONSTRAINT ct_pub_aut_per_id_pub__p_adskp "
                          + "PRIMARY KEY (person_id, publication_id)");

            stmt.addBatch("ALTER TABLE ONLY ct_publications_authorship "
                          + "ADD CONSTRAINT ct_publi_auth_publi_id_f_6aw9g "
                          + "FOREIGN KEY (publication_id) "
                          + "REFERENCES ct_publication_bundles(bundle_id)");

            stmt.addBatch("ALTER TABLE ONLY ct_publications_authorship "
                          + "ADD CONSTRAINT ct_public_autho_per_id_f_ot1p6 "
                          + "FOREIGN KEY (person_id) "
                          + "REFERENCES cms_person_bundles(bundle_id)");

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

            final ResultSet publicationRs =
                            queryPublicationStmt.executeQuery(
                    "SELECT parent_id "
                    + "FROM cms_items "
                    + "JOIN cms_publications "
                    + "WHERE cms_items.item_id = cms_publications.publication_id");

            while (publicationRs.next()) {
                stmt.addBatch(String.format("INSERT INTO cms_publication_bundles (bundle_id) "
                                            + "VALUES (%d)",
                                            publicationRs.getInt(1)));
                stmt.addBatch(String.format(
                        "UPDATE acs_objects "
                        + "SET default_domain_class = 'com.arsdigita.cms.contenttypes.PublicationBundle', "
                        + "object_type = 'com.arsdigita.cms.contenttypes.PublicationBundle' "
                        + "WHERE object_id = %d",
                        publicationRs.getInt(1)));
            }

            final List<String> processedEntries = new ArrayList<String>();
            for (AssocEntry entry : oldData) {
                if (processedEntries.contains(String.format(
                        "%s-%s",
                        entry.getPublicationDraftBundleId().toString(),
                        entry.getAuthorBundleDraftId().toString()))) {
                    continue;
                }

                stmt.addBatch(String.format(
                        "INSERT INTO ct_publications_authorship ("
                        + "publication_id, "
                        + "person_id, "
                        + "editor, "
                        + "authorship_order) "
                        + "VALUES (%s, %s, %s, %s)",
                        entry.getPublicationDraftBundleId().toString(),
                        entry.getAuthorBundleDraftId().toString(),
                        entry.getEditor().toString(),
                        entry.getAuthorOrder().toString()));

                if ((entry.getPublicationLiveBundleId() != null)
                    && (entry.getAuthorBundleLiveId() != null)) {
                    stmt.addBatch(String.format(
                            "INSERT INTO ct_publications_authorship ("
                            + "publication_id, "
                            + "person_id, "
                            + "editor, "
                            + "authorship_order) "
                            + "VALUES (%s, %s, %s, %s)",
                            entry.getPublicationLiveBundleId().toString(),
                            entry.getAuthorBundleLiveId().toString(),
                            entry.getEditor().toString(),
                            entry.getAuthorOrder().toString()));
                }

                if (entry.getPublicationLiveBundleId() != null) {
                    stmt.addBatch(String.format(
                            "DELETE FROM cms_published_links "
                            + "WHERE pending = %s "
                            + "AND draft_target = %s",
                            entry.getPublicationLiveId().toString(),
                            entry.getAuthorDraftId().toString()));
                }

                if (entry.getAuthorBundleLiveId() != null) {
                    stmt.addBatch(String.format(
                            "DELETE FROM cms_published_links "
                            + "WHERE pending = %s "
                            + "AND draft_target = %s",
                            entry.getAuthorLiveId().toString(),
                            entry.getPublicationDraftId().toString()));
                }

                processedEntries.add(String.format(
                        "%s-%s",
                        entry.getPublicationDraftBundleId().toString(),
                        entry.getAuthorBundleDraftId().
                        toString()));
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
        private BigDecimal authorDraftId;
        private BigDecimal publicationLiveId;
        private BigDecimal authorLiveId;
        private BigDecimal publicationDraftBundleId;
        private BigDecimal authorBundleDraftId;
        private BigDecimal publicationLiveBundleId;
        private BigDecimal authorBundleLiveId;
        private Boolean editor;
        private Integer authorOrder;

        public AssocEntry() {
        }

        public BigDecimal getAuthorBundleDraftId() {
            return authorBundleDraftId;
        }

        public void setAuthorBundleDraftId(BigDecimal authorBundleDraftId) {
            this.authorBundleDraftId = authorBundleDraftId;
        }

        public BigDecimal getAuthorBundleLiveId() {
            return authorBundleLiveId;
        }

        public void setAuthorBundleLiveId(BigDecimal authorBundleLiveId) {
            this.authorBundleLiveId = authorBundleLiveId;
        }

        public BigDecimal getAuthorDraftId() {
            return authorDraftId;
        }

        public void setAuthorDraftId(BigDecimal authorDraftId) {
            this.authorDraftId = authorDraftId;
        }

        public BigDecimal getAuthorLiveId() {
            return authorLiveId;
        }

        public void setAuthorLiveId(BigDecimal authorLiveId) {
            this.authorLiveId = authorLiveId;
        }

        public Integer getAuthorOrder() {
            return authorOrder;
        }

        public void setAuthorOrder(Integer authorOrder) {
            this.authorOrder = authorOrder;
        }

        public Boolean getEditor() {
            return editor;
        }

        public void setEditor(Boolean editor) {
            this.editor = editor;
        }

        public BigDecimal getPublicationDraftBundleId() {
            return publicationDraftBundleId;
        }

        public void setPublicationDraftBundleId(
                BigDecimal publicationDraftBundleId) {
            this.publicationDraftBundleId = publicationDraftBundleId;
        }

        public BigDecimal getPublicationDraftId() {
            return publicationDraftId;
        }

        public void setPublicationDraftId(BigDecimal publicationDraftId) {
            this.publicationDraftId = publicationDraftId;
        }

        public BigDecimal getPublicationLiveBundleId() {
            return publicationLiveBundleId;
        }

        public void setPublicationLiveBundleId(
                BigDecimal publicationLiveBundleId) {
            this.publicationLiveBundleId = publicationLiveBundleId;
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

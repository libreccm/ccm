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
 * Upgrade for association between GenericContact and GenericPerson (6.6.4 to
 * 6.6.5)
 *
 * @author Jens Pelzetter
 * @version $Id: GenericContactPersonAssocUpgrade.java 1501 2012-02-10 16:49:14Z
 * jensp $
 */
public class GenericContactPersonAssocUpgrade extends Program {

    public GenericContactPersonAssocUpgrade() {
        super("GenericContactPersonAssocUpgrade", "1.0.0", "");
    }

    public static void main(final String[] args) {
        new GenericContactPersonAssocUpgrade().run(args);
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
                    "SELECT person_id, contact_id, link_order, link_key "
                    + "FROM cms_person_contact_map "
                    + "JOIN cms_items on person_id = item_id "
                    + "WHERE version = 'draft' ");

            while (oldAssocResult.next()) {
                AssocEntry entry = new AssocEntry();

                entry.setPersonDraftId(oldAssocResult.getBigDecimal(1));
                entry.setPersonDraftBundleId(getParentIdFor(
                        entry.getPersonDraftId(), conn));
                entry.setContactDraftId(oldAssocResult.getBigDecimal(2));
                entry.setContactDraftBundleId(getParentIdFor(
                        entry.getContactDraftId(), conn));

                entry.setPersonPublicId(getPublicIdFor(
                        entry.getPersonDraftId(), conn));
                entry.setPersonPublicBundleId(getPublicIdFor(
                        entry.getPersonDraftBundleId(), conn));
                entry.setContactPublicId(getPublicIdFor(
                        entry.getContactDraftId(), conn));
                entry.setContactPublicBundleId(getPublicIdFor(
                        entry.getContactDraftBundleId(), conn));

                entry.setLinkOrder(oldAssocResult.getInt(3));
                entry.setLinkKey(oldAssocResult.getString(4));

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
            stmt.execute("DROP TABLE cms_person_contact_map");
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

            stmt.addBatch("CREATE TABLE cms_person_bundles ( "
                          + "bundle_id integer NOT NULL)");

            stmt.addBatch("CREATE TABLE cms_contact_bundles ( "
                          + "bundle_id integer NOT NULL)");

            stmt.addBatch("CREATE TABLE cms_person_contact_map ("
                          + "person_id integer NOT NULL,"
                          + "contact_id integer NOT NULL,"
                          + "link_order integer,"
                          + "link_key character varying(100)"
                          + ")");

            stmt.addBatch("ALTER TABLE ONLY cms_person_bundles "
                          + "ADD CONSTRAINT cms_pers_bundl_bund_id_p_7xuzi "
                          + "PRIMARY KEY (bundle_id);");

            stmt.addBatch("ALTER TABLE ONLY cms_person_bundles "
                          + "ADD CONSTRAINT cms_pers_bundl_bund_id_f__rzge "
                          + "FOREIGN KEY (bundle_id) "
                          + "REFERENCES cms_bundles(bundle_id);");

            stmt.addBatch("ALTER TABLE ONLY cms_contact_bundles "
                          + "ADD CONSTRAINT cms_cont_bundl_bund_id_p_2p6vp "
                          + "PRIMARY KEY (bundle_id);");

            stmt.addBatch("ALTER TABLE ONLY cms_contact_bundles "
                          + "ADD CONSTRAINT cms_cont_bundl_bund_id_f_m8aga "
                          + "FOREIGN KEY (bundle_id) "
                          + "REFERENCES cms_bundles(bundle_id);");

            stmt.addBatch("ALTER TABLE ONLY cms_person_contact_map "
                          + "ADD CONSTRAINT cms_per_con_map_con_id_p_g1cii "
                          + "PRIMARY KEY (contact_id, person_id)");

            stmt.addBatch("ALTER TABLE ONLY cms_person_contact_map "
                          + "ADD CONSTRAINT cms_per_con_map_con_id_f_peoc2 "
                          + "FOREIGN KEY (contact_id) "
                          + "REFERENCES cms_contact_bundles(bundle_id);");

            stmt.addBatch("ALTER TABLE ONLY cms_person_contact_map "
                          + "ADD CONSTRAINT cms_per_con_map_per_id_f_g82jn "
                          + "FOREIGN KEY (person_id) "
                          + "REFERENCES cms_person_bundles(bundle_id);");
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
            final Statement queryPersonsStmt = conn.createStatement();
            final Statement queryContactsStmt = conn.createStatement();
            final Statement stmt = conn.createStatement();

            final ResultSet personsRs =
                            queryPersonsStmt.executeQuery(
                    "SELECT parent_id "
                    + "FROM cms_items "
                    + "JOIN cms_persons "
                    + "ON cms_items.item_id = cms_persons.person_id");

            while (personsRs.next()) {
                stmt.addBatch(String.format("INSERT INTO cms_person_bundles (bundle_id) "
                                            + "VALUES (%d)",
                                            personsRs.getInt(1)));
                stmt.addBatch(String.format(
                        "UPDATE acs_objects "
                        + "SET default_domain_class = 'com.arsdigita.cms.contenttypes.GenericPersonBundle',"
                        + "object_type = 'com.arsdigita.cms.contenttypes.GenericPersonBundle' "
                        + "WHERE object_id = %d",
                        personsRs.getInt(1)));
            }

            final ResultSet contactsRs = queryContactsStmt.executeQuery(
                    "SELECT DISTINCT parent_id "
                    + "FROM cms_items "
                    + "JOIN cms_contacts "
                    + "ON cms_items.item_id = cms_contacts.contact_id");

            while (contactsRs.next()) {
                stmt.addBatch(String.format("INSERT INTO cms_contact_bundles (bundle_id) "
                                            + "VALUES (%s)",
                                            contactsRs.getInt(1)));
                stmt.addBatch(String.format(
                        "UPDATE acs_objects "
                        + "SET default_domain_class = 'com.arsdigita.cms.contenttypes.GenericContactBundle', "
                        + "object_type = 'com.arsdigita.cms.contenttypes.GenericContactBundle'  "
                        + "WHERE object_id = %d",
                        contactsRs.getInt(1)));
            }

            final List<String> processedEntries =
                               new ArrayList<String>();
            for (AssocEntry entry : oldData) {
                if (processedEntries.contains(
                        String.format("%s-%s",
                                      entry.getPersonDraftBundleId().toString(),
                                      entry.getContactDraftBundleId().toString()))) {
                    continue;
                }

                stmt.addBatch(String.format(
                        "INSERT INTO cms_person_contact_map ("
                        + "person_id, "
                        + "contact_id, "
                        + "link_order, "
                        + "link_key) "
                        + "VALUES (%s, %s, %d, '%s')",
                        entry.getPersonDraftBundleId().toString(),
                        entry.getContactDraftBundleId().toString(),
                        entry.getLinkOrder(),
                        entry.getLinkKey()));

                if ((entry.getPersonPublicBundleId() != null)
                    && (entry.getContactPublicBundleId() != null)) {
                    stmt.addBatch(String.format(
                            "INSERT INTO cms_person_contact_map ("
                            + "person_id, "
                            + "contact_id, "
                            + "link_order, "
                            + "link_key) "
                            + "VALUES (%s, %s, %d, '%s')",
                            entry.getPersonPublicBundleId().toString(),
                            entry.getContactPublicBundleId().toString(),
                            entry.getLinkOrder(),
                            entry.getLinkKey()));
                }

                if (entry.getPersonPublicBundleId() != null) {
                    stmt.addBatch(String.format(
                            "UPDATE cms_published_links "
                            + "SET pending = %s, "
                            + "pending_source = %s, "
                            + "draft_target = %s "
                            + "WHERE pending = %s "
                            + "AND draft_target = %s",
                            entry.getPersonPublicBundleId().toString(),
                            entry.getPersonPublicBundleId().toString(),
                            entry.getContactDraftBundleId(),
                            entry.getPersonPublicId(),
                            entry.getContactDraftId()));
                }

                if (entry.getContactPublicBundleId() != null) {
                    stmt.addBatch(String.format(
                            "UPDATE cms_published_links "
                            + "SET pending = %s, "
                            + "pending_source = %s, "
                            + "draft_target = %s "
                            + "WHERE pending = %s "
                            + "AND draft_target = %s",
                            entry.getContactPublicBundleId().toString(),
                            entry.getContactPublicBundleId().toString(),
                            entry.getPersonDraftBundleId(),
                            entry.getContactPublicId(),
                            entry.getPersonDraftId()));
                }

                processedEntries.add(String.format(
                        "%s-%s",
                        entry.getPersonDraftBundleId().
                        toString(),
                        entry.getContactDraftBundleId().toString()));
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
            System.err.println("Failed to commiting changes.");
            printStackTrace(ex);
            rollback(conn);
            return;
        }

        close(conn);
    }

    private class AssocEntry {

        private BigDecimal personDraftId;
        private BigDecimal contactDraftId;
        private BigDecimal personPublicId;
        private BigDecimal contactPublicId;
        private BigDecimal personDraftBundleId;
        private BigDecimal contactDraftBundleId;
        private BigDecimal personPublicBundleId;
        private BigDecimal contactPublicBundleId;
        private Integer linkOrder;
        private String linkKey;

        public AssocEntry() {
        }

        public BigDecimal getContactDraftBundleId() {
            return contactDraftBundleId;
        }

        public void setContactDraftBundleId(BigDecimal contactDraftBundleId) {
            this.contactDraftBundleId = contactDraftBundleId;
        }

        public BigDecimal getContactDraftId() {
            return contactDraftId;
        }

        public void setContactDraftId(BigDecimal contactDraftId) {
            this.contactDraftId = contactDraftId;
        }

        public BigDecimal getContactPublicBundleId() {
            return contactPublicBundleId;
        }

        public void setContactPublicBundleId(BigDecimal contactPublicBundleId) {
            this.contactPublicBundleId = contactPublicBundleId;
        }

        public BigDecimal getContactPublicId() {
            return contactPublicId;
        }

        public void setContactPublicId(BigDecimal contactPublicId) {
            this.contactPublicId = contactPublicId;
        }

        public String getLinkKey() {
            return linkKey;
        }

        public void setLinkKey(String linkKey) {
            this.linkKey = linkKey;
        }

        public Integer getLinkOrder() {
            return linkOrder;
        }

        public void setLinkOrder(Integer linkOrder) {
            this.linkOrder = linkOrder;
        }

        public BigDecimal getPersonDraftBundleId() {
            return personDraftBundleId;
        }

        public void setPersonDraftBundleId(BigDecimal personDraftBundleId) {
            this.personDraftBundleId = personDraftBundleId;
        }

        public BigDecimal getPersonDraftId() {
            return personDraftId;
        }

        public void setPersonDraftId(BigDecimal personDraftId) {
            this.personDraftId = personDraftId;
        }

        public BigDecimal getPersonPublicBundleId() {
            return personPublicBundleId;
        }

        public void setPersonPublicBundleId(BigDecimal personPublicBundleId) {
            this.personPublicBundleId = personPublicBundleId;
        }

        public BigDecimal getPersonPublicId() {
            return personPublicId;
        }

        public void setPersonPublicId(BigDecimal personPublicId) {
            this.personPublicId = personPublicId;
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
            System.out.println("Rollback failed.");
        }
    }

    private void close(final Connection conn) {
        try {
            conn.close();
        } catch (SQLException ex) {
            System.err.println("Failed to close JDBC connectio.");
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

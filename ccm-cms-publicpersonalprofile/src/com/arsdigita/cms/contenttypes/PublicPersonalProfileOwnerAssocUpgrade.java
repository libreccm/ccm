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
 * Upgrade for the owner association (6.6.1 to 6.6.2). Warning: Update of
 * ccm-cms from 6.6.4 to 6.6.5 has be executed before this update.
 *
 * @author Jens Pelzetter
 * @version $Id: PublicPersonalProfileOwnerAssocUpgrade.java 1501 2012-02-10
 * 16:49:14Z jensp $
 */
public class PublicPersonalProfileOwnerAssocUpgrade extends Program {

    public PublicPersonalProfileOwnerAssocUpgrade() {
        super("PublicPersonalProfileOwnerAssocUpgrade", "1.0.0", "");
    }

    public static final void main(final String[] args) {
        new PublicPersonalProfileOwnerAssocUpgrade().run(args);
    }

    public void doRun(final CommandLine cmdLine) {
        System.out.println("Starting upgrade...");

        List<OldAssocEntry> oldData = new ArrayList<OldAssocEntry>();

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
                    "SELECT profile_id, owner_id, owner_order "
                    + "FROM ct_public_personal_profile_owner_map");

            while (oldAssocResult.next()) {
                oldData.add(new OldAssocEntry(oldAssocResult.getBigDecimal(1),
                                              oldAssocResult.getBigDecimal(2),
                                              oldAssocResult.getInt(3)));
            }
        } catch (SQLException ex) {
            System.err.println("Failed to retrieve old data.");
            printStackTrace(ex);
            return;
        }

        try {
            System.out.println("Droping old table...");
            final Statement stmt = conn.createStatement();
            stmt.execute("DROP TABLE ct_public_personal_profile_owner_map");
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

            stmt.addBatch("CREATE TABLE ct_public_personal_profile_bundles ( "
                          + "bundle_id integer NOT NULL)");

            stmt.addBatch("CREATE TABLE ct_public_personal_profile_owner_map ( "
                          + "profile_id integer NOT NULL, "
                          + "owner_id integer NOT NULL, "
                          + "owner_order integer)");

            stmt.addBatch("ALTER TABLE ONLY ct_public_personal_profile_bundles "
                          + "ADD CONSTRAINT ct_pub_per_pro_bun_bun_p_zhc9i "
                          + "PRIMARY KEY (bundle_id)");

            stmt.addBatch("ALTER TABLE ONLY ct_public_personal_profile_bundles "
                          + " ADD CONSTRAINT ct_pub_per_pro_bun_bun_f__jr2_ "
                          + "FOREIGN KEY (bundle_id) "
                          + "REFERENCES cms_bundles(bundle_id)");

            stmt.addBatch("ALTER TABLE ONLY ct_public_personal_profile_owner_map "
                          + "ADD CONSTRAINT ct_pub_per_pro_own_map_p_rr7ie "
                          + "PRIMARY KEY (owner_id, profile_id)");

            stmt.addBatch("ALTER TABLE ONLY ct_public_personal_profile_owner_map "
                          + "ADD CONSTRAINT ct_pub_per_pro_own_map_f_cd7_1 "
                          + "FOREIGN KEY (owner_id) "
                          + "REFERENCES cms_person_bundles(bundle_id)");

            stmt.addBatch(
                    "ALTER TABLE ONLY ct_public_personal_profile_owner_map "
                    + "ADD CONSTRAINT ct_pub_per_pro_own_map_f_ugs15 "
                    + "FOREIGN KEY (profile_id) "
                    + "REFERENCES ct_public_personal_profile_bundles(bundle_id)");
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
            final Statement queryProfilesStmt = conn.createStatement();
            final Statement stmt = conn.createStatement();

            final ResultSet profilesRs =
                            queryProfilesStmt.executeQuery(
                    "SELECT parent_id "
                    + "FROM cms_items "
                    + "JOIN ct_public_personal_profiles "
                    + "ON cms_items.item_id = ct_public_personal_profiles.profile_id");

            while (profilesRs.next()) {
                stmt.addBatch(String.format(
                        "INSERT INTO ct_public_personal_profile_bundles (bundle_id) "
                        + "VALUES (%d)",
                        profilesRs.getInt(1)));
                stmt.addBatch(String.format(
                        "UPDATE acs_objects "
                        + "SET default_domain_class = 'com.arsdigita.cms.contenttypes.PublicPersonalProfileBundle' "
                        + "WHERE object_id = %d",
                        profilesRs.getInt(1)));
            }

            final List<String> processedEntries = new ArrayList<String>();
            for (OldAssocEntry entry : oldData) {
                BigDecimal profileBundleId;
                BigDecimal ownerBundleId;

                profileBundleId = getParentIdFor(entry.getProfileId(), conn);
                ownerBundleId = getParentIdFor(entry.ownerId, conn);

                if (processedEntries.contains(
                        String.format("%s-%s",
                                      profileBundleId.toString(),
                                      ownerBundleId.toString()))) {
                    continue;
                }

                stmt.addBatch(String.format(
                        "INSERT INTO ct_public_personal_profile_owner_map ("
                        + "profile_id, "
                        + "owner_id, "
                        + "owner_order) "
                        + "VALUES (%s, %s, %d)",
                        profileBundleId.toString(),
                        ownerBundleId.toString(),
                        entry.getOwnerOrder()));

                processedEntries.add(String.format(
                        "%s-%s",
                        profileBundleId.toString(),
                        ownerBundleId.toString()));
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

    private class OldAssocEntry {

        private BigDecimal profileId;
        private BigDecimal ownerId;
        private Integer ownerOrder;

        public OldAssocEntry(final BigDecimal profileId,
                             final BigDecimal ownerId,
                             final Integer ownerOrder) {
            this.profileId = profileId;
            this.ownerId = ownerId;
            this.ownerOrder = ownerOrder;
        }

        public BigDecimal getOwnerId() {
            return ownerId;
        }

        public void setOwnerId(BigDecimal ownerId) {
            this.ownerId = ownerId;
        }

        public Integer getOwnerOrder() {
            return ownerOrder;
        }

        public void setOwnerOrder(Integer ownerOrder) {
            this.ownerOrder = ownerOrder;
        }

        public BigDecimal getProfileId() {
            return profileId;
        }

        public void setProfileId(BigDecimal profileId) {
            this.profileId = profileId;
        }
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

    private BigDecimal getParentIdFor(final BigDecimal id,
                                      final Connection conn)
            throws SQLException {
        final Statement stmt = conn.createStatement();

        final ResultSet rs = stmt.executeQuery(String.format(
                "SELECT parent_id FROM cms_items where item_id = %s",
                id.toString()));

        while (rs.next()) {
            return rs.getBigDecimal(1);
        }

        return null;
    }
}

package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.util.jdbc.Connections;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public abstract class AbstractAssocUpgrade {

    public AbstractAssocUpgrade() {
        //Nothing
    }

    protected abstract String getTableName();

    protected abstract String getOwnerIdCol();

    protected abstract String getMemberIdCol();

    protected abstract Map<String, String> getAttributes();

    protected abstract String getPrimaryKeyConstraintName();

    protected abstract String getOwnerConstraintName();

    protected abstract String getMemberConstraintName();

    protected abstract String getOwnerTableName();

    protected abstract String getMemberTableName();
    
    protected void doUpgrade() {
        System.out.println("Starting upgrade...");
        final List<AssocEntry> oldData = new ArrayList<AssocEntry>();

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
            final ResultSet oldAssocResult = stmt.executeQuery(String.format(
                    "SELECT * "
                    + "FROM %s "
                    + "JOIN cms_items on %s = item_id "
                    + "WHERE version = 'draft' ",
                    getTableName(),
                    getOwnerIdCol()));

            while (oldAssocResult.next()) {
                AssocEntry entry = new AssocEntry();

                entry.setOwnerDraftId(oldAssocResult.getBigDecimal(getOwnerIdCol()));
                entry.setOwnerDraftBundleId(getParentIdFor(entry.getOwnerDraftId(), conn));

                entry.setMemberDraftId(oldAssocResult.getBigDecimal(getMemberIdCol()));
                entry.setMemberDraftBundleId(getParentIdFor(entry.getMemberDraftId(), conn));

                entry.setOwnerPublicId(getPublicIdFor(entry.getOwnerDraftId(), conn));
                entry.setOwnerPublicBundleId(getPublicIdFor(entry.getOwnerDraftBundleId(), conn));

                entry.setMemberPublicId(getPublicIdFor(entry.getMemberDraftId(), conn));
                entry.setMemberPublicBundleId(getPublicIdFor(entry.getMemberDraftBundleId(), conn));

                for (Map.Entry<String, String> attribute : getAttributes().entrySet()) {
                    entry.addAttribute(attribute.getKey(), oldAssocResult.getString(attribute.getKey()));
                }

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
            stmt.execute(String.format("DROP TABLE %s", getTableName()));
        } catch (SQLException ex) {
            System.err.println("Failed to drop old table.");
            printStackTrace(ex);
            rollback(conn);
            close(conn);
            return;
        }

        try {
            System.out.printf("Creating new table %s...\n", getTableName());
            final Statement stmt = conn.createStatement();

            final StringBuilder attributesBuilder = new StringBuilder();
            for (Map.Entry<String, String> attribute : getAttributes().entrySet()) {
                if (attributesBuilder.length() > 0) {
                    attributesBuilder.append(",\n");
                }
                attributesBuilder.append(attribute.getKey()).append(' ').append(attribute.getValue());
            }
            stmt.addBatch(String.format("CREATE TABLE %s ("
                                        + "%s integer NOT NULL,"
                                        + "%s integer NOT NULL," 
                                        + "%s"
                                        + ")",
                                        getTableName(),
                                        getOwnerIdCol(),
                                        getMemberIdCol(),
                                        attributesBuilder.toString()));

            stmt.addBatch(String.format("ALTER TABLE %s "
                                        + "ADD CONSTRAINT %s "
                                        + "PRIMARY KEY (%s, %s)",
                                        getTableName(),
                                        getPrimaryKeyConstraintName(),
                                        getOwnerIdCol(),
                                        getMemberIdCol()));

            stmt.addBatch(String.format("ALTER TABLE %s "
                                        + "ADD CONSTRAINT %s "
                                        + "FOREIGN KEY (%s)"
                                        + "REFERENCES %s(bundle_id)",
                                        getTableName(),
                                        getOwnerConstraintName(),
                                        getOwnerIdCol(),
                                        getOwnerTableName()));

            stmt.addBatch(String.format("ALTER TABLE %s "
                                        + "ADD CONSTRAINT %s "
                                        + "FOREIGN KEY (%s)"
                                        + "REFERENCES %s(bundle_id)",
                                        getTableName(),
                                        getMemberConstraintName(),
                                        getMemberIdCol(),
                                        getMemberTableName()));

            stmt.executeBatch();

        } catch (SQLException ex) {
            System.err.printf("Failed to create new table '%s'.\n", getTableName());
            printStackTrace(ex);
            rollback(conn);
            close(conn);
            return;
        }

        try {
            System.out.println("Filling new table with data...");

            final List<String> processedEntries =
                               new ArrayList<String>();
            final Statement stmt = conn.createStatement();

            for (AssocEntry entry : oldData) {
                if (processedEntries.contains(
                        String.format("%s-%s",
                                      entry.getOwnerDraftBundleId().toString(),
                                      entry.getMemberDraftBundleId().toString()))) {
                    continue;
                }

                final StringBuilder attributeCols = new StringBuilder();
                for (Map.Entry<String, String> attribute : getAttributes().entrySet()) {
                    attributeCols.append(",");
                    attributeCols.append(attribute.getKey());
                }
                final StringBuilder attributeValues = new StringBuilder();
                for (Map.Entry<String, String> attribute : getAttributes().entrySet()) {
                    attributeValues.append(",");
                    if (attribute.getValue().startsWith("character") || attribute.getValue().startsWith("BIT") || attribute.getValue().startsWith("boolean")) {
                        attributeValues.append('\'');
                    }
                    attributeValues.append(entry.getAttributes().get(attribute.getKey()));
                    if (attribute.getValue().startsWith("character") || attribute.getValue().startsWith("BIT") || attribute.getValue().startsWith("boolean")) {
                        attributeValues.append('\'');
                    }
                }
                stmt.addBatch(String.format("INSERT INTO %s ("
                                            + "%s,"
                                            + "%s"
                                            + "%s) "
                                            + "VALUES (%s, %s %s)",
                                            getTableName(),
                                            getOwnerIdCol(),
                                            getMemberIdCol(),
                                            attributeCols.toString(),
                                            entry.getOwnerDraftBundleId().toString(),
                                            entry.getMemberDraftBundleId().toString(),
                                            attributeValues.toString()));
                if ((entry.getOwnerPublicBundleId() != null)
                    && (entry.getMemberPublicBundleId() != null)) {
                    stmt.addBatch(String.format("INSERT INTO %s ("
                                                + "%s,"
                                                + "%s"
                                                + "%s) "
                                                + "VALUES (%s, %s %s)",
                                                getTableName(),
                                                getOwnerIdCol(),
                                                getMemberIdCol(),
                                                attributeCols.toString(),
                                                entry.getOwnerPublicBundleId().toString(),
                                                entry.getMemberPublicBundleId().toString(),
                                                attributeValues.toString()));
                }

                if (entry.getOwnerPublicId() != null) {
                    stmt.addBatch(String.format("DELETE FROM cms_published_links "
                                                + "WHERE pending = %s "
                                                + "AND draft_target = %s",
                                                entry.getOwnerPublicId().toString(),
                                                entry.getMemberDraftId().toString()));
                }

                if (entry.getMemberPublicId() != null) {
                    stmt.addBatch(String.format("DELETE FROM cms_published_links "
                                                + "WHERE pending = %s"
                                                + "AND draft_target = %s",
                                                entry.getMemberPublicId().toString(),
                                                entry.getOwnerDraftId().toString()));
                }

                processedEntries.add(String.format("%s-%s",
                                                   entry.getOwnerDraftBundleId().toString(),
                                                   entry.getMemberDraftBundleId().toString()));

                stmt.executeBatch();
            }
        } catch (SQLException ex) {
            System.err.println("Failed to fill table.");
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

    private class AssocEntry {

        private BigDecimal ownerDraftId;
        private BigDecimal memberDraftId;
        private BigDecimal ownerPublicId;
        private BigDecimal memberPublicId;
        private BigDecimal ownerDraftBundleId;
        private BigDecimal memberDraftBundleId;
        private BigDecimal ownerPublicBundleId;
        private BigDecimal memberPublicBundleId;
        private Map<String, String> attributes = new HashMap<String, String>();

        public AssocEntry() {
        }

        public BigDecimal getOwnerDraftId() {
            return ownerDraftId;
        }

        public void setOwnerDraftId(final BigDecimal ownerDraftId) {
            this.ownerDraftId = ownerDraftId;
        }

        public BigDecimal getMemberDraftId() {
            return memberDraftId;
        }

        public void setMemberDraftId(final BigDecimal memberDraftId) {
            this.memberDraftId = memberDraftId;
        }

        public BigDecimal getOwnerPublicId() {
            return ownerPublicId;
        }

        public void setOwnerPublicId(final BigDecimal ownerPublicId) {
            this.ownerPublicId = ownerPublicId;
        }

        public BigDecimal getMemberPublicId() {
            return memberPublicId;
        }

        public void setMemberPublicId(final BigDecimal memberPublicId) {
            this.memberPublicId = memberPublicId;
        }

        public BigDecimal getOwnerDraftBundleId() {
            return ownerDraftBundleId;
        }

        public void setOwnerDraftBundleId(final BigDecimal ownerDraftBundleId) {
            this.ownerDraftBundleId = ownerDraftBundleId;
        }

        public BigDecimal getMemberDraftBundleId() {
            return memberDraftBundleId;
        }

        public void setMemberDraftBundleId(final BigDecimal memberDraftBundleId) {
            this.memberDraftBundleId = memberDraftBundleId;
        }

        public BigDecimal getMemberPublicBundleId() {
            return memberPublicBundleId;
        }

        public void setMemberPublicBundleId(BigDecimal memberPublicBundleId) {
            this.memberPublicBundleId = memberPublicBundleId;
        }

        public BigDecimal getOwnerPublicBundleId() {
            return ownerPublicBundleId;
        }

        public void setOwnerPublicBundleId(BigDecimal ownerPublicBundleId) {
            this.ownerPublicBundleId = ownerPublicBundleId;
        }

        public Map<String, String> getAttributes() {
            return Collections.unmodifiableMap(attributes);
        }

        public void addAttribute(final String name, final String value) {
            attributes.put(name, value);
        }

        public void setAttributes(final Map<String, String> attributes) {
            this.attributes = attributes;
        }

    }
}

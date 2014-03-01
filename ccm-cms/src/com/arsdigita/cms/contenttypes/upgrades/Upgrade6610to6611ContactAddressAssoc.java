package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.util.cmd.Program;
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
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class Upgrade6610to6611ContactAddressAssoc extends Program {

    public Upgrade6610to6611ContactAddressAssoc() {
        super("Upgrade-6.6.10-to-6.6.11_AddressAssoc", "1.0.0", "", true, true);
    }

    @Override
    public void doRun(final CommandLine cmdLine) {
        upgradeBundles();
        upgradeAssoc();
    }

    public static void main(final String[] args) {
        new Upgrade6610to6611ContactAddressAssoc().run(args);
    }

    private void upgradeBundles() {
        System.out.println("Starting upgrade part 1 of 2");

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
            System.err.println("Creating new table 'cms_address_bundles...'");
            final Statement stmt = conn.createStatement();

            stmt.addBatch("CREATE TABLE cms_address_bundles (bundle_id integer NOT NULL)");
            stmt.addBatch("ALTER TABLE ONLY cms_address_bundles "
                          + "ADD CONSTRAINT cms_addr_bundl_bund_id_p_f_pvc "
                          + "PRIMARY KEY (bundle_id)");
            stmt.addBatch("ALTER TABLE ONLY cms_address_bundles "
                          + "ADD CONSTRAINT cms_addr_bundl_bund_id_f_n3leu "
                          + "FOREIGN KEY (bundle_id) REFERENCES cms_bundles(bundle_id)");

            stmt.executeBatch();

        } catch (SQLException ex) {
            System.err.println("Failed to create table 'cms_address_bundles'");
            printStackTrace(ex);
            rollback(conn);
            close(conn);
            return;
        }

        try {
            System.out.println("Filling new table 'cms_address_bundle' with data...");
            final Statement queryAddressesStmt = conn.createStatement();
            final Statement stmt = conn.createStatement();

            final ResultSet addressesRs = queryAddressesStmt.executeQuery(
                "SELECT parent_id "
                + "  FROM cms_items "
                + "  JOIN cms_addresses "
                + "    ON cms_items.item_id = cms_addresses.address_id");

            while (addressesRs.next()) {
                stmt.addBatch(String.
                    format("INSERT INTO cms_address_bundles(bundle_id) VALUES (%d)",
                           addressesRs.getInt(1)));
                stmt.addBatch(String.format(
                    "UPDATE acs_objects "
                    + "SET default_domain_class = 'com.arsdigita.cms.contenttypes.GenericAddressBundle',"
                    + "object_type = 'com.arsdigita.cms.contenttypes.GenericAddressBundle'"
                    + "WHERE object_id = %d",
                    addressesRs.getInt(1)));
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

    private void upgradeAssoc() {
        System.out.println("Starting upgrade part 2 of 2");

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
        final List<ContactBundleAddressBundlePair> newAssocs
                                                   = new ArrayList<ContactBundleAddressBundlePair>();
        try {
            final Statement stmt = conn.createStatement();
            final List<ContactAddressPair> oldAssocs = new ArrayList<ContactAddressPair>();
            final ResultSet oldAssocResult = stmt.executeQuery(
                "SELECT contact_id, address_id "
                + "FROM cms_contacts");

            while (oldAssocResult.next()) {
                ContactAddressPair pair = new ContactAddressPair();
                pair.setContactId(oldAssocResult.getBigDecimal("contact_id"));
                pair.setAddressId(oldAssocResult.getBigDecimal("address_id"));

                oldAssocs.add(pair);
            }

            stmt.addBatch("CREATE TABLE cms_contact_address_map ("
                          + "contact_id integer NOT NULL,"
                          + "address_id integer NOT NULL,"
                          + "link_order integer)");

            stmt.addBatch("ALTER TABLE ONLY cms_contact_address_map "
                          + "ADD CONSTRAINT cms_con_add_map_add_id_p_r1p86 "
                          + "PRIMARY KEY (contact_id, address_id)");

            stmt.addBatch("ALTER TABLE ONLY cms_contact_address_map "
                          + "ADD CONSTRAINT cms_con_add_map_con_id_f_u7txu "
                          + "FOREIGN KEY (contact_id) "
                          + "REFERENCES cms_contact_bundles(bundle_id)");

            stmt.addBatch("ALTER TABLE ONLY cms_contact_address_map "
                          + "ADD CONSTRAINT cms_con_add_map_add_id_f_92kx1 "
                          + "FOREIGN KEY (address_id) "
                          + "REFERENCES cms_address_bundles(bundle_id)");

            for (ContactAddressPair pair : oldAssocs) {
                if (pair.getAddressId() != null) {
                    ContactBundleAddressBundlePair assocPair = new ContactBundleAddressBundlePair();
                    System.out.printf(
                        "Executing SELECT parent_id FROM cms_items WHERE item_id = %s\n",
                        pair.getContactId().toString());
                    ResultSet contactBundleIdResult = stmt.executeQuery(
                        String.format("SELECT parent_id FROM cms_items WHERE item_id = %s",
                                      pair.getContactId().toString()));
                    contactBundleIdResult.next();
                    assocPair.setContactBundleId(contactBundleIdResult.getBigDecimal("parent_id"));
                    System.out.printf(
                        "Executing SELECT parent_id FROM cms_items WHERE item_id = %s\n",
                        pair.getAddressId().toString());
                    ResultSet addressBundleIdResult = stmt.executeQuery(
                        String.format("SELECT parent_id FROM cms_items WHERE item_id = %s",
                                      pair.getAddressId().toString()));
                    addressBundleIdResult.next();
                    assocPair.setAddressBundleId(addressBundleIdResult.
                        getBigDecimal("parent_id"));

                    System.out.println("Aadding to new assoc list...");
                    newAssocs.add(assocPair);
                }
            }

            System.out.println("Inserting data into new assoc table...");
            for (ContactBundleAddressBundlePair pair : newAssocs) {
                stmt.addBatch(String.format(
                    "INSERT INTO cms_contact_address_map "
                    + "(contact_id, address_id, link_order)"
                    + "VALUES (%s, %s, 1)",
                    pair.getContactBundleId().toString(),
                    pair.getAddressBundleId().toString()));
            }

            stmt.addBatch("ALTER TABLE cms_contacts DROP COLUMN address_id");

            stmt.executeBatch();

        } catch (SQLException ex) {
            System.err.
                println("Failed to create table 'cms_address_bundles' and fill it with data.");
            printStackTrace(ex);
            rollback(conn);
            close(conn);
            return;
        } catch (Exception ex) {
            System.err.
                println("Failed to create table 'cms_address_bundles' and fill it with data.");
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

    private void printStackTrace(final Exception ex) {
        ex.printStackTrace(System.err);
    }

    private class ContactAddressPair {

        private BigDecimal contactId;
        private BigDecimal addressId;

        public ContactAddressPair() {
            //Nothing
        }

        public BigDecimal getContactId() {
            return contactId;
        }

        public void setContactId(final BigDecimal contactId) {
            this.contactId = contactId;
        }

        public BigDecimal getAddressId() {
            return addressId;
        }

        public void setAddressId(final BigDecimal addressId) {
            this.addressId = addressId;
        }

    }

    private class ContactBundleAddressBundlePair {

        private BigDecimal contactBundleId;
        private BigDecimal addressBundleId;

        public ContactBundleAddressBundlePair() {
            //Nothing
        }

        public BigDecimal getContactBundleId() {
            return contactBundleId;
        }

        public void setContactBundleId(final BigDecimal contactBundleId) {
            this.contactBundleId = contactBundleId;
        }

        public BigDecimal getAddressBundleId() {
            return addressBundleId;
        }

        public void setAddressBundleId(final BigDecimal addressBundleId) {
            this.addressBundleId = addressBundleId;
        }

    }

}

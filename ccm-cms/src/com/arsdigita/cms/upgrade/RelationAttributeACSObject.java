package com.arsdigita.cms.upgrade;

import com.arsdigita.cms.RelationAttribute;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.util.cmd.Program;
import com.arsdigita.util.jdbc.Connections;
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
public class RelationAttributeACSObject extends Program {

    public RelationAttributeACSObject() {
        super("RelationAttributeACSObejct", "1.0.0", "");
    }

    public static final void main(final String[] args) {
        new RelationAttributeACSObject().run(args);
    }

    @Override
    protected void doRun(final CommandLine cmdLine) {
        new KernelExcursion() {
            @Override
            protected void excurse() {

                final Connection connection = Connections.acquire(RuntimeConfig.getConfig().getJDBCURL());
                try {
                    connection.setAutoCommit(false);
                } catch (SQLException ex) {
                    System.err.println("Failed to configure JDBC connection.");
                    printStackTrace(ex);
                    close(connection);
                    return;
                }

                final List<RelationAttributeEntry> entries = new ArrayList<RelationAttributeEntry>();
                try {
                    final Statement stmt = connection.createStatement();                    
                    
                    final ResultSet result = stmt.executeQuery(
                            "SELECT attribute, attr_key, lang, name, description FROM cms_relation_attribute;");

                    while (result.next()) {
                        final RelationAttributeEntry entry = new RelationAttributeEntry();
                        entry.setAttribute(result.getString("attribute"));
                        entry.setKey(result.getString("attr_key"));
                        entry.setLanguage(result.getString("lang"));
                        entry.setName(result.getString("name"));
                        entry.setDescription(result.getString("description"));
                        entries.add(entry);
                    }
                    System.out.printf("Found %d RelationAttributes entries.\n", entries.size());

                    stmt.addBatch("ALTER TABLE cms_relation_attribute DROP CONSTRAINT cms_rel_att_att_key_at_u_nh3g1");
                    stmt.addBatch("DROP TABLE cms_relation_attribute;");
                    stmt.addBatch("CREATE TABLE cms_relation_attribute (object_id integer NOT NULL,"
                                  + "attribute character varying(100) NOT NULL,"
                                  + "attr_key character varying(100) NOT NULL,"
                                  + "lang character varying(2) NOT NULL,"
                                  + "name character varying(100) NOT NULL,"
                                  + "description character varying(500))");
                    stmt.addBatch("ALTER TABLE ONLY cms_relation_attribute "
                                  + " ADD CONSTRAINT cms_rela_attrib_obj_id_p_qdgsr PRIMARY KEY (object_id);");
                    stmt.addBatch("ALTER TABLE ONLY cms_relation_attribute "
                                  + "ADD CONSTRAINT cms_rel_att_att_key_at_u_nh3g1 UNIQUE (attribute, attr_key, lang);");
                    stmt.addBatch("ALTER TABLE ONLY cms_relation_attribute "
                                  + "ADD CONSTRAINT cms_rela_attrib_obj_id_f_23qc3 FOREIGN KEY (object_id) REFERENCES acs_objects(object_id);");

                    stmt.executeBatch();

                    connection.commit();
                    
                    close(connection);

                } catch (SQLException ex) {
                    System.err.printf("SQL Error\n");
                    //ex.printStackTrace(System.err);
                    SQLException currentEx = ex;
                    while (currentEx != null) {
                        ex.printStackTrace(System.err);
                        System.out.println(" ");
                        currentEx = currentEx.getNextException();
                    }
                    rollback(connection);
                    close(connection);
                    return;
                }

                for (RelationAttributeEntry entry : entries) {
                    createRelationAttribute(entry);
                }                                

            }
        }.excurse();
    }

    private void createRelationAttribute(final RelationAttributeEntry entry) {
        final RelationAttribute attribute = new RelationAttribute();
        attribute.setAttribute(entry.getAttribute());
        attribute.setKey(entry.getKey());
        attribute.setLanguage(entry.getLanguage());
        attribute.setName(entry.getName());
        attribute.setDescription(entry.getDescription());
        attribute.save();
    }

    ;

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

    private class RelationAttributeEntry {

        private String attribute;
        private String key;
        private String language;
        private String name;
        private String description;

        public RelationAttributeEntry() {
            //Nothing
        }

        public String getAttribute() {
            return attribute;
        }

        public void setAttribute(final String attribute) {
            this.attribute = attribute;
        }

        public String getKey() {
            return key;
        }

        public void setKey(final String key) {
            this.key = key;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(final String language) {
            this.language = language;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}

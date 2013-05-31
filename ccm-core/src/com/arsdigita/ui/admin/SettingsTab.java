package com.arsdigita.ui.admin;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.packaging.Config;
import com.arsdigita.runtime.ConfigRegistry;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.Parameter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Tab for viewing and changing the settings in the registry.
 *
 * @author Jens Pelzetter
 */
public class SettingsTab extends BoxPanel implements AdminConstants {

    private GlobalizedMessage title;

    public SettingsTab() {

        setClassAttr("sidebarNavPanel");
        setAttribute("navbar-title", "Settings");

        final BoxPanel box = new BoxPanel(BoxPanel.VERTICAL);
        box.setClassAttr("main");

        final ConfigRegistry registry = new ConfigRegistry();
        final Config config = new Config(registry);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(outputStream);
        config.load(printStream);
        final String errors = outputStream.toString();

        box.add(new Label(errors));
                
        final List contexts = registry.getContexts();
        for (Object context : contexts) {
            final String storage = registry.getStorage((Class) context);
            add(new Label(String.format("<strong>%s</strong>", ((Class) context).getName()), false));
        }
        
        final List parameters = config.getParameters();
        for(int i = 0; i < parameters.size(); i++) {
            final Parameter parameter = (Parameter) parameters.get(i);
            add(new Label(parameter.getName()));
        }

        add(box);

    }

    private class SettingsTable extends Table implements TableActionListener {

        public SettingsTable() {
            super();

            setEmptyView(new Label("No settings found"));

            //Add columns here

            setModelBuilder(new SettingsTableModelBuilder());
        }

        public void cellSelected(TableActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public void headSelected(TableActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private class SettingsTableModelBuilder extends LockableImpl implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            return new SettingsTableModel(table);
        }
    }

    private class SettingsTableModel implements TableModel {

        private final Table table;
        private final String errors;
        private final ConfigRegistry registry;
        private final Config config;

        public SettingsTableModel(final Table table) {
            this.table = table;

            registry = new ConfigRegistry();
            config = new Config(registry);
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final PrintStream printStream = new PrintStream(outputStream);
            config.load(printStream);
            errors = outputStream.toString();



        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        public boolean nextRow() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public Object getElementAt(int columnIndex) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public Object getKeyAt(int columnIndex) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}

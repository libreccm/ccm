/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 *
 * @author koalamann
 */
public class IdSearchResultTable extends Table {

    private TextField textField;
    protected Label label;

    public IdSearchResultTable(TextField textfield) {
        super();
        this.textField = textfield;
        label = new Label("bla");
        if (textfield == null) {
            setEmptyView(new Label("..."));
        } else {
            setEmptyView(new Label("......"));
        }

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
                0,
                new Label("Result:")));

        columnModel.get(0).setCellRenderer(
                new TableCellRenderer() {
                    @Override
                    public Component getComponent(final Table table,
                            final PageState state,
                            final Object value,
                            final boolean isSelected,
                            final Object key,
                            final int row,
                            final int column) {
                        
                        String str = (String) textField.getValue(state);

                        if (isInteger(str)) {
                            int number = Integer.parseInt(str);
                            BigDecimal id = new BigDecimal(number);

                            try {
                                ContentItem item = (ContentItem) DomainObjectFactory
                                .newInstance(new OID(
                                                ContentItem.BASE_DATA_OBJECT_TYPE,
                                                id));

                                ContentSection section = item.getContentSection();
                                ItemResolver resolver = section.getItemResolver();

//        if (displayName.length() > 20) {
//            displayName = displayName.substring(0, 15);
//        }
                                Link link = new Link(
                                        item.getDisplayName(),
                                        resolver.generateItemURL(
                                                state,
                                                (item.getDraftVersion()),
                                                section,
                                                (item.getDraftVersion()).getVersion()));

                                return link;
                            } catch (DataObjectNotFoundException e) {
                                return new Label("Error: Could not find item with the id " + number);
                            }

                        } else {
                            return new Label("Error: the item-id has to be numeric");
                        }

                    }
                });

        addTableActionListener(new TableActionListener() {
            @Override
            public void cellSelected(final TableActionEvent event) {
                //ToDo
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }
        });

        setModelBuilder(new IdSearchResultTableModelBuilder());

    }

    private class IdSearchResultTableModelBuilder extends LockableImpl
            implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table,
                final PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            return new IdSearchResultTableModel(state);
        }

    }

    private class IdSearchResultTableModel implements TableModel {

//        private final List<Domain> domains;
        private int index = -1;

        public IdSearchResultTableModel(final PageState state) {
//            LOGGER.debug("Creating DomainsTableModel");
            final String filterTerm = (String) textField.getValue(state);
            if (filterTerm == null) {
                label = new Label("filterTerm is null");
            } else {

                if (filterTerm.isEmpty()) {
                    //wenn keine ID eingegeben
                    label = new Label("bitte korrekte ID eingeben");
                } else {
                    label = new Label(filterTerm);
                }
            }
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public boolean nextRow() {
            index++;
            if (index >= 1) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public Object getElementAt(final int columnIndex) {

//            final Domain domain = domains.get(index);
//            if (columnIndex == 0) {
            return label;
//            } else {
//                return new Label("getElementAtWrongColumnindex");
//            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return 1;
        }

    }

    /*
     * method to check if a string is an Integer 
     *
     * source: http://stackoverflow.com/questions/237159/whats-the-best-way-to-check-to-see-if-a-string-represents-an-integer-in-java
     */
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

}

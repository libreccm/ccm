/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.formbuilder;

import com.arsdigita.formbuilder.util.PersistentContainerHelper;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;

import org.apache.log4j.Logger;

/**
 * This class is responsible for persisting Bebop OptionGroups. The OptionGroup
 * is saved with the save() method. To resurrect the OptionGroup, use the constructor
 * taking the id of the saved OptionGroup and then invoke createComponent().
 *
 * @author Peter Marklund
 * @version $Id: PersistentOptionGroup.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public abstract class PersistentOptionGroup extends PersistentWidget {

    private static final Logger s_log =
        Logger.getLogger(PersistentOptionGroup.class);

    // Just like the PersistentFormSection We delegate the association
    // with the child components to this object
    private PersistentContainerHelper m_container =
        new PersistentContainerHelper(this);

    private static final String OTHER = "optionGroupOther";
    private static final String OTHER_LABEL = "optionGroupOtherLabel";
    private static final String OTHER_WIDTH = "optionGroupOtherWidth";
    private static final String OTHER_HEIGHT = "optionGroupOtherHeight";
    private static final String OTHER_VALUE = "optionGroupOtherValue";

    private static final String COLUMNS = "optionGroupColumns";

    public static final int DEFAULT_COLUMNS = 1;
    public static final int DEFAULT_OTHER_OPTION_WIDTH = 20;
    public static final int DEFAULT_OTHER_OPTION_HEIGHT = 20;

    /**
     * BASE_DATA_OBJECT_TYPE represents the full name of the
     * underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.Widget";

    // *** Constructors -------------

    /**
     * Constructor that creates a new OptionGroup domain object that
     * can be saved to the database later on.
     */
    public PersistentOptionGroup(String typeName) {
        super(typeName);
    }

    public PersistentOptionGroup(ObjectType type) {
        super(type);
    }

    public PersistentOptionGroup(DataObject obj) {
        super(obj);
    }

    /**
     * Extending classes can use this constructor to set the sub class
     * id and object type.
     */
    public PersistentOptionGroup(OID oID)
        throws DataObjectNotFoundException {

        super(oID);
    }

    /*
    protected void beforeDelete() {
        clearOptions();
        super.beforeDelete();
    }
    */

    public Component createComponent() {
        OptionGroup optionGroup = createOptionGroup();

        if( hasOtherOption() ) {
            optionGroup.addOtherOption( getOtherOptionLabel(),
                                        getOtherOptionWidth(),
                                        getOtherOptionHeight() );
        }

        return optionGroup;
    }

    /**
     * Create a sub class of OptionGroup. This method
     * must be implemented by sub clases.
     */
    protected abstract OptionGroup createOptionGroup();

    // *** Public API

    public void addOption(PersistentOption option) {

        m_container.addComponent(option);
    }

    public void addOption(PersistentOption option, boolean selected) {

        m_container.addComponent(option);
        setOptionSelected(option, selected);
    }

    public void addOption(PersistentOption option, int position) {

        m_container.addComponent(option, position);
    }

    public void addOption(PersistentOption option, int position, boolean selected) {

        m_container.addComponent(option, position);
        setOptionSelected(option, selected);
    }

    public void removeOption(PersistentOption option) {

        m_container.removeComponent(option);
    }

    // this is only used for single selection option groups
    private final static String TOO_MANY_OPTIONS_SELECTED =
        "Only one option may be selected by default on this option group.";

    public void setOptionSelected(PersistentOption option, boolean selected) {

        if (!isMultiple()) {
            // only one option may be selected
            // to this selected list better be empty
            Assert.isTrue(getSelectedOptions().size() == 0, TOO_MANY_OPTIONS_SELECTED);
        }

        m_container.setComponentSelected(option, selected);
    }

    public abstract boolean isMultiple();

    /**
     * Will remove the association with the options and also attempt
     * to delete the options themselves.
     */
    public void clearOptions() {
        if( s_log.isDebugEnabled() ) {
            s_log.debug( "Clearing options for " + getID() );
        }

        // We need to fetch the options so that we can delete them too
        DataAssociationCursor options = m_container.getComponents();

        // Delete the options themselves
        while (options.next()) {
            PersistentComponent componentFactory = (PersistentComponent)
                DomainObjectFactory.newInstance( options.getDataObject() );

            if( s_log.isDebugEnabled() ) {
                s_log.debug( "Deleting option " + componentFactory.getID() );
            }

            componentFactory.delete();
        }

        // Delete the association with the options
        //m_container.clearComponents();
    }

    /**
     * Returns an iterator over the PersistentOption objects contained in this
     * option group
     */
    public DataAssociationCursor getOptions() {
        return m_container.getComponents();
    }

    protected DataAssociationCursor getSelectedOptions() {
        DataAssociationCursor options = getOptions();
        options.addEqualsFilter( "link.isSelected", Boolean.TRUE );
        return options;
    }

    //*** Attribute metadata

    /**
     * I am overriding this method since I don't want default value to be
     * supplied on the add/edit form of PersistentOptionGroups
     */
    public AttributeMetaDataList getAttributeMetaData() {

        AttributeMetaDataList list = new AttributeMetaDataList();

        list.add(new AttributeMetaData("parameterName", "HTML parameter name", true)); // required

        return list;
    }

    protected void addDataToComponent(Component component) {

        OptionGroup optionGroup = (OptionGroup)component;

        // Add all options to the option group
        DataAssociationCursor options = getOptions();
        while(options.next()) {
            PersistentOption option = (PersistentOption)
                DomainObjectFactory.newInstance( options.getDataObject() );

            Option bebopOption = (Option) option.createComponent();
            optionGroup.addOption( bebopOption );

            if( Boolean.TRUE.equals( options.getLinkProperty( "isSelected" ) ) ) {
                optionGroup.setOptionSelected( bebopOption );
            }
        }
    }

    public void setHasOtherOption( boolean hasOtherOption ) {
        setComponentAttribute( OTHER,
                               new Boolean( hasOtherOption ).toString() );
        setComponentAttribute( OTHER_VALUE, OptionGroup.OTHER_OPTION );
    }

    public boolean hasOtherOption() {
        return Boolean.TRUE.toString().equals( getComponentAttribute( OTHER ) );
    }

    public void setOtherOptionLabel( String label ) {
        setComponentAttribute( OTHER_LABEL,
                               label );
    }

    public String getOtherOptionLabel() {
        return (String) getComponentAttribute( OTHER_LABEL );
    }

    public void setOtherOptionWidth( int width ) {
        setComponentAttribute( OTHER_WIDTH, new Integer( width ).toString() );
    }

    public int getOtherOptionWidth() {
        String width = getComponentAttribute( OTHER_WIDTH );

        if( StringUtils.emptyString( width ) )
            return DEFAULT_OTHER_OPTION_WIDTH;
        return Integer.valueOf( width ).intValue();
    }

    public void setOtherOptionHeight( int height ) {
        setComponentAttribute( OTHER_HEIGHT, new Integer( height ).toString() );
    }

    public int getOtherOptionHeight() {
        String height = getComponentAttribute( OTHER_HEIGHT );

        if( StringUtils.emptyString( height ) )
            return DEFAULT_OTHER_OPTION_HEIGHT;
        return Integer.valueOf( height ).intValue();
    }

    public void setColumns( int columns ) {
        setComponentAttribute( COLUMNS, new Integer( columns ).toString() );
    }

    public int getColumns() {
        String columns = getComponentAttribute( COLUMNS );

        if( StringUtils.emptyString( columns ) ) return DEFAULT_COLUMNS;
        return Integer.valueOf( columns ).intValue();
    }
}

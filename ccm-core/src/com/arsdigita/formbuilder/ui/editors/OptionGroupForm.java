/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.formbuilder.ui.editors;


import com.arsdigita.formbuilder.util.GlobalizationUtil ; 

import com.arsdigita.formbuilder.PersistentOption;
import com.arsdigita.formbuilder.PersistentOptionGroup;
import com.arsdigita.formbuilder.PersistentWidget;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.BooleanParameter;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

import java.math.BigDecimal;
import java.util.TooManyListenersException;


public abstract class OptionGroupForm extends WidgetLabelForm {
    private OptionGroup m_value;
    private Label m_value_label;

    private RadioGroup m_other;
    private TextField m_otherText;
    private TextField m_otherWidth;
    private TextField m_otherHeight;
    private TextField m_columns;

    private RequestLocal m_group = new RequestLocal();

    private SingleSelectionModel m_control;
    
    public OptionGroupForm(String name,
                           SingleSelectionModel form,
                           SingleSelectionModel control) {
        super(name, form, control);

        m_control = control;
    }

    protected void addWidgets(FormSection section) {
        super.addWidgets(section);

        m_other = new RadioGroup( new ArrayParameter( new BooleanParameter( "other" ) ) );
        m_other.addValidationListener( new NotNullValidationListener() );
        m_other.addOption( new Option( "true", "Yes" ) );
        m_other.addOption( new Option( "false", "No" ) );
        m_other.setOptionSelected( "false" );

        m_otherText = new TextField( new StringParameter( "otherText" ) );

        m_otherWidth = new TextField( new IntegerParameter( "otherWidth" ) );
        m_otherWidth.setSize( 3 );

        m_otherHeight = new TextField( new IntegerParameter( "otherHeight" ) );
        m_otherHeight.setSize( 3 );

        m_columns = new TextField( new IntegerParameter( "columns" ) );
        m_columns.setSize( 3 );

        section.add( new Label( "\"Other\" option" ) );
        section.add( m_other );
        section.add( new Label( "\"Other\" option label" ) );
        section.add( m_otherText );
        section.add( new Label( "\"Other\" option field width" ) );
        section.add( m_otherWidth );
        section.add( new Label( "\"Other\" option field height" ) );
        section.add( m_otherHeight );
        
        section.add( new Label( "Display columns" ) );
        section.add( m_columns );

        m_value = getOptionGroup("value");
        m_value_label = new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.value"));

        section.add(m_value_label, ColumnPanel.RIGHT);
        section.add(m_value);
        try {
            m_value.addPrintListener(new ControlFormPrintListener());
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("This will never happen", ex);
        }
    }

    protected abstract OptionGroup getOptionGroup(String name);

    private PersistentOptionGroup getGroup( PageState ps ) {
        PersistentOptionGroup group = (PersistentOptionGroup) m_group.get( ps );
        if( null != group ) return group;

        BigDecimal control = (BigDecimal)m_control.getSelectedKey(ps);
        if (control != null) {
            group = (PersistentOptionGroup)getWidget(control);
        }

        m_group.set( ps, group );
        return group;
    }

    public void generateXML(PageState state,
                            Element parent) {
        PersistentOptionGroup group = getGroup( state );

        boolean widgetsVisible = (group == null) ? false : !group.getOptions().isEmpty();

        m_value.setVisible(state, widgetsVisible);
        m_value_label.setVisible(state, widgetsVisible);

        super.generateXML(state, parent);
    }

    protected void initWidgets(FormSectionEvent e,
                               PersistentWidget w)
        throws FormProcessException {
        super.initWidgets(e, w);

        PageState ps = e.getPageState();
        PersistentOptionGroup group = getGroup( ps );

        if( null == group ) return;

        if( group.hasOtherOption() ) {
            m_other.setValue( ps, Boolean.TRUE );
        }

        m_otherText.setValue( ps, group.getOtherOptionLabel() );
        m_otherWidth.setValue( ps, new Integer( group.getOtherOptionWidth() ) );
        m_otherHeight.setValue( ps, new Integer( group.getOtherOptionHeight() ) );
        m_columns.setValue( ps, new Integer( group.getColumns() ) );
    }

    protected void processWidgets(FormSectionEvent e,
                                  PersistentWidget w)
        throws FormProcessException {
        super.processWidgets(e, w);

        PageState ps = e.getPageState();

        Boolean otherOption = ((Boolean[]) m_other.getValue( ps ))[0];

        PersistentOptionGroup group = (PersistentOptionGroup) w;
        group.setHasOtherOption( otherOption.booleanValue() );

        String label = (String) m_otherText.getValue( ps );

        Integer widthI = (Integer) m_otherWidth.getValue( ps );
        int width = ( widthI == null ) ?
            PersistentOptionGroup.DEFAULT_OTHER_OPTION_WIDTH : widthI.intValue();

        Integer heightI = (Integer) m_otherHeight.getValue( ps );
        int height = ( heightI == null ) ?
            PersistentOptionGroup.DEFAULT_OTHER_OPTION_HEIGHT : heightI.intValue();

        Integer columnsI = (Integer) m_columns.getValue( ps );
        int columns = ( columnsI == null ) ? 
            PersistentOptionGroup.DEFAULT_COLUMNS : columnsI.intValue();

        group.setOtherOptionLabel( label );
        group.setOtherOptionWidth( width );
        group.setOtherOptionHeight( height );
        group.setColumns( columns );
    }

    private class ControlFormPrintListener implements PrintListener {
        public void prepare(PrintEvent e) {
            PersistentOptionGroup group = getGroup( e.getPageState() );
            if( null == group ) return;

            OptionGroup grp = (OptionGroup)e.getTarget();

            DataAssociationCursor options = group.getOptions();
            while (options.next()) {
                PersistentOption o = (PersistentOption)
                    DomainObjectFactory.newInstance( options.getDataObject() );

                grp.addOption(new Option(o.getParameterValue(), o.getLabel()));
            }
        }
    }
}

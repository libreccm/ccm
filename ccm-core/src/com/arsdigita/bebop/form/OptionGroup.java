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
package com.arsdigita.bebop.form;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.ParameterModelWrapper;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

import java.util.Iterator;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.log4j.Logger;

/**
 * A class representing any widget that contains a list options.
 *   
 *    @author Karl Goldstein 
 *    @author Uday Mathur    
 *    @author Rory Solomon   
 *    @author Michael Pih    
 *    @version $Id: OptionGroup.java 738 2005-09-01 12:36:52Z sskracic $ */
public abstract class OptionGroup extends Widget
    implements BebopConstants {

    private static final Logger s_log = Logger.getLogger( OptionGroup.class );

    /**
     * The XML element to be used by individual options belonging to this
     * group. This variable has to be initialized by every subclass of
     * OptionGroup.
     * LEGACY: An abstract method would be the better design, but changing it
     * would break the API.  */
    protected String m_xmlElement;

    // this only needs to be an ArrayList for multiple selection option groups
    private ArrayList m_selected;

    private ArrayList m_options;

    private Widget m_otherOption = null;

    private Form m_form = null;
    private boolean m_isDisabled = false;
    private boolean m_isReadOnly = false;

    public static final String OTHER_OPTION = "__other__";

    // request-local copy of selected elements, options
    private RequestLocal m_requestOptions = new RequestLocal() {
        @Override
            public Object initialValue(PageState ps) {
                return new ArrayList();
            }
        };

    public final boolean isCompound() {
        return true;
    }

    // this is only used for single selection option groups
    private final static String TOO_MANY_OPTIONS_SELECTED =
        "Only one option may be selected by default on this option group.";

    /** The ParameterModel for mutliple OptionGroups is always an array
     *  parameter */
    protected OptionGroup(ParameterModel model) {
        super(model);
        m_options = new ArrayList();
        m_selected = new ArrayList();
    }

    /**
     *      Returns an Iterator of all the default Options in this group.
     */
    public Iterator getOptions() {
        return m_options.iterator();
    }

    /**
     * Returns an Iterator of all the default Options in this group,
     * plus any request-specific options.
     */
    public Iterator getOptions(PageState ps) {
        ArrayList allOptions = new ArrayList();
        allOptions.addAll(m_options);
        ArrayList requestOptions = (ArrayList)m_requestOptions.get(ps);
        for (Iterator i = requestOptions.iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (!allOptions.contains(obj)) {
                allOptions.add(obj);
            }
        }
        return allOptions.iterator();
    }

    public void clearOptions() {
        Assert.isUnlocked(this);
        m_options = new ArrayList();
    }

    /**
     * Adds a new option.
     * @param opt The {@link Option} to be added.  Note: the argument
     * is modified and associated with this OptionGroup, regardless of
     * what its group was.
     */
    public void addOption(Option opt) {
        addOption(opt, null);
    }

    public void removeOption(Option opt) {
        removeOption(opt, null);
    }

    /**
     * Adds a new option for the scope of the current request, or
     * to the page as a whole if there is no current request.
     *
     * @param opt The {@link Option} to be added.  Note: the argument
     * is modified and associated with this OptionGroup, regardless of
     * what its group was.
     * @param ps the current page state.  if ps is null, adds option to the
     * default option list.
     */
    public void addOption(Option opt, PageState ps) {
        ArrayList list = m_options;
        if (ps == null) {
            Assert.isUnlocked(this);
        } else {
            list = (ArrayList)m_requestOptions.get(ps);
        }
        opt.setGroup( this );
        list.add(opt);
    }


    public void removeOption(Option opt, PageState ps) {
        ArrayList list = m_options;
        if (ps == null) {
            Assert.isUnlocked(this);
        } else {
            list = (ArrayList)m_requestOptions.get(ps);
        }
        list.remove(opt);
    }

    public void removeOption(String key) {
        removeOption(key, null);
    }

    /**
     * Removes the first option whose key is isEqual
     * to the key that is passed in.
     */
    public void removeOption(String key, PageState ps) {
        // This is not an entirely efficient technique. A more
        // efficient solution is to switch to using a HashMap.
        ArrayList list = m_options;
        if (ps == null) {
            Assert.isUnlocked(this);
        } else {
            list = (ArrayList)m_requestOptions.get(ps);
        }

        Iterator i = list.iterator();
        Option o = null;
        while ( i.hasNext() ) {
            o = (Option) i.next();
            if ( o.getValue().equals(key) ) {
                list.remove(o);
                break;
            }
        }

    }

    /**
     * Add an "Other (please specify)" type option to the widget
     *
     * @param hasOtherOption true is the widget has an "Other" option
     * @param width The width, in characters, of the "Other" entry area
     * @param height The height, in characters, of the "Other" entry area. If
     *  this is 1 then a TextField is used. Otherwise a TextArea is used.
     */
    public void addOtherOption( String label, int width, int height ) {
        Assert.isUnlocked(this);

        Option otherOption = new Option( OTHER_OPTION, label );
        addOption( otherOption );

        final ParameterModel model = getParameterModel();

        if( 1 == height ) {
            TextField field =
                new TextField( model.getName() + ".other" );
            field.setSize( width );

            m_otherOption = field;
        } else {
            TextArea area =
                new TextArea( model.getName() + ".other" );
            area.setCols( width );
            area.setRows( height );

            m_otherOption = area;
        }

        if( null != m_form ) {
            m_otherOption.setForm( m_form );

            if( m_isDisabled ) {
                m_otherOption.setDisabled();
            }
            if( m_isReadOnly ) {
                m_otherOption.setReadOnly();
            }
        }

        setParameterModel( new ParameterModelWrapper( model ) {
            @Override
            public ParameterData createParameterData( final HttpServletRequest request,
                                                      Object defaultValue,
                                                      boolean isSubmission ) {

                final String[] values =
                    request.getParameterValues( getName() );
                String[] otherValues =
                    request.getParameterValues( getName() + ".other" );

                String other = ( null == otherValues ) ? null : otherValues[0];

                if( null != values ) {
                    for( int i = 0; i < values.length; i++ ) {
                        if( OTHER_OPTION.equals( values[i] ) ) {
                            values[i] = other;
                        }
                    }
                }

                s_log.debug( "createParameterData in OptionGroup" );

                return super.createParameterData( new HttpServletRequestWrapper( request ) {
                    @Override
                    public String[] getParameterValues( String key ) {
                        if( s_log.isDebugEnabled() ) {
                            s_log.debug( "Getting values for " + key );
                        }

                        if( model.getName().equals( key ) ) {
                            return values;
                        }
                        return super.getParameterValues( key );
                    }
                }, defaultValue, isSubmission );
            }

            private void replaceOther( String[] values, String other ) {
            }
        } );
    }

    /** Make an option selected by default.  Updates the parameter
     *  model for the option group accordingly.
     *  @param value the value of the option to be added to the
     *  by-default-selected set.  */
    public void setOptionSelected(String value) {
        Assert.isUnlocked(this);
        if (!isMultiple()) {
            // only one option may be selected
            // to this selected list better be empty
            Assert.isTrue(m_selected.size() == 0, TOO_MANY_OPTIONS_SELECTED);
            m_selected.add(value);
            getParameterModel().setDefaultValue( value );
        } else {
            m_selected.add(value);
            getParameterModel().setDefaultValue( m_selected.toArray() );
        }
    }

    /** make an option selected by default
     *  @param option the option to be added to the by-default-selected set.
     */
    public void setOptionSelected(Option option) {
        setOptionSelected(option.getValue());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        OptionGroup cloned = (OptionGroup)super.clone();
        cloned.m_options = (ArrayList) m_options.clone();
        cloned.m_selected =
            (ArrayList) m_selected.clone();
        return cloned;
    }

    /**
     * Is this a multiple (and not single) selection option group?
     * Note that this should really be declared abstract, but we can't
     * because it used to be in the direct subclass Select and making
     * it abstract could break other subclasses that don't declare
     * isMultiple.  So we have a trivial implementation instead.
     *
     * @return true if this OptionGroup can have more than one
     * selected option; false otherwise.
     */
    public boolean isMultiple() {
        return true;
    }

    @Override
    public void setDisabled() {
        m_isDisabled = true;

        if( null != m_otherOption ) {
            m_otherOption.setDisabled();
        }

        super.setDisabled();
    }

    @Override
    public void setReadOnly() {
        m_isReadOnly = true;

        if( null != m_otherOption ) {
            m_otherOption.setReadOnly();
        }

        super.setReadOnly();
    }

    @Override
    public void setForm( Form form ) {
        m_form = form;
        if( null != m_otherOption ) {
            m_otherOption.setForm(form);
        }

        super.setForm( form );
    }

    /**
     * Generates the DOM for the select widget
     * <p>Generates DOM fragment:
     * <p><pre><code>&lt;bebop:* name=... [onXXX=...]&gt;
     *   &lt;bebop:option name=... [selected]&gt; option value &lt;/bebop:option%gt;
     * ...
     * &lt;/bebop:*select&gt;</code></pre>
     */
    @Override
    public void generateWidget( PageState state, Element parent ) {
        Element optionGroup =
            parent.newChildElement( getElementTag(), BEBOP_XML_NS );
        optionGroup.addAttribute( "name", getName() );
        if ( isMultiple() ) {
            optionGroup.addAttribute( "multiple", "multiple" );
        }
        exportAttributes( optionGroup );

        for ( Iterator i = getOptions(state); i.hasNext(); ) {
            Option o = (Option) i.next();
            o.generateXML( state, optionGroup );
        }

        if( null != m_otherOption ) {
            m_otherOption.generateXML(state, optionGroup);
        }
    }
}

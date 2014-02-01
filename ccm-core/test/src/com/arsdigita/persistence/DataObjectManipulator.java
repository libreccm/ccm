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
package com.arsdigita.persistence;

import com.arsdigita.db.DbHelper;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.CheckedWrapperException;
import com.arsdigita.util.StringUtils;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.Column;
import com.redhat.persistence.metadata.Mapping;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.metadata.Value;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 * DataObjectManipulator
 *
 * @author <a href="mailto:jorris@arsdigita.com"Jon Orris</a>
 * @version $Revision: #13 $ $Date: 2004/08/16 $
 */
public class DataObjectManipulator {

    
    private static final Logger s_log =
            Logger.getLogger(DataObjectManipulator.class.getName());

    private static final Column getColumn(Property p) {
        Root root = SessionManager.getSession().getMetadataRoot().getRoot();
        ObjectMap om = root.getObjectMap
                (root.getObjectType(p.getContainer().getQualifiedName()));
        Mapping m = om.getMapping(Path.get(p.getName()));
        if (m instanceof Value) {
            return ((Value) m).getColumn();
        } else {
            return null;
        }
    }

    public DataObjectManipulator() {
        makeManipulators();

    }

    public void setDefaultProperty(Property p, DataObject data) throws Exception {
        SimpleTypeManipulator manip = getManipulator(p);
        manip.setDefaultProperty( p, data );

    }

    public SimpleTypeManipulator getManipulator(Property p) {
        SimpleTypeManipulator manip = (SimpleTypeManipulator) m_manipulators.get(p.getJavaClass());
        if( null == manip ) {
            throw new IllegalArgumentException("Unsupported java type: " + p.getJavaClass() + " for Property " + p.getName());
        }
        else {
            return manip;
        }

    }

    public void updateAllPropertyCombinations(Property p, DataObject data) throws Exception {
        DataObjectManipulator.SimpleTypeManipulator manip = getManipulator(p);
        manip.updateAllPropertyCombinations(p, data);
    }


    static Map s_defaults = new HashMap();
    static {
        s_defaults.put( java.math.BigInteger.class, BigInteger.ONE );
        s_defaults.put( java.math.BigDecimal.class, new BigDecimal("1") );
        s_defaults.put( java.lang.Boolean.class, Boolean.TRUE );
        s_defaults.put( java.lang.Byte.class, new Byte((byte) 1) );
        s_defaults.put( java.lang.Character.class, new Character('A') );
        s_defaults.put( java.util.Date.class, new Date() );
        s_defaults.put( java.lang.Double.class, new Double(37.45) );
        s_defaults.put( java.lang.Float.class, new Float(92.1f) );
        s_defaults.put( java.lang.Integer.class, new Integer(42) );
        s_defaults.put( java.lang.Long.class, new Long(500000) );
        s_defaults.put( java.lang.Short.class, new Short((short) 57 ) );
        s_defaults.put( java.lang.String.class, "ArsDigita Corporation" );
        s_defaults.put( byte[].class, null );
        s_defaults.put( java.sql.Clob.class, "Kinda long string to represent a CLOB for database insertion" );
    }



    public abstract class SimpleTypeManipulator {
        // The class that the subtype gets bound to. When calling against a Property
        // object, the precondition Property.getJavaClass().equals(m_propertyClass)
        // must hold.
        final Class m_propertyClass;
        /* This is the default  value for a given property type
        * @invariant m_default.getClass().equals(m_propertyClass)
        */
        final Object m_default;
        // List of variants for the property type. All are of type m_propertyClass
        Collection m_variants;

        SimpleTypeManipulator(Class propertyClass) {
            m_propertyClass = propertyClass;
            m_default = s_defaults.get(propertyClass);
            m_manipulators.put( propertyClass, this );
            makeVariants();
        }

        public void assertPropertyClass(Property p) {
            final boolean classesDiffer = !m_propertyClass.equals(p.getJavaClass());
            if(classesDiffer) {
                String msg = "Property: " + p.getName();
                msg += " has a different JavaClass! Expecting " + m_propertyClass;
                msg += " but property is " + p.getJavaClass();
                throw new IllegalArgumentException(msg);
            }
        }
        /**
         *  Abstract method that creates and populates the m_variants collection.
         *  Implemented for each Manipulator subtype.
         *
         *  @post getVariantValues() != null
         */
        abstract void makeVariants();

        /**
         *  Returns the Class to which this manipulator is mapped.
         */
        Class getPropertyClass()  {
            return m_propertyClass;
        }

        void setDefaultProperty(Property p, DataObject data) {
            assertPropertyClass(p);
            data.set( p.getName(), getDefaultValue());
        }


        /**
         *  Gets the default value for the propertyClass.
         *
         *  @post getDefaultValue() == null || getDefaultValue().getClass().equals(getPropertyClass())
         */
        final Object getDefaultValue() {
            return m_default;
        }

        Collection getVariantValues() {
            return m_variants;
        }


        /**
         *  This method checks to see if an error that resulted from setting a property
         *  and saving a data object was valid or not. For example, passing null to a
         *  non-null property is valid.
         *  If the error was not valid, the Exception parameter is rethrown.
         *  The default implementation logs the error and rethrows it.
         *
         *  @param t The exception that resulted from the invalid update.
         *  @param p The Property that was being updated.
         *  @param data The DataObject that was being saved.
         *  @param value The value that the property was being set to.
         */
        public void checkSetError(Exception t, Property p, DataObject data, Object value) throws Exception {
            logSetError(p, value);
            s_log.debug("Default checkSetError");
            throw new CheckedWrapperException(t);
        }

        /**
         *  Checks to see if the value that should have been set is the same as the
         *  value retrieved from the database. If not, an exception is thrown.
         *
         *  @param propertyName The name of the property that was updated.
         *  @param inMemoryValue The value that the property was set to.
         *  @param fromDatabaseValue The value of the property fetched from the database
         *      after saving.
         */
        public void checkEquals( String propertyName, Object inMemoryValue, Object fromDatabaseValue ) throws Exception {
            if (bothAreNonNull(propertyName, inMemoryValue, fromDatabaseValue)) {
                if( !inMemoryValue.equals(fromDatabaseValue) ) {
                    equalsError(propertyName, inMemoryValue, fromDatabaseValue);
                }
            }
        }

        final boolean bothAreNonNull( String propertyName, Object inMemoryValue, Object fromDatabaseValue) throws Exception {
            if( inMemoryValue == null && fromDatabaseValue == null ) {
                return false;
            }
            final boolean onlyOneIsNull = ( inMemoryValue == null || fromDatabaseValue == null );
            if( onlyOneIsNull ) {
                equalsError(propertyName, inMemoryValue, fromDatabaseValue);
            }
            return true;
        }

        protected void equalsError( String propertyName, Object inMemoryValue, Object fromDatabaseValue ) throws Exception {
            String msg = "Property " + propertyName + " not correctly updated! ";
            msg += " in memory: " + inMemoryValue;
            msg += " db value: " + fromDatabaseValue;
            throw new Exception(msg);
        }

        /**
         *  Logs the fact that a Property failed to update. If the column is specified,
         *  this is also logged. The attemted update value is also logged.
         *
         *  @param p The property that failed to update.
         *  @param value The value that was used to update the property.
         */
        void logSetError(Property p, Object value) {

            s_log.debug("Failed to set property " + p.getName());
            final boolean columnIsSpecified =  getColumn(p) != null;

            if( columnIsSpecified ) {
                s_log.debug("Bound to column " + getColumn(p).getQualifiedName());
            }
            else {
                s_log.debug("Column is not specified for property");
            }
            s_log.debug("New Value is: " + value);

        }

        /**
         *  Iterates over all of the variantValues for the given Property and
         *  attempts to update the property to that value. If an invalid failure
         *  to update occurs, an exception is thrown.
         *
         *  @param p The property to update.
         *  @param data The DataObject to save.
         *
         *  @pre p.equals(data.getObjectType.getProperty(p.getName()))
         */
        public void updateAllPropertyCombinations(Property p, DataObject data) throws Exception {
            OID id = data.getOID();
            s_log.info("Property " + p.getName() + " class is: " + p.getJavaClass());
            // Verify that nulls are handled correctly for all columns
            setProperty(p, data, null);
            data = SessionManager.getSession().retrieve(id);

            Iterator iter = getVariantValues().iterator();
            while(iter.hasNext()) {
                Object value = iter.next();
                setProperty(p, data, value);
                // It is neccessary to re-fetch the DataObject, since some 'failed' updates
                // will not cause the test to fail, but will leave the DataObject in an inconsistent state.
                // These are proper update failures, such as an oversized default String value being sent
                // to a constrained column.
                data = SessionManager.getSession().retrieve(id);
            }
        }

        /**
         *  Attempts to update the Property to a given value. If an invalid failure
         *  to update occurs, an exception is thrown. If the update appears successful,
         *  the DataObject is re-fetched from the database, and the property value compared
         *  to what it should be. If they differ, an exception is thrown.
         *
         *  @param p The property to update.
         *  @param data The DataObject to save.
         *  @param value The value to update the property with.
         *
         *  @pre p.equals(data.getObjectType.getProperty(p.getName()))
         */
        void setProperty(Property p, DataObject data, Object value) throws Exception {
            final String propName = p.getName();
            OID id = data.getOID();
            s_log.debug("setting property : " + data.getObjectType().getQualifiedName() + "." + data.getObjectType().getName() + "." + p.getName());// + " to value: " + value);
            s_log.debug("Old value was: " + data.get(p.getName()));
            final boolean valueIsNull = ( value == null || value instanceof String && ((String) value).length() == 0);
            boolean savedNullInRequiredField = false;
            try {
                data.set( p.getName(), value );
                data.save();
                // This is neccessary since fail cannot be called here.
                // Its exception would be caught. Need to check this
                // value after the catch block
                savedNullInRequiredField = ( p.isRequired() && valueIsNull );
            } catch (Exception t) {
                if( p.isRequired() && valueIsNull ) {
                    return;
                }
                if( p.isNullable() && valueIsNull ) {
                    String msg = "Failed to save DataObject: " +  data.getObjectType().getName();
                    msg += "\nTried to update nullable property " + p.getName();
                    msg += " with null value and failed!";
                    msg += "\nException is: " + t.getMessage();
                    s_log.debug(msg);
                    throw new CheckedWrapperException(msg, t);
                }
                else {
                    s_log.debug("Failed to set property " + p.getName());
                    s_log.debug("Checking error");
                    checkSetError(t, p, data, value);
                    //                    if (DbHelper.getDatabase(getSession().getConnection()) == DbHelper.DB_POSTGRES) {
                    throw new AbortMetaTestException();
                    //                    } else {
                    //                        return;
                    //                    }
                }
            }

            if (savedNullInRequiredField) {
                String msg = "DataObject saved null value in a required Property: " + p.getName();
                s_log.debug(msg);
                throw new Exception(msg);
            }
            DataObject fromDatabase = SessionManager.getSession().retrieve(id);
            Object newValue = fromDatabase.get(p.getName());
            checkEquals( p.getName(), value, newValue );

        }

    }  // end SimpleTypeManipulator


    private void makeManipulators() {
        SimpleTypeManipulator manip;

        manip = new SimpleTypeManipulator(java.math.BigInteger.class) {
            void makeVariants() {
                m_variants = new LinkedList();
                m_variants.add(new BigInteger(Integer.toString(Integer.MIN_VALUE)));
                m_variants.add(new BigInteger(Integer.toString(Integer.MIN_VALUE + 1)));
                m_variants.add(BigInteger.ZERO);
                m_variants.add(new BigInteger(Integer.toString(Integer.MAX_VALUE - 1)));
                m_variants.add(new BigInteger(Integer.toString(Integer.MAX_VALUE)));

            }
        };


        manip = new SimpleTypeManipulator(java.math.BigDecimal.class) {
            void makeVariants() {
                m_variants = new LinkedList();
                m_variants.add(new BigDecimal(new BigInteger(Integer.toString(Integer.MIN_VALUE))));
                m_variants.add(new BigDecimal(new BigInteger(Integer.toString(Integer.MIN_VALUE + 1))));
                m_variants.add(new BigDecimal(BigInteger.ZERO));
                m_variants.add(new BigDecimal(new BigInteger(Integer.toString(Integer.MAX_VALUE - 1))));
                m_variants.add(new BigDecimal(new BigInteger(Integer.toString(Integer.MAX_VALUE))));



            }
        };


        manip = new SimpleTypeManipulator(java.lang.Boolean.class) {
            void makeVariants() {
                m_variants = new LinkedList();
                m_variants.add(Boolean.FALSE);
                m_variants.add(Boolean.TRUE);
            }
        };


        manip = new SimpleTypeManipulator(java.lang.Byte.class) {
            void makeVariants() {
                m_variants = new LinkedList();
                m_variants.add(new Byte(Byte.MIN_VALUE));
                m_variants.add(new Byte((byte)(Byte.MIN_VALUE + 1)));
                m_variants.add(new Byte((byte)0));
                m_variants.add(new Byte((byte)(Byte.MAX_VALUE - 1)));
                m_variants.add(new Byte(Byte.MAX_VALUE));

            }
        };


        manip = new SimpleTypeManipulator(java.lang.Character.class) {
            void makeVariants() {
                m_variants = new LinkedList();
                if (DbHelper.getDatabase(SessionManager.getSession().getConnection())
                        != DbHelper.DB_POSTGRES) {
                    // pg jdbc seems to barf on this
                    m_variants.add(new Character(Character.MIN_VALUE));
                }
                m_variants.add(new Character
                        ((char) (Character.MIN_VALUE + 1)));
                m_variants.add(new Character('a'));
                m_variants.add(new Character('b'));
                m_variants.add(new Character('c'));
                m_variants.add(new Character('x'));
                m_variants.add(new Character('y'));
                m_variants.add(new Character('z'));

            }
        };


        manip = new SimpleTypeManipulator(java.util.Date.class) {
            void makeVariants() {
                m_variants = new LinkedList();
                m_variants.add(new Date());

                Calendar cal = Calendar.getInstance();
                cal.set(2000, 0, 1);
                m_variants.add(cal.getTime());
                cal.set(1999, 11, 31);
                m_variants.add(cal.getTime());
                cal.set(2000, 2, 29);
                m_variants.add(cal.getTime());

            }

            /**
             *  Checks to see if the value that should have been set is the same as the
             *  value retrieved from the database. If not, an exception is thrown.
             *
             *  @param propertyName The name of the property that was updated.
             *  @param inMemoryValue The value that the property was set to.
             *  @param fromDatabaseValue The value of the property fetched from the database
             *      after saving.
             */
            public void checkEquals( String propertyName, Object inMemoryValue, Object fromDatabaseValue ) throws Exception {
                if( inMemoryValue == null && fromDatabaseValue == null ) {
                    return;
                }
                final boolean onlyOneIsNull = ( inMemoryValue == null || fromDatabaseValue == null );
                if( onlyOneIsNull ) {
                    equalsError( propertyName, inMemoryValue, fromDatabaseValue);
                }
                else {
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime((Date) inMemoryValue);
                    cal1.set(Calendar.MILLISECOND, 0);
                    Date first = cal1.getTime();

                    Calendar cal2 = Calendar.getInstance();
                    cal2.setTime((Date) fromDatabaseValue);
                    cal2.set(Calendar.MILLISECOND, 0);
                    Date second = cal2.getTime();

                    if( !first.equals(second) ) {
                        equalsError( propertyName, inMemoryValue, fromDatabaseValue);
                    }
                }

            }

        };


        manip = new SimpleTypeManipulator(java.lang.Double.class) {
            void makeVariants() {
                m_variants = new LinkedList();
                m_variants.add(new Double("1.0E-130"));
                m_variants.add(new Double(-300.92199));
                m_variants.add(new Double(-16.60));
                m_variants.add(new Double(0));
                m_variants.add(new Double(300.92199));
                m_variants.add(new Double(16.60));
                m_variants.add(new Double("9.99E125"));

            }
            public void checkEquals( String propertyName, Object inMemoryValue, Object fromDatabaseValue ) throws Exception {
                if( null == inMemoryValue && null == fromDatabaseValue ) {
                    return;
                }
                final boolean onlyOneIsNull = ( null == inMemoryValue || null == fromDatabaseValue );
                if( onlyOneIsNull ) {
                    equalsError( propertyName, inMemoryValue, fromDatabaseValue);
                }

                Double memoryDouble = (Double) inMemoryValue;
                Double databaseDouble = (Double) fromDatabaseValue;

                if (Double.doubleToLongBits(memoryDouble.doubleValue()) != Double.doubleToLongBits(databaseDouble.doubleValue())) {
                    equalsError( propertyName, inMemoryValue, fromDatabaseValue);
                }
            }
        };


        manip = new SimpleTypeManipulator(java.lang.Float.class) {
            void makeVariants() {
                m_variants = new LinkedList();
                m_variants.add(new Float("1.0E-130"));
                m_variants.add(new Float(-300.92199f));
                m_variants.add(new Float(-16.60f));
                m_variants.add(new Float(0));
                m_variants.add(new Float(300.92199f));
                m_variants.add(new Float(16.60f));
                m_variants.add(new Float("9.99E10"));


            }

            public void checkEquals( String propertyName, Object inMemoryValue, Object fromDatabaseValue ) throws Exception {
                if( null == inMemoryValue && null == fromDatabaseValue ) {
                    return;
                }
                final boolean onlyOneIsNull = ( null == inMemoryValue || null == fromDatabaseValue );
                if( onlyOneIsNull ) {
                    equalsError( propertyName, inMemoryValue, fromDatabaseValue);
                }

                Float memoryFloat = (Float) inMemoryValue;
                Float databaseFloat = (Float) fromDatabaseValue;

                if (Float.floatToIntBits(memoryFloat.floatValue()) != Float.floatToIntBits(databaseFloat.floatValue())) {
                    equalsError( propertyName, inMemoryValue, fromDatabaseValue);
                }
            }

        };


        manip = new SimpleTypeManipulator(java.lang.Integer.class) {
            void makeVariants() {
                m_variants = new LinkedList();
                m_variants.add(new Integer(Integer.MIN_VALUE));
                m_variants.add(new Integer(Integer.MIN_VALUE + 1));
                m_variants.add(new Integer(0));
                m_variants.add(new Integer(Integer.MAX_VALUE - 1));
                m_variants.add(new Integer(Integer.MAX_VALUE));

            }
        };


        manip = new SimpleTypeManipulator(java.lang.Long.class) {
            void makeVariants() {
                m_variants = new LinkedList();
                m_variants.add(new Long(Long.MIN_VALUE));
                m_variants.add(new Long(Long.MIN_VALUE + 1));
                m_variants.add(new Long(0));
                m_variants.add(new Long(Long.MAX_VALUE - 1));
                m_variants.add(new Long(Long.MAX_VALUE));

            }
        };


        manip = new SimpleTypeManipulator(java.lang.Short.class) {
            void makeVariants() {
                m_variants = new LinkedList();
                m_variants.add(new Short(Short.MIN_VALUE));
                m_variants.add(new Short((short)(Short.MIN_VALUE + 1)));
                m_variants.add(new Short((short)0));
                m_variants.add(new Short((short)(Short.MAX_VALUE - 1)));
                m_variants.add(new Short(Short.MAX_VALUE));

            }
        };


        manip = new SimpleTypeManipulator(java.lang.String.class) {
            void makeVariants() {
                m_variants = new LinkedList();
                m_variants.add("FooBar");
                m_variants.add("");
                m_variants.add(repeatChar('a', 5));
            }
            /**
             *  Iterates over all of the variantValues for the given Property and
             *  attempts to update the property to that value. If an invalid failure
             *  to update occurs, an exception is thrown.
             *
             *  @param p The property to update.
             *  @param data The DataObject to save.
             *
             *  @pre p.equals(data.getObjectType.getProperty(p.getName()))
             */
            public void updateAllPropertyCombinations(Property p, DataObject data) throws Exception {
                super.updateAllPropertyCombinations(p, data);
                Column column = getColumn(p);
                // Verify that the boundary cases for column size are correctly handled.
                if (column != null && column.getSize() > 0) {
                    try {
                        OID id = data.getOID();
                        final int size = column.getSize();
                        String justBelowBoundary = repeatChar('X', size - 1);
                        setProperty(p, data, justBelowBoundary);
                        data = SessionManager.getSession().retrieve(id);

                        String atBoundary = repeatChar('X', size);
                        setProperty(p, data, atBoundary);
                        data = SessionManager.getSession().retrieve(id);

                        if (DbHelper.getDatabase
                                (SessionManager.getSession().getConnection())
                                == DbHelper.DB_POSTGRES) {
                            return;
                        }

                        String justAboveBoundary = repeatChar('X', size + 1);
                        setProperty(p, data, justAboveBoundary);
                        data = SessionManager.getSession().retrieve(id);

                    } catch (Exception e) {
                        s_log.debug("Failure to update string property combinations for property: " + p.getName());
                        s_log.debug("Column: " + column.getQualifiedName() + " Size: " + column.getSize());
                        throw e;
                    }
                }
            }
            /**
             *  This method checks to see if an error that resulted from setting a property
             *  and saving a data object was valid or not. For example, passing null to a
             *  non-null property is valid.
             *  If the error was not valid, the Exception parameter is rethrown.
             *  This implementation checks for special errors that can occur with String
             *  Properties.
             *
             *  @param t The exception that resulted from the invalid update.
             *  @param p The Property that was being updated.
             *  @param data The DataObject that was being saved.
             *  @param value The value that the property was being set to.
             */
            public void checkSetError(Exception t, Property p, DataObject data, Object value) throws Exception {


                s_log.debug("checkSetError for Strings!");
                final boolean columnIsSpecified =  getColumn(p) != null;
                final boolean isColumnSizeIssue = (t.getMessage().indexOf("inserted value too large") != -1) ||
                        (t.getMessage().indexOf("can bind a LONG value only") != -1) ||
                        (t.getMessage().indexOf("value too long for type") != -1);

                if( isColumnSizeIssue ) {
                    if( p.getType().equals(MetadataRoot.STRING) ) {

                        String stringValue = (String) value;
                        s_log.debug("Value was too large for column. Size: " + stringValue.length());


                        if( columnIsSpecified ) {
                            final int size = getColumn(p).getSize();
                            if( stringValue.length() <= size ) {

                                String msg = "Column length appears to be invalid! Lengh is " + size;
                                s_log.debug(msg);
                                logSetError(p, value);
                                throw new CheckedWrapperException(msg, t);
                            }
                        } else {
                            s_log.debug("Column does not have size specified in the PDL, so it is not possible to determine if this is an error");

                        }
                    }

                } else {
                    logSetError(p, value);
                    throw new CheckedWrapperException(t);
                }

            }

            /**
             *  Checks to see if the value that should have been set is the same as the
             *  value retrieved from the database. If not, an exception is thrown.
             *
             *  @param propertyName The name of the property that was updated.
             *  @param inMemoryValue The value that the property was set to.
             *  @param fromDatabaseValue The value of the property fetched from the database
             *      after saving.
             */
            public void checkEquals( String propertyName, Object inMemoryValue, Object fromDatabaseValue ) throws Exception {

                final boolean bothStringsAreNull =
                        StringUtils.emptyString(inMemoryValue) && StringUtils.emptyString(fromDatabaseValue);
                if( bothStringsAreNull == false ) {
                    super.checkEquals(propertyName, inMemoryValue, fromDatabaseValue);
                }
            }

        };


        manip = new SimpleTypeManipulator(byte[].class) {
            void makeVariants() {
                m_variants = new LinkedList();
            }
        };


        manip = new SimpleTypeManipulator(java.sql.Clob.class) {
            void makeVariants() {
                m_variants = new LinkedList();
                m_variants.add("");
                m_variants.add(repeatChar('a', 500));
                m_variants.add(repeatChar('a', 5000));
                m_variants.add(repeatChar('a', 50000));

            }
        };


    }

    /**
     *  Makes a String consisting of a given character repated a set number of times.
     *  Possibly should move to StringUtils.
     *
     *  @param c The character to repeat.
     *  @param numRepetitions The number of times to repeat the character.
     *  @return The new String
     *
     *  @pre numRepetitions > 0
     *  @post $result.length() == numRepetitions
     */
    static String repeatChar(final char c, final int numRepetitions) {
        StringBuffer sb = new StringBuffer(numRepetitions);
        for(int count = 0; count < numRepetitions; count++) {
            sb.append(c);
        }

        return sb.toString();
    }



    private Map m_manipulators = new HashMap();
}

class AbortMetaTestException extends RuntimeException {}

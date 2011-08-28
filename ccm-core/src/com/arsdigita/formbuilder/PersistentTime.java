package com.arsdigita.formbuilder;


// This class is an ACSObject using BigDecimals for its ids
import java.math.BigDecimal;

// This factory creates a Time
import com.arsdigita.bebop.form.Time;

// Every PersistentComponentFactory can create a Bebop Component
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.form.Widget;

// Id class used by internal constructor
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;

// For instantiating the TimeParameter
import com.arsdigita.bebop.parameters.TimeParameter;
import com.arsdigita.formbuilder.util.FormBuilderUtil;

// Thrown if the underlying DataObject with given id cannot be found
import com.arsdigita.domain.DataObjectNotFoundException;

// ACS 5 uses Log4J for logging
import org.apache.log4j.Logger;


/**
 * This class is responsible for persisting Bebop Times. The Time
 * is saved with the save() method. To resurrect the Time, use the constructor
 * taking the id of the saved Time and then invoke createComponent().
 *
 * @author Peter Marklund
 * @version $Id: PersistentTime.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public class PersistentTime extends PersistentWidget {

    public static final String versionId = "$Id: PersistentTime.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $TimeTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(PersistentTime.class.getName());

    private Class VALUE_CLASS = new java.util.Date().getClass();

    /**
     * The fully qualified name of the underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.Widget";

    // *** Constructors -------------

    /**
     * Constructor that creates a new Time domain object that
     * can be saved to the database later on. This class was only
     * included to make it possible to use this DomainObject with the
     * FormGenerator (to make the class JavaBean compliant). Use the constructor
     * taking a parameter name instead if possible.
     */
    public PersistentTime() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor that creates a new Time domain object that
     * can be saved to the database later on.
     */
    public PersistentTime(String typeName) {
        super(typeName);
    }

    public PersistentTime(ObjectType type) {
        super(type);
    }

    public PersistentTime(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor that retrieves an existing Time domain object
     * from the database.
     *
     * @param id The object id of the Time domain object to retrieve
     */
    public PersistentTime(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Extending classes can use this constructor to set the sub class
     * id and object type.
     */
    public PersistentTime(OID oID)
        throws DataObjectNotFoundException {

        super(oID);
    }

    public static PersistentTime create(String parameterName) {
        PersistentTime d = new PersistentTime();
        d.setup(parameterName);
        return d;
    }

    /**
     * Create the Time whose persistence is managed
     * by this domain object.
     */
    public Component createComponent() {

        Time time = null;

        // If there is a speical TimeParameterClass - instantiate that
        if (getTimeParameter() != null) {

            TimeParameter timeParameter = (TimeParameter)
                FormBuilderUtil.instantiateObject(getTimeParameter(),
                                                  new Class [] {getParameterName().getClass()},
                                                  new Object [] {getParameterName()});

            time = new Time(timeParameter);

        } else {
            time = new Time(getParameterName());
        }

        copyValuesToWidget(time);

        return time;
    }

    protected void copyValuesToWidget(Widget widget) {
        super.copyValuesToWidget(widget);

        Time time = (Time)widget;

    }

    /**
     * Returns a java.util.Date Class
     */
    protected Class getValueClass() {

        return VALUE_CLASS;
    }

    //*** Attribute Methods
    public void setTimeParameter(String timeParameterClass) {

        setComponentAttribute("timeParameter", timeParameterClass);
    }

    /**
     * Will return null if no value has been set.
     */
    public String getTimeParameter() {
        return getComponentAttribute("timeParameter");
    }

}

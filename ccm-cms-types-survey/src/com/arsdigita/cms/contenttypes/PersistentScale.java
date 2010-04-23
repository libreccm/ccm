/*
 * PersistentScale
 *
 * This is a persistent scale widget, which will create a checkbox group with a
 * configurable number of automatically created options.
 *
 */
package com.arsdigita.cms.contenttypes;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.PersistentRadioGroup;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Sören Bernstein
 */
public class PersistentScale extends PersistentWidget {

    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.formbuilder.Widget";
    private static final String OPTION_LIST = "optionList";
    private static final String QUESTION_LIST = "questionList";
    private ArrayList m_questions = new ArrayList();

    // *** Constructors -------------
    /**
     * Constructor that creates a new CheckboxGroup domain object that
     * can be saved to the database later on.
     */
    public PersistentScale() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor that creates a new CheckboxGroup domain object that
     * can be saved to the database later on.
     */
    public PersistentScale(String typeName) {
        super(typeName);
    }

    public PersistentScale(ObjectType type) {
        super(type);
    }

    public PersistentScale(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor that retrieves an existing CheckboxGroup domain object
     * from the database.
     *
     * @param id The object id of the CheckboxGroup domain object to retrieve
     */
    public PersistentScale(BigDecimal id)
            throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Extending classes can use this constructor to set the sub class
     * id and object type.
     */
    public PersistentScale(OID oid)
            throws DataObjectNotFoundException {

        super(oid);
    }

    public static PersistentScale create(String parameterName) {
        PersistentScale c = new PersistentScale();
        c.setup(parameterName);
        return c;
    }

    public Component createComponent() {

        // HACK For testing only
        addScaleEntry("HACK For testing only 1:");
        addScaleEntry("HACK For testing only 2:");
        addScaleEntry("HACK For testing only 3:");

        int i = 0;
        Iterator questionIter = m_questions.listIterator();

        // Component
        SimpleContainer container = new SimpleContainer();

        // For every question generate a checkbox group with the defined options
        while (questionIter.hasNext()) {

            // Question
            container.add(new Label((String) questionIter.next()));

            // OptionGroup
            container.add(generateScaleOptionGroup(i));

        }

        return container;
    }

    /**
     * Create the RadioGroup whose persistence is managed
     * by this domain object.
     */
    protected OptionGroup generateScaleOptionGroup(int nr) {

        RadioGroup radioGroup = null;

//        RadioGroup radioGroup = new RadioGroup(getParameterName() + "_" + nr);

//        m_options.addDataToComponent(radioGroup);
//        copyValuesToWidget(radioGroup);

        try {
            radioGroup = (RadioGroup) getOptionList().createOptionGroup(getParameterName() + "_" + nr);
            radioGroup.setClassAttr("horizontal");
        } catch (NullPointerException ex) {
            radioGroup = new RadioGroup(getParameterName() + "_" + nr);
        }

        return radioGroup;
    }

    /**
     * Create the CheckboxGroup whose persistence is managed
     * by this domain object.
     */
    public void addScaleEntry(String scaleEntry) {
        m_questions.add(scaleEntry);
    }

    public void removeScaleEntry(int idx) {
        m_questions.remove(idx);
    }

    public String getScaleEntry(int idx) {
        return (String) m_questions.get(idx);
    }

    public PersistentRadioGroup getOptionList() {

        try {
            return new PersistentRadioGroup(new BigDecimal(getComponentAttribute(OPTION_LIST)));
        } catch (NullPointerException ex) {
            // Create the PersistentRadioGroup
            PersistentRadioGroup prg = PersistentRadioGroup.create(this.getParameterName() + "_options");
            setComponentAttribute(OPTION_LIST, prg.getID().toString());
            return prg;
        }
    }
}

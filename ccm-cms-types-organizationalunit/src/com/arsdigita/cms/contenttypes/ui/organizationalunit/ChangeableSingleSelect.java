package com.arsdigita.cms.contenttypes.ui.organizationalunit;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.util.Assert;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class ChangeableSingleSelect extends SingleSelect {

    private ArrayList m_options;
    private RequestLocal m_requestOptions = new RequestLocal() {

        @Override
        public Object initialValue(PageState ps) {
            return new ArrayList();
        }
    };

    public ChangeableSingleSelect(String name) {
        super(new StringParameter(name));
    }

    public ChangeableSingleSelect(ParameterModel model) {
        super(model);
    }

    @Override
    public Iterator getOptions() {
        return m_options.iterator();
    }

    @Override
    public Iterator getOptions(PageState ps) {
        ArrayList allOptions = new ArrayList();
        allOptions.addAll(m_options);
        ArrayList requestOptions = (ArrayList) m_requestOptions.get(ps);
        for (Iterator i = requestOptions.iterator(); i.hasNext();) {
            Object obj = i.next();
            if (!allOptions.contains(obj)) {
                allOptions.add(obj);
            }
        }
        return allOptions.iterator();
    }

    @Override
    public void clearOptions() {
        //Assert.assertNotLocked(this);
        this.m_options = new ArrayList();
    }

    @Override
    public void addOption(Option opt, PageState ps) {
        if (this.m_options == null) {
            this.m_options = new ArrayList();
        }
        ArrayList list = m_options;
        if (ps == null) {
            //Assert.assertNotLocked(this);
        } else {
            list = (ArrayList) m_requestOptions.get(ps);
        }
        opt.setGroup(this);
        list.add(opt);
    }

    public void removeOption(Option opt, PageState ps) {
        ArrayList list = m_options;
        if (ps == null) {
            //Assert.assertNotLocked(this);
        } else {
            list = (ArrayList) m_requestOptions.get(ps);
        }
        list.remove(opt);
    }

    @Override
    public void removeOption(String key) {
        removeOption(key, null);
    }

    @Override
    public void removeOption(String key, PageState ps) {
        // This is not an entirely efficient technique. A more
        // efficient solution is to switch to using a HashMap.
        ArrayList list = m_options;
        if (ps == null) {
            //Assert.assertNotLocked(this);
        } else {
            list = (ArrayList) m_requestOptions.get(ps);
        }

        Iterator i = list.iterator();
        Option o = null;
        while (i.hasNext()) {
            o = (Option) i.next();
            if (o.getValue().equals(key)) {
                list.remove(o);
                break;
            }
        }
    }
}

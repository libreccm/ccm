<%@ taglib prefix="define" uri="/WEB-INF/bebop-define.tld" %>
<%@ taglib prefix="show" uri="/WEB-INF/bebop-show.tld" %>
<%@ page import="com.arsdigita.bebop.PageState" %>
<%@ page import="com.arsdigita.bebop.List" %>
<%@ page import="com.arsdigita.bebop.list.ListModelBuilder" %>
<%@ page import="com.arsdigita.bebop.list.ListModel" %>
<%@ page import="com.arsdigita.bebop.form.*" %>
<%@ page import="com.arsdigita.bebop.event.*" %>

<define:page name="p" title="foo">
  <define:list name="myList">
    <% 
     myList.setModelBuilder(new ListModelBuilder() { 
        private Object[] m_values = new Object[] {"foo", "bar", "baz"};
        private boolean m_locked;
        
        public ListModel makeModel(List list, PageState state) {
            return new ListModel() {
                private int i = -1;
                
                public boolean next() { 
                    i += 1;
                    return ( i < m_values.length );
                }
                
                public Object getElement() { 
                    return m_values[i]; 
                }

                public String getKey() { 
                    return String.valueOf(i); 
                }
            };
        }

        public void lock() {
            m_locked = true;
        }

        public final boolean isLocked() {
            return m_locked;
        }
    });
  %>
  </define:list>
</define:page>

<show:page>
showing page with list.

<p>
<% int i = 0; %>
<ul>
<show:list name="myList">
   <li>list item #<%= ++i %>: <show:listItem/>.  next...
</show:list>

</show:page>

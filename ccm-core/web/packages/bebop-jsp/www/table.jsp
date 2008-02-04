<%@ taglib prefix="define" uri="/WEB-INF/bebop-define.tld" %>
<%@ taglib prefix="show" uri="/WEB-INF/bebop-show.tld" %>
<%@ page import="com.arsdigita.bebop.*" %>
<%@ page import="com.arsdigita.bebop.form.*" %>
<%@ page import="com.arsdigita.bebop.event.*" %>

<define:page name="p" title="foo">
<% 
  Object tableValues[][] = new Object[][] {
       {"Bill Schneider", "bschneid@arsdigita.com", "developer"},
       {"Doug Harris", "dharris@arsdigita.com", "developer"},
       {"Roger Metcalf", "rmetcalf@arsdigita.com", "developer"},
       {"Rich James", "rjames@arsdigita.com", "sales/marketing"},
       {"Harry Greenspun", "harryg@arsdigita.com", "VP sales"}
    };
  Object tableHeaders[] = new Object[] { 
        "Name", "Email", "title"
    };

  Table t = new Table(tableValues, tableHeaders);
  t.setIdAttr("myTable");
  p.add(t);
%>
</define:page>

<show:page> This is a demonstration of using JSP to style the contents
of a model-backed Table.  The &lt;show:table>, &lt;show:thead>,
&lt;show:tbody>, and &lt;show:row> tags iterate over the rows and
columns of data from the model and the &lt;show:col/> places the
component for an individual table column.  

<p>If the Table contains a cell renderer, the &lt;show:col/> contents
will be globally styled according to the rules for whatever component
is there.  

<p>Note that any HTML &lt;tr> and &lt;td> tags must be manually
placed.  But this is a good thing because then a regular JSP/HTML
editor can modify table colors, spacing, etc.

&lt;show:col>,

<table cellspacing="4">
<show:table name="myTable">
 <tr bgcolor="#99ccff">
  <show:thead>
   <th><show:col/></th>
  </show:thead>
 </tr>
  <show:tbody>
   <tr bgcolor="#99ffcc">
      <show:row>
        <td><show:col/></td>
      </show:row>
   </tr>
  </show:tbody>
</show:table>
</table>

</show:page>
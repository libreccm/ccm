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
package com.arsdigita.webdevsupport;

/**
 *
 * @author Aram Kananov(akananov@redhat.com)
 * @version 1.0
 **/

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.db.ConnectionManager;
import com.arsdigita.db.DbHelper;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

class QueryPlanComponent extends com.arsdigita.bebop.SimpleContainer {

    private ParameterModel m_query_id = new IntegerParameter("query_id");
    private ParameterModel m_request_id = new IntegerParameter("request_id");

    private static final Label NO_PLAN = new Label(
                                                   "You don't have the plan table installed. " +
                                                   "Please source $ORACLE_HOME/rdbms/admin/utlxplan.sql " +
                                                   "if you want to enable this functionality."
                                                   );

    public QueryPlanComponent() {
        super();

        Label queryTextLabel = new Label( new PrintListener() {
            public void prepare(PrintEvent e) {
                PageState s = e.getPageState();
                Label l = (Label) e.getTarget();
                l.setLabel("<h3>Query:</h3> <blockquote><pre>" + StringUtils.quoteHtml( getSQL(s) ) +
                           "</pre></blockquote>");
            }
        });
        queryTextLabel.setOutputEscaping(false);
        add(queryTextLabel);

        Label queryVarsLabel = new Label( new PrintListener() {
                public void prepare(PrintEvent e) {
                    PageState s = e.getPageState();
                    Label l = (Label) e.getTarget();
                    l.setLabel("<h3>Variables:</h3> " + getBindVars(s));
                }
            });
        queryVarsLabel.setOutputEscaping(false);
        add(queryVarsLabel);

        Label queryPlanLabel = new Label("<h3>Execution Plan</h3>");
        queryPlanLabel.setOutputEscaping(false);
        add(queryPlanLabel);
        
		Table table = null;
		
        if (DbHelper.getDatabase() == DbHelper.DB_ORACLE) {
			TableModelBuilder m_QueryPlanTableModelBuilder =
				new AbstractTableModelBuilder() {
				public TableModel makeModel(Table t, PageState pageState) {
					return new OracleQueryPlanTableModel(pageState);
				}
			};

			String[] headings =
				new String[] {
					"Step#",
					"Parent#",
					"Level",
					"Operation",
					"Options",
					"Object Name",
					"Rows",
					"Bytes",
					"Cost" };

			table = new Table(m_QueryPlanTableModelBuilder, headings) {
				public void generateXML(PageState ps, Element parent) {
					if (QueryPlan.planTableExists()) {
						super.generateXML(ps, parent);
					} else {
						NO_PLAN.generateXML(ps, parent);
					}
				}
			};
		} else {
			TableModelBuilder m_QueryPlanTableModelBuilder =
				new AbstractTableModelBuilder() {
				public TableModel makeModel(Table t, PageState pageState) {
					return new PostgresQueryPlanTableModel(pageState);
				}
			};

			String[] headings = new String[] { "Step" };

			table = new Table(m_QueryPlanTableModelBuilder, headings) {
				public void generateXML(PageState ps, Element parent) {
					super.generateXML(ps, parent);
				}
			};

			table.setDefaultCellRenderer(new TableCellRenderer() {
				public Component getComponent(
					Table table,
					PageState state,
					Object value,
					boolean isSelected,
					Object key,
					int row,
					int column) {
					Label l = new Label("<pre>" + (String) value + "</pre>");
					l.setOutputEscaping(false);
					return l;
				}
			});
		}
        
        table.setWidth("100%");
        table.setBorder("1");
        add(table);

    }

    private class OracleQueryPlanTableModel implements TableModel {
        DataQuery m_query;
        String m_planID;

        public OracleQueryPlanTableModel(PageState pageState) {
            m_planID = QueryPlan.getQueryPlanID(getSQL(pageState));
            m_query = SessionManager.getSession().retrieveQuery(
                                                                "com.arsdigita.webdevsupport.queryPlan"
                                                                );
            m_query.setParameter("plan_id", m_planID);
        }

        public int getColumnCount() {
            return 9;
        }

        public boolean nextRow() {
            boolean nRow = m_query.next();
            if (!nRow) {
                m_query.close() ;
                QueryPlan.deleteQueryPlan(m_planID);
            }
            return nRow;
        }

        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
            case 0: return m_query.get("id");
            case 1: return m_query.get("parentID");
            case 2: return m_query.get("lvl");
            case 3: return m_query.get("operation");
            case 4: return m_query.get("execOptions");
            case 5: return m_query.get("objectName");
            case 6: return m_query.get("cardinality");
            case 7: return m_query.get("bytes");
            case 8: return m_query.get("cost");
            default: return null;
            }
        }

        public Object getKeyAt(int columnIndex) {
            return m_query.get("id");
        }
    }

    
	private class PostgresQueryPlanTableModel implements TableModel {
		ResultSet m_query;
		Statement m_stmt = null;

		public PostgresQueryPlanTableModel(PageState pageState) {
			Connection conn = ConnectionManager.getCurrentThreadConnection();
			try { 
				m_stmt = conn.createStatement();
				m_query = m_stmt.executeQuery("Explain "
						+ QueryLog.substituteSQL(getQueryInfo(pageState)));
			} catch (SQLException e) {
				throw new UncheckedWrapperException(e);
			}
		}

		public int getColumnCount() {
			return 1;
		}

		public boolean nextRow() {
			try {
				boolean nRow = m_query.next();
				if (!nRow) {
					m_query.close();
					m_stmt.close();
				}
				return nRow;
			} catch (Exception e) {
				throw new UncheckedWrapperException(e);
			}
		}

		public Object getElementAt(int columnIndex) {
		    try {
			switch (columnIndex) {
			case 0: return m_query.getObject(1);
			default: return null;
			}
		    } catch (Exception e) {
		    	throw new UncheckedWrapperException(e);
		    }
		}

		public Object getKeyAt(int columnIndex) {
			try {
			  return m_query.getObject(1);
			} catch (Exception e) {
				throw new UncheckedWrapperException(e);
			}
		}
	}
    
    private String getSQL(PageState s) {

        Integer request_id = (Integer) s.getValue(m_request_id);
        Integer query_id = (Integer) s.getValue(m_query_id);

        RequestInfo ri = WebDevSupportListener.getInstance().getRequest(
                                                                request_id.intValue()
                                                                );
        if (ri != null) {
            QueryInfo qi = ri.getQuery(query_id.intValue());
            if (qi != null) {
                return qi.getQuery();
            }
        }
        return null;
    }

    private Map getBindVars(PageState s) {

        Integer request_id = (Integer) s.getValue(m_request_id);
        Integer query_id = (Integer) s.getValue(m_query_id);
        RequestInfo ri = WebDevSupportListener.getInstance().getRequest(
                                                                request_id.intValue()
                                                                );
        if (ri != null) {
            QueryInfo qi = ri.getQuery(query_id.intValue());
            if (qi != null) {
                return qi.getBindvars();
            }
        }
        return null;
    }
    
	private QueryInfo getQueryInfo(PageState s) {

		Integer request_id = (Integer) s.getValue(m_request_id);
		Integer query_id = (Integer) s.getValue(m_query_id);
		RequestInfo ri =
			WebDevSupportListener.getInstance().getRequest(request_id.intValue());
		if (ri != null) {
			QueryInfo qi = ri.getQuery(query_id.intValue());
			if (qi != null) {
				return qi;
			}
		}
		return null;
	}

}

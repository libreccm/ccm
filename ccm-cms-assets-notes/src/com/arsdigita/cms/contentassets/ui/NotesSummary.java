package com.arsdigita.cms.contentassets.ui;

import java.text.DateFormat;
import java.util.Date;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AdditionalDisplayComponent;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.User;
import com.arsdigita.cms.contentassets.Note;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.util.LockableImpl;

public class NotesSummary extends Table implements AdditionalDisplayComponent {
	
	private ItemSelectionModel m_itemSelectionModel;
	
	public NotesSummary() {
		super();
		setModelBuilder(new NotesTableBuilder());
		TableColumnModel model = getColumnModel();
		model.add( new TableColumn( 0, "Note"   ));
		model.add( new TableColumn( 1, "Date"  ));
        model.add( new TableColumn( 2, "By"   ));
        setRowSelectionModel(null);
        setColumnSelectionModel(null);
        model.get(0).setCellRenderer(new TableCellRenderer() {

			public Component getComponent(Table table, PageState state, Object value, boolean isSelected, Object key, int row, int column) {
				Label t = new Label((String)value);
	            t.setOutputEscaping(false);
	            
	            return t;
	        }

	        
			});
        
        
        
		
	}
	
	public void setItemSelectionModel(ItemSelectionModel model) {
		m_itemSelectionModel = model;
	}
	
	private class NotesTableBuilder extends LockableImpl implements TableModelBuilder {

		
		public TableModel makeModel(Table t, PageState state) {
			return new NotesTableModel(m_itemSelectionModel.getSelectedItem(state));
			
		}

		
		
	}
	
	
	private class NotesTableModel implements TableModel {

		private DataCollection  m_notes = null;
		private Note m_currentNote;
		
		public NotesTableModel (ContentItem item) {
			if (item != null) {
				m_notes = Note.getNotes(item);
				// cg already ordered by rank m_notes.addOrder(Note.CREATION_DATE + " desc");
			}
		}
		public int getColumnCount() {
			return 3;
		}

		public Object getElementAt(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return m_currentNote.getContent();
				
				
			case 1:
				String displayDate = "Not recorded";
				Date creationDate = m_currentNote.getCreationDate();
				if (creationDate != null) {
					displayDate =  DateFormat.getDateInstance(DateFormat.MEDIUM).format(creationDate);
				}
				return displayDate;
				
				
			case 2:
				String displayAuthor = "Not recorded";
				User author = m_currentNote.getNoteAuthor();
				if (author != null) {
					displayAuthor = author.getName();
				}
				return displayAuthor;
				
			default :
				throw new IndexOutOfBoundsException(
					"Column index " + columnIndex + " not in table model.");
			}
		}

		public Object getKeyAt(int columnIndex) {
			return m_currentNote.getID();
		}

		public boolean nextRow () {
            if ( m_notes.next() ) {
                m_currentNote = (Note)DomainObjectFactory.newInstance(m_notes.getDataObject());
                return true;
            } else {
            	m_notes.close();
            	return false;
            }
            
        }
		
	}

}


	

package com.arsdigita.cms.docmgr.ui;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.xml.Element;

/**
 * @author Peter Kopunec
 */
public class PagingControlContainer extends BoxPanel {
	
	public static final String CLASS_PAGING_CONTROL = "pageControlClass";
	public static final String CLASS_SELECTED = "selected";
	
	public static final String LABEL_PREVIOUS = "< Previous";
	public static final String LABEL_NEXT = "Next >";

	private static final int m_size = 9;
	
	private final IntegerParameter m_pageNo;
	private final RequestLocal m_maxPages;
	private final int m_itemsPerPage;
	
	private Label m_prevLabel;
	private ActionLink m_prev;
	private Label m_firstLabel;
	private ActionLink m_first;
	private Label m_separator1 = new Label("...");
	private Label[] m_pageLinksLabel;
	private ActionLink[] m_pageLinks;
	private Label m_separator2 = new Label("...");
	private Label m_nextLabel;
	private ActionLink m_next;
	private Label m_lastLabel;
	private ActionLink m_last;
	
	/**
	 * Constructor
	 * 
	 * @param pageNo current page number
	 * @param maxPages 'get' returns count of pages
	 */
	public PagingControlContainer(IntegerParameter pageNo, RequestLocal maxPages) {
		this(pageNo, maxPages, 20);
	}
	
	/**
	 * Constructor
	 * 
	 * @param pageNo current page number
	 * @param maxPages 'get' returns count of pages
	 */
	public PagingControlContainer(IntegerParameter pageNo, RequestLocal maxPages, int itemsPerPage) {
		super(BoxPanel.HORIZONTAL);
		
		m_pageNo = pageNo;
		m_maxPages = maxPages;
		m_itemsPerPage = itemsPerPage;
		
		setClassAttr(CLASS_PAGING_CONTROL);
		
		m_prevLabel = new Label(LABEL_PREVIOUS);
		add(m_prevLabel);
		m_prev = new ActionLink(LABEL_PREVIOUS);
		m_prev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				PageState ps = ae.getPageState();
				Integer pageNo = (Integer) ps.getValue(m_pageNo);
				int pn = 0;
				if (pageNo != null) {
					pn = pageNo.intValue();
				}
				if (pn > 0) {
					pn--;
				}
				ps.setValue(m_pageNo, new Integer(pn));
			}
		});
		add(m_prev);
		
		m_firstLabel = new Label("1");
		m_firstLabel.setClassAttr(CLASS_SELECTED);
		add(m_firstLabel);
		m_first = new ActionLink("1");
		m_first.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				PageState ps = ae.getPageState();
				ps.setValue(m_pageNo, new Integer(0));
			}
		});
		add(m_first);
		
		add(m_separator1);
		
		Label label;
		
		m_pageLinksLabel = new Label[m_size];
		m_pageLinks = new ActionLink[m_size];
		for (int i = 0; i < m_size; i++) {
			m_pageLinksLabel[i] = new Label(new PageLinkPrintListener(i));
			m_pageLinksLabel[i].setClassAttr(CLASS_SELECTED);
			add(m_pageLinksLabel[i]);
			
			label = new Label(new PageLinkPrintListener(i));
			m_pageLinks[i] = new ActionLink(label);
			m_pageLinks[i].addActionListener(new PageLinkActionListener(i));
			add(m_pageLinks[i]);
		}
		
		add(m_separator2);
		
		m_lastLabel = new Label(new PrintListener() {
			public void prepare(PrintEvent pe) {
				PageState ps = pe.getPageState();
				Label l = (Label) pe.getTarget();
				l.setLabel(String.valueOf(m_maxPages.get(ps)), ps);
			}
		});
		m_lastLabel.setClassAttr(CLASS_SELECTED);
		add(m_lastLabel);
		label = new Label(new PrintListener() {
			public void prepare(PrintEvent pe) {
				PageState ps = pe.getPageState();
				Label l = (Label) pe.getTarget();
				l.setLabel(String.valueOf(m_maxPages.get(ps)), ps);
			}
		});
		m_last = new ActionLink(label);
		m_last.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				PageState ps = ae.getPageState();
				ps.setValue(m_pageNo, new Integer(((Integer) m_maxPages.get(ps)).intValue() - 1));
			}
		});
		add(m_last);
		
		m_nextLabel = new Label(LABEL_NEXT);
		add(m_nextLabel);
		m_next = new ActionLink(LABEL_NEXT);
		m_next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				PageState ps = ae.getPageState();
				Integer pageNo = (Integer) ps.getValue(m_pageNo);
				int pn = pageNo == null ? 0 : pageNo.intValue();
				pn++;
				ps.setValue(m_pageNo, new Integer(pn));
			}
		});
		add(m_next);
	}
	
	public int getPageNo(PageState ps) {
		Integer pn = (Integer) ps.getValue(m_pageNo);
		int pageNo = pn == null ? 0 : pn.intValue();
		return pageNo;
	}
	
	public int getMaxPages(PageState ps) {
		Integer mp = (Integer) m_maxPages.get(ps);
		int maxPages = mp == null ? 0 : mp.intValue();
		return maxPages;
	}
		
	public int getFirstRowNo(PageState ps) {
		Integer pn = (Integer) ps.getValue(m_pageNo);
		int pageNo = (pn == null ? 0 : pn.intValue());
		return ((int) (pageNo * m_itemsPerPage)) + 1;
	}
	
	public int getLastRowNo(PageState ps) {
		return getFirstRowNo(ps) + m_itemsPerPage;
	}
	
	public void generateXML(PageState ps, Element e) {
		int pageNo = getPageNo(ps);
		int maxPages = getMaxPages(ps);
		
		m_prevLabel.setVisible(ps, pageNo == 0);
		m_prev.setVisible(ps, pageNo > 0);
		m_firstLabel.setVisible(ps, pageNo == 0);
		m_first.setVisible(ps, pageNo > 0);
		m_separator1.setVisible(ps, (maxPages > m_size + 2 ? pageNo > m_size / 2 + 1 : false));
		for (int i = 0; i < m_size; i++) {
			if (maxPages > i + 2) {
				int pnl = calcPageNo(ps, i) - 1;
				m_pageLinksLabel[i].setVisible(ps, pnl == pageNo);
				m_pageLinks[i].setVisible(ps, pnl != pageNo);
			}
			else {
				m_pageLinksLabel[i].setVisible(ps, false);
				m_pageLinks[i].setVisible(ps, false);
			}
		}
		m_separator2.setVisible(ps, (maxPages > m_size + 2 ? pageNo + 1 < maxPages - m_size / 2 - 1 : false));
		m_lastLabel.setVisible(ps, (maxPages > 1 ? pageNo + 1 >= maxPages : false));
		m_last.setVisible(ps, (maxPages > 1 ? pageNo + 1 < maxPages : false));
		m_nextLabel.setVisible(ps, pageNo + 1 >= maxPages);
		m_next.setVisible(ps, pageNo + 1 < maxPages);
		
		super.generateXML(ps, e);
	}
	
	private int calcPageNo(PageState ps, int pageOffset) {
		Integer pn = (Integer) ps.getValue(m_pageNo);
		int pageNo = pn == null ? 0 : pn.intValue();
		
		Integer mp = (Integer) m_maxPages.get(ps);
		int maxPages = mp == null ? 0 : mp.intValue();
		
		if (maxPages <= m_size + 2 || pageNo < m_size / 2 + 2) {// can display all page's links or is page no. in the beginning pages
			pageNo = 2 + pageOffset;
		}
		else {
			if (pageNo > maxPages - (m_size / 2 + 2)) {// is page no. in the finishing pages
				pageNo = maxPages - m_size + pageOffset;
			}
			else {
				pageNo = pageNo - m_size / 2 + 1 + pageOffset;
			}
		}
		
		return pageNo;
	}
	
	class PageLinkPrintListener implements PrintListener {
		
		private int m_pageOffset;
		
		PageLinkPrintListener(int pageOffset) {
			m_pageOffset = pageOffset;
		}
		
		public void prepare(PrintEvent pe) {
			PageState ps = pe.getPageState();
			int pageNo = calcPageNo(ps, m_pageOffset);
			Label l = (Label) pe.getTarget();
			l.setLabel(String.valueOf(pageNo), ps);
		}
	}
	
	class PageLinkActionListener implements ActionListener {
		
		private int m_pageOffset;
		
		PageLinkActionListener(int pageOffset) {
			m_pageOffset = pageOffset;
		}
		
		public void actionPerformed(ActionEvent ae) {
			PageState ps = ae.getPageState();
			int pageNo = calcPageNo(ps, m_pageOffset);
			ps.setValue(m_pageNo, new Integer(pageNo - 1));
		}
	}
}

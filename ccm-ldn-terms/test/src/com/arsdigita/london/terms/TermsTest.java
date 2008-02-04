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
 */

package com.arsdigita.london.terms;

import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.SessionManager;


import org.apache.log4j.Logger;

import java.util.Date;

import java.net.URL;
import java.net.MalformedURLException;


/**
 * Test all aspects of the Term class.
 */
public class TermsTest extends BaseTestCase {

    private final static Logger s_log = Logger.getLogger(TermsTest.class);

    private final static int NONPREFERRED_COUNT = 5;
    private final static int PREFERRED_COUNT = 10;
    private final static int NARROWER_COUNT = 5;

    private final static int TOTAL_COUNT = NONPREFERRED_COUNT +
        NARROWER_COUNT + PREFERRED_COUNT;

    private Domain m_domain1;

    private Term[] m_terms1;
    private Term[] m_p_terms1;
    private Term[] m_np_terms1;
    private Term[] m_na_terms1;

    private Domain m_domain2;

    private Term[] m_terms2;
    private Term[] m_p_terms2;
    private Term[] m_np_terms2;
    private Term[] m_na_terms2;

    private Domain m_domain3;

    private Term[] m_terms3;
    private Term[] m_p_terms3;
    private Term[] m_np_terms3;
    private Term[] m_na_terms3;

    /**
     * Constructs a TermTest with the specified name.
     *
     * @param name Test case name.
     **/
    public TermsTest( String name ) {
        super( name );
    }

    public void setUp() {
        try {
            m_domain1 = Domain.create("eg-terms1",
                                      new URL("http://www.example.com/terms-1"),
                                      "Terms 1",
                                      "Terms in set 1",
                                      "1.01",
                                      new Date());
            
            m_p_terms1 = setUpTerms(m_domain1, PREFERRED_COUNT, new Term[] {}, 0);
            m_np_terms1 = setUpTerms(m_domain1, NONPREFERRED_COUNT, 
                                     m_p_terms1, PREFERRED_COUNT);
            m_na_terms1 = setUpTerms(m_domain1, NARROWER_COUNT, new Term[] {}, 
                                     PREFERRED_COUNT + NONPREFERRED_COUNT);
            m_terms1 = unifyTerms(m_p_terms1, m_np_terms1, m_na_terms1);



            m_domain2 = Domain.create("eg-terms2",
                                      new URL("http://www.example.com/terms-2"),
                                      "Terms 2",
                                      "Terms in set 2",
                                      "2.02",
                                      new Date());
            m_p_terms2 = setUpTerms(m_domain2, PREFERRED_COUNT, new Term[] {}, 0);
            m_np_terms2 = setUpTerms(m_domain2, NONPREFERRED_COUNT, 
                                     m_p_terms2, PREFERRED_COUNT);
            m_na_terms2 = setUpTerms(m_domain2, NARROWER_COUNT, new Term[] {}, 
                                     PREFERRED_COUNT + NONPREFERRED_COUNT);
            m_terms2 = unifyTerms(m_p_terms2, m_np_terms2, m_na_terms2);
            


            m_domain3 = Domain.create("eg-terms3",
                                      new URL("http://www.example.com/terms-3"),
                                      "Terms 3",
                                      "Terms in set 3",
                                      "3.03",
                                      new Date());
            m_p_terms3 = setUpTerms(m_domain3, PREFERRED_COUNT, new Term[] {}, 0);
            m_np_terms3 = setUpTerms(m_domain3, NONPREFERRED_COUNT, 
                                     m_p_terms3, PREFERRED_COUNT);
            m_na_terms3 = setUpTerms(m_domain3, NARROWER_COUNT, new Term[] {}, 
                                     PREFERRED_COUNT + NONPREFERRED_COUNT);
            m_terms3 = unifyTerms(m_p_terms3, m_np_terms3, m_na_terms3);
            
                                    
        } catch (MalformedURLException ex) {
            throw new UncheckedWrapperException("Cannot parse url", ex);
        }
    }

    public void tearDown() {
        // You never know with new persistence if there is an
        // error lurking that won't appear until the session
        // is flushed. So just to be sure...
        SessionManager.getSession().flushAll();
    }

    public Term[] setUpTerms(Domain domain,
                             int count,
                             Term[] preferred,
                             int offset) {
        Term[] terms = new Term[count];

        for (int i = 0 ; i < count ; i++) {   
            terms[i] = Term.create(new Integer(i+offset),
                                   domain.getKey() + "Term0" + i,
                                   false,
                                   domain.getKey() + "term0" + i,
                                   domain);
        }

        return terms;
    }
    
    public Term[] unifyTerms(Term[] preferred,
                             Term[] nonpreferred,
                             Term[] narrower) {
        Term[] terms = new Term[preferred.length + 
                                nonpreferred.length + 
                                narrower.length];

        for (int i = 0 ; i < preferred.length ; i++) {
            terms[i] = preferred[i];
        }
        for (int i = 0 ; i < nonpreferred.length ; i++) {
            terms[preferred.length + i] = nonpreferred[i];
        }
        for (int i = 0 ; i < narrower.length ; i++) {
            terms[preferred.length + nonpreferred.length + i] = narrower[i];
        }

        return terms;
    }
    
    public void testDomainTerms() {
        _testDomainTerms(m_domain1, m_terms1);
        _testDomainTerms(m_domain2, m_terms2);
        _testDomainTerms(m_domain3, m_terms3);
    }

    private void _testDomainTerms(Domain domain,
                                  Term[] expected) {
        DomainCollection actual = domain.getTerms();
        actual.addOrder(Term.UNIQUE_ID);

        int i = 0;
        while (actual.next()) {
            Term term = (Term)actual.getDomainObject();
            assertEquals(term, expected[i]);
            i++;
        }
    }

    public void testNarrowerTerms() {
        _testNarrowerTerms(m_p_terms1, m_np_terms1, m_na_terms1);
        _testNarrowerTerms(m_p_terms2, m_np_terms2, m_na_terms2);
        _testNarrowerTerms(m_p_terms3, m_np_terms3, m_na_terms3);
    }

    private void _testNarrowerTerms(Term[] preferred,
                                    Term[] nonpreferred,
                                    Term[] narrower) {
        for (int i = 0 ; i < preferred.length ; i++) {
            int n = i % narrower.length;
            boolean def = i < narrower.length;
            preferred[i].addNarrowerTerm(narrower[n],
                                         def,
                                         true);
        }
        
        for (int i = 0 ; i < preferred.length ; i++) {
            int n = i % nonpreferred.length;
            boolean def = i < nonpreferred.length;
            preferred[i].addNarrowerTerm(nonpreferred[n],
                                         false,
                                         true);
        }
        
        for (int i = 0 ; i < preferred.length ; i++) {
            DomainCollection terms = preferred[i].getNarrowerTerms();
            if (s_log.isDebugEnabled()) {
                s_log.debug("Testing narrower terms of " + preferred[i]);
            }

            while (terms.next()) {
                Term actual = (Term)terms.getDomainObject();

                int n1 = i % narrower.length;
                int n2 = i % nonpreferred.length;
                
                Term expectedNarrow = narrower[n1];
                Term expectedNon = nonpreferred[n2];

                if (s_log.isDebugEnabled()) {
                    s_log.debug("Got " + actual + " was expecting " + 
                                expectedNarrow + " or " + expectedNon);
                }
                
                assertTrue(actual.equals(expectedNon) ||
                           actual.equals(expectedNarrow));
            }
        }
        
        for (int i = 0 ; i < narrower.length ; i++) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Testing broader terms of " + narrower[i]);
            }

            DomainCollection terms = narrower[i].getBroaderTerms();
            while (terms.next()) {
                Term actual = (Term)terms.getDomainObject();
                boolean found = false;
                for (int j = 0 ; j < preferred.length && !found ; j++) {
                    found = preferred[j].equals(actual);
                }

                if (s_log.isDebugEnabled()) {
                    s_log.debug("Got " + actual);
                }
                
                assertTrue(found);
            }
        }
        
        for (int i = 0 ; i < narrower.length ; i++) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Testing default broader term of " + narrower[i]);
            }

            Term actual = narrower[i].getBroaderTerm();
            Term expected = preferred[i];
            
            if (s_log.isDebugEnabled()) {
                s_log.debug("Got " + actual + " was expecting " + expected);
            }

            assertEquals(actual, expected);
        }
    }


}

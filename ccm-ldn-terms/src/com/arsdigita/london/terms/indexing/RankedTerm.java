/*
 * Copyright (C) 2009 Permeance Technologies Pty Ltd. All Rights Reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package com.arsdigita.london.terms.indexing;

import java.math.BigDecimal;

import com.arsdigita.london.terms.Term;
import com.arsdigita.util.Assert;

/**
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
public class RankedTerm {

    private final Term m_term;

    private final BigDecimal m_ranking;

    public RankedTerm(Term term, BigDecimal ranking) {
        Assert.exists(term);
        Assert.exists(ranking);
        Assert.isTrue(ranking.compareTo(BigDecimal.ONE) < 0);
        Assert.isTrue(ranking.compareTo(BigDecimal.ZERO) > 0);
        m_term = term;
        m_ranking = ranking.setScale(4, BigDecimal.ROUND_HALF_DOWN);
    }

    public Term getTerm() {
        return m_term;
    }

    public BigDecimal getRanking() {
        return m_ranking;
    }
}

/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
package com.arsdigita.cms.scipublications.exporter.ris;

/**
 * Reference types supported by the RIS format.
 *
 * @author Jens Pelzetter
 */
public enum RisTypes {

    /**
     * Abstract
     */
    ABST,
    /**
     * Audiovisual material
     */
    ADVS,
    /**
     * Art Work
     */
    ART,
    /**
     * Bill/Resolution
     */
    BILL,
    /**
     * Book, Whole
     */
    BOOK,
    /**
     * Case
     */
    CASE,
    /**
     * Book chapter
     */
    CHAP,
    /**
     * Computer program
     */
    COMP,
    /**
     * Conference proceedings
     */
    CONF,
    /**
     * Catalog
     */
    CTLG,
    /**
     * Data file
     */
    DATA,
    /**
     * Electronic citation
     */
    ELEC,
    /**
     * Generic
     */
    GEN,
    /**
     * Hearing
     */
    HEAR,
    /**
     * Internet Communication
     */
    ICOMM,
    /**
     * In Press
     */
    INPR,
    /**
     * Journal (full)
     */
    JFULL,
    /**
     * Journal
     */
    JOUR,
    /**
     * Map
     */
    MAP,
    /**
     * Magazine article
     */
    MGZN,
    /**
     * Motion picture
     */
    MPCT,
    /**
     * Music score
     */
    MUSIC,
    /**
     * Newspaper
     */
    NEWS,
    /**
     * Pamphlet
     */
    PAMP,
    /**
     * Patent
     */
    PAT,
    /**
     * Personal communication
     */
    PCOMM,
    /**
     * Report
     */
    RPRT,
    /**
     * Serial (Book, Monograph)
     */
    SER,
    /**
     * Slide
     */
    SLIDE,
    /**
     * Sound recording
     */
    SOUND,
    /**
     * Statute
     */
    STAT,
    /**
     * Thesis/Dissertation
     */
    THES,
    /**
     * Unenacted bill/resoution
     */
    UNBILL,
    /**
     * Unpublished work
     */
    UNPB,
    /**
     * Video recording
     */
    VIDEO
}

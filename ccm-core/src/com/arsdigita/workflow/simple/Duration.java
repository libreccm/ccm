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
 *
 */
package com.arsdigita.workflow.simple;

import java.util.Date;

/**
 *
 * Contains a task's due date and duration information,
 * computed in server locale with millisecond precision.
 *
 * Start date can be set at instatantiation time or later.
 * Duration and due date can be set at any time after instatantiation.
 *
 * @author Stefan Deusch 
 * @author Khy Huang
 * @version $Id: Duration.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public class Duration {

    /** the duration in minutes                                              */
    private int m_duration = 0;
    /** Start date                                                           */
    private Date m_startDate = new Date();

    /**
     * Constructor with duration in days, hours, and minutes.
     * Start time is local server time.
     *
     * @param days the number of days
     * @param hours the number of hours
     * @param minutes the number of minutes
     *
     **/
    public Duration(int days, int hours, int minutes) {
        setDuration(days,hours,minutes);
    }

    /**
     * Constructor with duration in minutes.
     * Start time is local server time.
     * @param number the number of minutes
     *
     **/
    public Duration(int minutes) {
        setDuration(minutes);
    }

    /**
     * Constructor that leaves duration unspecified.
     * If you use this constructor in a two-step
     * process to determine the duration and the
     * start date, you should call setDuration() later.
     *
     * @param startDate the start date
     * @see #setDuration(int)
     **/
    public Duration(Date startDate) {
        this.m_startDate = (Date)startDate.clone();
    }

    /**
     * Constructor with start date, and duration in
     * days, hours, and minutes.
     *
     * @param startDate the start date
     * @param days  the duration in days
     * @param hours the duration in hours
     * @param minutes the duration in minutes
     *
     **/
    public Duration(Date startDate, int days,
                    int hours, int minutes) {

        this.m_startDate = (Date)startDate.clone();

        setDuration(days,hours,minutes);
    }


    /**
     * Constructor with start date, and duration
     * in minutes.
     *
     * @param startDate the start date
     * @param minutes the duration in minutes
     *
     **/
    public Duration(Date startDate, int minutes) {

        // once startDate is set, keep a clone
        this.m_startDate = (Date)startDate.clone();

        setDuration(minutes);
    }


    /**
     * Sets the duration in days, hours, and minutes.
     *
     * @param days the duration in days
     * @param hours the duration in hours
     * @param minutes the duration in minutes
     *
     **/
    public final void setDuration(int days, int hours, int minutes) {
        setDuration(days*24*60 + hours*60 + minutes);
    }

    /**
     * Sets the duration in minutes.
     *
     * @param minutes the duration in minutes
     *
     **/
    public final void setDuration(int minutes) {
        m_duration = minutes;
    }


    /**
     * Returns the start date.
     * @return the start date.

     *
     **/
    public Date getStartDate() {
        return  m_startDate;
    }


    /**
     * Returns the due date.
     * @return the due date.
     *
     */
    public Date getDueDate() {
        return  new Date(m_startDate.getTime()+m_duration*60*1000);
    }

    /**
     * Returns the number of minutes
     * of the duration.
     * @return the duration in minutes.
     *
     */
    public int getDuration() {
        return m_duration;
    }


    /**
     * Checks if system clock is past the
     * overdue date.
     * @return <code>true</code>if it is past
     * the overdue date; <code>false</code> otherwise.
     *
     **/
    public boolean isPassedOverDue() {
        Date curr_date = new Date();
        return curr_date.after(getDueDate());
    }

    /**
     * Checks if a date is past the overdue date.
     * If yes, returns true.
     * @return <code>true</code>if the date is past
     * the overdue date; <code>false</code> otherwise.
     *
     * @param the date to compare with
     *
     **/
    public boolean isPassedOverDue(Date date) {
        return date.after(getDueDate());
    }

    /**
     * Prints a summary to a java.io.PrintStream.
     *
     * @param out the output print stream to print to
     *
     */
    public void printSummary(java.io.PrintStream out) throws Exception {
        out.print("Start Date: "+getStartDate()+"\n");
        out.print("Duration  : "+getDuration() +" [m_duration]\n");
        out.print("Due   Date: "+getDueDate()  +"\n");
    }

    /*
     * for standalone testing
     *
     */
    public static void main(String[] arg) {
        Duration d;
        try {
            System.out.println("set a process to start now " +
                               ", lasting 1d, 1h, 1 min, and print summary");
            d = new Duration(new Date(),1,1,1);
            d.printSummary(System.out);

            System.out.println("Change duration to 10 days " +
                               ", 100 hours, 1000 minutes");
            d.setDuration(10,100,1000);
            d.printSummary(System.out);

            System.out.println("Set startDate to old dueDate and set  " +
                               "duration to -10 days, -100 hours, -1000 mins");
            d = new Duration(d.getDueDate(), -10, -100, -1000);
            d.printSummary(System.out);

            System.out.println("Reset duration to 10 days, 100 hours,  " +
                               "1000 minutes and reset startDate to tomorrow");
            d.setDuration(10,100,1000);

        } catch(Exception e) {
            System.out.println("Something happened: "+e);
        }

    }

}

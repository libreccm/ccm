/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.portation.cmd;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 3/5/18
 */
public class ExportLogger {

    public static void fetching(final String classNames) {
        System.out.println(String.format(
                "\tFetching %s from database...", classNames));
    }

    public static void converting(final String classNames) {
        System.out.println(String.format(
                "\tConverting %s...", classNames));
    }

    public static void created(final String className,
                               final int count) {
        System.out.println(String.format(
                "\t\tCreated %d %s.", count, className));
    }
    public static void skipped(final String className,
                               final int count) {
        System.out.println(String.format(
                "\t\tSkipped %d %s.", count, className));
    }
    public static void found(final String className,
                               final int count) {
        System.out.println(String.format(
                "\t\tFound %d %s.", count, className));
    }

    public static void sorting(final String classNames) {
        System.out.println(String.format(
                "\tSorting %s...", classNames));
    }

    public static void ranSort(final String className,
                               final int runs) {
        System.out.println(String.format(
                "\t\tSorted %s in %d runs.", className, runs));
    }



    public static void exporting(final String className) {
        System.out.println(String.format(
                "\tExporting %s...", className));
    }
}

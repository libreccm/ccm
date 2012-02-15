/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.developersupport;

/**
 * Dynamic proxies produced by the {@link LoggingProxyFactory} implement
 * this interface.
 * 
 * @see LoggingProxyFactory
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2003-06-17
 * @version $Revision: #5 $ $Date: 2004/08/16 $
 **/
public interface LoggerConfigurator {

     void setLogger(String logger);

     void setLevel(String level);

     void enableStackTraces();

     // TODO: add API for specifying match patterns so that calls can be logged
     // selectively, depending on whether they match or fail to match a
     // particular pattern.  The pattern format should probably allow the
     // following syntax:
     //   *  "fooBar" - match any method that contains "fooBar" as a substring
     //   *  "^fooBaz" - match any method that starts with "fooBaz"
     //   *  "fooBaz$" - match any method that ends with "fooBaz"
     //   *  "^fooBaz$" - match the "fooBaz" method exactly.
}

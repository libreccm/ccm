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
package com.arsdigita.packaging;

import java.io.IOException;
import java.util.List;
import org.apache.commons.cli.CommandLine;

/**
 * Interface used for the "delegate-design-pattern" and therefore sets all 
 * method-declarations which have to be implemented by the helper class
 * "LoadCenterDeligate" known as the delegate to support the "load"- 
 * and "unload"-commands. 
 * 
 * The "delegate-design-pattern" is a possibility to imitate multiple 
 * inheritance and thus reduce the redundancy.
 * 
 * @author Tobias Osmers <tosmers@uni-bremen.de>
 * @version $Revision: #1 $ $Date: 2015/04/22 $
 */
public interface LoadCenter {
    enum LoadType {LOAD, UNLOAD};
    
    /**
     * Gets all packages to be unloaded either from the command-line or a file,
     * if the option flag [--packagekeys-file FILE] has been set and puts them 
     * in a list of packages (by their package-keys).
     * 
     * @param line The command-line with all options and arguments
     * @return The list of packages to be unloaded
     * @throws IOException 
     */
    List getAllPackages(CommandLine line) throws IOException;
    
    /**
     * Gets all loaders to the given package-list and sorts them before re-
     * turning. Creates a map that assigns to every package-key an equivalent 
     * loader. This loader contains a bunch of informations (required, 
     * provided, scripts) provided by an ".load"-file. Then all loaders from 
     * the map (pkg-key -> loader) are composed into an array of loaders and 
     * sorted.
     * 
     * @param line The command-line with all options and arguments
     * @param packages The list of packages to be loaded
     * @param loadType Weather packages are been loaded or unloaded
     * @return A sorted list of loaders
     * @throws Error 
     */
    Loader[] getAllLoaders(CommandLine line, List packages, 
            LoadType loadType) throws Error;
     
    /**
     * Determines if all steps (config, schema, data, inits) have to be 
     * performed.
     * 
     * @param line The command-line with all options and arguments
     * @return True if all options need to be performed
     */
    boolean hasAllOptions(CommandLine line);
    
    /**
     * Checks existence and accessibility of the database and does a rollback 
     * if necessary.
     * 
     * @return True on success, otherwise false
     */
    boolean checkDatabase();
    
    /**
     * Sets back the configuration to the original packages. Goes through
     * all packages from the configuration-context and removes the ones 
     * contained in the list of packages which will be loaded.
     * 
     * @param config The configuration
     * @param packages The packages to be loaded
     * @return True on success, otherwise false
     */
    boolean rollbackConfig(Config config, List packages);
    
    /**
     * Checks the initializer dependencies set in the ".load"-file.
     * 
     * @param loaders A list of loaders to the corresponding packages
     *               to-be-loaded
     * @param sessionName Name of the session
     * @return true on success, otherwise false
     */
    boolean checkInitializerDependencies(final Loader[] loaders, 
                                                    String sessionName);
    
}

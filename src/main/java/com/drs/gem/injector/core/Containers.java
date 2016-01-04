/*
 * Copyright (C) 2015 Diarsid
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.drs.gem.injector.core;

import java.util.HashMap;
import java.util.Map;

import com.drs.gem.injector.exceptions.NoSuchContainerException;

/**
 * Interface for handling multiple {@link Container} instances. Stores new containers 
 * and provides static methods for container creation, removing and obtaining existed 
 * containers by their names.
 * 
 * @author Diarsid
 * @see Container
 */
public interface Containers {
    
    Map<String, Container> containers = new HashMap<>();
    
    /**
     * Returns a new Container instance. It is implied that modules will
     * be explicitly declared later in container via {@link #declareModule(
     * java.lang.String, java.lang.String, ModuleType) 
     * .declareModule().} 
     * 
     * @param name  name of this new Container.
     * @return      new Container instance.
     */
    static Container buildContainer(String name) {        
        GemInjectorFactory factory = new GemInjectorFactory();
        Container container = new ModulesContainer(factory);
        containers.put(name, container);
        return container;
    }
    
    /**
     * Returns a new Container instance. Accepts array of 
     * {@link Declaration Declarations} as info about declared modules.
     * If Container has been initialized in this way, it is not permitted
     * to use {@link #declareModule(
     * java.lang.String, java.lang.String, ModuleType) method because 
     * modules information has been already given by declarations.
     * 
     * @param name  name of this new Container.
     * @param declarations  array of module declarations.
     * @return              new Container instance.
     * @see                 Declaration
     */
    static Container buildContainer(String name, Declaration... declarations) {
        GemInjectorFactory factory = new GemInjectorFactory();
        Container container = new ModulesContainer(factory, declarations);
        containers.put(name, container);
        return container;
    }
    
    /**
     * Returns {@link Container} specified by its name. If name is incorrect or Container 
     * with such name does not exists, {@link NoSuchContainerException} will be thrown.
     * 
     * @param name  name of required module. 
     * @return      required Container instance, if it exists.
     */
    static Container getContainer(String name) {
        Container container = containers.get(name);
        if ( container == null ) {
            throw new NoSuchContainerException(
                    "Container '" + name + "' does not exist.");
        } else {
            return container;
        }
    }
    
    /**
     * Removes specified {@link Container} instance.
     * 
     * @param name  name of container to be removed.
     * @return      true if container with this name was removed, false otherwise.
     */
    static boolean deleteContainer(String name) {
        return (containers.remove(name) != null);
    }
    
    /**
     * Deletes all {@link Container Containers}. 
     */
    static void clear() {
        containers.clear();
    }
}

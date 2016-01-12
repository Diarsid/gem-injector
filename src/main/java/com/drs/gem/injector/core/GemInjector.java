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
import java.util.Set;

import com.drs.gem.injector.exceptions.ContainerInitializationException;
import com.drs.gem.injector.exceptions.NoSuchContainerException;

/**
 * Class for handling multiple {@link Container} instances. Stores new containers 
 * and provides static methods for container creation, removing and obtaining existed 
 * containers by their names.
 * 
 * @author Diarsid
 * @see Container
 */
public final class GemInjector {
    
    private final static Map<String, Container> containers = new HashMap<>();
    
    private GemInjector() {}
    
    /**
     * <p>
     * Returns a new {@link Container} instance. In order to begin using it, 
     * it is necessary to declare modules which will be used in this 
     * container.</p>
     * 
     * <p>Read more about module declarations in {@link Container}.</p>
     * 
     * @param name  name of this new {@link Container}.
     * @return      new {@link Container} instance.
     */
    public static Container buildContainer(String name) {
        if (containers.containsKey(name)) {
            throw new ContainerInitializationException(
                    "Container with name '" + 
                    name + "' already exists.");
        } 
        Factory factory = new Factory();
        Container container = new ModulesContainer(factory);
        containers.put(name, container);
        return container;
    }
    
    /**
     * <p>Returns a new {@link Container} instance. Accepts array of 
     * {@link Declaration Declarations} as info about declared modules.
     * If {@link Container} has been initialized in this way, it is not permitted
     * to use {@link Container#declareModule(String, String, GemModuleType) .declareModule()} 
     * method.</p>
     * 
     * <p>Read more about module declarations in {@link Container}.</p>
     * 
     * @param name          name of this new {@link Container}.
     * @param declarations  array of module {@link Declaration declarations}.
     * @return              new {@link Container} instance.
     * @see                 Declaration
     */
    public static Container buildContainer(String name, Declaration... declarations) {
        if (containers.containsKey(name)) {
            throw new ContainerInitializationException(
                    "Container with name '" + 
                    name + "' already exists.");
        }
        Factory factory = new Factory();
        Container container = new ModulesContainer(factory, declarations);
        containers.put(name, container);
        return container;
    }
    
    /**
     * Returns previous created {@link Container} specified by its name. 
     * If name is incorrect or Container 
     * with this name does not exists, {@link NoSuchContainerException} 
     * will be thrown.
     * 
     * @param name  name of required module. 
     * @return      required Container instance, if it exists.
     */
    public static Container getContainer(String name) {
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
     * @param name  name of {@link Container container} that should be removed.
     * @return      true if {@link Container container} with this name 
     *              was removed, false otherwise.
     */
    public static boolean removeContainer(String name) {
        return (containers.remove(name) != null);
    }
    
    /**
     * Deletes all existed {@link Container containers}.
     */
    public static void clear() {
        containers.clear();
    }
    
    /**
     * Returns {@link Set} including names of all existed 
     * {@link Container containers}.
     * 
     * @return  names of all existed {@link Container containers}.
     */
    public static Set<String> getAllContainerNames() {
        return containers.keySet();
    }
}

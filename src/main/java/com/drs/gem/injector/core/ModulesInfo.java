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

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import com.drs.gem.injector.module.Module;

/**
 * Interface that hides {@link com.drs.gem.injector.core.ModulesContainer 
 * ModulesContainer} object to provide only getter-like methods that grants
 * access to information about modules, their types and constructors.
 * 
 * @author Diarsid
 * @see com.drs.gem.injector.core.ModulesContainer
 */
interface ModulesInfo {
       
    /**
     * Returns true if module of specified interface is singleton and false 
     * if it is prototype.
     * 
     * @param moduleInterface   class object of declared module interface
     * @return                  true if module type is singleton, false if
     *                          type is prototype
     * @see                     com.drs.gem.injector.core.ModulesContainer
     */
    boolean isModuleSingleton(Class moduleInterface);
    
    /**
     * Returns Map<Class, Module> that contains entries where key is module  
     * interface class object with Module Type SINGLETON and value is 
     * corresponding module instance which is fully initialized and ready 
     * to work.
     * 
     * @return  all initialized singletons declared in this container
     * @see     com.drs.gem.injector.core.ModulesContainer
     */
    Map<Class, Module> getSingletons();
    
    /**
     * Returns constructor that will be used to instantiate new objects
     * of specified module or {@link com.drs.gem.injector.core.ModuleBuilder
     * ModuleBuilder} of specified module.
     * 
     * @param moduleInterface   class object of declared module interface
     * @return                  appropriate module constructor
     * @see                     com.drs.gem.injector.core.ModulesContainer
     */
    Constructor getConstructor(Class moduleInterface);
    
    /**
     * Checks if appropriate constructor assigned to specified module exists.
     * 
     * @param moduleInterface   class object of declared module interface
     * @return                  true if there is constructor in this container 
     *                          that assigned to specified module
     * @see                     com.drs.gem.injector.core.ModulesContainer
     */
    boolean ifConstructorExists(Class moduleInterface);
    
    List<ModuleMetaData> getModuleDependenciesData(Class moduleInterface);
}

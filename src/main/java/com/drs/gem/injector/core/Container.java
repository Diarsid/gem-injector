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

import com.drs.gem.injector.module.Module;

/**
 * Interface represents Dependency Injection container. 
 * Container instance is used to declare new modules, initialize, 
 * inject dependencies into all modules declared via this container
 * instance and obtaining fully initialized module instances. 
 * 
 * @author Diarsid
 */
public interface Container {
    
    /**
     * Method to declare new module in container.
     * 
     * @param moduleInterface   canonical name of declared module interface.
     * @param moduleImplem      canonical name of declared module implementation class.
     * @param type              module's type, SINGLETON or PROTOTYPE.
     * @see                     com.drs.gem.injector.core.ModuleType
     */
    void declareModule(String moduleInterface, String moduleImplem, ModuleType type);
    
    /**
     * Start point of container work. Container Collects all information 
     * about modules and initialize all singleton modules that have been 
     * declared via this container instance. 
     */
    void init();
    
    /**
     * Force container to use {@link RecursiveInjector} for module instantiation 
     * instead of default {@link PriorityLoopInjector PriorityLoopInjector}. See {@link 
     * com.drs.gem.injector.core.ModulesContainer ModulesContainer} for more 
     * usage details and {@link com.drs.gem.injector.core.Injector
     * Injector} interface for explanation about difference between two injector types.
     */
    void useRecursiveInjector();
    
    /**
     * Returns appropriate module instance that is fully initialized.
     * 
     * @param <M>           actual module interface.
     * @param moduleClass   class object of actual module interface.
     * @return              fully initialized module.
     * @see                 com.drs.gem.injector.module.Module
     */
    <M extends Module> M getModule(Class<M> moduleClass);
}

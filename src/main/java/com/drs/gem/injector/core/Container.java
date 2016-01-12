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

import com.drs.gem.injector.module.GemModule;

/**
 * <p>Interface represents Dependency Injection container. 
 * Container instance is used to declare new modules, initialize, 
 * inject dependencies into all modules declared via this container
 * instance and obtaining fully initialized module instances.</p>
 * 
 * <p>Modules in container can be declared in two ways:<br></p>
 * <ul>
 * <li>using {@link #declareModule(String, String, GemModuleType) .declareModule()} 
 * method to provide module descriptions;</li>
 * <li>using {@link Declaration} interface during container creation in 
 * {@link GemInjector#buildContainer(java.lang.String, com.drs.gem.injector.core.Declaration...) 
 * GemInjector.buildContainer()}. It provides one abstract method 
 * {@link Declaration#getDeclaredModules() getDeclaredModules()} that returns 
 * {@link java.util.Set Set} of {@link GemModuleDeclaration} objects each of 
 * which describes its own module.</li>
 * </ul>
 * 
 * <p><b>Important note.</b><br>
 * You are not allowed to mix declaration types in one container. If you have 
 * chosen to use {@link #declareModule(String, String, GemModuleType) 
 * .declareModule()} you can not specify {@link Declaration} for the same 
 * container. And vise versa, if you have specified appropriate {@link 
 * Declaration} for this container it is not permitted to use {@link 
 * #declareModule(String, String, GemModuleType) .declareModule()} with it.</p>
 * 
 * <p><b>Important note №2.</b><br>
 * You can declare modules with {@link Declaration} or with {@link 
 * #declareModule(String, String, GemModuleType) .declareModule()} but you 
 * are not allowed to introduce new modules after {@link #init()} method has
 * been invoked.</p>
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
     * @see                     com.drs.gem.injector.core.GemModuleType
     */
    void declareModule(String moduleInterface, String moduleImplem, GemModuleType type);
    
    /**
     * Start point of container's work. <br>
     * Container collects all information 
     * about modules and initializes all singleton modules declared in
     * this container instance. 
     */
    void init();
    
    /**
     * <p>There are two different possible dependency collection and injection 
     * algorithms. One of them uses method recursion, other uses a lot of
     * loop with a number of if-else branching.</p>
     * 
     * <p>Default algorithm is one that uses loops and if-else conditions. 
     * This method forces container to use recursive algorithm.</p>
     * 
     * <p><b>It is not recommended to use recursive one</b>. It was retained 
     * only due to historical reasons.</p>
     * 
     */
    void useRecursiveInjector();
    
    /**
     * Returns fully initialized instance of specified module.
     * 
     * @param <M>           actual module interface.
     * @param moduleClass   class object of actual module interface.
     * @return              fully initialized module.
     * @see                 com.drs.gem.injector.module.GemModule
     */
    <M extends GemModule> M getModule(Class<M> moduleClass);
}

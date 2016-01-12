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
import java.lang.reflect.InvocationTargetException;

import com.drs.gem.injector.exceptions.ModuleInstantiationException;
import com.drs.gem.injector.exceptions.ModuleNotFoundException;
import com.drs.gem.injector.module.GemModule;
import com.drs.gem.injector.module.GemModuleBuilder;

/**
 * <p>RecursiveInjector implements {@link Injector} interface and is responsible 
 * for procedure of module object creation and its dependencies searching.</p>
 * 
 * This implementation uses recursive method invocations to collect module
 * dependencies and instantiate module object. It initially calls {@link #newModule(
 * Constructor, Class) .newModule()} method for asked GemModule and then
 * calls {@link #findDependencies(Class, Class[]) .findDependencies()}
 * method for searching direct dependencies of this module.
 * 
 * @author Diarsid
 */
class RecursiveInjector implements Injector {
    
    private final ModulesInfo modulesInfo;

    RecursiveInjector(ModulesInfo info) {
        this.modulesInfo = info;
    }
    
    /**
     * Accepts info required for new module creation.
     * If module has zero dependencies, it will be instantiated and returned.
     * If not, method will launch the process of dependencies searching. When
     * all required dependencies are found, module is being constructed.
     * 
     * @param   buildCons       appropriate module Constructor. It can be 
     *                          also constructor of module builder.
     * @param   moduleInterface class object of module interface
     * @return                  module object
     */    
    @Override
    public GemModule newModule(Constructor buildCons, Class moduleInterface){        
        Class[] dependencies = buildCons.getParameterTypes();
        try {
            
            Object obj;
            if (dependencies.length == 0){
                obj = buildCons.newInstance();
            } else {
                GemModule[] depModules = findDependencies(moduleInterface, dependencies);
                obj = buildCons.newInstance(depModules);
            }
            
            if (ifObjectIsModuleBuilder(obj)){
                GemModuleBuilder builder = (GemModuleBuilder) obj;
                return builder.buildModule();
            } else {
                return (GemModule) obj;
            }
            
        } catch (IllegalAccessException e){
            throw new ModuleInstantiationException(
                    moduleInterface.getCanonicalName() + 
                    " instantiation exception: illegel access.", e);
        } catch (IllegalArgumentException e){
            throw new ModuleInstantiationException(
                    moduleInterface.getCanonicalName() + 
                    " instantiation exception: " +
                    "illegel arguments in constructor.", e);
        } catch (InstantiationException e) {
            throw new ModuleInstantiationException(
                    moduleInterface.getCanonicalName() + 
                    " instantiation exception: " +
                    "underlying implementation class is abstract.", e);
        } catch (InvocationTargetException e) {
            throw new ModuleInstantiationException(
                    moduleInterface.getCanonicalName() + 
                    " instantiation exception: " +
                    "underlying constructor throws exception.", e);
        }
    }
    
    /**
     * Tries to find modules of classes given as dependencies. If 
     * they are singletons, tries to find them in container's singletons 
     * storage. If not, recursively launches the process of new module creation.
     * 
     * @param   moduleInterf    module whose dependencies will be found in method.
     * @param   dependencies    classes represent set of module's dependencies.
     * @return                  module objects which are dependencies for this module.
     */    
    private GemModule[] findDependencies(Class moduleInterf, Class[] dependencies){
        GemModule[] foundModules = new GemModule[dependencies.length];        
        for (int i = 0; i < foundModules.length; i++){
            
            Class dependencyModule = dependencies[i];
            
            if (modulesInfo.isModuleSingleton(dependencyModule)){
                GemModule module = modulesInfo.getSingletons().get(dependencyModule);
                if (module == null){
                    throw new ModuleNotFoundException(
                            "Dependency injection algorithm is broken in " + 
                            moduleInterf.getCanonicalName() + ": " + 
                            dependencyModule.getCanonicalName() + 
                            " not found in Injector::Map<Class, Module> modules.");
                }
                foundModules[i] = module;
            } else {
                Constructor buildCons = modulesInfo.getConstructorOfModule(dependencyModule);
                GemModule module = newModule(buildCons, dependencyModule);
                foundModules[i] = module;
            }            
        }
        return foundModules;
    }
    
    private boolean ifObjectIsModuleBuilder(Object obj){
        return GemModuleBuilder.class.isAssignableFrom(obj.getClass());
    }
}

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
import com.drs.gem.injector.module.Module;
import com.drs.gem.injector.module.ModuleBuilder;

/**
 * RecursiveInjector implements {@link com.drs.gem.injector.core.Injector
 * Injector} interface and is responsible for procedure of module object 
 * creation using Constructor of every module and search of its dependencies.
 * 
 * This implementation uses recursive method invocations to collect and instantiate 
 * module object. It initially calls {@link RecursiveInjector#newModule(
 * java.lang.reflect.Constructor, java.lang.Class)} method for asked Module and then
 * calls {@link RecursiveInjector#findDependencies(java.lang.Class, java.lang.Class[]) }
 * method to find direct dependencies of this module. If any dependency is not a 
 * singleton module or is a singleton and not placed in container`s singleton storage yet, 
 * {@link RecursiveInjector#newModule(java.lang.reflect.Constructor, java.lang.Class)} 
 * will be called again to instantiate it. And if it has its own dependencies method
 * {@link RecursiveInjector#findDependencies(java.lang.Class, java.lang.Class[]) } will 
 * be called again, and so on. NewModule() and findDependencies() methods will be called
 * recursively until all modules is found. 
 * 
 * @author Diarsid
 */
class RecursiveInjector implements Injector {
    
    private final ModulesInfo modulesInfo;

    RecursiveInjector(ModulesInfo info) {
        this.modulesInfo = info;
    }
    
    /**
     * Creates new module object and finds all required dependencies for it.
     * 
     * @param   buildCons       appropriate module Constructor. It can be 
     *                          also constructor of module builder.
     * @param   moduleInterface class object of module interface
     * @return                  module object
     */    
    @Override
    public Module newModule(Constructor buildCons, Class moduleInterface){        
        Class[] dependencies = buildCons.getParameterTypes();
        try {
            
            Object obj;
            if (dependencies.length == 0){
                obj = buildCons.newInstance();
            } else {
                Module[] depModules = findDependencies(moduleInterface, dependencies);
                obj = buildCons.newInstance(depModules);
            }
            
            if (ifObjectIsModuleBuilder(obj)){
                ModuleBuilder builder = (ModuleBuilder) obj;
                return builder.buildModule();
            } else {
                return (Module) obj;
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
     * Finds all modules which are represented by corresponding Class objects.
     * If class object is a class object of singleton module, then corresponding
     * module object will be retrieved from singletons storage.
     * If class object is a class object of prototype module, then new module 
     * will be created via {@link 
     * #newModule(java.lang.reflect.Constructor, java.lang.Class) .newModule()} method.
     * 
     * These action will be performed recursively until all required dependencies 
     * will be collected from storage (for singletons) or created afresh (for
     * prototypes).
     * 
     * @param   moduleInterf    module whose dependencies will be found in method
     * @param   dependencies    classes represent set of module's dependencies
     * @return                  module objects which are dependencies for this module
     */    
    private Module[] findDependencies(Class moduleInterf, Class[] dependencies){
        Module[] foundModules = new Module[dependencies.length];        
        for (int i = 0; i < foundModules.length; i++){
            
            Class dependencyModule = dependencies[i];
            
            if (modulesInfo.isModuleSingleton(dependencyModule)){
                Module module = modulesInfo.getSingletons().get(dependencyModule);
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
                Module module = newModule(buildCons, dependencyModule);
                foundModules[i] = module;
            }            
        }
        return foundModules;
    }
    
    private boolean ifObjectIsModuleBuilder(Object obj){
        return ModuleBuilder.class.isAssignableFrom(obj.getClass());
    }
}

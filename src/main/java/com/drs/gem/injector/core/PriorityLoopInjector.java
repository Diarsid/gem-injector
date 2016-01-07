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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.drs.gem.injector.exceptions.ModuleInstantiationException;
import com.drs.gem.injector.exceptions.ModuleNotFoundException;
import com.drs.gem.injector.module.Module;
import com.drs.gem.injector.module.ModuleBuilder;

/**
 * PriorityLoopInjector implements {@link com.drs.gem.injector.core.Injector
 * Injector} interface and is responsible for procedure of module object 
 * creation using Constructor of every module and search of its dependencies.
 * 
 * This implementation uses loop with if-else branching and temporary 
 * dependencies storage collections to initialize the module and find 
 * all its dependencies. 
 * 
 * @author Diarsid
 */
class PriorityLoopInjector implements Injector {
    
    private final ModulesInfo modulesInfo;
    private final Map<Class, Queue<Module>> moduleDependencies;
    private List<ModuleMetaData> moduleDependData;

    PriorityLoopInjector(ModulesInfo info) {
        this.modulesInfo = info;
        this.moduleDependencies = new HashMap<>();
        this.moduleDependData = null;
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
    public Module newModule(Constructor buildCons, Class moduleInterface) {        
        moduleDependencies.clear();
        moduleDependData = null;
        moduleDependData = modulesInfo.getModuleDependenciesData(moduleInterface);
        
        dependenciesPriorityReverseCheck();
        debug(moduleInterface);
        collectModuleDependenciesFor(moduleInterface);
        
        if ( moduleDependencies.containsKey(moduleInterface) ){
            Module module = moduleDependencies.get(moduleInterface).poll();
            moduleDependencies.clear();
            moduleDependData = null;
            return module;
        } else {
            throw new ModuleNotFoundException(
                    "Dependency injection algorithm broken in " + 
                    moduleInterface.getCanonicalName() +
                    ": unable to find corresponding module after dependecy " +
                    "searching process.");
        }        
    }
    
    private void debug(Class module) {
        System.out.println("  [CONTAINER] - begin assembling dependencies for " + module.getSimpleName());
        //debugPrintDeps();
    }
    
    private void debugPrintDeps() {
        System.out.println("  [CONTAINER] - dependencies:");
        for (int i = 0; i < moduleDependData.size(); i++) {
            System.out.println("  "+i+" : "+moduleDependData.get(i).getModuleInterface().getSimpleName() + "  <- "+moduleDependData.get(i).getType().toString());
        }
    }
    
    private void dependenciesPriorityReverseCheck() {
        List<ModuleMetaData> actualized = new ArrayList<>();
        actualized.add(moduleDependData.get(moduleDependData.size()-1));
        for ( int i = 0; i < actualized.size(); i++) {
            ModuleMetaData module = actualized.get(i);
            if ( module.getType().equals(ModuleType.PROTOTYPE) ) {
                /*
                   * If module is prototype, all its dependencies are 
                   * required because it will be initialized afresh
                   * every time. 
                   */
                Class[] moduleDepcies = module.getConstructor().getParameterTypes();
                for (Class moduleDepcy : moduleDepcies) {
                    actualized.add(modulesInfo.getMetaDataOfModule(moduleDepcy));
                }
            } else {
                /*
                   * If module is singleton it may not need to obtain, store 
                   * and initialize its dependencies because singleton may 
                   * already be initialized and stored in container.
                   */
                if ( ! modulesInfo.getSingletons().containsKey(module.getModuleInterface())) {
                    /*
                        * If module is singleton and it has not been initialized yet
                        * is only case when it is needed to obtain and store its
                        * dependencies.
                        */
                    Class[] moduleDepcies = module.getConstructor().getParameterTypes();
                    for (Class moduleDepcy : moduleDepcies) {
                        actualized.add(modulesInfo.getMetaDataOfModule(moduleDepcy));
                    }
                }
            }
        }
        Collections.sort(actualized);
        moduleDependData = actualized;
    }
    
    /**
     * Pivotal method which is responsible to module initialization.
     * Walks through the collection of ModuleMetaData objects which has been sorted 
     * by their natural ordering. Processes every ModuleMetaData object to initialize 
     * module which this ModuleMetaData represents and places new module object into
     * temporary storage.
     * 
     * All modules (including the asked one) will be placed in this temporary storage.
     */
    private void collectModuleDependenciesFor(Class searchedModule) {
        for ( ModuleMetaData metaData : moduleDependData ){
            
            Module module = null;
            if ( metaData.getType().equals(ModuleType.SINGLETON) ){
                module = findOrInitSingleton(metaData);
            } else {
                module = initNewModule(metaData);     
            }
            
            boolean found = (module != null);
            // if module is singleton it is not required to store
            // it in temporary dependencies storage because it is 
            // alreday stored in container singletons storage and 
            // is accessible from there. 
            boolean isNeeded
                    = !modulesInfo.isModuleSingleton(metaData.getModuleInterface());
            // But if this module is the main module searched now by 
            // this injector, it must be stored.
            if (metaData.getModuleInterface().equals(searchedModule)) {
                isNeeded = true;
            }
            
            if ( found && isNeeded ) {
                if (moduleDependencies.containsKey(metaData.getModuleInterface())) {
                    moduleDependencies.get(metaData.getModuleInterface()).add(module);
                } else {
                    Deque<Module> modules = new ArrayDeque<>();
                    modules.add(module);
                    moduleDependencies.put(metaData.getModuleInterface(), modules);
                }
            }
        }
    }   
    
    private Module findOrInitSingleton(ModuleMetaData metaData) {        
        if ( modulesInfo.getSingletons().containsKey(metaData.getModuleInterface()) ) {
            return modulesInfo.getSingletons().get(metaData.getModuleInterface());
        } else {
            return initNewModule(metaData);
        }       
    }
    
    private Module initNewModule(ModuleMetaData metaData) {
        if ( metaData.getConstructor().getParameterCount() == 0 ) {
            return constructModuleWithoutDepend(metaData);
        } else {
            return constructModuleWithDepend(metaData);                    
        }
    }      
    
    private Module constructModuleWithoutDepend(ModuleMetaData metaData) {
        Object obj = instantiateBuildObject(metaData);
        return getModuleFromBuildObject(obj);
    }
    
    /**
     * Given object can be either Module instance or ModuleBuilder instance. Method
     * resolves it and return appropriate Module object.
     * 
     * @param obj   Object that can be either Module instance or ModuleBuilder instance
     * @return      Module obtained or casted from specified object
     */
    private Module getModuleFromBuildObject(Object obj){
        if ( ifObjectIsModuleBuilder(obj) ) {
            ModuleBuilder builder = (ModuleBuilder) obj;
            return (Module) builder.buildModule();
        } else {
            return (Module) obj;
        }
    }
    
    /**
     * Constructs module if it has dependencies. Uses temporary modules storage
     * to obtain all necessary dependencies.
     * 
     * @param metaData  represents module that should be constructed
     * @return          Module object that is fully initialized
     */    
    private Module constructModuleWithDepend(ModuleMetaData metaData) {
        Class[] moduleDep = metaData.getConstructor().getParameterTypes();
        Module[] depModules = new Module[moduleDep.length];
        
        for ( int i = 0; i < moduleDep.length; i++ ) {
            Class dependency = moduleDep[i];
            if ( modulesInfo.isModuleSingleton(dependency) ) {
                if ( modulesInfo.getSingletons().containsKey(dependency) ) {
                    depModules[i] = modulesInfo.getSingletons().get(dependency);
                } else {
                    throw new ModuleNotFoundException(
                            "Dependency injection algorithm is broken: Module " +
                            dependency.getCanonicalName() + 
                            " not found in singleton container's storage during" +
                            " dependency collection for " + 
                            metaData.getModuleInterface().getCanonicalName() + " Module.");
                }
            } else {
                if ( moduleDependencies.containsKey(dependency) ) {
                    depModules[i] = moduleDependencies.get(dependency).remove();
                } else {
                    throw new ModuleNotFoundException(
                            "Dependency injection algorithm is broken: Module " +
                            dependency.getCanonicalName() + 
                            " not found in temporary modules storage during" +
                            " dependency collection for " + 
                            metaData.getModuleInterface().getCanonicalName() + " Module.");
                }   
            }            
        }
        
        Object obj = instantiateBuildObject(metaData, depModules);
        return getModuleFromBuildObject(obj);
    }
    
    /**
     * Instantiates new object using appropriate module constructor. This 
     * object can contain module itself or ModuleBuilder object. Method also 
     * hides try-catch block to wrap different reflection exceptions that can arise
     * while object creation with ModuleInstantiationException. 
     * 
     * @param metaData      represents module that will be instantiated
     * @param depModules    Module[] array represents previously collected dependencies 
     *                      of this module. If module doesn't have any dependencies array
     *                      should have zero length.
     * @return              Module object. It may be ModuleBuilder object.
     */
    private Object instantiateBuildObject(ModuleMetaData metaData, Module... depModules){
        try {
            if ( depModules.length == 0 ){
                return metaData.getConstructor().newInstance();
            } else {
                return metaData.getConstructor().newInstance(depModules);
            }
        } catch (IllegalAccessException e){
            throw new ModuleInstantiationException(
                    metaData.getModuleInterface().getCanonicalName() + 
                    " instantiation exception: illegel access.", e);
        } catch (IllegalArgumentException e){
            throw new ModuleInstantiationException(
                    metaData.getModuleInterface().getCanonicalName() + 
                    " instantiation exception: " +
                    "illegel arguments in constructor.", e);
        } catch (InstantiationException e) {
            throw new ModuleInstantiationException(
                    metaData.getModuleInterface().getCanonicalName() + 
                    " instantiation exception: " +
                    "underlying implementation class is abstract.", e);
        } catch (InvocationTargetException e) {
            throw new ModuleInstantiationException(
                    metaData.getModuleInterface().getCanonicalName() + 
                    " instantiation exception: " +
                    "underlying constructor throws exception.", e);
        } 
        
    }     
    
    private boolean ifObjectIsModuleBuilder(Object obj){
        return ModuleBuilder.class.isAssignableFrom(obj.getClass());
    }
}

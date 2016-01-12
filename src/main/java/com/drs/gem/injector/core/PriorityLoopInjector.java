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
import com.drs.gem.injector.module.GemModule;
import com.drs.gem.injector.module.GemModuleBuilder;

/**
 * <p>PriorityLoopInjector implements {@link Injector} interface and is 
 * responsible for procedure of module object creation and its 
 * dependencies searching.</p>
 * 
 * <p>This implementation uses loop with if-else branching and temporary 
 * dependencies storage collections to initialize the module and find 
 * all its dependencies. </p>
 * 
 * @author Diarsid
 */
final class PriorityLoopInjector implements Injector {
    
    private final ModulesInfo modulesInfo;
    private final Map<Class, Queue<GemModule>> assembledDepcyMods;
    private List<ModuleMetaData> declaredDepciesDatas;

    PriorityLoopInjector(ModulesInfo info) {
        this.modulesInfo = info;
        this.assembledDepcyMods = new HashMap<>();
        this.declaredDepciesDatas = null;
    }
    
    private void clearInjector() {
        assembledDepcyMods.clear();
        declaredDepciesDatas = null;
    }
    
    /**
     * Creates new module object and searches all required dependencies for it.
     * 
     * @param   buildCons       appropriate module Constructor. It can be 
     *                          also constructor of module builder.
     * @param   moduleInterface class object of module interface.
     * @return                  module object.
     */
    @Override
    public GemModule newModule(Constructor buildCons, Class moduleInterface) {        
        clearInjector();
        getActualDependenciesOf(moduleInterface);
        collectModuleDependenciesFor(moduleInterface);
        
        if ( assembledDepcyMods.containsKey(moduleInterface) ){
            GemModule module = assembledDepcyMods.get(moduleInterface).poll();
            clearInjector();
            return module;
        } else {
            clearInjector();
            throw new ModuleNotFoundException(
                    "Dependency injection algorithm is broken in " + 
                    moduleInterface.getCanonicalName() +
                    ": unable to find corresponding module after dependecy " +
                    "searching process.");
        }        
    } 
    
    /**
     * Checks if module actual dependencies have already been collected earlier.
     * If they have been collected, obtains them and proceeds. If not, invokes 
     * other method being responsible for collecting
     * them and stores them in module's ModuleMetaData corresponding object.
     * 
     * @param moduleInterface.
     */
    private void getActualDependenciesOf(Class moduleInterface) {
        if (modulesInfo.getMetaDataOfModule(moduleInterface).getDependencies() != null) {
            declaredDepciesDatas = modulesInfo.getMetaDataOfModule(moduleInterface).getDependencies();            
        } else {
            declaredDepciesDatas = modulesInfo.getModuleDependenciesData(moduleInterface);
            dependenciesPriorityReverseCheck();
            modulesInfo.getMetaDataOfModule(moduleInterface).setActualDependencies(declaredDepciesDatas);            
        } 
    }
    
    /**
     * Takes list of ModuleMetaData sorted by their priority and sweeps out 
     * those objects which also have lower priority of main required module 
     * but are not its real dependencies. 
     */
    private void dependenciesPriorityReverseCheck() {
        List<ModuleMetaData> actualized = new ArrayList<>();
        actualized.add(declaredDepciesDatas.get(declaredDepciesDatas.size()-1));
        for ( int i = 0; i < actualized.size(); i++) {
            ModuleMetaData module = actualized.get(i);
            if ( module.getType().equals(GemModuleType.PROTOTYPE) ) {
                // If module is prototype, all its dependencies are 
                // required because it will be initialized afresh
                // every time.
                Class[] moduleDepcies = module.getConstructor().getParameterTypes();
                for (Class moduleDepcy : moduleDepcies) {
                    actualized.add(modulesInfo.getMetaDataOfModule(moduleDepcy));
                }
            } else {
                // If module is singleton it may not need to obtain, store 
                // and initialize its dependencies because singleton may 
                // already be initialized and stored in container.                
                if ( ! modulesInfo.getSingletons().containsKey(module.getModuleInterface())) {
                    // If module is singleton and it has not been initialized yet
                    // is only case when it is needed to obtain and store its
                    // dependencies.
                    Class[] moduleDepcies = module.getConstructor().getParameterTypes();
                    for (Class moduleDepcy : moduleDepcies) {
                        actualized.add(modulesInfo.getMetaDataOfModule(moduleDepcy));
                    }
                }
            }
        }
        Collections.sort(actualized);
        declaredDepciesDatas = actualized;
    }
    
    /**
     * <p>Pivotal method being responsible for module initialization.
     * Walks through the collection of ModuleMetaData objects sorted 
     * by their priority. <br>
     * Processes every ModuleMetaData object to initialize 
     * module which this ModuleMetaData represents and places new module object into
     * temporary storage.</p>
     * 
     * <p>All modules (including the main required one) created during 
     * this method execution will be placed in this temporary storage.</p>
     */
    private void collectModuleDependenciesFor(Class searchedModule) {
        for ( ModuleMetaData metaData : declaredDepciesDatas ){
            
            GemModule module = null;
            if ( metaData.getType().equals(GemModuleType.SINGLETON) ){
                module = findOrInitSingleton(metaData);
            } else {
                module = initNewModule(metaData);     
            }
            
            boolean found = (module != null);
            // if module is singleton it is not required to store
            // it in temporary dependencies storage because it is 
            // already stored in container singletons storage and 
            // is accessible from there. 
            boolean isNeeded
                    = !modulesInfo.isModuleSingleton(metaData.getModuleInterface());
            // But if this module is the main module searched now by 
            // this injector, it must be stored even it is singleton.
            if (metaData.getModuleInterface().equals(searchedModule)) {
                isNeeded = true;
            }
            
            if ( found && isNeeded ) {
                if (assembledDepcyMods.containsKey(metaData.getModuleInterface())) {
                    assembledDepcyMods.get(metaData.getModuleInterface()).add(module);
                } else {
                    Deque<GemModule> modules = new ArrayDeque<>();
                    modules.add(module);
                    assembledDepcyMods.put(metaData.getModuleInterface(), modules);
                }
            }
        }
    }   
    
    private GemModule findOrInitSingleton(ModuleMetaData metaData) {        
        if ( modulesInfo.getSingletons().containsKey(metaData.getModuleInterface()) ) {
            return modulesInfo.getSingletons().get(metaData.getModuleInterface());
        } else {
            return initNewModule(metaData);
        }       
    }
    
    private GemModule initNewModule(ModuleMetaData metaData) {
        if ( metaData.getConstructor().getParameterCount() == 0 ) {
            return constructModuleWithoutDepend(metaData);
        } else {
            return constructModuleWithDepend(metaData);                    
        }
    }      
    
    private GemModule constructModuleWithoutDepend(ModuleMetaData metaData) {
        Object obj = instantiateBuildObject(metaData);
        return getModuleFromBuildObject(obj);
    }
    
    /**
     * Given object can be either GemModule instance or GemModuleBuilder instance. Method
     * detects it and return appropriate GemModule object.
     * 
     * @param obj   Object that can be either GemModule instance or 
     *              GemModuleBuilder instance.
     * @return      GemModule obtained or casted from specified object.
     */
    private GemModule getModuleFromBuildObject(Object obj){
        if ( ifObjectIsModuleBuilder(obj) ) {
            GemModuleBuilder builder = (GemModuleBuilder) obj;
            return (GemModule) builder.buildModule();
        } else {
            return (GemModule) obj;
        }
    }
    
    /**
     * Constructs module if it has dependencies. Uses temporary modules storage
     * to obtain all necessary dependencies.
     * 
     * @param metaData  represents module that should be constructed
     * @return          GemModule object that is fully initialized
     */    
    private GemModule constructModuleWithDepend(ModuleMetaData metaData) {
        Class[] moduleDep = metaData.getConstructor().getParameterTypes();
        GemModule[] depModules = new GemModule[moduleDep.length];
        
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
                            " collecting dependencies for " + 
                            metaData.getModuleInterface().getCanonicalName() + " Module.");
                }
            } else {
                if ( assembledDepcyMods.containsKey(dependency) ) {
                    depModules[i] = assembledDepcyMods.get(dependency).remove();
                } else {
                    throw new ModuleNotFoundException(
                            "Dependency injection algorithm is broken: Module " +
                            dependency.getCanonicalName() + 
                            " not found in temporary modules storage during" +
                            " collecting dependencies for " + 
                            metaData.getModuleInterface().getCanonicalName() + " Module.");
                }   
            }            
        }
        
        Object obj = instantiateBuildObject(metaData, depModules);
        return getModuleFromBuildObject(obj);
    }
    
    /**
     * Instantiates new object.<br>
     * It can be either the object of the module interface or the object 
     * of the module builder interface.
     * 
     * @param metaData      represents module that will be instantiated.
     * @param depModules    GemModule[] array representing previously collected 
     *                      dependencies of this module. If module doesn't 
     *                      have any dependencies array has zero length.
     * @return              GemModule object. It may be GemModuleBuilder object.
     */
    private Object instantiateBuildObject(ModuleMetaData metaData, GemModule... depModules){
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
        return GemModuleBuilder.class.isAssignableFrom(obj.getClass());
    }
}

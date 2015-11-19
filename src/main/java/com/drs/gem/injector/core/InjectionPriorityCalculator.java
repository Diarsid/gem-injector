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
import java.util.HashSet;
import java.util.Set;

import com.drs.gem.injector.exceptions.CyclicDependencyException;
import com.drs.gem.injector.exceptions.UndeclaredDependencyException;

/**
 * Class is responsible for calculation of module's real dependencies 
 * quantity. See detailed description in {@link 
 * com.drs.gem.injector.core.ModuleMetaData ModuleMetaData}.
 * 
 * @author  Diarsid
 * @see     com.drs.gem.injector.core.ModuleMetaData
 */
class InjectionPriorityCalculator {
    
    private final ModulesInfo info;

    InjectionPriorityCalculator(ModulesInfo info) {
        this.info = info;
    }
    
    /**
     * Calculates injection priority. It value is actually the quantity of 
     * modules dependencies.
     * See detailed description in 
     * {@link com.drs.gem.injector.core.ModuleMetaData ModuleMetaData}.
     * 
     * @param metaData    ModuleMetaData which actual priority should be calculated
     * @return      quantity of module dependencies, i.e. its injection priority
     * @see         com.drs.gem.injector.core.ModuleMetaData
     */    
    int calculatePriority(ModuleMetaData metaData){
        if (metaData.getConstructor().getParameterCount() != 0){
            return this.calculateFullDependenciesQty(metaData);
        } else {
            return 0;
        }     
    }
    
    /**
     * Takes module's Constructor from ModuleMetaData objects and 
 obtains all its parameters which are modules. Then takes constructor 
     * of each those modules and obtains their parameters too, calculates 
     * them and so on, until all zero-dependencies modules will be reached.
     * Sum of all those dependencies will determine real dependencies quantity
     * of this concrete module.
     * 
     * In fact, this method will walk through the dependencies graph begins 
     * from this module and will calculate all nodes (dependencies in graph)
     * that it can reach from this module until it will find all nodes that 
     * don't have any dependencies. 
     * 
     * @param metaData    ModuleMetaData contains description of module which
              real dependencies quantity should be calculated
     * @return      quantity of module dependencies
     */
    private int calculateFullDependenciesQty(ModuleMetaData metaData){
        
        Set<Constructor> dependencies = new HashSet<>();
        
        Class[] firstLevelDependencies = metaData.getConstructor().getParameterTypes();
        for (Class firstLevelDependency : firstLevelDependencies){
            checkIfDependencyDeclaredAsModule(firstLevelDependency);
            Constructor dependencyConstr = info.getConstructor(firstLevelDependency);
            dependencies.add(dependencyConstr);
        }
        
        Set<Constructor> currentLevelDependencies = new HashSet<>(dependencies);
        Set<Constructor> nextLevelDependencies = new HashSet<>();
        
        while( ! currentLevelDependencies.isEmpty()){
            
            for (Constructor dependency : currentLevelDependencies){
                Class[] nestedDependencies = dependency.getParameterTypes();
                for (Class dependencyClass : nestedDependencies){
                    checkIfModuleHasCyclicDependencies(
                            metaData, dependencyClass, dependency.getDeclaringClass());
                    checkIfDependencyDeclaredAsModule(dependencyClass);
                    Constructor depCon = info.getConstructor(dependencyClass);
                    if ( ! dependencies.contains(depCon)){
                        nextLevelDependencies.add(depCon);
                    }                        
                }
            }
            
            currentLevelDependencies.clear();
            currentLevelDependencies.addAll(nextLevelDependencies);
            dependencies.addAll(nextLevelDependencies);
            nextLevelDependencies.clear();
        } 
        return dependencies.size();
    }
    
    private void checkIfModuleHasCyclicDependencies(
            ModuleMetaData metaData, Class dependency, Class declaration){
        
        if (metaData.getModuleInterface().equals(dependency)){
            throw new CyclicDependencyException(
                    "Cyclic dependency detected: " + 
                    metaData.getModuleInterface().getCanonicalName() + " depend on " +
                    declaration.getCanonicalName() + 
                    " which has cyclic dependency on " + 
                    metaData.getModuleInterface().getCanonicalName());
        }
    }
    
    private void checkIfDependencyDeclaredAsModule(Class dependencyClass){
        if ( ! info.ifConstructorExists(dependencyClass)){
            throw new UndeclaredDependencyException(
                    "Modules dependency injection broken: " +
                    dependencyClass.getCanonicalName() +
                    " does not declared as module.");
        }
    }
}

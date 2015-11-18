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
 *
 * @author Diarsid
 */
class InjectionPriorityCalculator {
    
    private final ModulesInfo info;

    InjectionPriorityCalculator(ModulesInfo info) {
        this.info = info;
    }
    
    
    
    int calculatePriority(InjectionPriority ip){
        if (ip.getConstructor().getParameterCount() != 0){
            return this.calculateFullDependenciesQty(ip);
        } else {
            return 0;
        }     
    }
    
    private int calculateFullDependenciesQty(InjectionPriority ip){
        
        Set<Constructor> dependencies = new HashSet<>();
        
        Class[] firstLevelDependencies = ip.getConstructor().getParameterTypes();
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
                            ip, dependencyClass, dependency.getDeclaringClass());
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
            InjectionPriority ip, Class dependency, Class declaration){
        
        if (ip.getModuleInterface().equals(dependency)){
            throw new CyclicDependencyException(
                    "Cyclic dependency detected: " + 
                    ip.getModuleInterface().getCanonicalName() + " depend on " +
                    declaration.getCanonicalName() + 
                    " which has cyclic dependency on " + 
                    ip.getModuleInterface().getCanonicalName());
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

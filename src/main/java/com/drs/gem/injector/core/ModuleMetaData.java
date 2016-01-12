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
import java.util.Objects;

import com.drs.gem.injector.exceptions.DependencyCalculationException;

/**
 * <p>Class representing module, provides data about it and contains
 * priority of each module when it is to be initialized.</p>
 * 
 * <p>It implements Comparable therefore collections of its objects could be 
 * sorted. 
 * Their natural ordering is determined by object's int priority field. Its value 
 * means how much real dependencies has module described by this
 * ModuleMetaData. Not only direct dependencies (constructor arguments)
 * are counted, but also dependencies of underlying modules, i.e. all
 * quantity of modules in dependencies graph that lead to this module.</p>
 * 
 * <p>Information about every module dependencies quantity is required 
 * in order to compose sequential module initialization order when all
 * modules without dependencies will be initialized firstly, modules 
 * with one dependency secondarily and so on. This approach ensures that
 * when it comes time for every module to be initialized, all required 
 * dependencies will be already initialized and injection will be 
 * performed properly.</p>
 * 
 * @author  Diarsid
 * @see     InjectionPriorityCalculator 
 */
final class ModuleMetaData implements Comparable<ModuleMetaData>{
    
    private final Class moduleInterface;
    private final Constructor moduleConstructor;
    private final GemModuleType type;
    private List<ModuleMetaData> actualDeps;
    private int priority;
    
    ModuleMetaData(Class moduleInterface, Constructor cons, GemModuleType type){        
        this.moduleInterface = moduleInterface;
        this.moduleConstructor = cons;
        this.type = type;
        this.priority = -1;
    }
    
    Constructor getConstructor(){
        return this.moduleConstructor;
    }
        
    Class getModuleInterface(){
        return moduleInterface;
    }
    
    GemModuleType getType(){
        return type;
    }
    
    /**
     * This priority has initial value of -1 means that its actual 
     * dependencies quantity has not been calculated yet. 
     * {@link InjectionPriorityCalculator} is responsible for dependencies 
     * quantity calculation.
     * 
     * @param   priority.
     * @see     InjectionPriorityCalculator.
     */
    void setPriority(int priority){
        this.priority = priority;
    }
    
    void setActualDependencies(List<ModuleMetaData> actualDatas) {
        this.actualDeps = actualDatas;
    }
    
    List<ModuleMetaData> getDependencies() {
        return this.actualDeps;
    }
        
    private void checkIfDependenciesIsCalculated(){
        if ( priority < 0 ){
            throw new DependencyCalculationException(
                    "Exception in ModuleMetaData(" +
                    moduleConstructor.getClass().getCanonicalName() +
                    "): injection priority of this object was not calculated "
                            + "before collection with ModuleMetaData "
                            + "objects ordering."
            );
        }
    }
    
    @Override
    public int compareTo(ModuleMetaData other){
        checkIfDependenciesIsCalculated();
        if ( this.priority < other.priority ){
            return -1;
        } else if ( this.priority > other.priority ){
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int hashCode() {
        checkIfDependenciesIsCalculated();
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.moduleConstructor);
        hash = 79 * hash + Objects.hashCode(this.priority);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        checkIfDependenciesIsCalculated();
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ModuleMetaData other = (ModuleMetaData) obj;
        if (!Objects.equals(this.moduleConstructor, other.moduleConstructor)) {
            return false;
        }
        if (!Objects.equals(this.priority, other.priority)) {
            return false;
        }
        return true;
    }
}

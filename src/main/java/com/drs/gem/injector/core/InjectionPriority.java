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
import java.util.Objects;

import com.drs.gem.injector.exceptions.DependencyCalculationException;

/**
 *
 * @author Diarsid
 */
class InjectionPriority implements Comparable<InjectionPriority>{
    
    private final Class moduleInterface;
    private final Constructor moduleConstructor;
    private final ModuleType type;
    private int injectionPriority;
    
    InjectionPriority(Class moduleInterface, Constructor cons, ModuleType type){        
        this.moduleInterface = moduleInterface;
        this.moduleConstructor = cons;
        this.type = type;
        this.injectionPriority = -1;
    }
    
    Constructor getConstructor(){
        return this.moduleConstructor;
    }
        
    Class getModuleInterface(){
        return moduleInterface;
    }
    
    ModuleType getType(){
        return type;
    }
    
    void setPriority(int priority){
        injectionPriority = priority;
    }
    
    private void checkIfDependenciesIsCalculated(){
        if ( injectionPriority < 0 ){
            throw new DependencyCalculationException(
                    "Exception in InjectionPriority(" +
                    moduleConstructor.getClass().getCanonicalName() +
                    "): injection priority was not calculated."
            );
        }
    }
    
    @Override
    public int compareTo(InjectionPriority other){
        checkIfDependenciesIsCalculated();
        if ( this.injectionPriority < other.injectionPriority ){
            return -1;
        } else if ( this.injectionPriority > other.injectionPriority ){
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
        hash = 79 * hash + Objects.hashCode(this.injectionPriority);
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
        final InjectionPriority other = (InjectionPriority) obj;
        if (!Objects.equals(this.moduleConstructor, other.moduleConstructor)) {
            return false;
        }
        if (!Objects.equals(this.injectionPriority, other.injectionPriority)) {
            return false;
        }
        return true;
    }
}

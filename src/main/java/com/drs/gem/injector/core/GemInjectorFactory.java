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

/**
 * Simple factory which is responsible for instantiation of
 * new objects that are required by demand in other classes.
 * 
 * @author  Diarsid
 * @see     com.drs.gem.injector.core.ModuleMetaData
 * @see     com.drs.gem.injector.core.InjectionPriorityCalculator
 * @see     com.drs.gem.injector.core.RecursiveInjector
 */
class GemInjectorFactory {

    GemInjectorFactory() {
    }
    
    /**
     * Factory method returns new {@link com.drs.gem.injector.core.RecursiveInjector RecursiveInjector}
     * object. 
     * 
     * @param info  ModulesInfo interface that used to obtain info about modules.
     * @return      new RecursiveInjector object.
     * @see         com.drs.gem.injector.core.ModulesInfo
     */
    Injector buildRecursiveInjector(ModulesInfo info){
        return new RecursiveInjector(info);
    }
    
    /**
     * Factory method returns new {@link com.drs.gem.injector.core.RecursiveInjector RecursiveInjector}
     * object. 
     * 
     * @param info  ModulesInfo interface that used to obtain info about modules.
     * @return      new RecursiveInjector object.
     * @see         com.drs.gem.injector.core.ModulesInfo
     */
    Injector buildLoopInjector(ModulesInfo info){
        return new LoopInjector(info);
    }
    
    /**
     * Factory method returns new {@link com.drs.gem.injector.core.ModuleMetaData 
     * ModuleMetaData} object. 
     * 
     * @param moduleInterface   canonical name of module interface.
     * @param cons              Constructor represent this module constructor.
     * @param type              module type.
     * @return                  ModuleMetaData instance.
     * @see                     com.drs.gem.injector.core.ModuleMetaData
     * @see                     com.drs.gem.injector.core.ModuleType
     */
    ModuleMetaData buildMetaData(
            Class moduleInterface,
            Constructor cons, 
            ModuleType type){
        
        return new ModuleMetaData(moduleInterface, cons, type);
    }
    
    /**
     * Factory method returns new 
     * {@link com.drs.gem.injector.core.InjectionPriorityCalculator 
     * InjectionPriorityCalculator} object.
     * 
     * @param info  ModulesInfo interface used to obtain info about modules.
     * @return      new InjectionPriorityCalculator object.
     * @see         com.drs.gem.injector.core.InjectionPriorityCalculator
     * @see         com.drs.gem.injector.core.ModulesInfo
     */
    InjectionPriorityCalculator buildCalculator(ModulesInfo info){
        return new InjectionPriorityCalculator(info);
    }
    
    /**
     * Factory method returns {@link com.drs.gem.injector.core.ContainerHelper
     * ModuleVerifier} object.
     * 
     * @return new ModuleVerifier object
     */
    ContainerHelper buildHelper(){
        return new ContainerHelper();
    }
}

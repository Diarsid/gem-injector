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

import com.drs.gem.injector.exceptions.InvalidBuilderDeclarationException;
import com.drs.gem.injector.exceptions.InvalidModuleImplementationException;
import com.drs.gem.injector.exceptions.InvalidModuleInterfaceException;
import com.drs.gem.injector.exceptions.ModuleDeclarationException;
import com.drs.gem.injector.module.Module;
import com.drs.gem.injector.module.ModuleBuilder;

/**
 * Contains methods for module verification and other helper methods.
 * 
 * @author Diarsid
 */
class ContainerHelper {

    ContainerHelper() {
    }
    
    /**
     * Determines if given module has appropriate {link@ 
     * com.drs.gem.injector.module.ModuleBuilder ModuleBuilder} class
     * in its package. If it doesn't returns null.
     * 
     * @param moduleImplemName  module implementation class canonical name
     * @return                  Class of ModuleBuilder or null if specified 
     *                          module doesn't have one.
     * @see                     com.drs.gem.injector.module.ModuleBuilder
     */
    Class ifModuleHasBuilder(String moduleImplemName){
        String moduleBuilderName = moduleImplemName + "Builder";
        try {
            Class builderClass = Class.forName(moduleBuilderName);
            return builderClass;
        } catch (ClassNotFoundException e){
            return null;
        }
    }
    
    /**
     * Check if given class actually implement {link@ 
     * com.drs.gem.injector.module.ModuleBuilder ModuleBuilder} interface.
     * 
     * @param builderClass  class to check if it is actual implementation 
     *                      of ModuleBuilder interface
     * @see                 com.drs.gem.injector.module.ModuleBuilder
     */
    void verifyModuleBuilder(Class builderClass){
        if ( ! ModuleBuilder.class.isAssignableFrom(builderClass) ){
            throw new InvalidBuilderDeclarationException(
                    "Invalid module builder implementation: " + 
                    builderClass.getCanonicalName() + 
                    "does not implement " + 
                    ModuleBuilder.class.getCanonicalName());
        }
    }
    
    /**
     * Verify that given class is actually is interface and is subinterface 
     * of {@link com.drs.gem.injector.module.Module Module} interface.
     * @param moduleInterface   class to verify if it is interface and extends 
     *                          Module interface
     * @see                     com.drs.gem.injector.module.Module
     */
    void verifyModuleInterface(Class moduleInterface){
        if ( ! moduleInterface.isInterface()){
            throw new ModuleDeclarationException(
                    "Incorrect module declaration: class " + 
                    moduleInterface.getCanonicalName() +
                    " is not interface.");
        } else if ( ! Module.class.isAssignableFrom(moduleInterface)){
            throw new InvalidModuleInterfaceException(
                    "Invalid Module interface: " + 
                    moduleInterface.getCanonicalName() + 
                    " does not implement " + Module.class.getCanonicalName() + ".");
        }
    }
    
    /**
     * Verify if specified module implementation class actually implement
     * specified module interface.
     * 
     * @param moduleInterface   module interface which given class should implement
     * @param moduleImplem      module implementation class that should be verified
     */
    void verifyModuleImplementation(Class moduleInterface, Class moduleImplem){
        if ( ! moduleInterface.isAssignableFrom(moduleImplem)){
            throw new InvalidModuleImplementationException(
                    "Invalid module implementation: " + 
                    moduleImplem.getCanonicalName() + 
                    "does not implement " + 
                    moduleInterface.getCanonicalName() + ".");
        }
    }
    
    /**
     * Returns module class by its canonical name or throws an exception.
     * 
     * @param className module canonical class name
     * @return          module actual class
     */
    Class getClassByName(String className){
        Class classImpl;
        try {
            classImpl = Class.forName(className);
            return classImpl;
        } catch (ClassNotFoundException e){
            throw new ModuleDeclarationException(
                    "Incorrect module declaration: class " +
                    className + " does not exist.");
        }
    }
    
    /**
     * Verifies that constructors array argument has actually only one 
     * constructor. Otherwise an exception will be thrown.
     * 
     * @param constructors constructors array to verify that it has only one object
     */
    void verifyIfModuleHasOneConstructor(Constructor... constructors){
        if (constructors.length != 1){
            Class moduleBuildClass = constructors[0].getDeclaringClass();
            if (moduleBuildClass.getSimpleName().contains("ModuleBuilder")){
                throw new InvalidBuilderDeclarationException(
                        "Invalid module builder implementation: " + 
                        moduleBuildClass.getCanonicalName() + 
                        " has more than one declared constructor.");
            } else {
                throw new InvalidModuleImplementationException(
                        "Invalid module implementation: " + 
                        moduleBuildClass.getCanonicalName() + 
                        " has more than one declared constructor.");
            }
        }
    }
}

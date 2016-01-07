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

import com.drs.gem.injector.exceptions.InvalidModuleBuilderImplementationException;
import com.drs.gem.injector.exceptions.InvalidModuleImplementationException;
import com.drs.gem.injector.exceptions.ModuleDeclarationException;
import com.drs.gem.injector.module.InjectedConstructor;
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
     * @param moduleImplemName  module implementation class canonical name.
     * @return                  Class of ModuleBuilder or null if specified 
     *                          module doesn't have one.
     * @see                     com.drs.gem.injector.module.ModuleBuilder.
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
     * Checks if given class actually implements {link@ 
     * com.drs.gem.injector.module.ModuleBuilder ModuleBuilder} interface.
     * 
     * @param builderClass  class that must be checked if it implements ModuleBuilder interface.
     * @see                 com.drs.gem.injector.module.ModuleBuilder.
     */
    void verifyModuleBuilder(Class builderClass){
        if ( ! ModuleBuilder.class.isAssignableFrom(builderClass) ){
            throw new InvalidModuleBuilderImplementationException(
                    "Invalid module builder implementation: " + 
                    builderClass.getCanonicalName() + 
                    "does not implement " + 
                    ModuleBuilder.class.getCanonicalName());
        }
    }
    
    /**
     * Verifies that given class is interface and it extends 
     * {@link com.drs.gem.injector.module.Module Module} interface.
     * @param moduleInterface   class that should be verified if it is interface and extends 
     *                          Module interface.
     * @see                     com.drs.gem.injector.module.Module.
     */
    void verifyModuleInterface(Class moduleInterface){
        if ( ! moduleInterface.isInterface()){
            throw new ModuleDeclarationException(
                    "Invalid module declaration: class " + 
                    moduleInterface.getCanonicalName() +
                    " is not interface.");
        } else if ( ! Module.class.isAssignableFrom(moduleInterface)){
            throw new InvalidModuleImplementationException(
                    "Invalid module interface: " + 
                    moduleInterface.getCanonicalName() + 
                    " does not implement " + Module.class.getCanonicalName() + ".");
        }
    }
    
    /**
     * Verifies if specified module implementation class actually implements
     * specified module interface.
     * 
     * @param moduleInterface   module interface which given class should implement.
     * @param moduleImplem      module implementation class that should be verified.
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
     * @param className module canonical class name.
     * @return          module actual class.
     */
    Class getClassByName(String className){
        Class classImpl;
        try {
            classImpl = Class.forName(className);
            return classImpl;
        } catch (ClassNotFoundException e){
            throw new ModuleDeclarationException(
                    "Invalid module declaration: class " +
                    className + " does not exist.");
        }
    }
    
    /**
     * Finds correct constructor among all constructors in the class. Constructor that
     * will be used for dependency injection must be marked with {@link 
     * InjectedConstructor} annotation. Module or ModuleBuilder class should have
     * only one constructor with this annotation otherwise an exception will be thrown.
     * 
     * @param constructors  constructors array that should be checked if it has only one object.
     * @return              constructor of processed module.
     */
    Constructor resolveModuleConstructors(Constructor... constructors){
        Constructor required = null;
        int annotatedConstrQty = 0;
        for (Constructor constr : constructors) {
            if ( constr.getAnnotation(InjectedConstructor.class) != null ) {
                required = constr;
                annotatedConstrQty++;
            }
        }
        
        if ( annotatedConstrQty == 1 ) {
            return required;
        } else {            
            Class moduleBuildClass = constructors[0].getDeclaringClass();
            boolean isBuilder = ModuleBuilder.class.isAssignableFrom(moduleBuildClass);
            
            if (isBuilder) {
                if ( annotatedConstrQty == 0 ) {
                    throw new InvalidModuleBuilderImplementationException(
                            "Invalid module builder implementation: " + 
                            moduleBuildClass.getCanonicalName() + 
                            " has no one constructor annotated with " +
                            "@InjectedConstructor annotation.");
                } else {
                    throw new InvalidModuleBuilderImplementationException(
                            "Invalid module builder implementation: " + 
                            moduleBuildClass.getCanonicalName() + 
                            " has more than one constructor annotated with " +
                            "@InjectedConstructor annotation.");
                }
            } else {
                if ( annotatedConstrQty == 0 ) {
                    throw new InvalidModuleImplementationException(
                            "Invalid module implementation: " + 
                            moduleBuildClass.getCanonicalName() + 
                            " has no one constructor annotated with " +
                            "@InjectedConstructor annotation.");
                } else {
                    throw new InvalidModuleImplementationException(
                            "Invalid module implementation: " + 
                            moduleBuildClass.getCanonicalName() + 
                            " has more than one constructor annotated with " +
                            "@InjectedConstructor annotation.");
                }
            }
        }
    }
}

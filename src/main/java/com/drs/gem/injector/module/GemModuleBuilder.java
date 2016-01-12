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

package com.drs.gem.injector.module;

/**
 * <p>This interface represents advanced mechanism of module object instantiation. 
 * It provides the facility to perform some preliminary 
 * actions or computations preceding the direct module object instantiation.
 * </p>
 *
 * <p>GemModuleBuilder introduces one abstract method {@link 
 * #buildModule() .buildModule()} that must be implemented in concrete
 * module builder class. This method must contain all preliminary actions 
 * that should be performed before or after module object can be returned 
 * for further usage.</p>
 * 
 * <p>It is not necessary to use module builder anyway, it just an
 * opportunity to incorporate different actions in the module object
 * creation process and grant it with more flexible behavior. </p>
 * 
 * <p>Because of this container is based on constructor injection principle all 
 * dependencies required for {@link #buildModule() .buildModule()} method 
 * execution must be declared as this module builder constructor
 * arguments.</p>
 * 
 * <p>In order to container can detect module builder class it must satisfy 
 * two requirements:</p>
 * <ul>
 * <li>it must be placed in the same package where module implementation class
 * itself is located;</li>
 * <li>it must have the same name as module implementation class name
 * but ends with "Builder" word. For example, if module class has name 
 * "MyModuleImpl" then module builder must have name "MyModuleImplBuilder".</li>
 * </ul>
 * 
 * <p>Just like module implementation classes, module builder implementation 
 * classes can have more than one constructor. And, just as 
 * module implementation classes, if module builder implementation class
 * has more than one constructor then one of them must be annotated with
 * {@link InjectedConstructor @InjectedConstructor}. If there is only one
 * constructor in class, it is not need to annotate it with this
 * annotation.</p>
 * 
 * Let's depict some example of module builder.
 * 
 * <pre> <code>
 * // This is the builder that should be used to assemble SomeModule object. 
 * class SomeModuleBuilder implements GemModuleBuilder&#60;SomeModule&#62; {
 *   
 *   private FirstModule first;
 *   private SecondModule second;
 *   
 *   // this constructor will not be used by container.
 *   SomeModuleBuilder(FirstModule firstModule, String someString, int someData) {
 *       this.first = firstModule;
 *   }
 *   
 *   // and this one, marked with appropriate annotation, will be used by 
 *   // container. FirstModule and SecondModule will be detected as module
 *   // builder dependencies and injected by container.
 *   &#64;InjectedConstructor
 *   SomeModuleBuilder(FirstModule first, SecondModule second) {
 *       this.first = first; 
 *       this.second = second;
 *   }
 *   
 *   // method to assemble required module.
 *   // Container will invoker this method during module initialization.
 *   &#64;Override 
 *   public SomeModule buildModule(){
 *       ... any preliminary actions can go here ...
 *       SomeModule mod = new SomeModule(... all required dependencies... );
 *       ... any actions with module object ...
 *       return mod;
 *   }
 * }
 * </code> </pre>
 * 
 * @author Diarsid
 */
public interface GemModuleBuilder<M extends GemModule> {
    
    /**
     * Returns fully initialized module implementation class instance. For more 
     * details see this interface description above.
     * 
     * @return fully initialized module instance
     */
    M buildModule();
}

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
 * Interface represents advanced mechanism of module implementation class
 * object instantiation. It provides the ability to perform some preliminary 
 * actions or computations that precede the direct module object instantiation.
 * 
 * Because of this container is based on constructor injection principle all 
 * dependencies that are required for further module initialization and preliminary 
 * actions execution must be declared as this ModuleBuilder constructor arguments.
 * 
 * In order to perform some actions between reception of injected dependencies
 * and immediate module object instantiation, implement these actions in {@link 
 * #buildModule() .buildModule()} method body. After it, {@link 
 * #buildModule() .buildModule()} method should return fully initialized and ready
 * to work module object. 
 * 
 * This ModuleBuilder implementation class must have only one constructor with all 
 * explicitly declared module dependencies. Container finds ModuleBuilder constructor, 
 * resolves its dependencies (if they have been declared as modules in this 
 * constructor), creates ModuleBuilder instance and calls .buildModule() method. 
 * Container ignores any other ModuleBuilder implementation class methods, that's why
 * all preliminary actions must be performed directly in .buildModule() method body.
 * 
 * @author Diarsid
 */
public interface ModuleBuilder<M extends Module> {
    
    /**
     * Returns fully initialized module implementation class instance. For more 
     * details see this interface description above.
     * 
     * @return fully initialized module instance
     */
    M buildModule();
}

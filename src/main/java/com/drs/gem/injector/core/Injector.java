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

import com.drs.gem.injector.module.Module;

/**
 * Interface represents object which is responsible for immediate object 
 * initialization and resolving object's dependencies.
 * 
 * There are two implementations of this interface {@link
 * com.drs.gem.injector.core.RecursiveInjector RecursiveInjector} and {@link 
 * com.drs.gem.injector.core.LoopInjector LoopInjector}.
 * 
 * RecursiveInjector uses recursive method invocations to collect dependencies 
 * and instantiate module object while LoopInjector uses loop with a lot of  
 * if-else branching and temporary dependencies storage collections to perform 
 * similar operations.
 * 
 * For more implementation details see appropriate classes.
 * 
 * @author Diarsid
 * @see com.drs.gem.injector.core.RecursiveInjector
 * @see com.drs.gem.injector.core.LoopInjector
 */
public interface Injector {
    
    /**
     * Creates new module object and finds all required dependencies for it.
     * 
     * @param   buildCons       appropriate module Constructor. It can be 
     *                          also constructor of module builder.
     * @param   moduleInterface class object of module interface
     * @return                  module object
     */
    Module newModule(Constructor buildCons, Class moduleInterface);
}

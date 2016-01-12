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

import com.drs.gem.injector.module.GemModule;

/**
 * <p>Interface representing object being responsible for immediate module  
 * object initialization, searching and injecting module's dependencies.</p>
 * 
 * <p>There are two implementations of this interface {@link RecursiveInjector} 
 * and {@link PriorityLoopInjector}.</p>
 * 
 * <p>RecursiveInjector uses recursive method invocations in order to collect, initialize 
 * and inject all required dependencies of specified module. It looks for 
 * dependencies from module constructor parameters, gets constructors of those
 * parameters and recursively repeats until all dependencies will be found
 * and algorithm reaches all modules without any dependencies.</p>
 * 
 * <p>In fact, 
 * RecursiveInjector walks through the dependency graph from the required module
 * as graph entry node. It walks through the graph until it will find all nodes 
 * that are reachable from entry node and stops when all such nodes-dependencies will 
 * be found. When node without its own dependencies is found, it is initialized and stored.
 * Then method returns recursively to its previous recursive invocation steps where  
 * it can initialize higher-level modules using existed already initialized lower-level 
 * modules. It returns from recursive invocation stack frame by frame until it will 
 * return to entry node and to the first method's call. At that
 * moment all required lower-level modules are initialized and collected, so that
 * searched module can be instantiated and injected with its dependencies.</p>
 * 
 * <p>PriorityLoopInjector has another work concept. During container initialization
 * process all modules are evaluated on how much real dependency they actually
 * have.<br>
 * This process looks like the process of walking through the dependency graph 
 * in RecursiveInjector, but there aren't any module initializations during this 
 * passage through the graph. It only counts the quantity of nodes in dependencies
 * graph of every module existing in this container.</p>
 * 
 * <p>When real dependencies quantity has been counted separately for each module, 
 * Injector ranks 
 * all modules by their priority where priority is number of module calculated
 * dependencies. </p>
 * 
 * <p>When it is required to assemble any module with <b>X</b> priority, PriorityLoopInjector 
 * obtains from container the list of all modules having priorities in range
 * from <b>0</b> to <b>X-1</b> and adds required module to the end of this list.
 * Then PriorityLoopInjector checks if there are modules in this
 * list that are not actually required for main module assembling process and sweep
 * them out of list. Then injector begins initializing modules one by one according to their
 * priority in ascending order and saving them. This approach ensures that when 
 * some module is to be initialized, all other modules that module
 * needs are already initialized because they have lower priority. <br>
 * Injector continues assembling and storing initialized modules while it 
 * reaches the end of the list where the main required module is located. Then injector 
 * assembles main module, returns it and stop its work.</p>
 * 
 * <p>For more implementation details see comments in appropriate Injector 
 * implementation classes.</p>
 * 
 * @author Diarsid
 * @see com.drs.gem.injector.core.RecursiveInjector
 * @see com.drs.gem.injector.core.PriorityLoopInjector
 */
interface Injector {
    
    /**
     * Creates new object of specified module class.
     * 
     * @param   buildCons       appropriate module Constructor. It can be 
     *                          also constructor of module builder.
     * @param   moduleInterface class object of module interface
     * @return                  module object
     */
    GemModule newModule(Constructor buildCons, Class moduleInterface);
}
